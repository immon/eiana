package org.iana.notifications.email;

import org.iana.notifications.NotificationSender;
import org.iana.notifications.NotificationSenderException;
import org.iana.notifications.PNotification;
import org.iana.rzm.common.validators.CheckTool;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class TemplateTypeDependentEmailSender implements NotificationSender {

    public static final String DEFAULT_NOTIFICATION_SENDER = "defaultNotificationSender";

    private Map<String, EmailSender> mailSendersConfig;

    public TemplateTypeDependentEmailSender(Map<String, EmailSender> mailSendersConfig) {
        CheckTool.checkNull(mailSendersConfig, "mail senders config");
        this.mailSendersConfig = mailSendersConfig;
    }

    public void send(PNotification notification) throws NotificationSenderException {

        String mailSenderType = notification.getMailSenderType();

        NotificationSender sender = mailSendersConfig.get(mailSenderType);

        if (sender == null)
            sender = mailSendersConfig.get(DEFAULT_NOTIFICATION_SENDER);

        if (sender == null)
            throw new NotificationSenderException("notification sender not found for notification type: " + notification.getMailSenderType());

        sender.send(notification);
    }

    public Set<String> getEmailSenderNames() {
        return new HashSet<String>(mailSendersConfig.keySet());
    }

    public EmailSender getEmailSender(String name) {
        return mailSendersConfig.get(name);
    }
}
