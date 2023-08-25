/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sample.docusign;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;

/**
 * @author Rafael Oliveira
 */
@RequestMapping("/docusign")
@RestController
public class DocuSignWorkflowActionRestController extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(
			@AuthenticationPrincipal Jwt jwt, @RequestBody String json)
		throws Exception {

		log(jwt, _log, json);

		sendDSEnvelope(jwt, json);

		return new ResponseEntity<>(json, HttpStatus.OK);
	}

	public void sendDSEnvelope(Jwt jwt, String json){
		WebClient.Builder builder = WebClient.builder();

		WebClient webClient = builder.baseUrl(
				lxcDXPServerProtocol + "://" + lxcDXPMainDomain
		).defaultHeader(
				HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
		).defaultHeader(
				HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
		).filter(
				logRequest()
		).build();

		JSONObject jsonObject = new JSONObject(json);

		webClient.post(
		).uri(
				"o/digital-signature-rest/v1.0/sites/35040/ds-envelopes"
		).bodyValue(
				"{ " +
						"\"dsDocument\": [" +
						" { " +
						"\"fileEntryExternalReferenceCode\": \"edd426b6-0aa3-ceae-ab87-a7a01baa3fd6\"," +
						" \"transformPDFFields\": false," +
						" \"fileExtension\": \"\"," +
						" \"name\": \"onboarding-application.pdf\"," +
						" \"id\": \"1\"," +
						" \"uri\": \"\"" +
						" }" +
						" ]," +
						" \"dsRecipient\": [" +
						" {" +
						" \"emailAddress\": \"rafael.oliveira+test@liferay.com\"," +
						" \"name\": \"Test Test\"," +
						" \"id\": \"1\"," +
						" \"tabs\": {}," +
						" \"status\": \"sent\"" +
						" }" +
						" ]," +
						" \"emailBlurb\": \"Please review and sign your submission\"," +
						" \"emailSubject\": \"Please review and sign your submission\"," +
						" \"name\": \"Please review and sign your submission\"," +
						" \"status\": \"sent\"" +
						"}"
		).header(
				HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue()
		).exchangeToMono(
				clientResponse -> {
					HttpStatus httpStatus = clientResponse.statusCode();

					if (httpStatus.is2xxSuccessful()) {
						return clientResponse.bodyToMono(String.class);
					}
					else if (httpStatus.is4xxClientError()) {
						return Mono.just(httpStatus.getReasonPhrase());
					}

					Mono<WebClientResponseException> mono =
							clientResponse.createException();

					return mono.flatMap(Mono::error);
				}
		).doOnNext(
				output -> {
					if (_log.isInfoEnabled()) {
						_log.info("Output: " + output);
					}
				}
		).subscribe();
	}

	private ExchangeFilterFunction logRequest() {
		return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
			_log.info("Request (Method): " + clientRequest.method());
			_log.info("Request (URL): " + clientRequest.url());
			clientRequest.headers().forEach((name, values) -> values.forEach(value -> _log.info(name + ": " + value)));
			return Mono.just(clientRequest);
		});
	}

	private static final Log _log = LogFactory.getLog(
			DocuSignWorkflowActionRestController.class);

}
