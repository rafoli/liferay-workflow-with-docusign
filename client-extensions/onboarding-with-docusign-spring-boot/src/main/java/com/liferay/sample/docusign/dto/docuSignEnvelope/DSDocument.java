package com.liferay.sample.docusign.dto.docuSignEnvelope;

public class DSDocument {
    private String fileEntryExternalReferenceCode;
    private boolean transformPDFFields = false;
    private String fileExtension = "";
    private String name;
    private String id = "1";
    private String uri = "";

    public String getFileEntryExternalReferenceCode() {
        return fileEntryExternalReferenceCode;
    }

    public void setFileEntryExternalReferenceCode(String fileEntryExternalReferenceCode) {
        this.fileEntryExternalReferenceCode = fileEntryExternalReferenceCode;
    }

    public boolean isTransformPDFFields() {
        return transformPDFFields;
    }

    public void setTransformPDFFields(boolean transformPDFFields) {
        this.transformPDFFields = transformPDFFields;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
