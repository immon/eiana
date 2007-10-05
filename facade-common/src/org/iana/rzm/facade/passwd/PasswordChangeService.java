package org.iana.rzm.facade.passwd;

import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.common.NoObjectFoundException;

/**
 * @author Jakub Laszkiewicz
 */
public interface PasswordChangeService {

    public void changePassword(String userName, String oldPassword, String newPwd, String newPwd2) throws PasswordChangeException, InfrastructureException;

    public void initPasswordChange(String userName, String link) throws InfrastructureException, PasswordChangeException;

    public void initPasswordChange(String userName, String link, String token) throws InfrastructureException, PasswordChangeException;

    public void finishPasswordChange(String userName, String token, String newPwd, String newPwd2) throws PasswordChangeException, InfrastructureException;

    /**
     * Returns a user uniquely identified by the given email and password. In case when such a unique identification
     * is not possible (two or more users having the same email and password), NonUniqueDataToRecoverUserException is thrown.
     *
     * @param email the email of the user to recover.
     * @param password the password of the user to recover.
     * @return the recovered user.
     * @throws NonUniqueDataToRecoverUserException thrown when recovery impossible due to non unique identification of a user.
     * @throws NoObjectFoundException thrown when no user found matching the given email and password.
     */
    public UserVO recoverUser(String email, String password) throws NonUniqueDataToRecoverUserException, NoObjectFoundException, InfrastructureException;
}
