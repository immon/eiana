package org.iana.rzm.web.admin.model;

import org.iana.rzm.facade.admin.config.binded.PgpConfig;
import org.iana.rzm.web.common.model.ValueObject;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: Apr 22, 2009
 * Time: 6:49:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class PgpConfigVOWrapper extends ValueObject implements ApplicationConfig{
    private PgpConfig config;


    public PgpConfigVOWrapper(PgpConfig config){
        this.config = config;
    }

    public Map<String, String> getInerConfiguration() {
        return config.getValuesMap();
    }

    public void setKey(String key) {
        config.setPrivateKey(key);
    }

    public String getKey() {
        return config.getPrivateKey();
    }
}
