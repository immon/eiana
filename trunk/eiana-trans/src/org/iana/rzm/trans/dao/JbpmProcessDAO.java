package org.iana.rzm.trans.dao;

import org.hibernate.Query;
import org.iana.criteria.Criterion;
import org.iana.rzm.trans.jbpm.JbpmContextFactory;
import org.iana.rzm.user.RZMUser;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Jakub Laszkiewicz
 */
public class JbpmProcessDAO implements ProcessDAO {
    private JbpmContextFactory jbpmContextFactory;
    private JbpmContext jbpmContext;

    private JbpmContext getContext() {
        if (jbpmContext == null)
            jbpmContext = jbpmContextFactory.getJbpmContext();
        return jbpmContext;
    }

    public JbpmProcessDAO(JbpmContextFactory jbpmContextFactory) {
        this.jbpmContextFactory = jbpmContextFactory;
    }

    public ProcessInstance getProcessInstance(final long processInstanceId) {
        return getContext().getProcessInstance(processInstanceId);
    }

    public ProcessInstance newProcessInstance(final String name) {
        return getContext().newProcessInstance(name);
    }

    public ProcessInstance newProcessInstanceForUpdate(final String name) {
        return getContext().newProcessInstanceForUpdate(name);
    }

    public List<ProcessInstance> findAll() {
        return getContext().getSession().createQuery("from ProcessInstance").list();
    }

    public List<ProcessInstance> find(final Criterion criteria) {
        JbpmProcessCriteriaTranslator translator = new JbpmProcessCriteriaTranslator(criteria);
        Query query = getContext().getSession().createQuery(translator.getHQL());
        int idx = 0;
        for (Object param : translator.getParams()) {
            query.setParameter(idx++, param);
        }
        return query.list();
    }

    public List<ProcessInstance> find(final Criterion criteria, final int offset, final int limit) {
        JbpmProcessCriteriaTranslator translator = new JbpmProcessCriteriaTranslator(criteria);
        Query query = getContext().getSession().createQuery(translator.getHQL());
        int idx = 0;
        for (Object param : translator.getParams()) {
            query.setParameter(idx++, param);
        }
        query.setFirstResult(offset)
                .setMaxResults(limit)
                .setFetchSize(limit);
        return query.list();
    }
    
    public int count(final Criterion criteria) {
        JbpmProcessCriteriaTranslator translator = new JbpmProcessCriteriaTranslator(criteria);
        String hql = translator.getHQL("select count(*)");
        Query query = getContext().getSession().createQuery(hql);
        int idx = 0;
        for (Object param : translator.getParams()) {
            query.setParameter(idx++, param);
        }
        Long result = (Long) query.uniqueResult();
        return result.intValue();
    }

    public List<ProcessInstance> findAllProcessInstances(final String domainName) {
        Query query = getContext().getSession().createQuery(
                "select pi " +
                        "from ProcessInstance as pi, " +
                        "HibernateLongInstance as hli, " +
                        "TransactionData as td " +
                        "   inner join td.currentDomain as domain " +
                        "where hli.value.class = 'org.iana.rzm.trans.TransactionData' " +
                        "   and hli.value.id = td.objId " +
                        "   and pi = hli.processInstance " +
                        "   and domain.name.name = :name "
        );
        query.setString("name", domainName);
        return query.list();
    }

    public List<ProcessInstance> findOpenProcessInstances(final String domainName) {
        Query query = getContext().getSession().createQuery(
                "select pi " +
                        "from ProcessInstance as pi, " +
                        "HibernateLongInstance as hli, " +
                        "TransactionData as td " +
                        "   inner join td.currentDomain as domain " +
                        "where hli.value.class = 'org.iana.rzm.trans.TransactionData' " +
                        "   and hli.value.id = td.objId " +
                        "   and pi = hli.processInstance " +
                        "   and domain.name.name = :name " +
                        "   and pi.end is null"
        );
        query.setString("name", domainName);
        return query.list();
    }

    public List<ProcessInstance> findAllProcessInstances(final RZMUser user) {
        if (user == null) throw new IllegalArgumentException("user is null");
        Query query = getContext().getSession().createQuery(
                "select pi " +
                        "from ProcessInstance as pi, " +
                        "HibernateLongInstance as hli, " +
                        "TransactionData as td " +
                        "   inner join td.currentDomain as domain, " +
                        "RZMUser as user " +
                        "   inner join user.roles as role " +
                        "where hli.value.class = 'org.iana.rzm.trans.TransactionData' " +
                        "   and hli.value.id = td.objId " +
                        "   and pi = hli.processInstance " +
                        "   and user.objId = :id " +
                        "   and role.class = SystemRole " +
                        "   and domain.name.name = role.name"
        );
        query.setLong("id", user.getObjId());
        return query.list();
    }

    public List<ProcessInstance> findAllProcessInstances(final RZMUser user, final String domainName) {
        if (user == null) throw new IllegalArgumentException("user is null");
        Query query = getContext().getSession().createQuery(
                "select pi " +
                        "from ProcessInstance as pi, " +
                        "HibernateLongInstance as hli, " +
                        "TransactionData as td " +
                        "   inner join td.currentDomain as domain, " +
                        "RZMUser as user " +
                        "   inner join user.roles as role " +
                        "where hli.value.class = 'org.iana.rzm.trans.TransactionData' " +
                        "   and hli.value.id = td.objId " +
                        "   and pi = hli.processInstance " +
                        "   and user.objId = :id " +
                        "   and role.class = SystemRole " +
                        "   and domain.name.name = :name " +
                        "   and role.name = :name"
        );
        query.setLong("id", user.getObjId());
        query.setString("name", domainName);
        return query.list();
    }

    public List<ProcessInstance> find(ProcessCriteria criteria) {
        if (criteria == null) throw new IllegalArgumentException("criteria is null");
        HqlQuery hqlQuery = ProcessCriteriaHqlTranslator.translate(criteria);
        System.out.println("HQL: " + hqlQuery.getHql());
        Query query = getContext().getSession().createQuery(hqlQuery.getHql());
        for (Map.Entry<String, Object> entry : hqlQuery.getParameters().entrySet()) {
            if (entry.getValue() instanceof Collection)
                query.setParameterList(entry.getKey(), (Collection) entry.getValue());
            else
                query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.list();
    }

    public void deploy(ProcessDefinition pd) {
        getContext().deployProcessDefinition(pd);
    }

    public void save(ProcessInstance pi) {
        getContext().save(pi);
    }

    public void delete(ProcessInstance pi) {
        // forcing deletion fo process instance variable (jBPM bug)
        if (pi.getContextInstance().hasVariable("TRANSACTION_DATA")) {
            Object variable = pi.getContextInstance().getVariable("TRANSACTION_DATA");
            // setting null because deleteVariable() does not work
            pi.getContextInstance().setVariable("TRANSACTION_DATA", null);
            getContext().getSession().delete(variable);
        }
        getContext().getGraphSession().deleteProcessInstance(pi);
    }

    public void close() {
        if (jbpmContext == null) return;
        jbpmContext.close();
        jbpmContext = null;
    }
}
