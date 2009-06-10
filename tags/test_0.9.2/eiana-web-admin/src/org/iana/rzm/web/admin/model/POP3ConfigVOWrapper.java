package org.iana.rzm.web.admin.model;

import org.iana.rzm.facade.admin.config.binded.Pop3Config;
import org.iana.rzm.web.common.model.ValueObject;

import java.util.Map;

public class POP3ConfigVOWrapper extends ValueObject implements ApplicationConfig {

    private Pop3Config config;

    public POP3ConfigVOWrapper(Pop3Config config) {
        this.config = config;
    }

    public void setHost(String host) {
        config.setHost(host);
    }

    public String getHost(){
        return config.getHost();
    }

    public void setPort(int port) {
        config.setPort(port);
    }

    public Integer getPort(){
        return config.getPort();
    }

    public void setUserName(String userName) {
        config.setUser(userName);
    }


    public String getUserName(){
        return config.getUser();
    }

    public void setPassword(String password) {
        config.setPassword(password);
    }


    public String getPassword(){
        return config.getPassword();
    }

    public void setSsl(boolean ssl) {
        config.setSSL(ssl);
    }

    public boolean isSsl(){
        return config.getSSL() != null && config.getSSL();
    }

    public void setDebug(boolean tls) {
        config.setDebug(tls);
    }

    public boolean isDebug(){
        return config.getDebug() != null && config.getDebug() ;
    }

    public Map<String, String> getInerConfiguration() {
        return config.getValuesMap();
    }
}
