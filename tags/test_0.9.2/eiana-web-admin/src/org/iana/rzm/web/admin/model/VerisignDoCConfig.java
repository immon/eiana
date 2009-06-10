package org.iana.rzm.web.admin.model;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: Apr 22, 2009
 * Time: 5:49:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface VerisignDoCConfig extends ApplicationConfig {

    public String getEmail();
    public String getKey();

    public void setEmail(String email);
    public void setKey(String key);

}
