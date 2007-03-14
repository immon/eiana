package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;
import org.jbpm.JbpmContext;

import java.util.List;

/**
 * This interface represents a service for retrieval and creation of domain modification transactions.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface TransactionManager {

    /**
     * Returns a transaction with a given id.
     *
     * @param id the identifier of the transaction to be found
     * @return the found transaction
     * @throws TransactionException
     */
    Transaction get(long id) throws NoSuchTransactionException;

    /**
     * Creates a new transaction based on a
     * @param domain
     * @return
     */
    Transaction create(Domain domain);

    Transaction modify(Domain domain);

    List<Transaction> findAll();

    List<Transaction> find(TransactionCriteria criteria);

    public List<Transaction> findAllProcessInstances(String domainName);

    //TEMPORARY METHOD, should be deleted later, when JbpmContext & spring problem will be reslowed.
    public void setJBPMContext(JbpmContext ctx);
}
