package com.liferay.sample.docusign.dto.docuSignEnvelope;


import com.liferay.sample.docusign.dto.docuSignTabs.Tab;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DSRecipient {
    private String emailAddress;
    private String name;
    private String id = "1";
    private Map<String, List<Tab>> tabs = new HashMap<String, List<Tab>>() {};
    private String status = "sent";

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
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

    public Map<String, List<Tab>> getTabs() {
        return tabs;
    }

    public void setTabs(Map<String, List<Tab>> tabs) {
        this.tabs = tabs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
