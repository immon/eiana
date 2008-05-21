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

    public void delete(String name) {
        Host host = get(name);
        if (host != null) dao.delete(host);
    }

    public void delete(Host host) {
        CheckTool.checkNull(host, "null host");
        dao.delete(host);
    }

    public void deleteAll() {
        for (Host host : findAll()) {
            delete(host);
        }
    }

    public void update(Host host) {
        CheckTool.checkNull(host, "null host");
        dao.update(host);
    }

    public List<Host> findAll() {
        return dao.findAll();
    }

    public List<Host> find(Criterion criteria) {
        return dao.find(criteria);
    }

    public int count(Criterion criteria) {
        return dao.count(criteria);
    }

    public List<Host> find(Criterion criteria, int offset, int limit) {
        CheckTool.checkNoNegative(offset, "offset is negative");
        CheckTool.checkNoNegative(limit, "limit is negative");
        return dao.find(criteria, offset, limit);
    }
}
