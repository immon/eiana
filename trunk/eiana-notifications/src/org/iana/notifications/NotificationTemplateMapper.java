/**
 * @author Piotr Tkaczyk
 */
package org.iana.notifications;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class NotificationTemplateMapper {
    Map<String, NotificationTemplate> templates = new HashMap<String, NotificationTemplate>();


    public NotificationTemplate getNotificationTemplate(String type) {
        return templates.get(type);
    }

    Map<String, NotificationTemplate> getTemplates() {
        return templates;
    }

    void setTemplates(Map<String, NotificationTemplate> templates) {
        this.templates = templates;
    }

    void addTemplate(String type, NotificationTemplate template) {
        if (template != null) addTemplate(type, template);
    }

    public List<NotificationTemplate> getTemplatesList() {
        if (templates == null) return null;
        List<NotificationTemplate> contactsList = new ArrayList<NotificationTemplate>();
        //no elements - return empty list
        if (templates.size() == 0) return contactsList;

        for (NotificationTemplate template : templates.values()) {
            contactsList.add(template);
        }

        return contactsList;
    }

    public void setTemplatesList(List<NotificationTemplate> templates) {
        if (templates == null) {
            this.templates = null;
        } else if (templates.size() == 0) {
            this.templates = new HashMap<String, NotificationTemplate>();
        } else {
            if (this.templates == null) this.templates = new HashMap<String, NotificationTemplate>();
            this.templates.clear();
            for (NotificationTemplate template : templates) {
                this.templates.put(template.getType(), template);
            }
        }
    }

    public void addTemplateList(NotificationTemplate template) {
        if (template != null) addTemplate(template.getType(), template);
    }
}
