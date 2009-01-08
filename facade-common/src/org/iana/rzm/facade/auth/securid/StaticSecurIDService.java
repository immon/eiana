package org.iana.rzm.facade.auth.securid;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.securid.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Patrycja Wegrzynowicz
 */
public class StaticSecurIDService implements SecurIDService {

    private File securIDInitFile;

    private SecurIDAuthenticationFactory securIDFactory;

    private static Map<String, SecurIdAuthentication> securIDMemo = new HashMap<String, SecurIdAuthentication>();

    public StaticSecurIDService(SecurIDAuthenticationFactory securIDFactory, String uri) throws URISyntaxException {
        CheckTool.checkNull(uri, "secur id init uri");
        CheckTool.checkNull(securIDFactory, "secur id factory");
        this.securIDInitFile = new File(new URI(uri));
        this.securIDFactory = securIDFactory;
    }

    public void authenticate(String userName, String securId) throws SecurIDNextCodeRequiredException, SecurIDNewPinRequiredException, SecurIDException {
        SecurIdAuthentication auth = securIDFactory.createSecurIdAuthentication();
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
        } catch (SecureIdException e) {
            close(auth);
            throw new SecurIDException(e);
        } catch (NewPinRequiredException e) {
            throw new SecurIDNewPinRequiredException(memo(auth));
        }
    }

    public void authenticateWithNextCode(String sessionId, String securId) throws SecurIDException {
        SecurIdAuthentication auth = get(sessionId);
        try {
            auth.nextToken(securId);
            closeAndRemove(sessionId);
        } catch (SecureIdException e) {
            // to close or not to close?
            throw new SecurIDException(e);
        } catch (NextCodeBadException e) {
            // to close or not to close?
            throw new SecurIDException(e);
        }
    }

    public void setPin(String sessionId, String pin) throws SecurIDInvalidPinException, SecurIDException {
        SecurIdAuthentication auth = get(sessionId);
        try {
            auth.newPin(pin);
            closeAndRemove(sessionId);
        } catch (SecureIdException e) {
            // to close or not to close?
            throw new SecurIDException(e);
        } catch (PinRejectedException e) {
            // to close or not to close?
            throw new SecurIDException(e);
        }
    }

    private String memo(SecurIdAuthentication auth) {
        String sessionId = UUID.randomUUID().toString();
        securIDMemo.put(sessionId, auth);
        return sessionId;
    }

    private void closeAndRemove(String sessionId) throws SecurIDException {
        SecurIdAuthentication auth = get(sessionId);
        close(auth);
        securIDMemo.remove(sessionId);
    }

    private void close(SecurIdAuthentication auth) throws SecurIDException {
        try {
            auth.close();
        } catch (AuthAgentException e) {
            throw new SecurIDException(e);
        }
    }

    private SecurIdAuthentication get(String sessionId) throws SecurIDException {
        SecurIdAuthentication ret = securIDMemo.get(sessionId);
        if (ret == null) throw new SecurIDException("session not found " + sessionId);
        return ret;
    }

}
