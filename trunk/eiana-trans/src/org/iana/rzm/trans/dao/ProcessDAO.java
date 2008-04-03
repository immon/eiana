package org.iana.rzm.trans.dao;

import org.iana.criteria.Criterion;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.trans.TransactionData;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public interface ProcessDAO {
    public ProcessInstance getProcessInstance(final long processInstanceId);

    public ProcessInstance newProcessInstance(final String name);

    public ProcessInstance newProcessInstanceAndSignal(final String name);

    public ProcessInstance newProcessInstance(final String name, final TransactionData data);

    public ProcessInstance newProcessInstanceForUpdate(final String name);

    public List<ProcessInstance> find(Criterion criteria);

    public List<ProcessInstance> find(final Criterion criteria, final int offset, final int limit);

    public int count(final Criterion criteria);

    public List<ProcessInstance> findAll();

    public void deleteAll();

    public List<ProcessInstance> findOpenProcessInstances(final String domainName);

    public List<ProcessInstance> findAllProcessInstances(final String domainName);

    public List<ProcessInstance> findAllProcessInstances(final RZMUser user);

    public List<ProcessInstance> findAllProcessInstances(final RZMUser user, final String domainName);

    public List<ProcessInstance> find(ProcessCriteria criteria);

    public void deployIfProcessDoesNotExist(ProcessDefinition pd);

    public void deploy(final ProcessDefinition pd);

    public void save(ProcessInstance pi);

    public void delete(ProcessInstance pi);

    public void signal(ProcessInstance pi);

    public void signal(ProcessInstance pi, String transitionName);

    public void close();
}
