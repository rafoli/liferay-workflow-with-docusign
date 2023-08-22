package com.liferay.sample.docusign.workflow.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Oliveira
 */
@Component(
	configurationPid = com.liferay.sample.docusign.workflow.configuration.PDFDocuSignConfiguration.PDF_DOCUSIGN_CONFIGURATION,
	immediate = true, service = PDFDocuSignConfigurator.class
)
public class PDFDocuSignConfigurator {

	@Activate
	@Modified
	public void activate(Map<String, Object> properties) {
		_pdfDocuSignConfiguration = ConfigurableUtil.createConfigurable(
			com.liferay.sample.docusign.workflow.configuration.PDFDocuSignConfiguration.class, properties);
	}

	public String getConfigurationDataByFormInstanceId(
			long formInstanceId, String[] arrayConfigurationJSONs)
		throws JSONException {

		for (String configurationJSON : arrayConfigurationJSONs) {
			JSONObject configurationJSONObj = _jsonFactory.createJSONObject(
				configurationJSON);

			long currentFormInstanceId = configurationJSONObj.getLong(
				"formInstanceId");

			if (currentFormInstanceId == formInstanceId) {
				return configurationJSONObj.getString("data");
			}
		}

		return null;
	}

	public String[] getDocuSignTabsJSONs() {
		return _pdfDocuSignConfiguration.docuSignTabsJSONs();
	}

	public String[] getFormPDFFieldsMappingJSONs() {
		return _pdfDocuSignConfiguration.formPDFFieldsMappingJSONs();
	}

	public String[] getPDFTemplateFileNames() {
		return _pdfDocuSignConfiguration.pdfTemplateFileNames();
	}

	public long getPDFTemplatesFolderId() {
		return _pdfDocuSignConfiguration.pdfTemplatesFolderId();
	}

	public long getPDFTemplatesGroupId() {
		return _pdfDocuSignConfiguration.pdfTemplatesGroupId();
	}

	@Reference
	private JSONFactory _jsonFactory;

	private volatile PDFDocuSignConfiguration _pdfDocuSignConfiguration;

}
