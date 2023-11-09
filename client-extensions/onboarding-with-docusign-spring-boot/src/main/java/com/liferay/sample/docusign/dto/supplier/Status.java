package com.liferay.sample.docusign.dto.supplier;

public class Status {
    private String label_i18n;
    private int code;
    private String label;

    public String getLabel_i18n() {
        return label_i18n;
    }

    public void setLabel_i18n(String label_i18n) {
        this.label_i18n = label_i18n;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
