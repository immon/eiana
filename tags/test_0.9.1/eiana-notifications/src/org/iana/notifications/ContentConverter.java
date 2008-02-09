package org.iana.notifications;

import org.iana.notifications.exception.NotificationException;

/**
 * @author Piotr Tkaczyk
 */

public interface ContentConverter {

    public String createSubject(TemplateContent templateContent) throws NotificationException;

    public String createBody(TemplateContent templateContent) throws NotificationException;
}
