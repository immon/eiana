package org.iana.rzm.facade.passwd;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.facade.accuracy.SpringCommonApplicationContext;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.passwd.*;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.test.spring.TransactionalSpringContextTests;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.springframework.context.ApplicationContext;

/**
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true)
public class PasswordChangeServiceTest {
    protected UserManager passwdUserManager;
    protected PasswordChangeService passwordChangeService;

    private final static String USER_NAME = "passer";
    private final static String WRONG_USER_NAME = "wrongpasser";
    private final static String USER_PASSWORD = "pass";
    private final static String WRONG_USER_PASSWORD = "wrongpass";

    private int userCounter = 0;

    @BeforeClass
    public void init() {
        ApplicationContext context = SpringApplicationContext.getInstance().getContext();
        passwdUserManager = (UserManager) context.getBean("userManager");
        passwordChangeService = (PasswordChangeService) context.getBean("passwordChangeService");
    }

    private static final String NEW_PASSWORD = "newpass";
    private static final String WRONG_NEW_PASSWORD = "newpass1";

    @Test
    public void successfulPasswordChange() throws Exception {
        String userName = createUser();
        passwordChangeService.changePassword(userName, USER_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
        RZMUser user = passwdUserManager.get(userName);
        assert user.isValidPassword(NEW_PASSWORD);
    }

    @Test(expectedExceptions = UserNotFoundException.class)
    public void passwordChangeWrongUser() throws Exception {
        passwordChangeService.changePassword(WRONG_USER_NAME, USER_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
    }

    @Test(expectedExceptions = InvalidUserPasswordException.class)
    public void passwordChangeWrongPassword() throws Exception {
        String userName = createUser();
        passwordChangeService.changePassword(userName, WRONG_USER_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
    }

    @Test(expectedExceptions = NewPasswordConfirmationMismatchException.class)
    public void passwordChangeNewPasswdConfMismatch() throws Exception {
        String userName = createUser();
        passwordChangeService.changePassword(userName, USER_PASSWORD, NEW_PASSWORD, WRONG_NEW_PASSWORD);
    }

    private static final String LINK = "http://link/";

    @Test
    public void successfulPasswordChangeInitAndFinish() throws PasswordChangeException, InfrastructureException {
        String userName = createUser();
        passwordChangeService.initPasswordChange(userName, LINK);
        RZMUser user = passwdUserManager.get(userName);
        assert user.getPasswordChangeToken() != null;
        assert user.getPasswordChangeToken().length() > 0;
        passwordChangeService.finishPasswordChange(userName, user.getPasswordChangeToken(), NEW_PASSWORD, NEW_PASSWORD);
        user = passwdUserManager.get(userName);
        assert user.isValidPassword(NEW_PASSWORD);
    }

    @Test(expectedExceptions = UserNotFoundException.class)
    public void passwordChangeInitWrongUser() throws PasswordChangeException, InfrastructureException {
        passwordChangeService.initPasswordChange(WRONG_USER_NAME, LINK);
    }

    @Test(expectedExceptions = UserNotFoundException.class)
    public void passwordChangeFinishWrongUser() throws PasswordChangeException, InfrastructureException {
        String userName = createUser();
        passwordChangeService.initPasswordChange(userName, LINK);
        RZMUser user = passwdUserManager.get(userName);
        assert user.getPasswordChangeToken() != null;
        assert user.getPasswordChangeToken().length() > 0;
        passwordChangeService.finishPasswordChange(WRONG_USER_NAME, user.getPasswordChangeToken(), NEW_PASSWORD, NEW_PASSWORD);
    }

    @Test(expectedExceptions = NewPasswordConfirmationMismatchException.class)
    public void passwordChangeFinishNewPasswdConfMismatch() throws PasswordChangeException, InfrastructureException {
        String userName = createUser();
        passwordChangeService.initPasswordChange(userName, LINK);
        RZMUser user = passwdUserManager.get(userName);
        assert user.getPasswordChangeToken() != null;
        assert user.getPasswordChangeToken().length() > 0;
        passwordChangeService.finishPasswordChange(userName, user.getPasswordChangeToken(), NEW_PASSWORD, WRONG_NEW_PASSWORD);
    }

    private static final String WRONG_TOKEN = "wrongtoken";

    @Test(expectedExceptions = PasswordChangeNotInitiatedException.class)
    public void passwordChangeFinishNotInitiatied() throws PasswordChangeException, InfrastructureException {
        String userName = createUser();
        passwordChangeService.finishPasswordChange(userName, WRONG_TOKEN, NEW_PASSWORD, NEW_PASSWORD);
    }

    @Test(expectedExceptions = InvalidPasswordChangeTokenException.class)
    public void passwordChangeFinishWrongToken() throws PasswordChangeException, InfrastructureException {
        String userName = createUser();
        passwordChangeService.initPasswordChange(userName, LINK);
        RZMUser user = passwdUserManager.get(userName);
        assert user.getPasswordChangeToken() != null;
        assert user.getPasswordChangeToken().length() > 0;
        passwordChangeService.finishPasswordChange(userName, WRONG_TOKEN, NEW_PASSWORD, NEW_PASSWORD);
    }

    @Test
    public void testRecoverUser() throws Exception {
        createUser("lostuser", "unique@dot.dot", "pwd123");
        UserVO user = passwordChangeService.recoverUser("unique@dot.dot", "pwd123");
        assert "lostuser".equals(user.getUserName());
    }

    @AfterClass
    public void cleanUp() {
        for (RZMUser user : passwdUserManager.findAll())
            passwdUserManager.delete(user.getLoginName());
    }

    private String createUser(String userName, String email, String password) {
        RZMUser user = new RZMUser();
        user.setLoginName(userName);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail(email);
        user.setPassword(password);
        passwdUserManager.create(user);
        return user.getLoginName();
    }

    private String createUser() {
        return createUser(USER_NAME + userCounter++, "email@domain.name", USER_PASSWORD);
    }
}
