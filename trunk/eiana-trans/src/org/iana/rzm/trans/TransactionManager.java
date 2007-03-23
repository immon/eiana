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
     * @throws NoSuchTransactionException thrown when no transaction found
     */
    Transaction getTransaction(long id) throws NoSuchTransactionException;

    // Transaction createTransaction(List<TransactionAction> actions);
    
    /**
     * Creates a new domain creation transaction.
     *
     * @param domain a domain to be created by the end of the transaction
     * @return the new domain creation transaction
     */
    Transaction createDomainCreationTransaction(Domain domain);

    /**
     * Creates a new domain modification transaction.
     *
     * @param domain a domain to be created by the end of the transaction
     * @return the new domain creation transaction
     */
    Transaction createDomainModificationTransaction(Domain domain);

    List<Transaction> findAll();

    List<Transaction> find(TransactionCriteria criteria);

    public List<Transaction> findAllProcessInstances(String domainName);

    //TEMPORARY METHOD, should be deleted later, when JbpmContext & spring problem will be reslowed.
    public void setJBPMContext(JbpmContext ctx);
}
