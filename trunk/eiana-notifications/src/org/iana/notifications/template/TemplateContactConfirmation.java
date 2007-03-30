/**
 * @author Piotr Tkaczyk
 */
package org.iana.notifications.template;

public class TemplateContactConfirmation {
    String domainName;
    String roleName;
    String mustAccept;

    public TemplateContactConfirmation(String domainName, String roleName, boolean mustAccept) {
        this.domainName = domainName;
        this.roleName = roleName;
        this.mustAccept = (mustAccept)? "must" : "are allowed to";
    }

    public String getDomainName() {
        return domainName;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getMustAccept() {
        return mustAccept;
    }
}
