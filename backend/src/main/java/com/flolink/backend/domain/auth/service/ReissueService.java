package com.flolink.backend.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ReissueService {

	void reissue(HttpServletRequest request, HttpServletResponse response);
}
