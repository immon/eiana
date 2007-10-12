package org.iana.notifications;

import org.iana.notifications.exception.NotificationException;
import org.iana.notifications.exception.TemplateNotSupportedException;
import org.iana.rzm.common.validators.CheckTool;

/**
 * @author Piotr Tkaczyk
 */

public class TemplateContentConverter implements ContentConverter {

    private NotificationTemplateManager templateManager;

    public TemplateContentConverter(NotificationTemplateManager manager) {
        CheckTool.checkNull(manager, "notification template manager cannot be null");
        templateManager = manager;
    }

    public String createSubject(TemplateContent templateContent) throws NotificationException {
        CheckTool.checkNull(templateContent, "null templateContent");
        NotificationTemplate template = templateManager.getNotificationTemplate(templateContent.getTemplateName());
        if (template == null) throw new TemplateNotSupportedException(templateContent.getTemplateName());
        return TemplateFiller.fill(template.getSubject(), templateContent.getValues());
    }

    public String createBody(TemplateContent templateContent) throws NotificationException {
        CheckTool.checkNull(templateContent, "null templateContent");
        NotificationTemplate template = templateManager.getNotificationTemplate(templateContent.getTemplateName());
        if (template == null) throw new TemplateNotSupportedException(templateContent.getTemplateName());
        return TemplateFiller.fill(template.getContent(), templateContent.getValues());
    }

}
