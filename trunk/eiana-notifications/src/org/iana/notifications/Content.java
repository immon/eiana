package org.iana.notifications;

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

    public abstract String getSubject();
    public abstract String getBody();
}
