package org.iana.rzm.domain.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainCriteria;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.dao.hibernate.HibernateDAO;
import org.iana.criteria.Criterion;

import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class HibernateDomainDAO extends HibernateDAO<Domain> implements DomainDAO {


    public HibernateDomainDAO() {
        super(Domain.class);
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

    public List<Domain> findDelegatedTo(Set<String> hostNames) {
        CheckTool.checkNull(hostNames, "host names");
        StringBuffer hql = new StringBuffer(
                "select distinct d from Domain as d inner join d.nameServers as ns where ns.name.name in ( "
        );
        for (String hostName : hostNames) hql.append("?,");
        hql.deleteCharAt(hql.length()-1);
        hql.append(")");
        return (List<Domain>) getHibernateTemplate().find(hql.toString(), hostNames.toArray());
    }

    public List<Domain> findAll() {
        String query = "from Domain";
        return (List<Domain>) getHibernateTemplate().find(query);
    }
}
