package org.iana.rzm.web.services;

import org.iana.rzm.facade.auth.AccessDeniedException;


public interface AccessDeniedHandler {

    public void handleAccessDenied(AccessDeniedException e, String page);
}
