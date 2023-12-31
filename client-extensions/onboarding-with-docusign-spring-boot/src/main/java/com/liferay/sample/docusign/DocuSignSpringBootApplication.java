/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sample.docusign;

import com.liferay.client.extension.util.spring.boot.ClientExtensionUtilSpringBootComponentScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author Rafael Oliveira
 */
@Import(ClientExtensionUtilSpringBootComponentScan.class)
@SpringBootApplication
public class DocuSignSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocuSignSpringBootApplication.class, args);
	}

}
