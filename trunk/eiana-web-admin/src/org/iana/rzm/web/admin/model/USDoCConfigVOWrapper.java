package org.iana.rzm.web.admin.model;

import org.iana.rzm.facade.admin.config.binded.USDoCOrgConfig;
import org.iana.rzm.web.common.model.ValueObject;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: Apr 22, 2009
 * Time: 4:16:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class USDoCConfigVOWrapper extends ValueObject implements VerisignDoCConfig{
    private USDoCOrgConfig config;

    public USDoCConfigVOWrapper(USDoCOrgConfig config){

        this.config = config;
    }

    public Map<String, String> getInerConfiguration() {
        return config.getValuesMap();
    }

    public String getEmail() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getKey() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setEmail(String email) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setKey(String key) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
