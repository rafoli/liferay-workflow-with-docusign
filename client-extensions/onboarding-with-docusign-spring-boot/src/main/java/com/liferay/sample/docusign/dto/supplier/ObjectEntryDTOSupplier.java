package com.liferay.sample.docusign.dto.supplier;

import java.util.List;
import java.util.Map;

public class ObjectEntryDTOSupplier {
    private Creator creator;
    private long dateCreated;
    private List<String> keywords;
    private String scopeKey;
    private long dateModified;
    private String id;
    private List<Object> taxonomyCategoryBriefs;
    private Map<String, String> properties;
    private String externalReferenceCode;
    private Status status;

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
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

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Object> getTaxonomyCategoryBriefs() {
        return taxonomyCategoryBriefs;
    }

    public void setTaxonomyCategoryBriefs(List<Object> taxonomyCategoryBriefs) {
        this.taxonomyCategoryBriefs = taxonomyCategoryBriefs;
    }

    public String getExternalReferenceCode() {
        return externalReferenceCode;
    }

    public void setExternalReferenceCode(String externalReferenceCode) {
        this.externalReferenceCode = externalReferenceCode;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
