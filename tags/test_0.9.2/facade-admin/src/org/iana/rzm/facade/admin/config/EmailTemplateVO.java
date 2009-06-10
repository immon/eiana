package org.iana.rzm.facade.admin.config;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EmailTemplateVO implements Serializable {

    private String name;

    private String subject;

    private String content;

    private boolean signed;

    private Set<String> addressees;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Set<String> getAddressees() {
        return addressees;
    }

    public void setAddressees(Set<String> addressees) {
        this.addressees = addressees;
    }

}
