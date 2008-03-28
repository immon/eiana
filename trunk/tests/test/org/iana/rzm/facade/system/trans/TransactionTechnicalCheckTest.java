package org.iana.rzm.facade.system.trans;

import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.facade.system.domain.vo.HostVO;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.user.RZMUser;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionTechnicalCheckTest extends CommonGuardedSystemTransaction {

    @BeforeClass
    public void init() {
        Domain domain1 = new Domain("technicalcheck");
        domain1.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain1);

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();
    }

    @Test(expectedExceptions = DNSTechnicalCheckException.class)
    public void testMinNumberOfNameServersTest() throws Exception {
        IDomainVO domain = getDomain("technicalcheck");
        domain.getNameServers().add(new HostVO("host"));

        setDefaultUser();
        gsts.createTransactions(domain, false, "", true, "");
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        processDAO.deleteAll();
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }
}
