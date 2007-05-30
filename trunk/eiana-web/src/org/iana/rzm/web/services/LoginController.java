package org.iana.rzm.web.services;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.iana.rzm.web.tapestry.RzmCallback;

public interface LoginController {

    public ILink loginUser(IEngineService service, IRequestCycle cycle, RzmCallback callback);

}
