package com.flolink.backend.global.auth.service;

public interface IdempotencyService {

	boolean isDuplicateRequest(String requestId);

	void saveRequestId(String requestId);
}
