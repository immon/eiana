package org.iana.rzm.facade.system.trans;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;

/**
 * The test of set-up of created date/created by and modified date/modified by.
 *
 * @author Patrycja Wegrzynowicz
 */
public class TransactionTimestampSetupTest extends CommonGuardedSystemTransaction {
    RZMUser iana;

    @BeforeClass
    public void init() {
        iana = new RZMUser("fn", "ln", "org", "iana", "iana@nowhere", "", false);
        iana.addRole(new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(iana);
        Domain domain = new Domain("timestampsetup");
        domain.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain);

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();
    }

    @Test
    public void testTransactionCreatedDate() throws Exception {
        IDomainVO domain = getDomain("timestampsetup", iana);
        domain.setRegistryUrl("timestampsetup.registry.url");

        setGSTSAuthUser(iana);
        List<TransactionVO> trans = gsts.createTransactions(domain, false);
        assert trans != null && trans.size() == 1;

        TransactionVO tran = trans.get(0);
        assert tran.getCreated() != null && "iana".equals(tran.getCreatedBy());

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
}
