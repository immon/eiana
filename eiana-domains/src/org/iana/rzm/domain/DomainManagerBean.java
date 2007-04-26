package org.iana.rzm.domain;

import org.iana.rzm.domain.dao.DomainDAO;

import java.util.List;

/**
 * 
 * @author Patrycja Wegrzynowicz
 */
public class DomainManagerBean implements DomainManager {

    private DomainDAO dao;

    public DomainManagerBean(DomainDAO dao) {
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

    public List<Domain> findAll() {
        return null;
    }

    public List<Domain> find(DomainCriteria criteria) {
        return null;
    }
}
