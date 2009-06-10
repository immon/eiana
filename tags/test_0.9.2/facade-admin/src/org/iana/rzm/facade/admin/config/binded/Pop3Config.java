package org.iana.rzm.facade.admin.config.binded;

import org.iana.rzm.facade.admin.config.impl.ConfigParameterNames;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class Pop3Config extends BindedParameter {

    public static final String POP3_HOST = ConfigParameterNames.POP3_HOST;
    public static final String POP3_USER = ConfigParameterNames.POP3_USER_NAME;
    public static final String POP3_PWD = ConfigParameterNames.POP3_USER_PWD;

    public static final String POP3_PORT = ConfigParameterNames.POP3_PORT;
    public static final String POP3_SSL = ConfigParameterNames.POP3_USE_SSL;
    public static final String POP3_DEBUG = ConfigParameterNames.POP3_DEBUG;

    public Pop3Config() {
        super(getParameterNames());
    }

    public Pop3Config(Map<String, String> values) {
        super(values);
    }

    public String getHost() {
        return getValue(POP3_HOST);
    }

    public void setHost(String host) {
        setValue(POP3_HOST, host);
    }

    public String getUser() {
        return getValue(POP3_USER);
    }

    public void setUser(String user) {
        setValue(POP3_USER, user);
    }

    public String getPassword() {
        return getValue(POP3_PWD);
    }

    public void setPassword(String password) {
        setValue(POP3_PWD, password);
    }

    public Integer getPort() {
        return getInetegerValue(POP3_PORT);
    }

    public void setPort(Integer port) {
        setValue(POP3_PORT, port);
    }

    public Boolean getSSL() {
        return getBooleanValue(POP3_SSL);
    }

    public void setSSL(Boolean ssl) {
        setValue(POP3_SSL, ssl);
    }

    public Boolean getDebug() {
        return getBooleanValue(POP3_DEBUG);
    }

    public void setDebug(Boolean debug) {
        setValue(POP3_DEBUG, debug);
    }

    public static List<String> getParameterNames() {
        return Arrays.asList(POP3_HOST, POP3_USER, POP3_PWD, POP3_PORT, POP3_SSL, POP3_DEBUG);
    }


}
