package org.iana.notifications;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.notifications.exception.NotificationException;
import org.iana.notifications.exception.TemplateNotSupportedException;

/**
 * @author Piotr Tkaczyk
 */

public class TemplateContentConverter implements ContentConverter {

    public String createSubject(TemplateContent templateContent) throws NotificationException {
        CheckTool.checkNull(templateContent, "null templateContent");
        NotificationTemplate template = NotificationTemplateManager.getInstance().getNotificationTemplate(templateContent.getTemplateName());
        if (template == null) throw new TemplateNotSupportedException(templateContent.getTemplateName());
        return TemplateFiller.fill(template.getSubject(), templateContent.getValues());
    }

    public String createBody(TemplateContent templateContent) throws NotificationException {
        CheckTool.checkNull(templateContent, "null templateContent");
        NotificationTemplate template = NotificationTemplateManager.getInstance().getNotificationTemplate(templateContent.getTemplateName());
        if (template == null) throw new TemplateNotSupportedException(templateContent.getTemplateName());
        return TemplateFiller.fill(template.getContent(), templateContent.getValues());
    }

}
