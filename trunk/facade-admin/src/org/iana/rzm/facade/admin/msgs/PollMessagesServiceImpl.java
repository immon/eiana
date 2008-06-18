package org.iana.rzm.facade.admin.msgs;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractFinderService;
import org.iana.rzm.trans.epp.poll.PollMsgManager;
import org.iana.rzm.trans.epp.poll.PollMsg;
import org.iana.rzm.user.UserManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Patrycja Wegrzynowicz
 */
public class PollMessagesServiceImpl extends AbstractFinderService<PollMsgVO> implements PollMessagesService {

    private static Logger logger = Logger.getLogger(PollMessagesServiceImpl.class);

    private PollMsgManager msgManager;

    public PollMessagesServiceImpl(UserManager userManager, PollMsgManager msgManager) {
        super(userManager);
        CheckTool.checkNull(msgManager, "msg manager");
        this.msgManager = msgManager;
    }

    public void markRead(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        PollMsg msg = getMsg(id);
        if (msg.isRead()) {
            logger.warn("poll msg " + id + " is already read.");
        } else {
            msg.setRead(true);
            msgManager.update(msg);
        }
    }

    public void markUnread(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        PollMsg msg = getMsg(id);
        if (!msg.isRead()) {
            logger.warn("poll msg " + id + " is already marked unread.");
        } else {
            msg.setRead(false);
            msgManager.update(msg);
        }
    }

    public void delete(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        PollMsg msg = getMsg(id);
        msgManager.delete(id);
    }

    public PollMsgVO get(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        return toVO(getMsg(id));
    }

    public int count(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        try {
            return msgManager.count(criteria);
        } catch (Exception e) {
            throw new InfrastructureException(e);
        }
    }

    public List<PollMsgVO> find(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        try {
            return toVO(msgManager.find(criteria));
        } catch (Exception e) {
            throw new InfrastructureException(e);
        }
    }

    public List<PollMsgVO> find(Criterion criteria, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        try {
            return toVO(msgManager.find(criteria, offset, limit));
        } catch (Exception e) {
            throw new InfrastructureException(e);
        }
    }

    private PollMsg getMsg(long id) throws NoObjectFoundException, InfrastructureException {
        PollMsg ret = null;
        try {
            ret = msgManager.get(id);
        } catch (Exception e) {
            throw new InfrastructureException(e);
        }
        if (ret == null) throw new NoObjectFoundException(id, "poll msg");
        return ret;
    }

    private PollMsgVO toVO(PollMsg msg) {
        return msg == null ? null : new PollMsgVO(
                msg.getId(), msg.getTransactionID(), msg.getTicketID(), msg.getEppID(), msg.getName(), msg.getStatus(), msg.getMessage(), msg.isRead(), msg.getCreated()
        );
    }

    private List<PollMsgVO> toVO(List<PollMsg> list) {
        List<PollMsgVO> ret = new ArrayList<PollMsgVO>();
        if (list != null) {
            for (PollMsg msg : list) ret.add(toVO(msg));
        }
        return ret;
    }
}
