package org.iana.rzm.facade.auth;

import org.apache.log4j.Logger;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.user.UserManager;

import javax.ejb.Stateless;
import java.util.Map;
import java.util.Collections;
import java.text.MessageFormat;

/**
 * @author Marcin Zajaczkowski
 */

@Stateless
public class AuthenticationServiceBean implements AuthenticationService {

    private final Logger loger = Logger.getLogger(getClass());

    private Map<String, Authenticator> authenticatorMap;
    private UserManager manager;

    public AuthenticationServiceBean(Map<String, Authenticator> authenticatorMap, UserManager manager) {
        CheckTool.checkNull(authenticatorMap, "authenticatorMap is null");
        CheckTool.checkNull(manager, "manager is null");
        this.authenticatorMap = authenticatorMap;
        this.manager = manager;
    }

    public Map<String, Authenticator> getAuthenticatorMap() {
        return Collections.unmodifiableMap(authenticatorMap);
    }

    public UserManager getManager() {
        return manager;
    }

    public AuthenticatedUser authenticate(AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {

        //maybe some runtime exception?
        if (data == null) throw new AuthenticationFailedException("AuthenticationData is null.");

        try {
            return getAuthneticator(data).authenticate(data, manager);

        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException(e);
        }
    }

    public AuthenticatedUser authenticate(AuthenticationToken token, AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {
        throw new IllegalStateException("Not implemented yet.");
    }

    /**
     *
     * @param data
     * @return
     * @throws AuthenticationInternalException No Authenticator was found for given AuthenticationData
     */
    private Authenticator getAuthneticator(AuthenticationData data) throws AuthenticationInternalException {

        Authenticator authenticator = authenticatorMap.get(data.getClass().getName());
        if (authenticator == null) throw new AuthenticationInternalException(MessageFormat.format(
                        "Unable to create Authenticator. No Authenticator configured for {0}", data.getClass().getName()));

        return authenticator;
    }
}
