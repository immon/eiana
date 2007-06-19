package org.iana.rzm.facade.system.domain;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.facade.system.trans.CommonGuardedSystemTransaction;
import org.iana.rzm.facade.system.trans.TransactionVO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.user.RZMUser;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests domain 'state' attribute which says whether or not
 * there is a transaction in progress for a given domain.
 *
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"facade-system"})
public class DomainActivityTest extends CommonGuardedSystemTransaction {

    long transactionID;

    @BeforeClass
    public void init() throws Exception {
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

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();
    }

    @AfterClass (alwaysRun = true)
    public void cleanUp() {
        try {
            for (ProcessInstance pi : processDAO.findAll())
                processDAO.delete(pi);
        } finally {
            processDAO.close();
        }
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }

    @Test
    public void testDomainNoActivityBeforeTransaction() throws Exception {
        setDefaultUser();

        IDomainVO domain = getDomain("activitytest");
        assert domain != null && domain.getState() == IDomainVO.State.NO_ACTIVITY;

        closeServices();
    }

    @Test (dependsOnMethods="testDomainNoActivityBeforeTransaction")
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

    @Test (dependsOnMethods="testDomainActivityDuringTransaction")
    public void testDomainNoActivityAfterTransaction() throws Exception {
        setDefaultUser();

        transitTransaction(transactionID, "close");

        IDomainVO domain = getDomain("activitytest");
        assert domain != null && domain.getState() == IDomainVO.State.NO_ACTIVITY;

        closeServices();
    }

    @Test
    public void testDomainNoActivityAfterCompletion() throws Exception {
        setDefaultUser();

        IDomainVO domain = getDomain("activitytest2");
        domain.setRegistryUrl("activitytest.registry.url");
        TransactionVO trans = createTransactions(domain, false).get(0);
        long transactionID = trans.getObjId();
        updateTransaction(transactionID, 0L, "COMPLETED", false);

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
        updateTransaction(transactionID, 0L, "WITHDRAWN", false);

        domain = getDomain("activitytest2");
        assert domain != null && domain.getState() == IDomainVO.State.NO_ACTIVITY;

        closeServices();
    }
}
