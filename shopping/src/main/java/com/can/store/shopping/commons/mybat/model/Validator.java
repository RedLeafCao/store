package com.can.store.shopping.commons.mybat.model;

/**
 * 验证码参数
 * 2019.08.13
 */
public class Validator {
    private Integer id = null;
    private String validatorCode;
    private String realLocation;
    private String validatorUrl;
    private String sessionId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValidatorCode() {
        return validatorCode;
    }

    public void setValidatorCode(String validatorCode) {
        this.validatorCode = validatorCode;
    }

    public String getRealLocation() {
        return realLocation;
    }

    public void setRealLocation(String realLocation) {
        this.realLocation = realLocation;
    }

    public String getValidatorUrl() {
        return validatorUrl;
    }

    public void setValidatorUrl(String validatorUrl) {
        this.validatorUrl = validatorUrl;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Validator{");
        sb.append("id=").append(id);
        sb.append(", validatorCode='").append(validatorCode).append('\'');
        sb.append(", realLocation='").append(realLocation).append('\'');
        sb.append(", validatorUrl='").append(validatorUrl).append('\'');
        sb.append(", sessionId='").append(sessionId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
