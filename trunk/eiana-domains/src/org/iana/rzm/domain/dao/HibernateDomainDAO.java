package org.iana.rzm.domain.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.iana.rzm.domain.Domain;
import org.hibernate.Session;
import org.hibernate.HibernateException;

import java.sql.SQLException;
import java.util.List;
import java.util.Iterator;

/**
 * @author Patrycja Wegrzynowicz
 */
public class HibernateDomainDAO extends HibernateDaoSupport implements DomainDAO {

    public Domain get(final long id) {
        return (Domain) getHibernateTemplate().get(Domain.class, id);
    }

    public Domain get(String name) {
        System.out.println("name = " + name);
        Iterator<Domain> it = getHibernateTemplate().iterate("from Domain d where d.name.name = ?", name);
        Domain ret = it == null || !it.hasNext() ? null : it.next();
        System.out.println("retrieved = " + ret.getName());
        return ret;
    }

    public void create(final Domain domain) {
        getHibernateTemplate().save(domain);
    }
}
