package org.iana.rzm.facade.system.trans;

import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.facade.system.domain.vo.HostVO;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionTechnicalCheckTest extends CommonGuardedSystemTransaction {

    protected void initTestData() {
        Domain domain1 = new Domain("technicalcheck");
        domain1.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain1);

    }

    @Test(expectedExceptions = DNSTechnicalCheckExceptionWrapper.class)
    public void testMinNumberOfNameServersTest() throws Exception {
        IDomainVO domain = getDomain("technicalcheck");
        domain.getNameServers().add(new HostVO("host"));

        setDefaultUser();
        GuardedSystemTransactionService.createTransactions(domain, false, "", true, "");
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
//        processDAO.deleteAll();
//        for (RZMUser user : userManager.findAll())
//            userManager.delete(user);
//        for (Domain domain : domainManager.findAll())
//            domainManager.delete(domain.getName());
    }
}
