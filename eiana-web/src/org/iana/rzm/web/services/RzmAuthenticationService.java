package org.iana.rzm.web.services;

import org.iana.rzm.facade.auth.AuthenticationFailedException;
import org.iana.rzm.facade.auth.AuthenticationRequiredException;
import org.iana.rzm.web.model.WebUser;

public interface RzmAuthenticationService {

    public WebUser login(String userName, String password) throws AuthenticationFailedException, AuthenticationRequiredException;
    public WebUser secureId(String userSName, String code) throws AuthenticationFailedException, AuthenticationRequiredException;

}
