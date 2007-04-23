package org.iana.rzm.facade.common;

import org.iana.notifications.NotificationSender;
import org.iana.notifications.EmailAddressee;
import org.iana.notifications.exception.NotificationException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;

/**
 * A notification-based implementation of EmailService.
 *
 * @author Patrycja Wegrzynowicz
 */
public class EmailServiceBean implements EmailService {

    private UserManager userManager;
    private NotificationSender sender;

    public EmailServiceBean(NotificationSender sender, UserManager userManager) {
        CheckTool.checkNull(sender, "null notification sender");
        CheckTool.checkNull(userManager, "null user manager");
        this.sender = sender;
        this.userManager = userManager;
    }

    public void sendEmail(String addresseeEmail, String subject, String body) throws InfrastructureException {
        sendEmail(addresseeEmail, "", subject, body);
    }

    public void sendEmail(String addresseeEmail, String addresseeName, String subject, String body) throws InfrastructureException {
        CheckTool.checkNull(addresseeEmail, "null addressee email");
        try {
            sender.send(new EmailAddressee(addresseeEmail, addresseeName), subject, body);
        } catch (NotificationException e) {
            throw new InfrastructureException("sending email", e);
        }
    }

    public void sendEmailToUser(UserVO addressee, String subject, String body) throws InfrastructureException, NoObjectFoundException {
        CheckTool.checkNull(addressee, "null user addressee");
        sendEmailToUser(addressee.getUserName(), subject, body);
    }

    public void sendEmailToUser(String userName, String subject, String body) throws NoObjectFoundException, InfrastructureException {
        CheckTool.checkNull(userName, "null user name");
        RZMUser user = userManager.get(userName);
        if (user == null) throw new NoObjectFoundException(userName, "user");
        try {
            sender.send(user, subject, body);
        } catch (NotificationException e) {
            throw new InfrastructureException("sending email to user " + userName, e);
        }
    }
}
