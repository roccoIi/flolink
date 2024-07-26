package com.flolink.backend.domain.room.service;

import java.util.List;

import com.flolink.backend.domain.room.dto.request.RoomCreateRequest;
import com.flolink.backend.domain.room.dto.request.RoomParticipateRequest;
import com.flolink.backend.domain.room.dto.request.RoomUpdateRequest;
import com.flolink.backend.domain.room.dto.response.RoomMemberInfoResponse;
import com.flolink.backend.domain.room.dto.response.RoomSummarizeResponse;

public interface RoomService {

	List<RoomSummarizeResponse> getAllRooms(final Integer userId);

	RoomSummarizeResponse createRoom(final Integer userId, final RoomCreateRequest roomCreateRequest);

	RoomSummarizeResponse registerRoom(final Integer userId, final RoomParticipateRequest roomParticipateRequest);

	String getMyRole(final Integer userId, final Integer roomId);

	List<RoomMemberInfoResponse> getRoomMemberInfos(final Integer userId, final Integer roomId);

	RoomSummarizeResponse updateRoomDetail(final Integer userId, final RoomUpdateRequest roomUpdateRequest);

	String exitRoom(final Integer userId, final Integer roomId);

	String kickRoomMember(final Integer userId, final Integer roomId, final Integer userRoomId);
}