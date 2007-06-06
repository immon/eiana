package org.iana.rzm.facade.system.domain;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.test.spring.TransactionalSpringContextTests;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true)
public class SystemDomainServiceTest extends TransactionalSpringContextTests {
    protected SystemDomainService SystemDomainServiceBean;
    protected UserManager userManager;
    protected DomainManager domainManager;

    private static final String USER_NAME_BASE = "domsertes";
    private static final String DOMAIN_NAME_1 = "domainservicetest1";
    private static final String DOMAIN_NAME_2 = "domainservicetest2";

    public SystemDomainServiceTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() {
        createDomain(DOMAIN_NAME_1);
        createDomain(DOMAIN_NAME_2);
        for (int i = 0; i < 6; i++) {
            RZMUser user = createUser(USER_NAME_BASE + i);
            if (i < 4)
                user.addRole(new SystemRole(SystemRole.SystemType.AC, DOMAIN_NAME_1, true, true));
            if (i > 1)
                user.addRole(new SystemRole(SystemRole.SystemType.TC, DOMAIN_NAME_2, true, true));
        }
    }

    private RZMUser createUser(String name) {
        RZMUser user = new RZMUser();
        user.setLoginName(name);
        user.setFirstName(name + "fn");
        user.setLastName(name + "ln");
        user.setEmail(name + "@some.com");
        userManager.create(user);
        return user;
    }

    private Domain createDomain(String name) {
        Domain domain = new Domain(name);
        domain.setSupportingOrg(new Contact("supportOrg"));
        Contact tech = new Contact("tech");
        tech.setEmail("tech@" + name + ".org");
        domain.setTechContact(tech);
        Contact admin = new Contact("admin");
        admin.setEmail("admin@" + name + ".org");
        domain.setAdminContact(admin);
        domainManager.create(domain);
        return domain;
    }

    @Test
    public void testSetAccessToDomain() {
        RZMUser[] users = getUsers();
        Domain domain1 = domainManager.get(DOMAIN_NAME_1);
        Domain domain2 = domainManager.get(DOMAIN_NAME_2);
        SystemDomainServiceBean.setAccessToDomain(users[0].getObjId(), domain1.getObjId(), true);
        SystemDomainServiceBean.setAccessToDomain(users[2].getObjId(), domain1.getObjId(), true);
        SystemDomainServiceBean.setAccessToDomain(users[2].getObjId(), domain2.getObjId(), true);
        SystemDomainServiceBean.setAccessToDomain(users[4].getObjId(), domain2.getObjId(), true);

        users = getUsers();
        assertUserRoles(users[0], 1, true);
        assertUserRoles(users[1], 1, false);
        assertUserRoles(users[2], 2, true);
        assertUserRoles(users[3], 2, false);
        assertUserRoles(users[4], 1, true);
        assertUserRoles(users[5], 1, false);
    }

    private void assertUserRoles(RZMUser user, int roleCount, boolean access) {
        assert user.getRoles().size() == roleCount;
        for (Role role : user.getRoles()) {
            assert !role.isAdmin();
            SystemRole systemRole = (SystemRole) role;
            assert systemRole.isAccessToDomain() == access;
        }
    }

    private RZMUser[] getUsers() {
        RZMUser users[] = new RZMUser[6];
        for (int i = 0; i < 6; i++)
            users[i] = userManager.get(USER_NAME_BASE + i);
        return users;
    }

    List<String> VALID_DOMAIN_1_ACCESS_USER_NAMES = Arrays.asList(USER_NAME_BASE + "0", USER_NAME_BASE + "2");
    List<String> VALID_DOMAIN_1_USER_NAMES = Arrays.asList(USER_NAME_BASE + "0", USER_NAME_BASE + "1",
            USER_NAME_BASE + "2", USER_NAME_BASE + "3");
    List<String> VALID_DOMAIN_2_ACCESS_USER_NAMES = Arrays.asList(USER_NAME_BASE + "2", USER_NAME_BASE + "4");
    List<String> VALID_DOMAIN_2_USER_NAMES = Arrays.asList(USER_NAME_BASE + "2", USER_NAME_BASE + "3",
            USER_NAME_BASE + "4", USER_NAME_BASE + "5");

    @Test(dependsOnMethods = "testSetAccessToDomain")
    public void testFindDomainUsers() {
        List<UserVO> domain1AcessUsers = SystemDomainServiceBean.findDomainUsers(DOMAIN_NAME_1, true);
        List<String> domain1AcessUsersNames = getUserNames(domain1AcessUsers);
        assert VALID_DOMAIN_1_ACCESS_USER_NAMES.equals(domain1AcessUsersNames) : "unexpected domain 1 users: " + domain1AcessUsersNames;

        List<UserVO> domain1Users = SystemDomainServiceBean.findDomainUsers(DOMAIN_NAME_1, false);
        List<String> domain1UsersNames = getUserNames(domain1Users);
        assert VALID_DOMAIN_1_USER_NAMES.equals(domain1UsersNames) : "unexpected domain 1 users: " + domain1UsersNames;

        List<UserVO> domain2AccessUsers = SystemDomainServiceBean.findDomainUsers(DOMAIN_NAME_2, true);
        List<String> domain2AccessUsersNames = getUserNames(domain2AccessUsers);
        assert VALID_DOMAIN_2_ACCESS_USER_NAMES.equals(domain2AccessUsersNames) : "unexpected domain 2 users: " + domain2AccessUsersNames;

        List<UserVO> domain2Users = SystemDomainServiceBean.findDomainUsers(DOMAIN_NAME_2, false);
        List<String> domain2UsersNames = getUserNames(domain2Users);
        assert VALID_DOMAIN_2_USER_NAMES.equals(domain2UsersNames) : "unexpected domain 2 users: " + domain2UsersNames;
    }

    private List<String> getUserNames(List<UserVO> users) {
        List<String> result = new ArrayList<String>();
        for (UserVO user : users)
            result.add(user.getUserName());
        return result;
    }

    protected void cleanUp() {
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }
}
