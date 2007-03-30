/**
 * @author Piotr Tkaczyk
 */
package org.iana.notifications.template;

public class TemplateContactConfirmationRemainder extends TemplateContactConfirmation {
    private int period;

    public TemplateContactConfirmationRemainder(String domainName, String roleName, boolean mustAccept, int period) {
        super(domainName, roleName, mustAccept);
        this.period = period;
    }

    public int getPeriod() {
        return this.period;
    }
}
