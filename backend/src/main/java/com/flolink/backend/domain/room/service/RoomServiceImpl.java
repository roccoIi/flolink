package com.flolink.backend.domain.room.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flolink.backend.domain.fcm.event.FcmEvent;
import com.flolink.backend.domain.fcm.repository.FcmRepository;
import com.flolink.backend.domain.noti.entity.Noti;
import com.flolink.backend.domain.noti.repository.NotiRepository;
import com.flolink.backend.domain.observer.service.ActivityService;
import com.flolink.backend.domain.plant.entity.Plant;
import com.flolink.backend.domain.plant.entity.enumtype.ActivityPointType;
import com.flolink.backend.domain.plant.entity.plantexp.PlantUserExp;
import com.flolink.backend.domain.plant.repository.PlantUserExpRepository;
import com.flolink.backend.domain.plant.service.PlantService;
import com.flolink.backend.domain.room.dto.request.NicknameUpdateRequest;
import com.flolink.backend.domain.room.dto.request.RoomCreateRequest;
import com.flolink.backend.domain.room.dto.request.RoomParticipateRequest;
import com.flolink.backend.domain.room.dto.request.RoomUpdateRequest;
import com.flolink.backend.domain.room.dto.response.RoomMemberInfoResponse;
import com.flolink.backend.domain.room.dto.response.RoomSummarizeResponse;
import com.flolink.backend.domain.room.entity.Nickname;
import com.flolink.backend.domain.room.entity.Room;
import com.flolink.backend.domain.room.entity.UserRoom;
import com.flolink.backend.domain.room.repository.NicknameRepository;
import com.flolink.backend.domain.room.repository.RoomRepository;
import com.flolink.backend.domain.room.repository.UserRoomRepository;
import com.flolink.backend.domain.user.entity.User;
import com.flolink.backend.domain.user.repository.UserRepository;
import com.flolink.backend.global.common.ResponseCode;
import com.flolink.backend.global.common.exception.BadRequestException;
import com.flolink.backend.global.common.exception.NotFoundException;
import com.flolink.backend.global.common.exception.UnAuthorizedException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

	private final ActivityService activityService;

	private final PlantService plantService;

	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	private final PlantUserExpRepository plantUserExpRepository;
	private final UserRoomRepository userRoomRepository;
	private final NicknameRepository nicknameRepository;
	private final NotiRepository notiRepository;
	private final FcmRepository fcmRepository;

	private final ApplicationEventPublisher eventPublisher;

	@Override
	public List<RoomSummarizeResponse> getAllRooms(final Integer userId) {
		return userRoomRepository.findAllByUserUserId(userId)
			.stream()
			.map((UserRoom) -> RoomSummarizeResponse.fromEntity(UserRoom.getRoom()))
			.toList();
	}

	public RoomSummarizeResponse getRoomById(final Integer roomId) {
		Room room = findRoomById(roomId);
		return RoomSummarizeResponse.fromEntity(room);
	}

	@Override
	@Transactional
	public RoomSummarizeResponse createRoom(final Integer userId, final RoomCreateRequest roomCreateRequest) {
		User user = findUserById(userId);
		if (user.getUserRoomList().size() >= 4) {
			throw new BadRequestException(ResponseCode.USER_ROOM_OVER_MAX_LIMIT);
		}

		Room room = roomRepository.save(roomCreateRequest.toEntity());

		UserRoom userRoom = userRoomRepository.save(UserRoom.of(user, room));
		userRoom.setRole("admin");

		plantService.createPlant(userRoom, room);

		return RoomSummarizeResponse.fromEntity(room);
	}

	@Override
	@Transactional
	public RoomSummarizeResponse registerRoom(final Integer userId,
		final RoomParticipateRequest roomParticipateRequest) {
		User user = findUserById(userId);
		Room room = findRoomById(roomParticipateRequest.getRoomId());
		if (user.getUserRoomList().size() >= 4) {
			throw new BadRequestException(ResponseCode.USER_ROOM_OVER_MAX_LIMIT);
		}
		for (UserRoom userRoom : user.getUserRoomList()) {
			if (Objects.equals(userRoom.getRoom(), room)) { //이미 가입 된 경우 200 주되 data는 비움
				return null;
			}
		}
		if (!room.getRoomParticipatePassword().equals(roomParticipateRequest.getRoomParticipatePassword())) {
			throw new UnAuthorizedException(ResponseCode.WRONG_PARTICIPATION_PASSWORD);
		}
		UserRoom userRoom = userRoomRepository.save(UserRoom.of(user, room));
		Plant plant = plantService.findByRoomId(room.getRoomId());

		if (!plantUserExpRepository.existsByPlantIdAndUserId(userId, plant.getPlantId())) {
			plantUserExpRepository.save(PlantUserExp.of(userRoom.getUser().getUserId(), plant));
		}

		return RoomSummarizeResponse.fromEntity(room);
	}

	@Override
	public String getMyRole(final Integer userId, final Integer roomId) {
		User user = findUserById(userId);
		Room room = findRoomById(roomId);
		UserRoom userRoom = findUserRoomByUserAndRoom(user, room);
		return userRoom.getRole();
	}

	@Override
	public List<RoomMemberInfoResponse> getRoomMemberInfos(final Integer userId, final Integer roomId) {
		User user = findUserById(userId);
		Room room = findRoomById(roomId);
		UserRoom userRoom = findUserRoomByUserAndRoom(user, room);

		List<Nickname> nicknames = userRoom.getNickNameList();
		List<RoomMemberInfoResponse> roomMemberInfoResponses = room.getUserRoomList()
			.stream()
			.map(RoomMemberInfoResponse::fromUserRoomEntity)
			.toList();

		for (RoomMemberInfoResponse response : roomMemberInfoResponses) {
			for (Nickname nickname : nicknames) {
				if (response.getTargetUserRoomId().equals(nickname.getTargetUserRoomId())) {
					response.setTargetNickname(nickname.getTargetNickname());
					break;
				}
			}
		}
		return roomMemberInfoResponses;
	}

	@Override
	@Transactional
	public String exitRoom(final Integer userId, final Integer roomId) {
		User user = findUserById(userId);
		Room room = findRoomById(roomId);
		UserRoom userRoom = findUserRoomByUserAndRoom(user, room);

		if (userRoom.getRole().equalsIgnoreCase("admin")) {
			List<UserRoom> userRoomList = room.getUserRoomList();
			userRoomList.sort(Comparator.comparing(UserRoom::getCreateAt));
			for (UserRoom ur : userRoomList) {
				if (!ur.getUser().getUserId().equals(userId)) {
					ur.setRole("admin");
					break;
				}
			}
		}

		userRoomRepository.delete(userRoom);
		room.getUserRoomList().remove(userRoom);
		if (room.getUserRoomList().isEmpty()) {
			roomRepository.delete(room);
		}
		return "success";
	}

	@Override
	@Transactional
	public String kickRoomMember(final Integer userId, final Integer roomId, final Integer targetUserRoomId) {
		String myRole = getMyRole(userId, roomId);

		if (!myRole.equalsIgnoreCase("admin")) {
			return "failed";
		}

		UserRoom userRoom = findUserRoomByUserRoomId(targetUserRoomId);

		userRoomRepository.delete(userRoom);
		return "success";
	}

	@Override
	@Transactional
	public void enterRoom(final Integer userId, final Integer roomId) {
		User user = findUserById(userId);
		Room room = findRoomById(roomId);
		UserRoom userRoom = findUserRoomByUserAndRoom(user, room);
		if (isFirstAttendanceOfToday(userRoom.getLastLoginTime())) {
			increaseExpAboutActivity(ActivityPointType.ATTENDANCE, room.getRoomId(), userRoom.getUserRoomId(), userId);
		}

		userRoom.updateLoginTime();
	}

	@Override
	@Transactional
	public String updateRoomMemberNickname(final Integer userId, final NicknameUpdateRequest nicknameUpdateRequest) {

		UserRoom userRoom = findUserRoomByUserIdAndRoomId(userId, nicknameUpdateRequest.getRoomId());
		Optional<Nickname> optionalNickname = nicknameRepository.findByUserRoomUserRoomIdAndTargetUserRoomId(
			userRoom.getUserRoomId(), nicknameUpdateRequest.getTargetUserRoomId());

		Nickname nickname = null;

		if (optionalNickname.isPresent()) {
			nickname = optionalNickname.get();
			nickname.setTargetNickname(nicknameUpdateRequest.getTargetNickname());
			nicknameRepository.save(nickname);
		} else {
			nickname = Nickname.builder()
				.userRoom(userRoom)
				.targetUserRoomId(nicknameUpdateRequest.getTargetUserRoomId())
				.targetNickname(nicknameUpdateRequest.getTargetNickname())
				.build();
			nicknameRepository.save(nickname);
		}
		return "success";
	}

	@Override
	@Transactional
	public RoomSummarizeResponse updateRoomDetail(final Integer userId, final RoomUpdateRequest roomUpdateRequest) {
		User user = findUserById(userId);
		Room room = findRoomById(roomUpdateRequest.getRoomId());
		UserRoom userRoom = findUserRoomByUserAndRoom(user, room);
		if (userRoom == null) {
			throw new NotFoundException(ResponseCode.USER_ROOM_NOT_FOUND);
		}

		if (roomUpdateRequest.getRoomParticipatePassword() != null) {
			if (!userRoom.getRole().equalsIgnoreCase("admin")) {
				throw new UnAuthorizedException(ResponseCode.NOT_AUTHORIZED);
			}
			room.setRoomParticipatePassword(roomUpdateRequest.getRoomParticipatePassword());
		}
		if (roomUpdateRequest.getRoomName() != null) {
			if (!userRoom.getRole().equalsIgnoreCase("admin")) {
				throw new UnAuthorizedException(ResponseCode.NOT_AUTHORIZED);
			}
			room.setRoomName(roomUpdateRequest.getRoomName());
		}
		boolean isNotiChanged = false;
		if (roomUpdateRequest.getNotice() != null) {
			isNotiChanged = true;
			room.setNotice(roomUpdateRequest.getNotice());
		}
		roomRepository.save(room);
		List<UserRoom> userRooms = room.getUserRoomList();
		if (isNotiChanged) {
			userRooms.stream()
				.filter((curUserRoom -> !curUserRoom.getUser().getUserId().equals(userId)))
				.forEach((curUserRoom) -> {
					Noti noti = Noti.builder()
						.userRoom(curUserRoom)
						.message("공지가 변경되었어요.")
						.createAt(LocalDateTime.now())
						.build();
					notiRepository.save(noti);

					if (curUserRoom.getUser().getFcm() != null) {
						try {
							FcmEvent fcmEvent = new FcmEvent(this, "공지가 변경되었어요.", "지금 바로 확인해보세요!",
								curUserRoom.getUser().getFcm().getFcmToken());
							eventPublisher.publishEvent(fcmEvent);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		}

		return RoomSummarizeResponse.fromEntity(room);
	}

	public User findUserById(final Integer userId) {
		return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ResponseCode.USER_NOT_FOUND));
	}

	public Room findRoomById(final Integer roomId) {
		return roomRepository.findById(roomId).orElseThrow(() -> new NotFoundException(ResponseCode.ROOM_NOT_FOUND));
	}

	public UserRoom findUserRoomByUserAndRoom(final User user, final Room room) {
		return userRoomRepository.findByUserAndRoom(user, room)
			.orElseThrow(() -> new NotFoundException(ResponseCode.USER_ROOM_NOT_FOUND));
	}

	public UserRoom findUserRoomByUserIdAndRoomId(final Integer userId, final Integer roomId) {
		return userRoomRepository.findByUserUserIdAndRoomRoomId(userId, roomId)
			.orElseThrow(() -> new NotFoundException(ResponseCode.USER_ROOM_NOT_FOUND));

	}

	public UserRoom findUserRoomByUserRoomId(final Integer userRoomId) {
		return userRoomRepository.findUserRoomByUserRoomId(userRoomId)
			.orElseThrow(() -> new NotFoundException(ResponseCode.USER_ROOM_NOT_FOUND));
	}

	@Override
	public Integer getMyUserRoomId(final Integer userId, final Integer roomId) {
		Integer userRoomId = null;
		UserRoom userRoom = findUserRoomByUserIdAndRoomId(userId, roomId);
		userRoomId = userRoom.getUserRoomId();
		return userRoomId;
	}

	private boolean isFirstAttendanceOfToday(LocalDateTime lastLoginTime) {
		if (lastLoginTime == null) {
			return true;
		}
		LocalDate today = LocalDate.now();
		LocalDate plantUpdateDate = lastLoginTime.toLocalDate();
		return plantUpdateDate.isBefore(today);
	}

	private void increaseExpAboutActivity(ActivityPointType type, Integer roomId, Integer userRoomId, Integer userId) {
		activityService.performActivity(userId, roomId, userRoomId, type);
	}
}
