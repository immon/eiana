package org.iana.rzm.web.common.services;

import org.apache.tapestry.*;
import org.apache.tapestry.engine.*;
import org.iana.rzm.web.common.callback.*;


public class LoginControllerImpl implements LoginController {
    
    public ILink loginUser(IEngineService service, IRequestCycle cycle, RzmCallback callback) {

        if(callback == null){
            return service.getLink(true, null);
        }

        if(callback.isExternal()){
            callback.performCallback(cycle);
            return null;
        }

        if(callback.isRedirect()){
            callback.performCallback(cycle);
        }

        return service.getLink(true, callback.getPageName());
    }

}
