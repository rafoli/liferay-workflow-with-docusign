package com.liferay.sample.docusign.dto.supplier;

public class SupplierDTO {
    private long classPK;
    private String companyId;
    private String objectDefinitionId;
    private ObjectEntryDTOSupplier objectEntryDTOSupplier;
    private String userName;
    private ObjectEntry objectEntry;
    private String userId;
    private int status;

    public long getClassPK() {
        return classPK;
    }

    public void setClassPK(long classPK) {
        this.classPK = classPK;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getObjectDefinitionId() {
        return objectDefinitionId;
    }

    public void setObjectDefinitionId(String objectDefinitionId) {
        this.objectDefinitionId = objectDefinitionId;
    }

    public ObjectEntryDTOSupplier getObjectEntryDTOSupplier() {
        return objectEntryDTOSupplier;
    }

    public void setObjectEntryDTOSupplier(ObjectEntryDTOSupplier objectEntryDTOSupplier) {
        this.objectEntryDTOSupplier = objectEntryDTOSupplier;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ObjectEntry getObjectEntry() {
        return objectEntry;
    }

    public void setObjectEntry(ObjectEntry objectEntry) {
        this.objectEntry = objectEntry;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
