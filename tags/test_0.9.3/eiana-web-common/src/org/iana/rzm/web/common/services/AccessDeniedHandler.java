package org.iana.rzm.web.common.services;

import org.iana.rzm.facade.auth.*;


public interface AccessDeniedHandler {

    public void handleAccessDenied(AccessDeniedException e, String page);
}
