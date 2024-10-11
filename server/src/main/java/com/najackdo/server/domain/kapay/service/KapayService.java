package com.najackdo.server.domain.kapay.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.najackdo.server.core.exception.ErrorCode;
import com.najackdo.server.core.response.ErrorResponse;
import com.najackdo.server.domain.kapay.dto.ApproveRequest;
import com.najackdo.server.domain.kapay.dto.ApproveResponse;
import com.najackdo.server.domain.kapay.dto.ReadyRequest;
import com.najackdo.server.domain.kapay.dto.ReadyResponse;
import com.najackdo.server.domain.user.entity.User;
import com.najackdo.server.domain.user.event.UserPaymentEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class KapayService {

	@Value("${kakaopay.api.secret.key}")
	private String kakaopaySecretKey;

	@Value("${cid}")
	private String cid;

	@Value("${kakaopay.api.host}")
	private String sampleHost;

	private String tid;

	private User user;

	private final ApplicationEventPublisher eventPublisher;

	public ReadyResponse ready(User user, String agent, String openType, String itemName, Integer totalAmount) {

		this.user = user;

		// 요청 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "DEV_SECRET_KEY " + kakaopaySecretKey);
		headers.setContentType(MediaType.APPLICATION_JSON);

		// 요청 파라미터 설정
		ReadyRequest readyRequest = ReadyRequest.builder()
			.cid(cid)
			.partnerOrderId("1")
			.partnerUserId("1")
			.itemName(itemName)
			.quantity(1)
			.totalAmount(totalAmount)
			.taxFreeAmount(0)
			.vatAmount(100)
			.approvalUrl(sampleHost + "/api/v1/kapay/approve/" + agent + "/" + openType)
			.cancelUrl(sampleHost + "/api/v1/kapay/cancel/" + agent + "/" + openType)
			.failUrl(sampleHost + "/api/v1/kapay/fail/" + agent + "/" + openType)
			.build();

		// 요청 전송
		HttpEntity<ReadyRequest> entityMap = new HttpEntity<>(readyRequest, headers);
		ResponseEntity<ReadyResponse> response = new RestTemplate().postForEntity(
			"https://open-api.kakaopay.com/online/v1/payment/ready",
			entityMap,
			ReadyResponse.class
		);


		// TID 저장 (승인 요청 시 사용)
		this.tid = response.getBody().getTid();
		return response.getBody();
	}

	@Transactional
	public ResponseEntity<?> approve(String pgToken) {
		try {


			// 요청 헤더 설정
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "SECRET_KEY " + kakaopaySecretKey);
			headers.setContentType(MediaType.APPLICATION_JSON);

			// 요청 파라미터 설정
			ApproveRequest approveRequest = ApproveRequest.builder()
				.cid(cid)
				.tid(tid)
				.partnerOrderId("1")
				.partnerUserId("1")
				.pgToken(pgToken)
				.build();

			// 요청 전송
			HttpEntity<ApproveRequest> entityMap = new HttpEntity<>(approveRequest, headers);
			ResponseEntity<String> response = new RestTemplate().postForEntity(
				"https://open-api.kakaopay.com/online/v1/payment/approve",
				entityMap,
				String.class
			);


			ObjectMapper objectMapper = new ObjectMapper();
			ApproveResponse approveResponse = objectMapper.readValue(response.getBody(), ApproveResponse.class);


			if (approveResponse != null) {
				Integer totalAmount = approveResponse.getAmount().getTotal(); // 결제 금액
				eventPublisher.publishEvent(new UserPaymentEvent(user, totalAmount));
			}

			return ResponseEntity.ok(approveResponse);

		} catch (HttpStatusCodeException ex) {
			String responseBody = ex.getResponseBodyAsString();
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode;
			try {
				jsonNode = objectMapper.readTree(responseBody);
			} catch (JsonProcessingException e) {
				return ResponseEntity.status(ex.getStatusCode())
					.body(ErrorResponse.of(ErrorCode.KAKAO_PAY_API_ERROR, "Error processing JSON", null));
			}

			JsonNode extrasNode = jsonNode.get("extras");
			String errorMessage =
				jsonNode.has("error_message") ? jsonNode.get("error_message").asText() : "Unknown error";

			return ResponseEntity.status(ex.getStatusCode())
				.body(ErrorResponse.of(ErrorCode.KAKAO_PAY_API_ERROR, errorMessage, extrasNode));
		} catch (Exception ex) {
			return ResponseEntity.status(500).body(ErrorResponse.of(ErrorCode.SERVER_ERROR, ex.getMessage()));
		}
	}
}
