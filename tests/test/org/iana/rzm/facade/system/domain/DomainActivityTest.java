package org.iana.rzm.facade.system.domain;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.CommonGuardedSystemTransaction;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/**
 * Tests domain 'state' attribute which says whether or not
 * there is a transaction in progress for a given domain.
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true, groups = {"facade-system"})
public class DomainActivityTest extends CommonGuardedSystemTransaction {

    long transactionID;

    protected void initTestData() {
        Domain domain = new Domain("activitytest");
        domain.addNameServer(new Host("host1.host"));
        domain.addNameServer(new Host("host2.host"));
        domain.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain);

        domain = new Domain("activitytest2");
        domain.addNameServer(new Host("host3.host"));
        domain.addNameServer(new Host("host4.host"));
        domain.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain);

        domain = new Domain("activitytest3");
        domain.addNameServer(new Host("host5.host"));
        domain.addNameServer(new Host("host6.host"));
        domain.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain);

        domain = new Domain("activitytest4");
        domain.addNameServer(new Host("host7.host"));
        domain.addNameServer(new Host("host8.host"));
        domain.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain);
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
//        processDAO.deleteAll();
//        for (RZMUser user : userManager.findAll())
//            userManager.delete(user);
//        for (Domain domain : domainManager.findAll())
//            domainManager.delete(domain.getName());
    }

    @Test
    public void testDomainNoActivityBeforeTransaction() throws Exception {
        setDefaultUser();

        IDomainVO domain = getDomain("activitytest");
        assert domain != null && domain.getState() == IDomainVO.State.NO_ACTIVITY;

        closeServices();
    }

    @Test(dependsOnMethods = "testDomainNoActivityBeforeTransaction")
    public void testDomainActivityDuringTransaction() throws Exception {
        setDefaultUser();

        IDomainVO domain = getDomain("activitytest");
        domain.setRegistryUrl("activitytest.registry.url");
        TransactionVO trans = createTransactions(domain, false).get(0);
        transactionID = trans.getObjId();

        domain = getDomain("activitytest");
        assert domain != null && domain.getState() == IDomainVO.State.OPERATIONS_PENDING;

        closeServices();
    }

    @Test(dependsOnMethods = "testDomainActivityDuringTransaction")
    public void testDomainNoActivityAfterTransaction() throws Exception {
        setDefaultUser();

        transitTransaction(transactionID, "close");

        IDomainVO domain = getDomain("activitytest");
        assert domain != null && domain.getState() == IDomainVO.State.NO_ACTIVITY;

        closeServices();
    }

    @Test
    public void testDomainActivityAfterTransactionUpdate() throws Exception {
        setDefaultUser();

        IDomainVO domain = getDomain("activitytest3");
        domain.setRegistryUrl("activitytest.registry.url");
        TransactionVO trans = createTransactions(domain, false).get(0);

        transitTransactionToState(trans.getTransactionID(), TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION.toString());

        domain = getDomain("activitytest3");
        assert domain.getState() == IDomainVO.State.OPERATIONS_PENDING;
    }

    @Test
    public void testDomainActivityAfterDomainUpdate() throws Exception {
        setDefaultUser();

        IDomainVO domain = getDomain("activitytest4");
        domain.setRegistryUrl("activitytest.registry.url");
        createTransactions(domain, false).get(0);

        domain = getDomain("activitytest4");
        assert domain.getState() == IDomainVO.State.OPERATIONS_PENDING;

        domain.setIanaCode("xyz");
        GuardedAdminDomainServiceBean.updateDomain(domain);

        domain = getDomain("activitytest4");
        assert domain.getState() == IDomainVO.State.OPERATIONS_PENDING;
    }

    @Test
    public void testDomainNoActivityAfterCompletion() throws Exception {
        setDefaultUser();

        IDomainVO domain = getDomain("activitytest2");
        domain.setRegistryUrl("activitytest.registry.url");
        TransactionVO trans = createTransactions(domain, false).get(0);
        long transactionID = trans.getObjId();
        transitTransactionToState(transactionID, "COMPLETED");

        domain = getDomain("activitytest2");
        assert domain != null && domain.getState() == IDomainVO.State.NO_ACTIVITY;

        closeServices();
    }

    @Test
    public void testDomainNoActivityAfterWithdrawal() throws Exception {
        setDefaultUser();

        IDomainVO domain = getDomain("activitytest2");
        domain.setRegistryUrl("activitytest.registry.url");
        TransactionVO trans = createTransactions(domain, false).get(0);
        long transactionID = trans.getObjId();
        transitTransactionToState(transactionID, "WITHDRAWN");

        domain = getDomain("activitytest2");
        assert domain != null && domain.getState() == IDomainVO.State.NO_ACTIVITY;

        closeServices();
    }
}
