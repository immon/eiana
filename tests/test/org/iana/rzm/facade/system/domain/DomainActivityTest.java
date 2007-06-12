package org.iana.rzm.facade.system.domain;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.iana.rzm.facade.system.trans.CommonGuardedSystemTransaction;
import org.iana.rzm.facade.system.trans.TransactionVO;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.PasswordAuth;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Host;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;

/**
 * Tests domain 'state' attribute which says whether or not
 * there is a transaction in progress for a given domain.
 *
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"facade-system"})
public class DomainActivityTest extends CommonGuardedSystemTransaction {

    RZMUser iana;
    long transactionID;

    @BeforeClass
    public void init() {
        iana = new RZMUser("fn", "ln", "org", "iana", "iana@nowhere", "", false);
        iana.addRole(new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(iana);
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
        IDomainVO domain = getDomain("activitytest", iana);
        assert domain != null && domain.getState() == IDomainVO.State.NO_ACTIVITY;
    }

    @Test (dependsOnMethods="testDomainNoActivityBeforeTransaction")
    public void testDomainActivityDuringTransaction() throws Exception {
        IDomainVO domain = getDomain("activitytest", iana);
        domain.setRegistryUrl("activitytest.registry.url");
        setGSTSAuthUser(iana);
        TransactionVO trans = gsts.createTransactions(domain, false).get(0);
        transactionID = trans.getObjId();

        domain = getDomain("activitytest", iana);
        assert domain != null && domain.getState() == IDomainVO.State.OPERATIONS_PENDING;
    }

    @Test (dependsOnMethods="testDomainActivityDuringTransaction")
    public void testDomainNoActivityAfterTransaction() throws Exception {
        setGSTSAuthUser(iana);
        gsts.transitTransaction(transactionID, "close");

        IDomainVO domain = getDomain("activitytest", iana);
        assert domain != null && domain.getState() == IDomainVO.State.NO_ACTIVITY;
    }

    @Test
    public void testDomainNoActivityAfterCompletion() throws Exception {
        setUser("iana");
        IDomainVO domain = getDomain("activitytest2", iana);
        domain.setRegistryUrl("activitytest.registry.url");
        TransactionVO trans = gsts.createTransactions(domain, false).get(0);
        long transactionID = trans.getObjId();
        ats.updateTransaction(transactionID, 0L, "COMPLETED", false);

        List<TransactionVO> open = gsts.findOpenTransactions();
        assert open.size() == 0;

        domain = getDomain("activitytest2", iana);
        assert domain != null && domain.getState() == IDomainVO.State.NO_ACTIVITY;
    }

    @Test
    public void testDomainNoActivityAfterWithdrawal() throws Exception {
        setUser("iana");
        IDomainVO domain = getDomain("activitytest2", iana);
        domain.setRegistryUrl("activitytest.registry.url");
        TransactionVO trans = gsts.createTransactions(domain, false).get(0);
        long transactionID = trans.getObjId();
        ats.updateTransaction(transactionID, 0L, "WITHDRAWN", false);

        domain = getDomain("activitytest2", iana);
        assert domain != null && domain.getState() == IDomainVO.State.NO_ACTIVITY;
    }

    private void setUser(String loginName) throws Exception {
        AuthenticatedUser user = authService.authenticate(new PasswordAuth(loginName, ""));
        ats.setUser(user);
        gsts.setUser(user);
    }
}
