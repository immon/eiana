package org.iana.securid;

import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.auth.securid.*;

import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class MockSecurIDService implements SecurIDService {

    private static Map<String, Integer> authData = new HashMap<String, Integer>();
    private static Map<String, Integer> pinData = new HashMap<String, Integer>();
    private static Map<String, String> session = new HashMap<String, String>();


    public void authenticate(String userName, String securId) throws
                                                              SecurIDNextCodeRequiredException,
                                                              SecurIDNewPinRequiredException,
                                                              SecurIDException,
                                                              AuthenticationFailedException {
        String sessionId = session.get(userName);
        if(sessionId == null){
            sessionId = nextSessionId(securId + userName);
            session.put(userName, sessionId);
        }

        Integer index = authData.get(sessionId);

        if(index == null){
            authData.put(sessionId, 1);
            throw new SecurIDNewPinRequiredException(sessionId);
        }else if( index == 1){
            authData.put(sessionId, 2);
            throw new   SecurIDNextCodeRequiredException(sessionId);
        }else{
            clean(sessionId, userName);
        }
        
    }

    private void clean(String sessionId, String userName) {
        session.remove(userName);
        authData.remove(sessionId);
    }

    private String nextSessionId(String prefix) {
        return prefix;
    }

    public void authenticateWithNextCode(String sessionId, String securId) throws SecurIDException {

    }

    public void setPin(String sessionId, String pin) throws SecurIDInvalidPinException, SecurIDException {
        Integer index = pinData.get(sessionId);
        if(index == null){
            pinData.put(sessionId, 1);
            throw new SecurIDInvalidPinException("Invalid Pin " + pin, sessionId);
        }else{
            pinData.remove(sessionId);
        }
    }
}
