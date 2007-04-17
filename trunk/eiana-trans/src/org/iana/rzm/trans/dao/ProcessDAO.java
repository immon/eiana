package org.iana.rzm.trans.dao;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.iana.rzm.user.RZMUser;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public interface ProcessDAO {
    public ProcessInstance getProcessInstance(final long processInstanceId);
    public ProcessInstance newProcessInstance(final String name);
    public ProcessInstance newProcessInstanceForUpdate(final String name);
    public List<ProcessInstance> findAll(); 
    public List<ProcessInstance> findAllProcessInstances(final String domainName);
    public List<ProcessInstance> findAllProcessInstances(final RZMUser user);
    public List<ProcessInstance> findAllProcessInstances(final RZMUser user, final String domainName);
    public List<ProcessInstance> find(ProcessCriteria criteria);
    public void deploy(final ProcessDefinition pd);
    public void save(ProcessInstance pi);
    public void delete(ProcessInstance pi);
    public void close();
}
