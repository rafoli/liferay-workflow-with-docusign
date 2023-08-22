package com.liferay.sample.docusign.workflow.configuration;

import com.liferay.configuration.admin.category.ConfigurationCategory;

import org.osgi.service.component.annotations.Component;

/**
 * @author Rafael Oliveira
 */
@Component(service = ConfigurationCategory.class)
public class PDFDocuSignConfigurationCategory implements ConfigurationCategory {

	@Override
	public String getCategoryIcon() {
		return _CATEGORY_ICON;
	}

	@Override
	public String getCategoryKey() {
		return _CATEGORY_KEY;
	}

	@Override
	public String getCategorySection() {
		return _CATEGORY_SECTION;
	}

	private static final String _CATEGORY_ICON = "cog";

	private static final String _CATEGORY_KEY = "pdf-docusign";

	private static final String _CATEGORY_SECTION = "samples";

}
