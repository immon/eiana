package org.iana.rzm.web.tapestry.components.contact;

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.common.model.SystemDomainVOWrapper;

public interface ContactServices {
    public SystemDomainVOWrapper getDomain(long domainId) throws NoObjectFoundException;
    public void handleObjectNotFound(NoObjectFoundException error, String page);
    public void handleAccessDenied(AccessDeniedException e, String page);
}
