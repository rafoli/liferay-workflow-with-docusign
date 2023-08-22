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

package com.liferay.sample.docusign.workflow.integration.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link DocuSignIntegrationLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DocuSignIntegrationLocalService
 * @generated
 */
public class DocuSignIntegrationLocalServiceWrapper
	implements DocuSignIntegrationLocalService,
			   ServiceWrapper<DocuSignIntegrationLocalService> {

	public DocuSignIntegrationLocalServiceWrapper() {
		this(null);
	}

	public DocuSignIntegrationLocalServiceWrapper(
		DocuSignIntegrationLocalService docuSignIntegrationLocalService) {

		_docuSignIntegrationLocalService = docuSignIntegrationLocalService;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _docuSignIntegrationLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public void requestDocuSignFromPDFTemplate(
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws Exception {

		_docuSignIntegrationLocalService.requestDocuSignFromPDFTemplate(
			workflowContext);
	}
	@Override
	public DocuSignIntegrationLocalService getWrappedService() {
		return _docuSignIntegrationLocalService;
	}

	@Override
	public void setWrappedService(
		DocuSignIntegrationLocalService docuSignIntegrationLocalService) {

		_docuSignIntegrationLocalService = docuSignIntegrationLocalService;
	}

	private DocuSignIntegrationLocalService _docuSignIntegrationLocalService;

}
