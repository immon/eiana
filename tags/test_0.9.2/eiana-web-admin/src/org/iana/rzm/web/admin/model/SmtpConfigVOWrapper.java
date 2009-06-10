package org.iana.rzm.web.admin.model;

import org.iana.rzm.facade.admin.config.binded.SmtpConfig;
import org.iana.rzm.web.common.model.ValueObject;

import java.util.Map;

public class SmtpConfigVOWrapper extends ValueObject implements ApplicationConfig {

    private SmtpConfig config;


    public SmtpConfigVOWrapper(SmtpConfig config) {
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

    public  Integer getPort(){
        return config.getPort();
    }

    public void setFromAddress(String fromAddress) {
        config.setFromAddress(fromAddress);
    }

    public String getFromAddress(){
        return config.getFromAddress();
    }

    public void setMailer(String mailer) {
        config.setMailer(mailer);
    }

    public String getMailer(){
        return config.getMailer();
    }

    public void setUserName(String userName) {
        config.setUserName(userName);
    }


    public String getUserName(){
        return config.getUserName();
    }

    public void setPassword(String password) {
        config.setUserPassword(password);
    }


    public String getPassword(){
        return config.getUserPassword();
    }


    public void setBounceAddress(String bounceAddress) {
        config.setSmtpFrom(bounceAddress);
    }

    public String getBounceAddress(){
        return config.getSmtpFrom();
    }

    public void setSsl(boolean ssl) {
        config.setUseSSL(ssl);
    }

    public boolean isSsl(){
        return config.getUseSSL() != null && config.getUseSSL();
    }

    public void setTls(boolean tls) {
        config.setUseTLS(tls);
    }

    public boolean isTls(){
        return config.getUseTLS() != null && config.getUseTLS() ;
    }

    public Map<String,String> getInerConfiguration() {
        return config.getValuesMap();
    }
}
