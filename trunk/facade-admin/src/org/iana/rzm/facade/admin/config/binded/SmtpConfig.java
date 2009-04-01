package org.iana.rzm.facade.admin.config.binded;

import org.iana.rzm.facade.admin.config.ConfigParameterNames;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class SmtpConfig extends BindedParameter {

    public static final String SMTP_MAILER = ConfigParameterNames.SMTP_MAILER;
    public static final String SMTP_MAILHOST = ConfigParameterNames.SMTP_HOST;
    public static final String SMTP_MAILHOST_PORT = ConfigParameterNames.SMTP_PORT;
    public static final String SMTP_FROM_ADDRESS = ConfigParameterNames.SMTP_FROM_ADDRESS;
    public static final String SMTP_USER_NAME = ConfigParameterNames.SMTP_USER_NAME;
    public static final String SMTP_USER_PWD = ConfigParameterNames.SMTP_USER_PWD;
    public static final String SMTP_USE_SSL = ConfigParameterNames.SMTP_USE_SSL;
    public static final String SMTP_USE_TLS = ConfigParameterNames.SMTP_USE_TLS;
    public static final String SMTP_SMTP_FROM = ConfigParameterNames.SMTP_FROM;

    public SmtpConfig() {
    }

    public SmtpConfig(Map<String, String> values) {
        super(values);
    }

    public String getMailer() {
        return getValue(SMTP_MAILER);
    }

    public void setMailer(String value) {
        setValue(SMTP_MAILER, value);
    }

    public String getMailHost() {
        return getValue(SMTP_MAILHOST);
    }

    public void setMailHost(String value) {
        setValue(SMTP_MAILHOST, value);
    }

    public Integer getMailHostPort() {
        return getInetegerValue(SMTP_MAILHOST_PORT);
    }

    public void setMailHostPort(Integer value) {
        setValue(SMTP_MAILHOST_PORT, value);
    }

    public String getFromAddress() {
        return getValue(SMTP_FROM_ADDRESS);
    }

    public void setFromAddress(String value) {
        setValue(SMTP_FROM_ADDRESS, value);
    }

    public String getUserName() {
        return getValue(SMTP_USER_NAME);
    }

    public void setUserName(String value) {
        setValue(SMTP_USER_NAME, value);
    }

    public String getUserPassword() {
        return getValue(SMTP_USER_PWD);
    }

    public void setUserPassword(String value) {
        setValue(SMTP_USER_PWD, value);
    }

    public Boolean getUseSSL() {
        return getBooleanValue(SMTP_USE_SSL);
    }

    public void setUseSSL(Boolean value) {
        setValue(SMTP_USE_SSL, value);
    }

    public Boolean getUseTLS() {
        return getBooleanValue(SMTP_USE_TLS);
    }

    public void setUseTLS(Boolean value) {
        setValue(SMTP_USE_TLS, value);
    }

    public String getSmtpFrom() {
        return getValue(SMTP_SMTP_FROM);
    }

    public void setSmtpFrom(String value) {
        setValue(SMTP_SMTP_FROM, value);
    }

    public static List<String> getParameterNames() {
        return Arrays.asList(SMTP_MAILER, SMTP_MAILHOST, SMTP_MAILHOST_PORT, SMTP_FROM_ADDRESS,
                SMTP_USER_NAME, SMTP_USER_PWD, SMTP_USE_SSL, SMTP_USE_TLS, SMTP_SMTP_FROM);
    }
}
