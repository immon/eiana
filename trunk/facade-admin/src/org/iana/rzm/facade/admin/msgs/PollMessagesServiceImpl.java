package org.iana.rzm.facade.admin.msgs;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractFinderService;
import org.iana.rzm.facade.user.UserVOManager;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class PollMessagesServiceImpl extends AbstractFinderService<PollMsgVO> implements PollMessagesService {

    private StatelessPollMessagesService statelessPollMessagesService;

    public PollMessagesServiceImpl(UserVOManager userManager, StatelessPollMessagesService statelessPollMessagesService) {
        super(userManager);
        CheckTool.checkNull(statelessPollMessagesService, "stateless poll messages service");
        this.statelessPollMessagesService = statelessPollMessagesService;
    }

    public void markRead(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        statelessPollMessagesService.markRead(id, getAuthenticatedUser());
    }

    public void markUnread(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        statelessPollMessagesService.markUnread(id, getAuthenticatedUser());
    }

    public void delete(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        statelessPollMessagesService.delete(id, getAuthenticatedUser());
    }

    public PollMsgVO get(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        return statelessPollMessagesService.get(id, getAuthenticatedUser());
    }

    public int count(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        return statelessPollMessagesService.count(criteria, getAuthenticatedUser());
    }

    public List<PollMsgVO> find(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        return statelessPollMessagesService.find(criteria, getAuthenticatedUser());
    }

    public List<PollMsgVO> find(Criterion criteria, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return statelessPollMessagesService.find(criteria, offset, limit, getAuthenticatedUser());
    }
}
