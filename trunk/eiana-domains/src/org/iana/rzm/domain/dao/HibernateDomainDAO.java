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
        List<Domain> list = getHibernateTemplate().find("from Domain d where d.name.name = ?", name);
        // todo bug in spring or hibernate
        // todo iterate returns object but all values are set to null
        //Iterator<Domain> it = getHibernateTemplate().iterate("from Domain d where d.name.name = ?", name);
        //Domain ret = (it == null || !it.hasNext()) ? null : it.next();
        Domain ret = (list.size() < 1) ? null : list.get(0);
        System.out.println("retrieved = " + ((ret == null) ? null : ret.getName()));
        return ret;
    }

    public void create(final Domain domain) {
        getHibernateTemplate().save(domain);
    }
}
