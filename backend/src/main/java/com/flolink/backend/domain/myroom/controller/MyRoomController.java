package com.flolink.backend.domain.myroom.controller;

import static com.flolink.backend.global.common.ResponseCode.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flolink.backend.domain.myroom.dto.request.HasItemRequest;
import com.flolink.backend.domain.myroom.dto.response.HasItemInfoResponse;
import com.flolink.backend.domain.myroom.dto.response.MyRoomResponse;
import com.flolink.backend.domain.myroom.service.HasItemService;
import com.flolink.backend.domain.myroom.service.MyRoomService;
import com.flolink.backend.global.common.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/myroom")
@RequiredArgsConstructor
@Tag(name = "MyRoom API", description = "마이룸과 관련된 API")
public class MyRoomController {

	private final HasItemService hasItemService;
	private final MyRoomService myRoomService;

	@GetMapping
	@Operation(summary = "마이룸 정보 조회")
	public ResponseEntity<CommonResponse> getMyRoom() {
		Integer userId = 1;
		MyRoomResponse inventory = myRoomService.getMyRoom(userId);
		return ResponseEntity.ok(CommonResponse.of(COMMON_SUCCESS, inventory));
	}

	@PatchMapping("/equip")
	@Operation(summary = "마이룸에 아이템 장착")
	public ResponseEntity<CommonResponse> equipItem(@RequestBody final HasItemRequest request) {
		Integer userId = 1;
		MyRoomResponse response = myRoomService.equipItem(userId, request);
		return ResponseEntity.ok(CommonResponse.of(COMMON_SUCCESS, response));
	}

	@PatchMapping("/unequip")
	@Operation(summary = "마이룸에 아이템 장착 해제")
	public ResponseEntity<CommonResponse> unequipItem(@RequestBody final HasItemRequest request) {
		Integer userId = 1;
		MyRoomResponse response = myRoomService.unequipItem(userId, request);
		return ResponseEntity.ok(CommonResponse.of(COMMON_SUCCESS, response));
	}

	@GetMapping("/inventory")
	@Operation(summary = "인벤토리 조회")
	public ResponseEntity<CommonResponse> getInventory() {
		Integer userId = 1;
		List<HasItemInfoResponse> inventory = hasItemService.getHasItems(userId);
		return ResponseEntity.ok(CommonResponse.of(COMMON_SUCCESS, inventory));
	}

}
