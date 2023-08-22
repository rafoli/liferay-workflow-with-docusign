package com.liferay.sample.docusign.workflow.integration.service.impl;

import com.liferay.digital.signature.manager.DSDocumentManager;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.model.DSDocument;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactoryUtil;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactoryUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.liferay.sample.docusign.workflow.configuration.PDFDocuSignConfigurator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Terry Jia
 */
@Component(immediate = true, service = DocuSignService.class)
public class DocuSignService {

	public File addFileEntryInZipBytes(
			byte[] dsDocumentsAsBytes, FileEntry file)
		throws IOException, PortalException {

		File zipFile = FileUtil.createTempFile(dsDocumentsAsBytes);

		ZipWriter zipWriter = ZipWriterFactoryUtil.getZipWriter(zipFile);

		zipWriter.addEntry(file.getFileName(), file.getContentStream());

		return zipFile;
	}

	public File extractPDFFileFromZipBytes(byte[] dsDocumentsAsBytes)
		throws IOException {

		File zipFile = FileUtil.createTempFile(dsDocumentsAsBytes);

		ZipReader zipReader = ZipReaderFactoryUtil.getZipReader(zipFile);

		List<String> entries = zipReader.getEntries();

		byte[] pdfBytes = null;

		for (String entry : entries) {
			if (!entry.equals("Summary.pdf")) {
				pdfBytes = zipReader.getEntryAsByteArray(entry);

				break;
			}
		}

		return FileUtil.createTempFile(pdfBytes);
	}

	public long getAttachmentFile(long formInstanceRecordId)
		throws PortalException {

		DDMFormInstanceRecord formInstanceRecord =
			_ddmFormInstanceRecordLocalService.getFormInstanceRecord(
				formInstanceRecordId);

		DDMFormInstance formInstance = formInstanceRecord.getFormInstance();

		DDMFormValues ddmFormValues = formInstanceRecord.getDDMFormValues();

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(true);

		DDMFormInstanceVersion formInstanceVersion =
			formInstance.getFormInstanceVersion(
				formInstanceRecord.getFormInstanceVersion());

		DDMStructureVersion structureVersion =
			formInstanceVersion.getStructureVersion();

		DDMForm ddmForm = structureVersion.getDDMForm();

		Map<String, DDMFormField> ddmFormFieldsReferencesMap =
			ddmForm.getDDMFormFieldsReferencesMap(true);

		for (Map.Entry<String, DDMFormField> entry :
				ddmFormFieldsReferencesMap.entrySet()) {

			DDMFormField ddmFormField = entry.getValue();

			if (Objects.equals("document_library", ddmFormField.getType())) {
				List<DDMFormFieldValue> ddmFormFieldValues =
					ddmFormFieldValuesMap.get(ddmFormField.getName());

				if (ListUtil.isEmpty(ddmFormFieldValues)) {
					continue;
				}

				Value values = ddmFormFieldValues.get(
					0
				).getValue();

				if (values != null) {
					String value = values.getString(values.getDefaultLocale());

					if (Validator.isNull(value)) {
						continue;
					}

					JSONObject fileJSONObj = _jsonFactory.createJSONObject(
						value);

					return fileJSONObj.getLong("fileEntryId");
				}
			}
		}

		return -1;
	}

	public String getDocuSignInfoData(long formInstanceRecordId, long companyId)
		throws PortalException {

		String className = DDMFormInstanceRecord.class.getName();

		ExpandoColumn column = _expandoColumnLocalService.getDefaultTableColumn(
			companyId, DDMFormInstanceRecord.class.getName(), "docuSign-info");

		ExpandoTable table = _expandoTableLocalService.getTable(
			column.getTableId());

		return String.valueOf(
			_expandoValueLocalService.getData(
				companyId, className, table.getName(), "docuSign-info",
				formInstanceRecordId));
	}

