/**
 * @author Piotr Tkaczyk
 */
package org.iana.notifications.template.def;

import org.hibernate.annotations.CollectionOfElements;
import org.iana.notifications.template.def.xml.AddresseeHelper;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Set;

@Entity
public class TemplateDef {
    @Id
    private String type;
    @Basic
    @Column(length = 1024)
    private String subject;
    @Basic
    @Column(length = 4096)
    private String content;
    @Basic
    private boolean signed;
    @Basic
    private String keyFileName;
    @Basic
    private String keyPassphrase;

    @CollectionOfElements
    private Set<String> addressees;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public void setSigned(String signed) {
        this.signed = Boolean.parseBoolean(signed);
    }

    public String getKeyFileName() {
        return keyFileName;
    }

    public void setKeyFileName(String keyFileName) {
        this.keyFileName = keyFileName;
    }

    public String getKeyPassphrase() {
        return keyPassphrase;
    }

    public void setKeyPassphrase(String keyPassphrase) {
        this.keyPassphrase = keyPassphrase;
    }

    public Set<String> getAddressees() {
        return addressees;
    }

    public void setAddressees(Set<String> addressees) {
        this.addressees = addressees;
    }

    public void setAddressees(AddresseeHelper adrrHelp) {
        this.addressees = adrrHelp.getAddreessees();
    }
}
