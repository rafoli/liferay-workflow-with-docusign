package com.liferay.sample.docusign.dto.docuSignConfig;

import java.util.List;

public class DocuSignConfigDTO {
    private Actions actions;
    private Creator creator;
    private String dateCreated;
    private String dateModified;
    private String externalReferenceCode;
    private int id;
    private List<String> keywords;
    private String scopeKey;
    private Status status;
    private List<Object> taxonomyCategoryBriefs;
    private DocuSignPDFTemplate docuSignPDFTemplate;
    private String docuSignTabs;

    public Actions getActions() {
        return actions;
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getExternalReferenceCode() {
        return externalReferenceCode;
    }

    public void setExternalReferenceCode(String externalReferenceCode) {
        this.externalReferenceCode = externalReferenceCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getScopeKey() {
        return scopeKey;
    }

    public void setScopeKey(String scopeKey) {
        this.scopeKey = scopeKey;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Object> getTaxonomyCategoryBriefs() {
        return taxonomyCategoryBriefs;
    }

    public void setTaxonomyCategoryBriefs(List<Object> taxonomyCategoryBriefs) {
        this.taxonomyCategoryBriefs = taxonomyCategoryBriefs;
    }

    public DocuSignPDFTemplate getDocuSignPDFTemplate() {
        return docuSignPDFTemplate;
    }

    public void setDocuSignPDFTemplate(DocuSignPDFTemplate docuSignPDFTemplate) {
        this.docuSignPDFTemplate = docuSignPDFTemplate;
    }

    public String getDocuSignTabs() {
        return docuSignTabs;
    }

    public void setDocuSignTabs(String docuSignTabs) {
        this.docuSignTabs = docuSignTabs;
    }
}
