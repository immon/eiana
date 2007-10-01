package org.iana.rzm.facade.passwd;

import org.iana.notifications.Notification;
import org.iana.notifications.NotificationSender;
import org.iana.notifications.TemplateContent;
import org.iana.notifications.exception.NotificationException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import pl.nask.util.StringTool;

import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jakub Laszkiewicz
 */
public class PasswordChangeServiceBean implements PasswordChangeService {
    public static final String PASSWORD_CHANGE_TEMPLATE_NAME = "password-change";

    private UserManager userManager;
    private NotificationSender notificationSender;

    public PasswordChangeServiceBean(UserManager userManager, NotificationSender notificationSender) {
        CheckTool.checkNull(userManager, "passwdUserManager");
        CheckTool.checkNull(notificationSender, "notificationSender");
        this.userManager = userManager;
        this.notificationSender = notificationSender;
    }

    public void changePassword(String userName, String oldPassword, String newPwd, String newPwd2) throws PasswordChangeException {
        CheckTool.checkNull(userName, "userName");
        CheckTool.checkNull(oldPassword, "oldPassword");
        CheckTool.checkNull(newPwd, "newPwd");
        CheckTool.checkNull(newPwd2, "newPwd2");
        RZMUser user = userManager.get(userName);
        if (user == null) throw new UserNotFoundException(userName);
        if (!user.isValidPassword(oldPassword)) throw new InvalidUserPasswordException(userName);
        if (!newPwd.equals(newPwd2)) throw new NewPasswordConfirmationMismatchException(userName);
        user.setPassword(newPwd);
        userManager.update(user);
    }

    public void initPasswordChange(String userName, String link) throws InfrastructureException, PasswordChangeException {
        CheckTool.checkNull(userName, "userName");
        CheckTool.checkNull(link, "link");
        try {
            RZMUser user = userManager.get(userName);
            if (user == null) throw new UserNotFoundException(userName);
            user.setPasswordChangeToken(generateToken());
            Map<String, String> values = new HashMap<String, String>();
            values.put("userName", userName);
            values.put("link", link + user.getPasswordChangeToken());
            TemplateContent templateContent = new TemplateContent(PASSWORD_CHANGE_TEMPLATE_NAME, values);
            Notification notification = new Notification();
            notification.setContent(templateContent);
            notificationSender.send(user, notification.getContent());
        } catch (NotificationException e) {
            throw new InfrastructureException(e);
        }
    }

    public void finishPasswordChange(String userName, String token, String newPwd, String newPwd2) throws PasswordChangeException {
        CheckTool.checkNull(userName, "userName");
        CheckTool.checkNull(token, "token");
        CheckTool.checkNull(newPwd, "newPwd");
        CheckTool.checkNull(newPwd2, "newPwd2");
        RZMUser user = userManager.get(userName);
        if (user == null) throw new UserNotFoundException(userName);
        if (!user.isSetPasswordChangeToken()) throw new PasswordChangeNotInitiatedException(userName);
        if (!user.isValidPasswordChangeToken(token)) throw new InvalidPasswordChangeTokenException(userName);
        if (!newPwd.equals(newPwd2)) throw new NewPasswordConfirmationMismatchException(userName);
        user.setPassword(newPwd);
        user.resetPasswordChangeToken();
        userManager.update(user);
    }

    private String generateToken() {
        return StringTool.encodeMd5(new UID().toString());
    }
}
