package org.iana.rzm.trans.dao;

import org.hibernate.Query;
import org.iana.rzm.trans.jbpm.JbpmContextFactory;
import org.jbpm.JbpmContext;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class JbpmProcessDAO implements ProcessDAO {
    private JbpmContextFactory jbpmContextFactory;
    private JbpmContext jbpmContext;

    public JbpmContext getContext() {
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
                        "   and domain.name.name = :name"
        );
        query.setString("name", domainName);
        return query.list();
    }

    public void deploy(final ProcessDefinition pd) {
        getContext().deployProcessDefinition(pd);
    }

    public void save(ProcessInstance pi) {
        getContext().save(pi);
    }

    public void close() {
        if (jbpmContext == null) return;
        jbpmContext.close();
        jbpmContext = null;
    }

    public GraphSession getGraphSession() {
        return jbpmContext.getGraphSession();
    }
}
