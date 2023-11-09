package com.liferay.sample.docusign;

import com.google.gson.Gson;
import com.liferay.sample.docusign.dto.docuSignConfig.DocuSignConfigDTO;
import com.liferay.sample.docusign.dto.docuSignEnvelope.DSDocument;
import com.liferay.sample.docusign.dto.docuSignEnvelope.DSRecipient;
import com.liferay.sample.docusign.dto.docuSignEnvelope.DocuSignEnvelopeDTO;
import com.liferay.sample.docusign.dto.docuSignTabs.DocuSignTabsDTO;
import com.liferay.sample.docusign.dto.docuSignTabs.Tab;
import com.liferay.sample.docusign.dto.supplier.SupplierDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import java.util.List;
import java.util.Map;
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
		WebClient webClient = buildWebClient();

		Gson gson = new Gson();
		SupplierDTO supplier = gson.fromJson(json, SupplierDTO.class);

		int docuSignConfigId = Integer.parseInt(supplier.getObjectEntryDTOSupplier().getProperties().get("r_supplierDocuSignConfig_c_docuSignConfigId"));

		Mono<String> docuSignConfigMono = retrieveDocuSignConfig(webClient, jwt, docuSignConfigId);

		docuSignConfigMono.subscribe(docuSignConfigMonoOutput -> {

			if (_log.isDebugEnabled()) {
				_log.info("docuSignConfigJSONObject: " + docuSignConfigMonoOutput);
			}

			DocuSignConfigDTO docuSignConfig = gson.fromJson(docuSignConfigMonoOutput, DocuSignConfigDTO.class);
			DocuSignTabsDTO docuSignTabs = gson.fromJson(docuSignConfig.getDocuSignTabs(), DocuSignTabsDTO.class);

			Mono<String> docuSignRequestMono = createDocuSignRequest(webClient, jwt, supplier, docuSignConfig, docuSignTabs);
			docuSignRequestMono
					.doOnNext(docuSignOutput -> {
						if (_log.isDebugEnabled()) {
							_log.debug("DocuSign request: " + docuSignOutput);
						}
					})
					.subscribe();
			}
		);
	}


	private static String getFileExternalReferenceCode(DocuSignConfigDTO docuSignConfig) {
		String href = docuSignConfig.getDocuSignPDFTemplate().getLink().getHref();
		String fileName = docuSignConfig.getDocuSignPDFTemplate().getName();

		String regex = "\\/documents\\/\\d+\\/\\d+\\/"+fileName+"\\/(.*)\\?version";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(href);
		if (matcher.find()) {
            return matcher.group(1);
		}

		throw new RuntimeException("file external reference code not found in: " + href);
	}

	private WebClient buildWebClient() {
		return WebClient.builder()
				.baseUrl(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.filter(logRequest())
				.build();
	}

	private Mono<String> retrieveDocuSignConfig(WebClient webClient, Jwt jwt, int docuSignConfigId) {
		return webClient.get()
				.uri("o/c/docusignconfigs/" + docuSignConfigId + "?nestedFields=externalReferenceCode")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue())
				.exchange()
				.flatMap(this::handleResponse);
	}

	private Mono<String> createDocuSignRequest(WebClient webClient, Jwt jwt, SupplierDTO supplier, DocuSignConfigDTO docuSignConfig, DocuSignTabsDTO docuSignTabs) {

		String fileExternalReferenceCode = getFileExternalReferenceCode(docuSignConfig);
		long groupId = supplier.getObjectEntry().getGroupId();
		String supplierEmail = supplier.getObjectEntryDTOSupplier().getProperties().get("supplierEmail");
		String supplierName = supplier.getObjectEntryDTOSupplier().getProperties().get("supplierName");

		_log.info("groupId: " + groupId);
		_log.info("fileExternalReferenceCode: " + fileExternalReferenceCode);
		_log.info("supplierEmail: " + supplierEmail);

		// DSDocument
		DSDocument dsDocument = new DSDocument();
		dsDocument.setFileEntryExternalReferenceCode(fileExternalReferenceCode);
		dsDocument.setName(docuSignConfig.getDocuSignPDFTemplate().getName());

		// DSRecipient
		DSRecipient dsRecipient = new DSRecipient();
		dsRecipient.setEmailAddress(supplierEmail);
		dsRecipient.setName(supplierName);
		populateDocuSignTabs(supplier, dsRecipient, docuSignTabs);

		// DSEnvelope
		DocuSignEnvelopeDTO docuSignEnvelope = new DocuSignEnvelopeDTO();
		docuSignEnvelope.getDsDocument().add(dsDocument);
		docuSignEnvelope.getDsRecipient().add(dsRecipient);
		docuSignEnvelope.setEmailBlurb("Please review and sign your submission");
		docuSignEnvelope.setEmailSubject("Please review and sign your submission");
		docuSignEnvelope.setName("Please review and sign your submission");
		docuSignEnvelope.setStatus("sent");

		Gson gson = new Gson();
		_log.info(gson.toJson(docuSignEnvelope));

		// POST
		return webClient.post()
				.uri("o/digital-signature-rest/v1.0/sites/" + groupId + "/ds-envelopes")
				.bodyValue(docuSignEnvelope)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue())
				.exchange()
				.flatMap(this::handleResponse);
	}

	private void populateDocuSignTabs(SupplierDTO supplier, DSRecipient dsRecipient, DocuSignTabsDTO docuSignTabs) {

		dsRecipient.getTabs().put(
				"signHereTabs",
				populateDocuSignTabs(supplier, docuSignTabs.getSignHereTabs()));

		dsRecipient.getTabs().put(
				"dateTabs",
				populateDocuSignTabs(supplier, docuSignTabs.getDateTabs()));

		dsRecipient.getTabs().put(
				"checkboxTabs",
				populateDocuSignTabs(supplier, docuSignTabs.getCheckboxTabs()));

		dsRecipient.getTabs().put(
				"textTabs",
				populateDocuSignTabs(supplier, docuSignTabs.getTextTabs()));
	}

	private List<Tab> populateDocuSignTabs(SupplierDTO supplier, List<Tab> tabs) {
		for (Tab textTab : tabs){
			Map<String, String> props = supplier.getObjectEntryDTOSupplier().getProperties();
			if (props.containsKey(textTab.getName())){
				textTab.setValue(props.get(textTab.getName()));
			}
		}

		return tabs;
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