	public byte[] getEnvelopeContent(
			long formInstanceRecordId, String docuSignInfoKey)
		throws PortalException {

		DDMFormInstanceRecord record =
			_ddmFormInstanceRecordLocalService.getDDMFormInstanceRecord(
				formInstanceRecordId);

		String docuSignInfoJSON = getDocuSignInfoData(
			formInstanceRecordId, record.getCompanyId());

		JSONObject docuSignInfoJSONObj = _jsonFactory.createJSONObject(
			docuSignInfoJSON);

		JSONObject supplierFormEnvelopeInfoJSONObj =
			docuSignInfoJSONObj.getJSONObject(docuSignInfoKey);

		String supplierFormEnvelopeId =
			supplierFormEnvelopeInfoJSONObj.getString("envelopeId");

		return _dsDocumentManager.getDSDocumentsAsBytes(
			record.getCompanyId(), record.getGroupId(), supplierFormEnvelopeId);
	}

	public String getSystemSenderEmail() throws PortalException {
		String adminScreenName = PropsUtil.get(
			PropsKeys.DEFAULT_ADMIN_SCREEN_NAME);

		User user = _userLocalService.getUserByScreenName(
			_portal.getDefaultCompanyId(), adminScreenName);

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				user.getUserId(), true);

		return portalPreferences.getValue(null, "admin.email.from.address");
	}

	public JSONObject populateJSONFieldValues(
			JSONObject docuSignInfoJSONObj, JSONArray acceptFieldReferences,
			long formInstanceRecordId)
		throws PortalException {

		DDMFormInstanceRecord formInstanceRecord =
			_ddmFormInstanceRecordLocalService.getFormInstanceRecord(
				formInstanceRecordId);

		DDMFormInstance formInstance = formInstanceRecord.getFormInstance();

		DDMFormValues ddmFormValues = formInstanceRecord.getDDMFormValues();

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(true);

		DDMFormInstanceVersion formInstanceVersion =
			formInstance.getFormInstanceVersion(
				formInstanceRecord.getFormInstanceVersion());

		DDMStructureVersion structureVersion =
			formInstanceVersion.getStructureVersion();

		DDMForm ddmForm = structureVersion.getDDMForm();

		Map<String, DDMFormField> ddmFormFieldsReferencesMap =
			ddmForm.getDDMFormFieldsReferencesMap(true);

		String formPDFFieldsMappingJSON =
			_pdfDocuSignConfigurator.getConfigurationDataByFormInstanceId(
				formInstance.getFormInstanceId(),
				_pdfDocuSignConfigurator.getFormPDFFieldsMappingJSONs());

		JSONObject formPDFFieldsMapping = _jsonFactory.createJSONObject(
			formPDFFieldsMappingJSON);

		for (Map.Entry<String, DDMFormField> entry :
				ddmFormFieldsReferencesMap.entrySet()) {

			String reference = entry.getKey();

			if (!_checkReference(acceptFieldReferences, reference)) {
				continue;
			}

			JSONObject referenceTabJSONObj = formPDFFieldsMapping.getJSONObject(
				reference);

			if (referenceTabJSONObj == null) {
				continue;
			}

			DDMFormField ddmFormField = entry.getValue();

			List<DDMFormFieldValue> ddmFormFieldValues =
				ddmFormFieldValuesMap.get(ddmFormField.getName());

			if (ListUtil.isEmpty(ddmFormFieldValues)) {
				continue;
			}

			Value values = ddmFormFieldValues.get(
				0
			).getValue();

			if (values == null) {
				continue;
			}

			String value = values.getString(values.getDefaultLocale());

			if (Validator.isNull(value)) {
				continue;
			}

			String fieldType = ddmFormField.getType();

			if (fieldType.equals("text") || fieldType.equals("numeric") ||
				fieldType.equals("date")) {

				JSONArray textTabs = docuSignInfoJSONObj.getJSONArray(
					"textTabs");

				referenceTabJSONObj.put("value", value);

				textTabs.put(referenceTabJSONObj);
			}
			else if (fieldType.equals("select") || fieldType.equals("radio")) {
				DDMFormFieldOptions ddmFormFieldOptions =
					ddmFormField.getDDMFormFieldOptions();

				if (value.startsWith("[\"")) {
					value = value.substring(2, value.length() - 2);
				}

				String optionReference = ddmFormFieldOptions.getOptionReference(
					value);

				if (Validator.isNull(optionReference)) {
					continue;
				}

				if (reference.contains("State")) {
					LocalizedValue optionLabels =
						ddmFormFieldOptions.getOptionLabels(value);

					if (optionLabels != null) {
						String optionLabel = optionLabels.getString(
							values.getDefaultLocale());

						JSONArray textTabs = docuSignInfoJSONObj.getJSONArray(
							"textTabs");

						referenceTabJSONObj.put("value", optionLabel);

						textTabs.put(referenceTabJSONObj);
					}
				}
				else {
					JSONObject optionReferenceTabJSONObj =
						referenceTabJSONObj.getJSONObject(optionReference);

					if (optionReferenceTabJSONObj == null) {
						continue;
					}

					JSONArray checkboxTabs = docuSignInfoJSONObj.getJSONArray(
						"checkboxTabs");

					optionReferenceTabJSONObj.put("selected", "true");

					checkboxTabs.put(optionReferenceTabJSONObj);
				}
			}
			else if (fieldType.equals("checkbox_multiple")) {
				DDMFormFieldOptions ddmFormFieldOptions =
					ddmFormField.getDDMFormFieldOptions();

				if (value.startsWith("[\"")) {
					value = value.substring(1, value.length() - 1);
				}

				String[] optionReferences = value.split(",");

				for (String optionReference : optionReferences) {
					optionReference = optionReference.substring(
						1, optionReference.length() - 1);

					String reference2 = ddmFormFieldOptions.getOptionReference(
						optionReference);

					JSONObject optionReferenceTabJSONObj =
						referenceTabJSONObj.getJSONObject(reference2);

					if (optionReferenceTabJSONObj == null) {
						continue;
					}

					JSONArray checkboxTabs = docuSignInfoJSONObj.getJSONArray(
						"checkboxTabs");

					optionReferenceTabJSONObj.put("selected", "true");

					checkboxTabs.put(optionReferenceTabJSONObj);
				}
			}
		}

		return docuSignInfoJSONObj;
	}

	public DSEnvelope sendDSEnvelope(
			String envelopeEmailBlurb, String envelopeEmailSubject,
			String envelopeName, String senderEmail, File file, User recipient,
			JSONObject tabsJSONObj, long companyId, long groupId)
		throws Exception {

		return _dsEnvelopeManager.addDSEnvelope(
			companyId, groupId,
			new DSEnvelope() {
				{
					dsDocuments = _getDSDocuments(file);
					dsRecipients = _getDSRecipients(recipient, tabsJSONObj);
					emailBlurb = envelopeEmailBlurb;
					emailSubject = envelopeEmailSubject;
					name = envelopeName;
					senderEmailAddress = senderEmail;
					status = "sent";
				}
			});
	}

	private boolean _checkReference(
		JSONArray acceptFieldReferences, String reference) {

		for (Object fieldReference : acceptFieldReferences) {
			String acceptFieldReference = (String)fieldReference;

			if (reference.equals(acceptFieldReference)) {
				return true;
			}
		}

		return false;
	}

	private List<DSDocument> _getDSDocuments(File file) throws Exception {
		return ListUtil.fromArray(_toDSDocument(file));
	}

	private List<DSRecipient> _getDSRecipients(
		User recipient, JSONObject tabJSONObj) {

		IntegerWrapper integerWrapper = new IntegerWrapper();

		List<DSRecipient> dsRecipients = new ArrayList<>();

		String recipientEmail = recipient.getEmailAddress();
		String recipientFullName = recipient.getFullName();

		DSRecipient dsRecipient = new DSRecipient() {
			{
				dsRecipientId = String.valueOf(integerWrapper.increment());
				emailAddress = recipientEmail;
				name = recipientFullName;
				tabsJSONObject = tabJSONObj;
			}
		};

		dsRecipients.add(dsRecipient);

		return dsRecipients;
	}

	private DSDocument _toDSDocument(File file) throws Exception {
		IntegerWrapper integerWrapper = new IntegerWrapper();

		return new DSDocument() {
			{
				data = Base64.encode(FileUtil.getBytes(file));
				dsDocumentId = String.valueOf(integerWrapper.increment());
				fileExtension = FileUtil.getExtension(file.getName());
				name = file.getName();
			}
		};
	}

	@Reference
	private DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;

	@Reference
	private DSDocumentManager _dsDocumentManager;

	@Reference
	private DSEnvelopeManager _dsEnvelopeManager;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private PDFDocuSignConfigurator _pdfDocuSignConfigurator;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}
