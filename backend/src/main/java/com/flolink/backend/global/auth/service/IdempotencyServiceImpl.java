package com.flolink.backend.global.auth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class IdempotencyServiceImpl implements IdempotencyService{

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	private static final long EXPIRATION_TIME = 3L; // TTL 설정 (60초 등 적절히 설정 가능)


	// Redis에서 중복된 요청인지 확인하는 메서드
	@Override
	public boolean isDuplicateRequest(String requestId) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(requestId));  // 이미 Redis에 저장된 경우 중복 요청으로 간주
	}

	// Redis에 요청 ID를 저장하는 메서드 (TTL 설정 포함)
	@Override
	public void saveRequestId(String requestId) {
		redisTemplate.opsForValue().set(requestId, "processed", EXPIRATION_TIME, TimeUnit.SECONDS); // TTL 설정
	}
}
