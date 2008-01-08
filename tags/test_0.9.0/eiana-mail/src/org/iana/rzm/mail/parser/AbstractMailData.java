package org.iana.rzm.mail.parser;

/**
 * @author Jakub Laszkiewicz
 */
public abstract class AbstractMailData implements MailData {
    private String originalSubject;
    private String originalBody;

    protected AbstractMailData() {
    }

    protected AbstractMailData(String originalSubject, String originalBody) {
        this.originalSubject = originalSubject;
        this.originalBody = originalBody;
    }

    public String getOriginalSubject() {
        return originalSubject;
    }

    public void setOriginalSubject(String originalSubject) {
        this.originalSubject = originalSubject;
    }

    public String getOriginalBody() {
        return originalBody;
    }

    public void setOriginalBody(String originalBody) {
        this.originalBody = originalBody;
    }
}
