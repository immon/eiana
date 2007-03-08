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

    public Domain get(String name) throws DomainException {
        return dao.get(name);
    }

    public Domain get(long id) throws DomainException {
        return dao.get(id);
    }

    public void create(Domain domain) throws DomainException {
        dao.create(domain);
    }

    public List<Domain> findAll() throws DomainException {
        return null;
    }

    public List<Domain> find(DomainCriteria criteria) throws DomainException {
        return null;
    }
}
