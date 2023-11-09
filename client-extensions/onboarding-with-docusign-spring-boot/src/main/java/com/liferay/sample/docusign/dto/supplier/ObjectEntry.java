package com.liferay.sample.docusign.dto.supplier;

import java.util.Map;

public class ObjectEntry {
    private long statusByUserId;
    private String statusDate;
    private long groupId;
    private Map<String, String> values;
    private String userName;
    private String uuid;
    private String userId;
    private String externalReferenceCode;
    private String companyId;
    private String objectDefinitionId;
    private String modifiedDate;
    private long objectEntryId;
    private String statusByUserName;
    private int mvccVersion;
    private String createDate;
    private int status;

    public long getStatusByUserId() {
        return statusByUserId;
    }

    public void setStatusByUserId(long statusByUserId) {
        this.statusByUserId = statusByUserId;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExternalReferenceCode() {
        return externalReferenceCode;
    }

    public void setExternalReferenceCode(String externalReferenceCode) {
        this.externalReferenceCode = externalReferenceCode;
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

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public long getObjectEntryId() {
        return objectEntryId;
    }

    public void setObjectEntryId(long objectEntryId) {
        this.objectEntryId = objectEntryId;
    }

    public String getStatusByUserName() {
        return statusByUserName;
    }

    public void setStatusByUserName(String statusByUserName) {
        this.statusByUserName = statusByUserName;
    }

    public int getMvccVersion() {
        return mvccVersion;
    }

    public void setMvccVersion(int mvccVersion) {
        this.mvccVersion = mvccVersion;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }
}
