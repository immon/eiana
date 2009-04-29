package org.iana.rzm.web.common.services;

import org.iana.rzm.facade.auth.AuthenticationFailedException;
import org.iana.rzm.facade.auth.AuthenticationRequiredException;
import org.iana.rzm.facade.auth.AuthenticationToken;
import org.iana.rzm.facade.auth.securid.SecurIDException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.passwd.NonUniqueDataToRecoverUserException;
import org.iana.rzm.facade.passwd.PasswordChangeException;
import org.iana.rzm.web.common.model.WebUser;
import org.iana.secureid.RSAPinData;

public interface RzmAuthenticationService {

    public WebUser login(String userName, String password) throws AuthenticationFailedException, AuthenticationRequiredException;
    public WebUser secureId(AuthenticationToken authenticationToken, String userSName, String code) throws AuthenticationFailedException, AuthenticationRequiredException;
    public void newPassword(String newPassword, String userName, String token, String newPasswordConfirmed) throws PasswordChangeException;
    public void resetPassword(String userName, String url, String token) throws PasswordChangeException;
    public String recoverUser(String email, String password) throws NonUniqueDataToRecoverUserException, NoObjectFoundException;
    public void newPin(String sessionId, String pin) throws SecurIDException;
    public WebUser nextCode(AuthenticationToken authenticationToken, String sessionId, String code)
        throws SecurIDException, AuthenticationRequiredException;

    public RSAPinData getPinInfo(String sessionId) throws SecurIDException;
}
