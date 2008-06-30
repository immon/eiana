package org.iana.rzm.facade.system.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.PasswordAuth;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DisabledUserTest extends CommonGuardedSystemTransaction {

    long domain1;

    long domain2;

    protected void initTestData() {
        Domain domain1 = new Domain("disableddomain1");
        domainManager.create(domain1);
        this.domain1 = domain1.getObjId();

        Domain domain2 = new Domain("disableddomain2");
        domainManager.create(domain2);
        this.domain2 = domain2.getObjId();

        RZMUser user1 = new RZMUser("fn", "ln", "org", "login1", "email@example.tld", "pwd", false);
        user1.addRole(new SystemRole(SystemRole.SystemType.AC, "disableddomain1"));
        user1.addRole(new SystemRole(SystemRole.SystemType.TC, "disableddomain1"));
        userManager.create(user1);

        RZMUser user2 = new RZMUser("fn", "ln", "org", "login2", "email@example.tld", "pwd", false);
        user1.addRole(new SystemRole(SystemRole.SystemType.AC, "disableddomain1"));
        user1.addRole(new SystemRole(SystemRole.SystemType.AC, "disableddomain2"));
        userManager.create(user2);
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

    @Test(expectedExceptions = {AccessDeniedException.class})
    public void testCreateAfterDisabled() throws Exception {
        AuthenticatedUser user  = authenticationServiceBean.authenticate(new PasswordAuth("login1", "pwd"));

        setUser(user);

        // has access
        createTransaction("disableddomain1");
        deleteTransactions();

        // remove access
        GuardedSystemDomainService.setAccessToDomain(user.getObjId(), domain1, false);

        // does not have access
        createTransaction("disableddomain1");
    }


    private List<TransactionVO> createTransaction(String domainName) throws Exception {
        setDefaultUser();
        try {
            IDomainVO domain = getDomain(domainName);
            domain.setRegistryUrl("" + new Random().nextLong());
            return createTransactions(domain, false);
        } finally {
            closeServices();
        }
    }

}

