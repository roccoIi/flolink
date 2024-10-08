package com.flolink.backend.domain.fcm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flolink.backend.domain.fcm.service.FcmService;
import com.flolink.backend.domain.user.dto.response.CustomUserDetails;
import com.flolink.backend.global.common.CommonResponse;
import com.flolink.backend.global.common.ResponseCode;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class FcmController {
	private final FcmService fcmService;

	@PostMapping("/register")
	@Operation(summary = "fcm 토큰 보내기")
	public ResponseEntity<?> registerFcm(Authentication authentication, @RequestBody String token) {
		log.info("===클라이언트로부터 FCM 수신 START===");
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		Integer userId = userDetails.getUserId();
		fcmService.saveToken(userId, token);
		log.info("===클라이언트로부터 FCM 수신 END===");
		return ResponseEntity.ok(CommonResponse.of(ResponseCode.COMMON_SUCCESS));
	}
}
