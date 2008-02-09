package org.iana.rzm.mail.parser;

/**
 * @author Piotr Tkaczyk
 */
public class ContentChecker {

    private static final String ACCEPT_STRING = "I ACCEPT";
    private static final String DECLINE_STRING = "I DECLINE";

    private boolean accepted = false;
    private boolean declined = false;

    public ContentChecker(String content) throws MailParserException {
        check(content.toUpperCase());
        if (isAccepted() && isDelcined())
            throw new MailParserException("both accept and decline are present");
        if (!isAccepted() && !isDelcined())
            throw new MailParserException("both accept and decline are missing");
    }

    public boolean isAccepted() {
        return accepted;
    }

    private boolean isDelcined() {
        return declined;
    }

    private void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    private void setDeclined(boolean declined) {
        this.declined = declined;
    }

    private void check(String content) {
        String[] splitted = content.split("\n|\r");
        for (String singleLine : splitted) {
            if (singleLine.trim().equals(ACCEPT_STRING)) setAccepted(true);
            if (singleLine.trim().equals(DECLINE_STRING)) setDeclined(true);
        }
    }
}
