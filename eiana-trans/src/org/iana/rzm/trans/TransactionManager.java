package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface TransactionManager {

    Transaction get(long id) throws TransactionException;

    List<Transaction> create(Domain domain) throws TransactionException;

    List<Transaction> findAll() throws TransactionException;

    List<Transaction> find(TransactionCriteria criteria) throws TransactionException;
}
