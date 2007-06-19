package org.iana.rzm.facade.system.trans;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * The test of set-up of created date/created by and modified date/modified by.
 *
 * @author Patrycja Wegrzynowicz
 */

@Test(sequential = true, groups = {"facade-system", "TransactionTimestampSetupTest"})
public class TransactionTimestampSetupTest extends CommonGuardedSystemTransaction {
    @BeforeClass
    public void init() {
        Domain domain = new Domain("timestampsetup");
        domain.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain);

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();
    }

    @Test
    public void testTransactionCreatedDate() throws Exception {
        setDefaultUser();

        IDomainVO domain = getDomain("timestampsetup");
        domain.setRegistryUrl("timestampsetup.registry.url");

        List<TransactionVO> trans = createTransactions(domain, false);
        assert trans != null && trans.size() == 1;

        TransactionVO tran = trans.get(0);
        assert tran.getCreated() != null && getDefaultUser().getLoginName().equals(tran.getCreatedBy());

        closeServices();
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
