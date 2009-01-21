package org.iana.rzm.facade.auth.securid;

import com.rsa.authagent.authapi.*;
import org.apache.log4j.*;
import org.iana.rzm.common.validators.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.user.*;
import org.iana.secureid.AccessDeniedException;
import org.iana.secureid.*;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class StaticSecurIDService implements SecurIDService {

    private static final Logger LOGGER = Logger.getLogger(StaticSecurIDService.class.getName());

    private File securIDInitFile;

    private SecurIDAuthenticationFactory securIDFactory;

    private UserManager userManager;

    private static Map<String, SecurIDEntry> securIDMemo = new HashMap<String, SecurIDEntry>();

    static class SecurIDEntry {

        private String sessionId;

        private String userName;

        private SecureIdAuthentication authenticator;

        public SecurIDEntry(String sessionId, String userName, SecureIdAuthentication authenticator) {
            this.sessionId = sessionId;
            this.userName = userName;
            this.authenticator = authenticator;
        }

        public String getSessionId() {
            return sessionId;
        }

        public String getUserName() {
            return userName;
        }

        public SecureIdAuthentication getAuthenticator() {
            return authenticator;
        }
    }

    public StaticSecurIDService(SecurIDAuthenticationFactory securIDFactory, File f, UserManager userManager)
        throws URISyntaxException {
        CheckTool.checkNull(f, "secur id init file");
        CheckTool.checkNull(securIDFactory, "secur id factory");
        CheckTool.checkNull(userManager, "user manager");
        this.securIDInitFile = new File(f.getAbsolutePath());
        this.securIDFactory = securIDFactory;
        this.userManager = userManager;
    }

    public AuthenticatedUser authenticate(String userName, String securId)
        throws AuthenticationFailedException, SecurIDNewPinRequiredException, SecurIDException {
        SecureIdAuthentication auth = createAuthenticator();
        try {
            auth.auth(userName, securId);
            close(auth);
            return createUser(userName);
        } catch (AuthAgentException e) {
            close(auth);
            throw new SecurIDException(e);
        } catch (NextCodeRequiredException e) {
            throw new SecurIDNextCodeRequiredException(memo(userName, auth));
        } catch (NewPinRequiredException e) {
            throw new SecurIDNewPinRequiredException(memo(userName, auth));
        } catch (AccessDeniedException e) {
            close(auth);
            throw new AuthenticationFailedException(e.getMessage());
        } catch (SecureIdException e) {
            close(auth);
            throw new SecurIDException(e);
        }
    }

    private SecureIdAuthentication createAuthenticator() throws SecurIDException {
        SecureIdAuthentication auth = securIDFactory.createSecurIdAuthentication();
        try {
            auth.init(securIDInitFile);
        } catch (AuthAgentException e) {
            throw new SecurIDException(e);
        }
        return auth;
    }

    private AuthenticatedUser authenticateWithNextCode(String sessionId, String securId) throws SecurIDException {
        SecurIDEntry sessionEntry = get(sessionId);
        try {
            sessionEntry.getAuthenticator().nextToken(securId);
            closeAndRemove(sessionEntry);
            return createUser(sessionEntry.getUserName());
        } catch (NextCodeBadException e) {
            closeAndRemove(sessionEntry);
            throw new SecurIDException(e);
        } catch (SecureIdException e) {
            closeAndRemove(sessionEntry);
            throw new SecurIDException(e);
        }

    }

    public AuthenticatedUser authenticateWithNextCode(AuthenticationToken token, String sessionId, String securId)
        throws AuthenticationRequiredException, SecurIDException {
        CheckTool.checkNull(token, "authentication token");
        if (!token.hasCredential(Authentication.PASSWORD)) {
            throw new AuthenticationRequiredException(Authentication.PASSWORD);
        }
        return authenticateWithNextCode(sessionId, securId);
    }

    public void setPin(String sessionId, String pin) throws SecurIDInvalidPinException, SecurIDException {
        SecurIDEntry entry = get(sessionId);
        try {
            entry.getAuthenticator().newPin(pin);
            closeAndRemove(entry);
        } catch (PinRejectedException e) {
            throw new SecurIDInvalidPinException(e.getMessage(), sessionId);
        } catch (SecureIdException e) {
            closeAndRemove(entry);
            throw new SecurIDException(e);
        }
    }

    public RSAPinData getPinInfo(String sessionId) throws SecurIDException {
        SecurIDEntry entry = get(sessionId);
        try {
            return entry.getAuthenticator().getSession().getRsaPinData();
        } catch (SecureIdException e) {
            closeAndRemove(entry);
            throw new SecurIDException(e);
        }
    }

    private String memo(String userName, SecureIdAuthentication auth) {
        String sessionId = UUID.randomUUID().toString();
        securIDMemo.put(sessionId, new SecurIDEntry(sessionId, userName, auth));
        return sessionId;
    }

    private void closeAndRemove(SecurIDEntry entry) throws SecurIDException {
        securIDMemo.remove(entry.getSessionId());
        close(entry.getAuthenticator());
    }

    private void close(SecureIdAuthentication auth) throws SecurIDException {
        try {
            auth.close();
        } catch (AuthAgentException e) {
            throw new SecurIDException(e);
        }
    }

    private SecurIDEntry get(String sessionId) throws SecurIDException {
        SecurIDEntry ret = securIDMemo.get(sessionId);
        if (ret == null) {
            throw new SecurIDException("session not found " + sessionId);
        }
        return ret;
    }

    private AuthenticatedUser createUser(String userName) throws SecurIDException {
        RZMUser user = userManager.get(userName);
        if (user == null) {
            throw new SecurIDException(MessageFormat.format("User {0} has not been found.", userName));
        }
        return new AuthenticatedUser(user.getObjId(), user.getLoginName(), user.isAdmin());
    }

}
