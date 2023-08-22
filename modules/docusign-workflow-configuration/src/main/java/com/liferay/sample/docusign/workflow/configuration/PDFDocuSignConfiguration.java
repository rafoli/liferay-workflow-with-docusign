
package com.liferay.sample.docusign.workflow.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Rafael Oliveira
 */
@ExtendedObjectClassDefinition(category = "pdf-docusign")
@Meta.OCD(
	id = PDFDocuSignConfiguration.PDF_DOCUSIGN_CONFIGURATION,
	localization = "content/Language", name = "PDF & DocuSign Configuration"
)
public interface PDFDocuSignConfiguration {

	public String PDF_DOCUSIGN_CONFIGURATION =
		"com.liferay.sample.docusign.workflow.configuration.PDFDocuSignConfiguration";

	@Meta.AD(
		description = "Form PDF Fields Mapping (JSON)",
		name = "formPDFFieldsMapping", required = false
	)
	public String[] formPDFFieldsMappingJSONs();

	@Meta.AD(
		description = "pdfTemplatesGroupId", name = "pdfTemplatesGroupId",
		required = false
	)
	public long pdfTemplatesGroupId();

	@Meta.AD(
		description = "pdfTemplatesFolderId", name = "pdfTemplatesFolderId",
		required = false
	)
	public long pdfTemplatesFolderId();

	@Meta.AD(
		description = "pdfTemplateFileNames", name = "pdfTemplateFileNames",
		required = false
	)
	public String[] pdfTemplateFileNames();

	@Meta.AD(
		description = "DocuSign Tabs (JSON)", name = "docuSignTabsJSON",
		required = false
	)
	public String[] docuSignTabsJSONs();

}
