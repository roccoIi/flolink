package com.flolink.backend.domain.payment.dto.response;

import java.math.BigDecimal;

import com.flolink.backend.domain.payment.entity.PaymentHistory;
import com.flolink.backend.domain.payment.entity.PaymentState;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentPrepareResponse {
	private String orderId;
	private String orderName;
	private BigDecimal amount;
	private PaymentState state;

	public static PaymentPrepareResponse fromEntity(PaymentHistory paymentHistory) {
		return PaymentPrepareResponse.builder()
			.orderId(paymentHistory.getOrderId())
			.orderName(paymentHistory.getOrderName())
			.amount(paymentHistory.getAmount())
			.state(paymentHistory.getState())
			.build();
	}
}
