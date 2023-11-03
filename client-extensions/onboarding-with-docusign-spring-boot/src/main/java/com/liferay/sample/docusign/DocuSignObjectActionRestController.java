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
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rafael Oliveira
 */
@RequestMapping("/docusign")
@RestController
public class DocuSignObjectActionRestController extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(
			@AuthenticationPrincipal Jwt jwt, @RequestBody String json)
		throws Exception {

		log(jwt, _log, json);

		sendDSEnvelope(jwt, json);

		return new ResponseEntity<>(json, HttpStatus.OK);
	}

	public void sendDSEnvelope(Jwt jwt, String json) {
		WebClient.Builder builder = WebClient.builder();
		WebClient webClient = buildWebClient();

		JSONObject jsonObject = new JSONObject(json);
		int groupId = getGroupId(jsonObject);
		String supplierEmail = getSupplierEmail(jsonObject);
		int docuSignConfigId = getDocuSignConfigId(jsonObject);

		Mono<String> docuSignConfigMono = retrieveDocuSignConfig(webClient, jwt, docuSignConfigId);

		docuSignConfigMono.subscribe(docuSignConfigMonoOutput -> {

			_log.info("docuSignConfigjsonObject: " + docuSignConfigMonoOutput);

			JSONObject docuSignConfigjsonObject = new JSONObject(docuSignConfigMonoOutput);
			String docuSignTabs = getDocuSignTabs(docuSignConfigjsonObject);
			String fileExternalReferenceCode = getFileExternalReferenceCode(docuSignConfigjsonObject);

			Mono<String> docuSignRequestMono = createDocuSignRequest(webClient, jwt, fileExternalReferenceCode, docuSignTabs, groupId, supplierEmail);
			docuSignRequestMono
					.doOnNext(docuSignOutput -> {
						if (_log.isInfoEnabled()) {
							_log.info("Output of the second request: " + docuSignOutput);
						}
					})
					.subscribe();
			}
		);
	}

	private static String getFileExternalReferenceCode(JSONObject docuSignConfigjsonObject) {
		JSONObject docuSignPDFTemplateJsonObject = docuSignConfigjsonObject.getJSONObject("docuSignPDFTemplate");
		JSONObject linkJSONObject = docuSignPDFTemplateJsonObject.getJSONObject("link");
		String href = linkJSONObject.getString("href");
		String fileName = docuSignPDFTemplateJsonObject.getString("name");

		String regex = "\\/documents\\/\\d+\\/\\d+\\/"+fileName+"\\/(.*)\\?version";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(href);
		if (matcher.find()) {
			String fileExternalReferenceCode = matcher.group(1);
			return fileExternalReferenceCode;
		}

		throw new RuntimeException("file external reference code not found in: " + href);
	}

	private static String getDocuSignTabs(JSONObject docuSignConfigjsonObject) {
		String docuSignTabs = docuSignConfigjsonObject.getString("docuSignTabs");
		return docuSignTabs;
	}

	private WebClient buildWebClient() {
		return WebClient.builder()
				.baseUrl(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.filter(logRequest())
				.build();
	}

	private int getGroupId(JSONObject jsonObject) {
		JSONObject objectEntryJsonObject = jsonObject.getJSONObject("objectEntry");
		return objectEntryJsonObject.getInt("groupId");
	}

	private String getSupplierEmail(JSONObject jsonObject) {
		JSONObject objectEntryJsonObject = jsonObject.getJSONObject("objectEntry");
		JSONObject objectEntryValuesJsonObject = objectEntryJsonObject.getJSONObject("values");
		return objectEntryValuesJsonObject.getString("supplierEmail");
	}

	private int getDocuSignConfigId(JSONObject jsonObject) {
		JSONObject objectEntryJsonObject = jsonObject.getJSONObject("objectEntry");
		JSONObject objectEntryValuesJsonObject = objectEntryJsonObject.getJSONObject("values");
		return objectEntryValuesJsonObject.getInt("r_supplierDocuSignConfig_c_docuSignConfigId");
	}

	private Mono<String> retrieveDocuSignConfig(WebClient webClient, Jwt jwt, int docuSignConfigId) {
		return webClient.get()
				.uri("o/c/docusignconfigs/" + docuSignConfigId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue())
				.exchange()
				.flatMap(clientResponse -> handleResponse(clientResponse));
	}

	private Mono<String> createDocuSignRequest(WebClient webClient, Jwt jwt, String fileExternalReferenceCode,  String docuSignTabs, int groupId, String supplierEmail) {

		_log.info("fileExternalReferenceCode: " + fileExternalReferenceCode);
		_log.info("supplierEmail: " + supplierEmail);
		_log.info("docuSignTabs: " + docuSignTabs);

		String requestBody = "{ " +
				"\"dsDocument\": [" +
				" { " +
				"\"fileEntryExternalReferenceCode\": \""+fileExternalReferenceCode+"\"," +
				" \"transformPDFFields\": false," +
				" \"fileExtension\": \"\"," +
				" \"name\": \"Onboarding Application\"," +
				" \"id\": \"1\"," +
				" \"uri\": \"\"" +
				" }" +
				" ]," +
				" \"dsRecipient\": [" +
				" {" +
				" \"emailAddress\": \""+supplierEmail+"\"," +
				" \"name\": \"Test Test\"," +
				" \"id\": \"1\"," +
				" \"tabs\":" + docuSignTabs + "," +
				" \"status\": \"sent\"" +
				" }" +
				" ]," +
				" \"emailBlurb\": \"Please review and sign your submission\"," +
				" \"emailSubject\": \"Please review and sign your submission\"," +
				" \"name\": \"Please review and sign your submission\"," +
				" \"status\": \"sent\"" +
				"}";

		_log.info("requestBody" + requestBody);

		return webClient.post()
				.uri("o/digital-signature-rest/v1.0/sites/" + groupId + "/ds-envelopes")
				.bodyValue(requestBody)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue())
				.exchange()
				.flatMap(clientResponse -> handleResponse(clientResponse));
	}

	private Mono<String> handleResponse(ClientResponse clientResponse) {
		HttpStatus httpStatus = clientResponse.statusCode();
		if (httpStatus.is2xxSuccessful()) {
			return clientResponse.bodyToMono(String.class);
		} else if (httpStatus.is4xxClientError()) {
			return Mono.just(httpStatus.getReasonPhrase());
		} else {
			return clientResponse.createException().flatMap(Mono::error);
		}
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
			DocuSignObjectActionRestController.class);

}
