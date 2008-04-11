package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.CommonGuardedSystemTransaction;
import org.iana.rzm.facade.system.trans.TransactionExistsException;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.user.RZMUser;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

/**
 * It tests that it's not possible to create a new transaction when there exists open transactions for the given domain.
 *
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"facade-system"})
public class MultipleRequestsTest extends CommonGuardedSystemTransaction {

    @BeforeClass
    public void init() throws Exception {
        Domain domain = new Domain("multiplerequest");
        domainManager.create(domain);
        processDAO.deploy(DefinedTestProcess.getDefinition());
    }

    @AfterMethod(alwaysRun = true)
    public void deleteTransactions() {
        processDAO.deleteAll();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }

    @Test(expectedExceptions = {TransactionExistsException.class})
    public void testTransactionExceptionWhenExistsOpenTransaction() throws Exception {
        createTransaction();
        createTransaction();
    }

    @Test
    public void testSuccessfulCreateAfterCompletion() throws Exception {
        createAndTransitTo("COMPLETED");
        List<TransactionVO> list = createTransaction();
        assert list.size() > 0;
    }

    @Test
    public void testSuccessfulCreateAfterWithdrawal() throws Exception {
        createAndTransitTo("WITHDRAWN");
        List<TransactionVO> list = createTransaction();
        assert list.size() > 0;
    }

    @Test
    public void testSuccessfulCreateAfterRejection() throws Exception {
        createAndTransitTo("REJECTED");
        List<TransactionVO> list = createTransaction();
        assert list.size() > 0;
    }

    private List<TransactionVO> createTransaction() throws Exception {
        setDefaultUser();
        try {
            IDomainVO domain = getDomain("multiplerequest");
            domain.setRegistryUrl("" + new Random().nextLong());
            return createTransactions(domain, false);
        } finally {
            closeServices();
        }
    }

    private void createAndTransitTo(String state) throws Exception {
        setDefaultUser();
        try {
            IDomainVO domain = getDomain("multiplerequest");
            domain.setRegistryUrl("new-url");
            List<TransactionVO> list = createTransactions(domain, false);
            for (TransactionVO trans : list) {
                transitTransactionToState(trans.getTransactionID(), state);
            }
        } finally {
            closeServices();
        }
    }
}
