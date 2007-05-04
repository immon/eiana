package org.iana.rzm.domain;

import org.iana.rzm.domain.dao.HostDAO;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.criteria.Criterion;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class HostManagerBean implements HostManager {

    private HostDAO dao;

    public HostManagerBean(HostDAO dao) {
        CheckTool.checkNull(dao, "host dao");
        this.dao = dao;
    }

    public Host get(String name) {
        return dao.get(name);
    }

    public Host get(long id) {
        return dao.get(id);
    }

    public List<Host> findAll() {
        return dao.findAll();
    }

    public List<Host> find(Criterion criteria) {
        return dao.find(criteria);
    }
}
