package org.iana.rzm.facade.passwd;

import org.iana.criteria.And;
import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.iana.notifications.NotificationSender;
import org.iana.notifications.PNotification;
import org.iana.notifications.PAddressee;
import org.iana.notifications.NotificationSenderException;
import org.iana.notifications.producers.NotificationProducer;
import org.iana.notifications.producers.NotificationProducerException;
import org.iana.notifications.producers.defaults.DefaultAddresseeProducer;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.user.MD5Password;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import pl.nask.util.StringTool;

import java.rmi.server.UID;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jakub Laszkiewicz
 */
public class PasswordChangeServiceBean implements PasswordChangeService {
    public static final String PASSWORD_CHANGE_TEMPLATE_NAME = "password-change";

    private UserManager userManager;
    private NotificationSender notificationSender;
    private NotificationProducer notificationProducer;

    public PasswordChangeServiceBean(UserManager userManager, NotificationSender notificationSender, NotificationProducer notificationProducer) {
        CheckTool.checkNull(userManager, "user manager");
        CheckTool.checkNull(notificationSender, "notification sender");
        CheckTool.checkNull(notificationProducer,  "notification producer");
        this.userManager = userManager;
        this.notificationSender = notificationSender;
        this.notificationProducer = notificationProducer;
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
        user.setPasswordExDate(null);
        userManager.update(user);
    }

    public void initPasswordChange(String userName, String link) throws InfrastructureException, PasswordChangeException {
        initPasswordChange(userName, link, generateToken());
    }

    public void initPasswordChange(String userName, String link, String token) throws InfrastructureException, PasswordChangeException {
        CheckTool.checkNull(userName, "userName");
        CheckTool.checkNull(link, "link");
        try {
            RZMUser user = userManager.get(userName);
            if (user == null) throw new UserNotFoundException(userName);
            user.setPasswordChangeToken(token);

            Map<String, Object> dataSource = new HashMap<String, Object>();
            dataSource.put("userName", userName);
            dataSource.put("link", link);
            PAddressee addr = new PAddressee(user.getName(), user.getEmail());
            dataSource.put(DefaultAddresseeProducer.ADDRESSEE, addr);
            List<PNotification> notifications = notificationProducer.produce(dataSource);
            for (PNotification notification : notifications) {
                notificationSender.send(notification);
            }
        } catch (NotificationProducerException e) {
            throw new InfrastructureException(e);
        } catch (NotificationSenderException e) {
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
        user.setPasswordExDate(null);
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
                new Equal("password.password", md5.getPassword())
        ));
        List<RZMUser> users = userManager.find(emailAndPassword);
        if (users.size() > 1) throw new NonUniqueDataToRecoverUserException();
        if (users.size() == 0) throw new NoObjectFoundException("user", email);
        return UserConverter.convert(users.get(0));
    }
}
