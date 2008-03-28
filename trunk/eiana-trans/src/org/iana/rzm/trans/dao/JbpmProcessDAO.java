package org.iana.rzm.trans.dao;

import org.hibernate.Query;
import org.iana.criteria.Criterion;
import org.iana.rzm.trans.process.general.springsupport.JbpmContextFactory;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.Transaction;
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
/*
    private JbpmContext jbpmContext;

    private JbpmContext ctx {
        if (jbpmContext == null)
            jbpmContext = jbpmContextFactory.getJbpmContext();
        return jbpmContext;
    }
*/

    public JbpmProcessDAO(JbpmContextFactory jbpmContextFactory) {
        this.jbpmContextFactory = jbpmContextFactory;
    }

    public ProcessInstance getProcessInstance(final long processInstanceId) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            return ctx.getProcessInstance(processInstanceId);
        } finally {
            ctx.close();
        }
    }

    public ProcessInstance newProcessInstance(final String name) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            return ctx.newProcessInstance(name);
        } finally {
            ctx.close();
        }
    }

    public ProcessInstance newProcessInstance(final String name, final TransactionData data) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            ProcessInstance instance =  ctx.newProcessInstance(name);
            instance.getContextInstance().setVariable(Transaction.TRANSACTION_DATA, data);
            return instance;
        } finally {
            ctx.close();
        }
    }

    public ProcessInstance newProcessInstanceForUpdate(final String name) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            return ctx.newProcessInstanceForUpdate(name);
        } finally {
            ctx.close();
        }
    }

    public List<ProcessInstance> findAll() {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            return ctx.getSession().createQuery("from ProcessInstance").list();
        } finally {
            ctx.close();
        }
    }

    public void deleteAll() {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            List<ProcessInstance> list = ctx.getSession().createQuery("from ProcessInstance").list();
            for (ProcessInstance inst : list) {
                deleteProcessInstance(inst, ctx);
            }
        } finally {
            ctx.close();
        }
    }

    public List<ProcessInstance> find(final Criterion criteria) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            JbpmProcessCriteriaTranslator translator = new JbpmProcessCriteriaTranslator(criteria);
            Query query = ctx.getSession().createQuery(translator.getHQL());
            int idx = 0;
            for (Object param : translator.getParams()) {
                query.setParameter(idx++, param);
            }
            return query.list();
        } finally {
            ctx.close();
        }
    }

    public List<ProcessInstance> find(final Criterion criteria, final int offset, final int limit) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            JbpmProcessCriteriaTranslator translator = new JbpmProcessCriteriaTranslator(criteria);
            Query query = ctx.getSession().createQuery(translator.getHQL());
            int idx = 0;
            for (Object param : translator.getParams()) {
                query.setParameter(idx++, param);
            }
            query.setFirstResult(offset)
                    .setMaxResults(limit)
                    .setFetchSize(limit);
            return query.list();
        } finally {
            ctx.close();
        }
    }

    public int count(final Criterion criteria) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            JbpmProcessCriteriaTranslator translator = new JbpmProcessCriteriaTranslator(criteria);
            Query query = ctx.getSession().createQuery(translator.getHQL("select count(*)"));
            int idx = 0;
            for (Object param : translator.getParams()) {
                query.setParameter(idx++, param);
            }
            Long result = (Long) query.uniqueResult();
            return result.intValue();
        } finally {
            ctx.close();
        }
    }

    public List<ProcessInstance> findAllProcessInstances(final String domainName) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            Query query = ctx.getSession().createQuery(
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
        } finally {
            ctx.close();
        }
    }

    public List<ProcessInstance> findOpenProcessInstances(final String domainName) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            Query query = ctx.getSession().createQuery(
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
        } finally {
            ctx.close();
        }
    }

    public List<ProcessInstance> findAllProcessInstances(final RZMUser user) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            if (user == null) throw new IllegalArgumentException("user is null");
            Query query = ctx.getSession().createQuery(
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
        } finally {
            ctx.close();
        }
    }

    public List<ProcessInstance> findAllProcessInstances(final RZMUser user, final String domainName) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            if (user == null) throw new IllegalArgumentException("user is null");
            Query query = ctx.getSession().createQuery(
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
        } finally {
            ctx.close();
        }
    }

    public List<ProcessInstance> find(ProcessCriteria criteria) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            if (criteria == null) throw new IllegalArgumentException("criteria is null");
            HqlQuery hqlQuery = ProcessCriteriaHqlTranslator.translate(criteria);
            System.out.println("HQL: " + hqlQuery.getHql());
            Query query = ctx.getSession().createQuery(hqlQuery.getHql());
            for (Map.Entry<String, Object> entry : hqlQuery.getParameters().entrySet()) {
                if (entry.getValue() instanceof Collection)
                    query.setParameterList(entry.getKey(), (Collection) entry.getValue());
                else
                    query.setParameter(entry.getKey(), entry.getValue());
            }
            return query.list();
        } finally {
            ctx.close();
        }
    }

    public void deploy(ProcessDefinition pd) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            ctx.deployProcessDefinition(pd);
        } finally {
            ctx.close();
        }
    }

    public void deployIfProcessDoesNotExist(ProcessDefinition pd) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            ProcessDefinition def = ctx.getGraphSession().findLatestProcessDefinition(pd.getName());
            if (def == null) {
                ctx.deployProcessDefinition(pd);
            }
        } finally {
            ctx.close();
        }
    }

    public void save(ProcessInstance pi) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            ctx.save(pi);
        } finally {
            ctx.close();
        }
    }

    public void delete(ProcessInstance pi) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            deleteProcessInstance(pi, ctx);
        } finally {
            ctx.close();
        }
    }

    private void deleteProcessInstance(ProcessInstance pi, JbpmContext ctx) {
        // forcing deletion fo process instance variable (jBPM bug)
        if (pi.getContextInstance().hasVariable("TRANSACTION_DATA")) {
            Object variable = pi.getContextInstance().getVariable("TRANSACTION_DATA");
            // setting null because deleteVariable() does not work
            pi.getContextInstance().setVariable("TRANSACTION_DATA", null);
            ctx.getSession().delete(variable);
        }
        ctx.getGraphSession().deleteProcessInstance(pi);
    }


    public void signal(ProcessInstance pi) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            pi.signal();
        } finally {
            ctx.close();
        }
    }

    public void signal(ProcessInstance pi, String transitionName) {
        JbpmContext ctx = jbpmContextFactory.getJbpmContext();
        try {
            pi.signal(transitionName);
        } finally {
            ctx.close();
        }
    }

    public void close() {
/*
        if (jbpmContext == null) return;
        jbpmContext.close();
        jbpmContext = null;
*/
    }
}
