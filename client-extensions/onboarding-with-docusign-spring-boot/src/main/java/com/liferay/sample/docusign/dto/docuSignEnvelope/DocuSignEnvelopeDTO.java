package com.liferay.sample.docusign.dto.docuSignEnvelope;

import java.util.ArrayList;
import java.util.List;

public class DocuSignEnvelopeDTO {
    private List<DSDocument> dsDocument = new ArrayList<>();
    private List<DSRecipient> dsRecipient = new ArrayList<>();
    private String emailBlurb;
    private String emailSubject;
    private String id;
    private String name;
    private String senderEmailAddress;
    private String status;

    public List<DSDocument> getDsDocument() {
        return dsDocument;
    }

    public void setDsDocument(List<DSDocument> dsDocument) {
        this.dsDocument = dsDocument;
    }

    public List<DSRecipient> getDsRecipient() {
        return dsRecipient;
    }

    public void setDsRecipient(List<DSRecipient> dsRecipient) {
        this.dsRecipient = dsRecipient;
    }

    public String getEmailBlurb() {
        return emailBlurb;
    }

    public void setEmailBlurb(String emailBlurb) {
        this.emailBlurb = emailBlurb;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSenderEmailAddress() {
        return senderEmailAddress;
    }

    public void setSenderEmailAddress(String senderEmailAddress) {
        this.senderEmailAddress = senderEmailAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
