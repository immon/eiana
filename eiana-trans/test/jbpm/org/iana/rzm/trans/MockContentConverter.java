package org.iana.rzm.trans;

import org.iana.notifications.ContentConverter;
import org.iana.notifications.TemplateContent;
import org.iana.notifications.exception.NotificationException;

/**
 * @author Piotr Tkaczyk
 */
public class MockContentConverter implements ContentConverter {

    public String createSubject(TemplateContent templateContent) throws NotificationException {
        return "MOCK";
    }

    public String createBody(TemplateContent templateContent) throws NotificationException {
        return "MOCK";
    }
}
