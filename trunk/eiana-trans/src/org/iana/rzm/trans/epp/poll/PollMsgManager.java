package org.iana.rzm.trans.epp.poll;

import org.iana.criteria.Criterion;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface PollMsgManager {

    public void create(PollMsg msg);

    public PollMsg get(long id);

    public int count(Criterion criteria);

    public List<PollMsg> find(Criterion criteria);

    public List<PollMsg> find(Criterion criteria, int offset, int limit);

}
