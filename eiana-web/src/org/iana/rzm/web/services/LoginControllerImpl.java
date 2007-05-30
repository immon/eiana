package org.iana.rzm.web.services;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.iana.rzm.web.tapestry.RzmCallback;


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
