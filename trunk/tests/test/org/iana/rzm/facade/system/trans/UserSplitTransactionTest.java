package org.iana.rzm.facade.system.trans;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.HostVO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.Transaction;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;
import java.util.Arrays;

/**
 * It tests a split performed on a user demand.
 * (Name server changes and other changes in a single or separated transactions).
 *
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"facade-system"})
public class UserSplitTransactionTest extends CommonGuardedSystemTransaction {

    RZMUser iana;

    @BeforeClass
    public void init() {
        super.init();
        iana = new RZMUser("fn", "ln", "org", "iana", "iana@nowhere", "", false);
        iana.addRole(new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(iana);
        Domain domain = new Domain("usersplittest");
        domain.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain);

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();
    }

    @Test
    public void testSingleTransaction() throws Exception {
        IDomainVO domain = getDomain("usersplittest", iana);
        domain.getNameServers().add(new HostVO("usersplittest.host"));
        domain.setRegistryUrl("usersplittest.registry.url");

        setGSTSAuthUser(iana);
        List<TransactionVO> trans = gsts.createTransactions(domain, false);

        assert trans != null && trans.size() == 1;
    }

    @Test
    public void testSeparatedTransaction() throws Exception {
        IDomainVO domain = getDomain("usersplittest", iana);
        domain.getNameServers().add(new HostVO("usersplittest.host"));
        domain.setRegistryUrl("usersplittest.registry.url");

        setGSTSAuthUser(iana);
        List<TransactionVO> trans = gsts.createTransactions(domain, true);

        assert trans != null && trans.size() == 2;
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
