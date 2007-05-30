package org.iana.rzm.web.services;

import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.model.DomainVOWrapper;
import org.iana.rzm.web.model.TransactionVOWrapper;

import java.util.List;

public interface RzmServices {

    public void changePassword(long userId, String newPassword);

    public List<TransactionVOWrapper> getOpenTransaction() throws NoObjectFoundException;

    public TransactionVOWrapper getTransaction(long id) throws NoObjectFoundException;

    public DomainVOWrapper getDomain(long domainId) throws NoObjectFoundException;

    public String getCountryName(String domainCode);

    public int getTotalTransactionCount();

    public List<TransactionVOWrapper> getTransactions();
}
