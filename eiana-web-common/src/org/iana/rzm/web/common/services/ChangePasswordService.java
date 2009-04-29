package org.iana.rzm.web.common.services;

import org.iana.rzm.facade.passwd.PasswordChangeException;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: Apr 28, 2009
 * Time: 4:36:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ChangePasswordService {

    public void changePassword(String username, String oldPassword, String newPassword, String confirmedNewPassword) throws PasswordChangeException;
}
