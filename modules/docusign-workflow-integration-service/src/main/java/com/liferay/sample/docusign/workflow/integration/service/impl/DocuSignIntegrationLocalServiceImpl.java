/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.sample.docusign.workflow.integration.service.impl;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.sample.docusign.workflow.configuration.PDFDocuSignConfigurator;
import com.liferay.sample.docusign.workflow.integration.service.base.DocuSignIntegrationLocalServiceBaseImpl;

import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.sample.docusign.workflow.integration.model.DocuSignIntegration",
	service = AopService.class
)
public class DocuSignIntegrationLocalServiceImpl
	extends DocuSignIntegrationLocalServiceBaseImpl {

	public void requestDocuSignFromPDFTemplate(
			Map<String, Serializable> workflowContext)
			throws Exception {
		if (_log.isDebugEnabled()) {
			_log.debug("requestDocuSignFromPDFTemplate start");
		}

		DDMFormInstanceRecord record =
				_ddmFormInstanceRecordLocalService.getDDMFormInstanceRecord(
						_getFormInstanceRecordId(workflowContext));

		File templateFile = _getPDFTemplateFile(record.getFormInstanceId());

		if (_log.isDebugEnabled()) {
			_log.debug("Starting requestDocuSignFromPDFTemplate...");
			_log.debug("Template File: " +  templateFile);
			_log.debug("FormInstanceRecord: " +  record);
		}

		_requestDocuSign(workflowContext, record, templateFile);

		_log.debug("requestDocuSign end");
	}

	private void _requestDocuSign(
			Map<String, Serializable> workflowContext,
			DDMFormInstanceRecord record, File templateFile)
			throws Exception {

		String docuSignTabsKey = GetterUtil.getString(
				workflowContext.get("docuSignTabsKey"));
		String newDocuSignInfoKey = GetterUtil.getString(
				workflowContext.get("newDocuSignInfoKey"));
		String envelopeName = GetterUtil.getString(
				workflowContext.get("envelopeName"));
		String envelopeEmailSubject = GetterUtil.getString(
				workflowContext.get("envelopeEmailSubject"));
		String envelopeEmailMessage = GetterUtil.getString(
				workflowContext.get("envelopeEmailMessage"));
		long userId = GetterUtil.getLong(workflowContext.get("userId"));
		long companyId = GetterUtil.getLong(workflowContext.get("companyId"));
		long groupId = GetterUtil.getLong(workflowContext.get("groupId"));

		User user = _userLocalService.getUser(userId);

		String docuSignTabsJSON =
				_pdfDocuSignConfigurator.getConfigurationDataByFormInstanceId(
						record.getFormInstanceId(),
						_pdfDocuSignConfigurator.getDocuSignTabsJSONs());

		JSONObject docuSignTabsJSONObj = _jsonFactory.createJSONObject(
				docuSignTabsJSON);

		JSONObject docuSignFormJSONObj = docuSignTabsJSONObj.getJSONObject(
				docuSignTabsKey);

		JSONObject docuSignTabs = docuSignFormJSONObj.getJSONObject(
				"docuSignTabs");

		JSONArray acceptFieldReferences = docuSignFormJSONObj.getJSONArray(
				"acceptFieldReferences");

		if (_log.isDebugEnabled()) {
			_log.debug("populateJSONFieldValues start");
		}

		docuSignTabs = _docuSignService.populateJSONFieldValues(
				docuSignTabs, acceptFieldReferences,
				record.getFormInstanceRecordId());

		if (_log.isDebugEnabled()) {
			_log.debug("populateJSONFieldValues end");
			_log.debug("sendDSEnvelope start");
			_log.debug("sender email: " + _docuSignService.getSystemSenderEmail());
		}


		DSEnvelope dsEnvelope = _docuSignService.sendDSEnvelope(
				envelopeEmailMessage, envelopeEmailSubject, envelopeName,
				_docuSignService.getSystemSenderEmail(), templateFile, user,
				docuSignTabs, companyId, groupId);

		if (_log.isDebugEnabled()) {
			_log.debug("Envelope: " + dsEnvelope);
			_log.debug("sendDSEnvelope end");
		}
	}

	private long _getFormInstanceRecordId(
			Map<String, Serializable> workflowContext)
			throws Exception {

		long formInstanceRecordVersionId = GetterUtil.getLong(
				workflowContext.get("entryClassPK"));

		DDMFormInstanceRecordVersion formInstanceRecordVersion =
				_ddmFormInstanceRecordVersionLocalService.
						getDDMFormInstanceRecordVersion(formInstanceRecordVersionId);

		DDMFormInstanceRecord formInstanceRecord =
				formInstanceRecordVersion.getFormInstanceRecord();

		return formInstanceRecord.getFormInstanceRecordId();
	}

	private File _getPDFTemplateFile(long formInstanceId)
			throws IOException, PortalException {

		String pdfTemplateFileName =
				_pdfDocuSignConfigurator.getConfigurationDataByFormInstanceId(
						formInstanceId,
						_pdfDocuSignConfigurator.getPDFTemplateFileNames());

		FileEntry fileEntry = _dlAppService.getFileEntryByFileName(
				_pdfDocuSignConfigurator.getPDFTemplatesGroupId(),
				_pdfDocuSignConfigurator.getPDFTemplatesFolderId(),
				pdfTemplateFileName);

		InputStream contentStream = fileEntry.getFileVersion(
		).getContentStream(
				true
		);

		File tempFile = FileUtil.createTempFile(contentStream);

		contentStream.close();

		return tempFile;
	}

	@Reference
	private DocuSignService _docuSignService;

	@Reference
	private PDFDocuSignConfigurator _pdfDocuSignConfigurator;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private DDMFormInstanceRecordLocalService
			_ddmFormInstanceRecordLocalService;

	@Reference
	private DDMFormInstanceRecordVersionLocalService
			_ddmFormInstanceRecordVersionLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private JSONFactory _jsonFactory;

	private static final Log _log = LogFactoryUtil.getLog(DocuSignIntegrationLocalServiceImpl.class);

}
