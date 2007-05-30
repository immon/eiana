package org.iana.rzm.web.services;

import org.apache.hivemind.lib.SpringBeanFactoryHolder;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.web.model.WebUser;

public class RzmAuthenticationServiceImpl implements RzmAuthenticationService{

    private AuthenticationService authenticationService;

    public WebUser login(String userName, String password) throws AuthenticationFailedException, AuthenticationRequiredException {
        AuthenticatedUser user = authenticationService.authenticate(new PasswordAuth(userName, password));
        return new WebUser(user);
    }

    public WebUser secureId(String userName, String code) throws AuthenticationFailedException, AuthenticationRequiredException {
        AuthenticatedUser user = authenticationService.authenticate(new SecurIDAuth(userName, code));
        return new WebUser(user);
    }

    public void setBeanFactoryHolder(SpringBeanFactoryHolder beanFactoryHolder) {
        authenticationService = (AuthenticationService) beanFactoryHolder.getBeanFactory().getBean("authenticationServiceBean");
        
    }


}
