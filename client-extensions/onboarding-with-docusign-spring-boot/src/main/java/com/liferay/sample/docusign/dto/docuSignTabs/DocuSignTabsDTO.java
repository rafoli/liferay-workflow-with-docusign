package com.liferay.sample.docusign.dto.docuSignTabs;

import java.util.List;

public class DocuSignTabsDTO {
    private List<Tab> signHereTabs;
    private List<Tab> textTabs;
    private List<Tab> dateTabs;
    private List<Tab> checkboxTabs;

    public List<Tab> getSignHereTabs() {
        return signHereTabs;
    }

    public void setSignHereTabs(List<Tab> signHereTabs) {
        this.signHereTabs = signHereTabs;
    }

    public List<Tab> getTextTabs() {
        return textTabs;
    }

    public void setTextTabs(List<Tab> textTabs) {
        this.textTabs = textTabs;
    }

    public List<Tab> getDateTabs() {
        return dateTabs;
    }

    public void setDateTabs(List<Tab> dateTabs) {
        this.dateTabs = dateTabs;
    }

    public List<Tab> getCheckboxTabs() {
        return checkboxTabs;
    }

    public void setCheckboxTabs(List<Tab> checkboxTabs) {
        this.checkboxTabs = checkboxTabs;
    }
}
