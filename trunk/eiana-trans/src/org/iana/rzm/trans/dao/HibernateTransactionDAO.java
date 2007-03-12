package org.iana.rzm.trans.dao;

import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class HibernateTransactionDAO extends HibernateDaoSupport implements TransactionDAO {
    public List<ProcessInstance> findAllProcessInstances(String domainName) {
        List<ProcessInstance> list = getHibernateTemplate().find(
                "select pi " +
                "from ProcessInstance as pi, " +
                "HibernateLongInstance as hli, " +
                "TransactionData as td " +
                "   inner join td.currentDomain as domain " +
                "where hli.value.class = 'org.iana.rzm.trans.TransactionData' " +
                "   and hli.value.id = td.objId " +
                "   and pi = hli.processInstance " +
                "   and domain.name.name = ?",
                domainName);
        return list;
    }
}
