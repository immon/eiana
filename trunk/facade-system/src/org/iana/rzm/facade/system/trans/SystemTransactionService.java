package org.iana.rzm.facade.system.trans;

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.TechnicalCheckException;
import org.iana.rzm.facade.system.domain.DomainVO;

import java.util.List;

/**
 * This interface provides a set of facade method to manipulate domain modification transactions.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface SystemTransactionService extends RZMStatefulService {

    /**
     * Returns a transaction identified by this transaction id. Note that this service is stateful and the user must be set prior any method call.
     *
     * @param id the rzm transaction identifier
     * @return the transaction identified by this id.
     * @throws AccessDeniedException when access is denied to the user or no user is set.
     * @throws NoObjectFoundException when no transaction found about this id.
     * @throws InfrastructureException when an internal error occured during processing of this method.
     */
    TransactionVO getTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    /**
     * Returns a list of transaction actions that are going to be created when this domain is passed to createTransaction method.
     * In other words, it detects all changes between the passed domain and current domain.
     *
     * @param domain the modified domain
     * @return a list of transaction actions representing the changes between the modified and current version of the domain; may be an empty list if no changes detected.
     * @throws AccessDeniedException when access is denied to the user or no user is set.
     * @throws NoObjectFoundException when no domain found with the specified name.
     * @throws InfrastructureException when an internal error occured during processing of this method.
     */
    TransactionActionsVO detectTransactionActions(IDomainVO domain)  throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    void performTransactionTechnicalCheck(IDomainVO domain) throws AccessDeniedException, TechnicalCheckException, InfrastructureException;

    /**
     * Creates a single transaction for all the changes applied to the domain.
     *
     * @param domain the modified domain.
     * @return the created transaction.
     * @throws AccessDeniedException
     * @throws NoObjectFoundException when no domain found with the specified name.
     * @throws InfrastructureException
     */
    TransactionVO createTransaction(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    /**
     * Creates the transaction splitting the transactions based on the splitNameServerChange flag.
     *
     * @param domain the modified domain.
     * @param splitNameServerChange the flag indicating that the transaction is to be splitted if the name servers are modified.
     * @return the created transactions.
     * @throws AccessDeniedException when access is denied to the user or no user is set.
     * @throws NoObjectFoundException when no domain found with the specified name.
     * @throws InfrastructureException when an internal error occured during processing of this method.
     */
    List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    /**
     * Accepts a transaction identified by this id on behalf of the user. Note that this service is stateful and the user must be set prior any method call.
     *
     * @param id the id of the transaction to be accepted.
     * @throws AccessDeniedException when access is denied to the user or no user is set.
     * @throws NoObjectFoundException when no transaction found about this id.
     * @throws InfrastructureException when an internal error occured during processing of this method.
     */
    void acceptTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    /**
     * Rejects a transaction identified by this id on behalf of the user. Note that this service is stateful and the user must be set prior any method call.
     *
     * @param id the id of the transaction to be rejected.
     * @throws AccessDeniedException when access is denied to the user or no user is set.
     * @throws NoObjectFoundException when no transaction found about this id.
     * @throws InfrastructureException when an internal error occured during processing of this method.
     */
    void rejectTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    /**
     * Transits a transaction identified by this id over the specified transition on behalf of the user. Note that this service is stateful and the user must be set prior any method call.
     *
     * @param id the id of the transaction to be transited to the given state.
     * @param transitionName the transition name over which the transaction is to be transited.
     * @throws AccessDeniedException when access is denied to the user or no user is set.
     * @throws NoObjectFoundException when no transaction found about this id.
     * @throws InfrastructureException when an internal error occured during processing of this method.
     */
    void transitTransaction(long id, String transitionName) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    /**
     * Returns a list of open transactions for the domain names administared by the user. Note that this service is stateful and the user must be set prior any method call.
     *
     * @return the transaction identified by this id.
     * @throws AccessDeniedException when access is denied to the user or no user is set.
     * @throws NoObjectFoundException when no transaction found about this id.
     * @throws InfrastructureException when an internal error occured during processing of this method.
     */
    List<TransactionVO> findOpenTransactions() throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    List<TransactionVO> findTransactions(TransactionCriteriaVO criteria) throws AccessDeniedException, InfrastructureException;    
}
