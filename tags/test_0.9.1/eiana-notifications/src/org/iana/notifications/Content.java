package org.iana.notifications;

import org.iana.notifications.exception.NotificationException;

import javax.persistence.*;

/**
 * The markup interface representing a notification content.
 * The provided classes (@see TextContent, @see TemplateContent) may be used
 * as well as a custom implemention may be provided.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr    Tkaczyk
 * @see TextContent
 * @see TemplateContent
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;

    public Long getObjId() {
        return objId;
    }

    public abstract String getSubject() throws NotificationException;

    public abstract String getBody() throws NotificationException;

    public boolean isTemplateContent() {
        return false;
    }

    public boolean isTextContent() {
        return false;
    }
}
