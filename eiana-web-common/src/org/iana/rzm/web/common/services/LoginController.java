package org.iana.rzm.web.common.services;

import org.apache.tapestry.*;
import org.apache.tapestry.engine.*;
import org.iana.rzm.web.common.callback.*;


public interface LoginController {

    public ILink loginUser(IEngineService service, IRequestCycle cycle, RzmCallback callback);

}
