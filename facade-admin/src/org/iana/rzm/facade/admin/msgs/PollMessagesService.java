package org.iana.rzm.facade.admin.msgs;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.FinderService;
import org.iana.rzm.facade.services.RZMStatefulService;

/**
 * This interface provides finder service for poll messages and basic manipulation to mark a message read or unread.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface PollMessagesService extends RZMStatefulService, FinderService<PollMsgVO> {

    public void markRead(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    public void markUnread(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    public void delete(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

}
