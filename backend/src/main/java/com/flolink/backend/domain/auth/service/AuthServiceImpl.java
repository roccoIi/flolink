package com.flolink.backend.domain.auth.service;

import java.time.LocalDateTime;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

import com.flolink.backend.domain.auth.dto.request.CheckAuthRequest;
import com.flolink.backend.domain.auth.dto.request.PhoneAuthRequest;
import com.flolink.backend.domain.auth.entity.Auth;
import com.flolink.backend.domain.auth.repository.AuthRepository;
import com.flolink.backend.global.common.ResponseCode;
import com.flolink.backend.global.common.exception.NotFoundException;
import com.flolink.backend.global.common.exception.TimeOutException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthRepository authRepository;

	/**
	 *
	 * @param phoneAuthRequest (휴대전화번호)
	 * 입력들어온 휴대전화번호로 인증문자 발송
	 */
	@Override
	public void sendAuthenticationNumber(PhoneAuthRequest phoneAuthRequest) {
		DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(
			"NCSP0K6DNQVU8CXM", "AJAWO3QEAIT3B34061IGMOBD3PQX9QQR", "https://api.solapi.com");

		String randomAuthNum = RandomStringUtils.randomNumeric(6);

		Message message = new Message();
		message.setFrom("01042121037");
		message.setTo(phoneAuthRequest.getTel());
		message.setText("본인확인 인증번호" + "[" + randomAuthNum + "]를 화면에 입력해주세요. 타인 노출 금지");

		// 만약에 이미 db에 발송이 되어있다면 넘어가기!(따닥 방지)
		if (authRepository.existsByTel(phoneAuthRequest.getTel())) {
			//TODO return 이 아니라 오류를 만들어야 하지 않을까? NOT FOUND exception??
			return;
		}

		try {
			messageService.send(message);
			authRepository.save(phoneAuthRequest.toEntity(randomAuthNum));
		} catch (NurigoMessageNotReceivedException exception) {
			// 발송에 실패한 메시지 목록을 확인할 수 있습니다!
			System.out.println(exception.getFailedMessageList());
			System.out.println(exception.getMessage());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}

	}

	/**
	 * @param checkAuthRequest (휴대전화번호, 인증번호)
	 * 입력된 인증번호가 휴대전화번호와 일치하는지 확인한 후 해당 일치한다면 성공토큰을 세선에, 아니라면 넣지 않는다. (오류메세지???)
	 */
	@Override
	public void checkAuthenticationNumber(CheckAuthRequest checkAuthRequest) {
		Auth auth = authRepository.findByTel(checkAuthRequest.getTel())
			.orElseThrow(() -> new NotFoundException(ResponseCode.NOT_FOUND_ERROR));

		try {
			// 현재시간(now) 이 유효기간을 지났다면 TimeOutException, 인증번호 불일치라면 NotFoundException
			LocalDateTime now = LocalDateTime.now();
			if (now.isAfter(checkAuthRequest.getExpiredAt())) {
				throw new TimeOutException(ResponseCode.TIME_OUT_EXCEPTION);
			} else if (!auth.getAuthNum().equals(checkAuthRequest.getAuthNum())) {
				throw new NotFoundException(ResponseCode.NOT_FOUND_ERROR);
			}
		} finally {
			authRepository.deleteByTel(checkAuthRequest.getTel());
		}

	}

}
