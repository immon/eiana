package org.iana.rzm.facade.passwd;

import org.iana.notifications.*;
import org.iana.notifications.exception.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.common.validators.*;
import org.iana.rzm.user.*;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.criteria.Criterion;
import org.iana.criteria.And;
import org.iana.criteria.Equal;
import pl.nask.util.*;

import java.rmi.server.*;
import java.util.*;

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
        initPasswordChange(userName,link, generateToken());
    }

    public void initPasswordChange(String userName, String link, String token) throws InfrastructureException, PasswordChangeException {
        CheckTool.checkNull(userName, "userName");
        CheckTool.checkNull(link, "link");
        try {
            RZMUser user = userManager.get(userName);
            if (user == null) throw new UserNotFoundException(userName);
            user.setPasswordChangeToken(token);
            Map<String, String> values = new HashMap<String, String>();
            values.put("userName", userName);
            values.put("link", link);
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

    public UserVO recoverUser(String email, String password) throws NonUniqueDataToRecoverUserException, NoObjectFoundException, InfrastructureException {
        CheckTool.checkNull(email, "email");
        CheckTool.checkNull(password, "password");
        MD5Password md5 = new MD5Password(password);
        Criterion emailAndPassword = new And(Arrays.asList(
                (Criterion)
                new Equal("email", email),
                new Equal("password.password", md5)
        ));
        List<RZMUser> users = userManager.find(emailAndPassword);
        if (users.size() > 1) throw new NonUniqueDataToRecoverUserException();
        if (users.size() == 0) throw new NoObjectFoundException("user", email);
        return UserConverter.convert(users.get(0));
    }
}
