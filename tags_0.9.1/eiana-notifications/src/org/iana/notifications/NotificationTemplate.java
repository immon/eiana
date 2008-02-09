/**
 * @author Piotr Tkaczyk
 */
package org.iana.notifications;

import pl.nask.util.ReflectionTool;

import java.io.IOException;

import org.iana.notifications.exception.InstancingNotificationException;
import org.iana.notifications.exception.NotificationException;

public class NotificationTemplate {
    private String type;
    private String subject;
    private String content;
    private boolean signed;
    private String keyFileName;
    private String keyPassphrase;


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

    public Notification getNotificationInstance(Object object) throws NotificationException {

        try {
            Notification notification = new Notification();
            String subject = ReflectionTool.replaceGetterChains(getSubject(), object);
            String body = ReflectionTool.replaceGetterChains(getContent(), object);
            notification.setContent(new TextContent(subject, body));
            notification.setType(type);

            return notification;

        } catch (IOException e) {
            throw new InstancingNotificationException("Notification instancing failed!", e);
        }
    }
}
