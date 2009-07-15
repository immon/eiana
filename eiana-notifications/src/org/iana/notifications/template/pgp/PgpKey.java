package org.iana.notifications.template.pgp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Piotr Tkaczyk
 */

@Entity
public class PgpKey {

    @Id
    private String name;
    @Basic
    @Column(length = 2048)
    private String armouredKey ;
    @Basic
    private String passphrase;

    public PgpKey() {
    }

    public PgpKey(String name, String armouredKey, String passphrase) {
        this.name = name;
        this.armouredKey = armouredKey;
        this.passphrase = passphrase;
    }

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
