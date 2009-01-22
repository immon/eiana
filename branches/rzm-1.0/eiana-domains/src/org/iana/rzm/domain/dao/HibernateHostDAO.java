package org.iana.rzm.domain.dao;

import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.Domain;
import org.iana.dao.hibernate.HibernateDAO;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class HibernateHostDAO extends HibernateDAO<Host> implements HostDAO {

    public HibernateHostDAO() {
        super(Host.class);
    }

    public Host get(String name) {
        List<Host> list = getHibernateTemplate().find("from Host h where h.name.name = ?", name);
        return (list.size() < 1) ? null : list.get(0);
    }

    public List<Host> findAll() {
        return find(null);
    }
}
