package org.iana.rzm.facade.admin.config;

import java.io.Serializable;

/**
 * @author Piotr Tkaczyk
 */
public class PgpKeyVO implements Serializable {

    private String name;

    private String armouredKey ;

    private String passphrase;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArmouredKey() {
        return armouredKey;
    }

    public void setArmouredKey(String armouredKey) {
        this.armouredKey = armouredKey;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }
}
