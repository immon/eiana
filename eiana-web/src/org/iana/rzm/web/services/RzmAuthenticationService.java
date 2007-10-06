package org.iana.rzm.web.services;

import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.passwd.*;
import org.iana.rzm.web.model.*;

public interface RzmAuthenticationService {

    public WebUser login(String userName, String password) throws AuthenticationFailedException, AuthenticationRequiredException;
    public WebUser secureId(String userSName, String code) throws AuthenticationFailedException, AuthenticationRequiredException;
    public void newPassword(String newPassword, String userName, String token, String newPasswordConfirmed) throws PasswordChangeException;
    public  void resetPassword(String userName, String url, String token) throws PasswordChangeException;
    public String recoverUser(String email, String password) throws NonUniqueDataToRecoverUserException, NoObjectFoundException;
}
