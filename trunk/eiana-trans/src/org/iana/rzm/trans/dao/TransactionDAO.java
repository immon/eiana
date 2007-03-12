package org.iana.rzm.trans.dao;

import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public interface TransactionDAO {
    public List<ProcessInstance> findAllProcessInstances(String domainName);
}
