package org.iana.rzm.web.admin.model;

import org.iana.rzm.facade.admin.config.binded.VerisignOrgConfig;
import org.iana.rzm.web.common.model.ValueObject;

import java.util.Map;

public class VersignConfigVOWrpper extends ValueObject implements VerisignDoCConfig{

    private VerisignOrgConfig config;


    public VersignConfigVOWrpper(VerisignOrgConfig config){
        this.config = config;
    }

    public String getKey() {
        return config.getPublicKey();
    }

    public void setKey(String key) {
        config.setPublicKey(key);
    }

    public String getEmail() {
        return config.getEmail();
    }

    public void setEmail(String email) {
        config.setEmail(email);
    }


    public Map<String, String> getInerConfiguration() {
        return config.getValuesMap();
    }
}
