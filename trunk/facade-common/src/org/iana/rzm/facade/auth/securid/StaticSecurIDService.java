package org.iana.rzm.facade.auth.securid;

import com.rsa.authagent.authapi.*;
import org.iana.rzm.common.validators.*;
import org.iana.rzm.facade.auth.*;
import org.iana.secureid.AccessDeniedException;
import org.iana.secureid.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class StaticSecurIDService implements SecurIDService {

    private File securIDInitFile;

    private SecurIDAuthenticationFactory securIDFactory;

    private static Map<String, SecureIdAuthentication> securIDMemo = new HashMap<String, SecureIdAuthentication>();

    public StaticSecurIDService(SecurIDAuthenticationFactory securIDFactory, String uri) throws URISyntaxException {
        CheckTool.checkNull(uri, "secur id init uri");
        CheckTool.checkNull(securIDFactory, "secur id factory");
        this.securIDInitFile = new File(new URI(uri));
        this.securIDFactory = securIDFactory;
    }

    public void authenticate(String userName, String securId)
        throws AuthenticationFailedException, SecurIDNewPinRequiredException, SecurIDException {
        SecureIdAuthentication auth = securIDFactory.createSecurIdAuthentication();
        try {
            auth.init(securIDInitFile);
        } catch (AuthAgentException e) {
            throw new SecurIDException(e);
        }
        try {
            auth.auth(userName, securId);
            close(auth);
        } catch (AuthAgentException e) {
            close(auth);
            throw new SecurIDException(e);
        } catch (NextCodeRequiredException e) {
            throw new SecurIDNextCodeRequiredException(memo(auth));
        } catch (NewPinRequiredException e) {
            throw new SecurIDNewPinRequiredException(memo(auth));
        } catch (AccessDeniedException e) {
            close(auth);
            throw new AuthenticationFailedException(e.getMessage());
        } catch (SecureIdException e) {
            close(auth);
            throw new SecurIDException(e);
        }
    }

    public void authenticateWithNextCode(String sessionId, String securId) throws SecurIDException {
        SecureIdAuthentication auth = get(sessionId);
        try {
            auth.nextToken(securId);
            closeAndRemove(sessionId);
        }catch (NextCodeBadException e){
            closeAndRemove(sessionId);
            throw new SecurIDException(e);
        } catch (SecureIdException e) {
            closeAndRemove(sessionId);
            throw new SecurIDException(e);
        }

    }

    public void setPin(String sessionId, String pin) throws SecurIDInvalidPinException, SecurIDException {
        SecureIdAuthentication auth = get(sessionId);
        try {
            auth.newPin(pin);
            closeAndRemove(sessionId);
        } catch (PinRejectedException e) {
            throw new SecurIDInvalidPinException(e.getMessage(), sessionId);
        } catch (SecureIdException e) {
            closeAndRemove(sessionId);
            throw new SecurIDException(e);
        }
    }

    private String memo(SecureIdAuthentication auth) {
        String sessionId = UUID.randomUUID().toString();
        securIDMemo.put(sessionId, auth);
        return sessionId;
    }

    private void closeAndRemove(String sessionId) throws SecurIDException {
        SecureIdAuthentication auth = get(sessionId);
        close(auth);
        securIDMemo.remove(sessionId);
    }

    private void close(SecureIdAuthentication auth) throws SecurIDException {
        try {
            auth.close();
        } catch (AuthAgentException e) {
            throw new SecurIDException(e);
        }
    }

    private SecureIdAuthentication get(String sessionId) throws SecurIDException {
        SecureIdAuthentication ret = securIDMemo.get(sessionId);
        if (ret == null) {
            throw new SecurIDException("session not found " + sessionId);
        }
        return ret;
    }

}
