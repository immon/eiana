package org.iana.rzm.facade.passwd;

import org.iana.rzm.common.exceptions.*;

/**
 * @author Jakub Laszkiewicz
 */
public interface PasswordChangeService {
    public void changePassword(String userName, String oldPassword, String newPwd, String newPwd2) throws PasswordChangeException;
    public void initPasswordChange(String userName, String link) throws InfrastructureException, PasswordChangeException;
    public void initPasswordChange(String userName, String link, String token) throws InfrastructureException, PasswordChangeException;
    public void finishPasswordChange(String userName, String token, String newPwd, String newPwd2) throws PasswordChangeException;
}
