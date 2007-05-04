package org.iana.rzm.domain;

import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.criteria.Criterion;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DomainManagerBean implements DomainManager {

    private DomainDAO dao;

    public DomainManagerBean(DomainDAO dao) {
        CheckTool.checkNull(dao, "domain dao");
        this.dao = dao;
    }

    public Domain get(String name) {
        return dao.get(name);
    }

    public Domain get(long id) {
        return dao.get(id);
    }

    public void create(Domain domain) {
        dao.create(domain);
    }

    public void update(Domain domain) {
        dao.update(domain);
    }

    public void delete(Domain domain) {
        dao.delete(domain);
    }

    public void delete(String name) {
        delete(get(name));
    }

    public List<Domain> findAll() {
        return dao.findAll();
    }

    public List<Domain> find(Criterion criteria) {
        return dao.find(criteria);
    }
}
