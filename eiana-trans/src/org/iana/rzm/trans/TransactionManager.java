package org.iana.rzm.trans;

import org.iana.criteria.Criterion;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.user.RZMUser;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;
import java.util.Set;

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
     * @throws NoModificationException thrown when no modification between the given domain and stored one is discovered
     */
    Transaction createDomainModificationTransaction(Domain domain) throws NoModificationException;

    Transaction createDomainModificationTransaction(Domain domain, String sumbitterEmail) throws NoModificationException;

    List<Transaction> findAll();

    List<Transaction> find(Criterion criteria);

    List<Transaction> find(Criterion criteria, int offset, int limit);

    int count(Criterion criteria);

    List<Transaction> find(TransactionCriteria criteria);

    public List<Transaction> findOpenTransactions(String domainName);

    public List<Transaction> findOpenTransactions(Set<String> domainNames);

    public List<Transaction> findTransactions(String domainName);

    public List<Transaction> findTransactions(Set<String> domainNames);

    public List<Transaction> findTransactions(RZMUser user);

    public List<Transaction> findTransactions(RZMUser user, String domainName);

    public void deleteTransaction(Transaction transaction) throws NoSuchTransactionException;

    public void deleteTransaction(Long transactionId) throws NoSuchTransactionException;
}
