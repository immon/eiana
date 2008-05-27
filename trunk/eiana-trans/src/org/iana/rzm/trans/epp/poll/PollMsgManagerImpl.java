package org.iana.rzm.trans.epp.poll;

import org.iana.criteria.Criterion;
import org.iana.dao.DataAccessObject;
import org.iana.rzm.common.validators.CheckTool;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class PollMsgManagerImpl implements PollMsgManager {

    DataAccessObject<PollMsg> msgDAO;

    public PollMsgManagerImpl(DataAccessObject<PollMsg> msgDAO) {
        CheckTool.checkNull(msgDAO, "msg dao");
        this.msgDAO = msgDAO;
    }

    public void create(PollMsg msg) {
        msgDAO.create(msg);
    }

    public PollMsg get(long id) {
        return msgDAO.get(id);
    }

    public void delete(long id) {
        PollMsg msg = get(id);
        if (msg != null) msgDAO.delete(msg);
    }

    public int count(Criterion criteria) {
        return msgDAO.count(criteria);
    }

    public List<PollMsg> find(Criterion criteria) {
        return msgDAO.find(criteria);
    }

    public List<PollMsg> find(Criterion criteria, int offset, int limit) {
        return msgDAO.find(criteria, offset, limit);
    }
}
