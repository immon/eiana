package org.iana.rzm.facade.system.domain;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.auth.AuthenticationService;
import org.iana.rzm.user.UserManager;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.Test;

/**
 * It tests a domain access flag set for a user.
 *
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true)
public class DomainAccessTest {

    ApplicationContext context = SpringApplicationContext.getInstance().getContext();
    AuthenticationService authService = (AuthenticationService) context.getBean("authenticationServiceBean");
    SystemDomainService domainService = (SystemDomainService) context.getBean("GuardedSystemDomainService");
    UserManager userManager = (UserManager) context.getBean("userManager");
    DomainManager domainManager = (DomainManager) context.getBean("domainManager");

/*
    @BeforeClass
    public void init() {
        RZMUser user1 = new RZMUser();
        user1.setLoginName("domainaccess-user1");
        user1.addRole(new SystemRole(SystemRole.SystemType.AC, "domainaccess1", true, true));
        user1.addRole(new SystemRole(SystemRole.SystemType.AC, "domainaccess2", true, true));
        user1.addRole(new SystemRole(SystemRole.SystemType.TC, "domainaccess2", true, true));
        user1.addRole(new SystemRole(SystemRole.SystemType.TC, "domainaccess3", true, true));
        userManager.create(user1);
        RZMUser user2 = new RZMUser();
        user2.setLoginName("domainaccess-user2");
        user2.addRole(new SystemRole(SystemRole.SystemType.AC, "domainaccess1", true, true));
        userManager.create(user2);
        RZMUser user3 = new RZMUser();
        user3.setLoginName("iana");
        user3.addRole(new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(user3);
        domainManager.create(new Domain("domainaccess1"));
        domainManager.create(new Domain("domainaccess2"));
    }

    public void testFindUserDomainsDefaultAccess() throws Exception {
        setUser("domainaccess-user2");
        List<SimpleDomainVO> domains = domainService.findUserDomains();
        assert domains.size() == 1;
    }

    public void testUserDefaultAccess() throws Exception {
        setUser("domainaccess-user2");
        UserVO user = domainService.getUser();
        assert user.hasAccessToDomain("domainaccess1");
    }

    public void testFindUserDomainsAllAccess() throws Exception {
        setUser("iana");
        setAccessToDomain("domainaccess-user1", "domainaccess1", true);
        setAccessToDomain("domainaccess-user1", "domainaccess2", true);
        setUser("domainaccess-user1");
        List<SimpleDomainVO> domains = domainService.findUserDomains();
        assert domains.size() == 2;
    }

    public void testUserAllAccess() throws Exception {
        setUser("iana");
        setAccessToDomain("domainaccess-user1", "domainaccess1", true);
        setAccessToDomain("domainaccess-user1", "domainaccess2", true);
        setUser("domainaccess-user1");
        UserVO user = domainService.getUser();
        assert user.hasAccessToDomain("domainaccess1");
        assert user.hasAccessToDomain("domainaccess2");
    }

    public void testFindUserDomainsAccess1() throws Exception {
        setUser("iana");
        setAccessToDomain("domainaccess-user1", "domainaccess1", true);
        setAccessToDomain("domainaccess-user1", "domainaccess2", false);
        setUser("domainaccess-user1");
        List<SimpleDomainVO> domains = domainService.findUserDomains();
        assert domains.size() == 1;
    }

    public void testUserAccess1() throws Exception {
        setUser("iana");
        setAccessToDomain("domainaccess-user1", "domainaccess1", true);
        setAccessToDomain("domainaccess-user1", "domainaccess2", false);
        setUser("domainaccess-user1");
        UserVO user = domainService.getUser();
        assert user.hasAccessToDomain("domainaccess1");
        assert !user.hasAccessToDomain("domainaccess2");
    }

    public void testFindUserDomainsAccess2() throws Exception {
        setUser("iana");
        setAccessToDomain("domainaccess-user1", "domainaccess1", false);
        setAccessToDomain("domainaccess-user1", "domainaccess2", true);
        setUser("domainaccess-user1");
        List<SimpleDomainVO> domains = domainService.findUserDomains();
        assert domains.size() == 1;
    }

    public void testUserAccess2() throws Exception {
        setUser("iana");
        setAccessToDomain("domainaccess-user1", "domainaccess1", false);
        setAccessToDomain("domainaccess-user1", "domainaccess2", true);
        setUser("domainaccess-user1");
        UserVO user = domainService.getUser();
        assert !user.hasAccessToDomain("domainaccess1");
        assert user.hasAccessToDomain("domainaccess2");
    }

    public void testFindUserDomainsNoAccess() throws Exception {
        setUser("iana");
        setAccessToDomain("domainaccess-user1", "domainaccess1", false);
        setAccessToDomain("domainaccess-user1", "domainaccess2", false);
        setUser("domainaccess-user1");
        List<SimpleDomainVO> domains = domainService.findUserDomains();
        assert domains.size() == 0;
    }

    public void testUserNoAccess() throws Exception {
        setUser("iana");
        setAccessToDomain("domainaccess-user1", "domainaccess1", false);
        setAccessToDomain("domainaccess-user1", "domainaccess2", false);
        setUser("domainaccess-user1");
        UserVO user = domainService.getUser();
        assert !user.hasAccessToDomain("domainaccess1");
        assert !user.hasAccessToDomain("domainaccess2");
    }

    private void setUser(String userName) throws Exception {
        domainService.setUser(authService.authenticate(new PasswordAuth(userName, "")));
    }

    private void setAccessToDomain(String userName, String domainName, boolean access) throws InfrastructureException {
        domainService.setAccessToDomain(userManager.get(userName).getObjId(), domainManager.get(domainName).getObjId(), access);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        userManager.delete("domainaccess-user1");
        userManager.delete("domainaccess-user2");

        domainManager.delete("domainaccess1");
        domainManager.delete("domainaccess2");
    }
*/
}
