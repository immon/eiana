package org.iana.rzm.facade.system.trans;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.facade.system.domain.vo.HostVO;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * It tests a split performed on a user demand.
 * (Name server changes and other changes in a single or separated transactions).
 *
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"facade-system", "UserSplitTransactionTest"})
public class UserSplitTransactionTest extends CommonGuardedSystemTransaction {

    protected void initTestData() {
        Domain domain = new Domain("usersplittest");
        domain.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain);

    }

    @Test
    public void testSingleTransaction() throws Exception {
        setDefaultUser();

        IDomainVO domain = getDomain("usersplittest");
        domain.getNameServers().add(new HostVO("usersplittest.host"));
        domain.setRegistryUrl("usersplittest.registry.url");

        List<TransactionVO> trans = createTransactions(domain, false);

        assert trans != null && trans.size() == 1;

        closeServices();
    }

    @Test
    public void testSeparatedTransaction() throws Exception {
        setDefaultUser();

        IDomainVO domain = getDomain("usersplittest");
        domain.getNameServers().add(new HostVO("usersplittest.host"));
        domain.setRegistryUrl("usersplittest.registry.url");

        List<TransactionVO> trans = createTransactions(domain, true);

        assert trans != null && trans.size() == 2;
        
        closeServices();
    }

    @AfterMethod(alwaysRun = true)
    public void deleteTransactions() {
        processDAO.deleteAll();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
//        for (RZMUser user : userManager.findAll())
//            userManager.delete(user);
//        for (Domain domain : domainManager.findAll())
//            domainManager.delete(domain.getName());
    }
}
