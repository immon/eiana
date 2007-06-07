package org.iana.rzm.facade.system.domain;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.admin.AdminUserService;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.auth.AuthenticationService;
import org.iana.rzm.facade.auth.AuthenticationData;
import org.iana.rzm.facade.auth.PasswordAuth;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Domain;
import org.springframework.context.ApplicationContext;

import java.util.List;

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

    @BeforeClass
    public void init() {
        RZMUser user1 = new RZMUser();
        user1.setLoginName("domainaccess-user1");
        user1.addRole(new SystemRole(SystemRole.SystemType.AC, "domainaccess1", true, true));
        user1.addRole(new SystemRole(SystemRole.SystemType.AC, "domainaccess2", true, true));
        user1.addRole(new SystemRole(SystemRole.SystemType.TC, "domainaccess2", true, true));
        userManager.create(user1);
        RZMUser user2 = new RZMUser();
        user2.setLoginName("domainaccess-user2");
        user2.addRole(new SystemRole(SystemRole.SystemType.AC, "domainaccess1", true, true));
        userManager.create(user2);

        domainManager.create(new Domain("domainaccess1"));
        domainManager.create(new Domain("domainaccess2"));
    }

    public void testFindUserDomainsDefaultAccess() throws Exception {
        domainService.setUser(authService.authenticate(new PasswordAuth("domainaccess-user2", "")));
        List<SimpleDomainVO> domains = domainService.findUserDomains();
        assert domains.size() == 1;
    }

    public void testFindUserDomainsAllAccess() throws Exception {
        domainService.setUser(authService.authenticate(new PasswordAuth("domainaccess-user1", "")));
        domainService.setAccessToDomain(userManager.get("domainaccess-user1").getObjId(), domainManager.get("domainaccess1").getObjId(), true);
        domainService.setAccessToDomain(userManager.get("domainaccess-user1").getObjId(), domainManager.get("domainaccess2").getObjId(), true);
        List<SimpleDomainVO> domains = domainService.findUserDomains();
        assert domains.size() == 2;
    }

    public void testFindUserDomainsAccess1() throws Exception {
        domainService.setUser(authService.authenticate(new PasswordAuth("domainaccess-user1", "")));
        domainService.setAccessToDomain(userManager.get("domainaccess-user1").getObjId(), domainManager.get("domainaccess1").getObjId(), true);
        domainService.setAccessToDomain(userManager.get("domainaccess-user1").getObjId(), domainManager.get("domainaccess2").getObjId(), false);
        List<SimpleDomainVO> domains = domainService.findUserDomains();
        assert domains.size() == 1;
    }

    public void testFindUserDomainsAccess2() throws Exception {
        domainService.setUser(authService.authenticate(new PasswordAuth("domainaccess-user1", "")));
        domainService.setAccessToDomain(userManager.get("domainaccess-user1").getObjId(), domainManager.get("domainaccess1").getObjId(), false);
        domainService.setAccessToDomain(userManager.get("domainaccess-user1").getObjId(), domainManager.get("domainaccess2").getObjId(), true);
        List<SimpleDomainVO> domains = domainService.findUserDomains();
        assert domains.size() == 1;
    }

    public void testFindUserDomainsNoAccess() throws Exception {
        domainService.setUser(authService.authenticate(new PasswordAuth("domainaccess-user1", "")));
        domainService.setAccessToDomain(userManager.get("domainaccess-user1").getObjId(), domainManager.get("domainaccess1").getObjId(), false);
        domainService.setAccessToDomain(userManager.get("domainaccess-user1").getObjId(), domainManager.get("domainaccess2").getObjId(), false);
        List<SimpleDomainVO> domains = domainService.findUserDomains();
        assert domains.size() == 0;
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        UserManager userManager = (UserManager) context.getBean("userManager");
        userManager.delete("domainaccess-user1");
        userManager.delete("domainaccess-user2");

        DomainManager domainManager = (DomainManager) context.getBean("domainManager");
        domainManager.delete("domainaccess1");
        domainManager.delete("domainaccess2");
    }
}
