package org.iana.rzm.facade.system.trans;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.user.RZMUser;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"facade-system"})
public class TransactionWithdrawalTest extends CommonGuardedSystemTransaction {

    @BeforeClass
    public void init() {
        Domain domain1 = new Domain("withdraw");
        domain1.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain1);

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();
    }

    @Test
    public void testWithdrawAllowed() throws Exception {
        IDomainVO domain = getDomain("withdraw");
        domain.setRegistryUrl("withdrawtest.registry.url");

        setDefaultUser();
        TransactionVO trans = gsts.createTransactions(domain, false).get(0);

        gsts.withdrawTransaction(trans.getTransactionID());

        TransactionVO ret = gsts.get(trans.getTransactionID());
        assert TransactionStateVO.Name.WITHDRAWN.toString().equals(ret.getState().getName().toString());

        closeServices();
    }

    @Test
    public void testWithdrawNotAllowed() throws Exception {
    }

    @AfterClass(alwaysRun = true)
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
}
