package org.iana.rzm.web.common.services;

import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.auth.securid.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.passwd.*;
import org.iana.rzm.web.common.model.*;
import org.iana.secureid.*;

public interface RzmAuthenticationService {

    public WebUser login(String userName, String password) throws AuthenticationFailedException, AuthenticationRequiredException;
    public WebUser secureId(AuthenticationToken authenticationToken, String userSName, String code) throws AuthenticationFailedException, AuthenticationRequiredException;
    public void newPassword(String newPassword, String userName, String token, String newPasswordConfirmed) throws PasswordChangeException;
    public  void resetPassword(String userName, String url, String token) throws PasswordChangeException;
    public String recoverUser(String email, String password) throws NonUniqueDataToRecoverUserException, NoObjectFoundException;
    public void newPin(String sessionId, String pin) throws SecurIDException;
    public WebUser nextCode(AuthenticationToken authenticationToken, String sessionId, String code)
        throws SecurIDException, AuthenticationRequiredException;

    public RSAPinData getPinInfo(String sessionId) throws SecurIDException;
}
