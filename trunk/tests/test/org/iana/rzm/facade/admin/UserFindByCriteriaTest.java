package org.iana.rzm.facade.admin;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.admin.users.AdminUserService;
import org.iana.rzm.facade.admin.users.UserCriteria;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.SystemRole;
import org.iana.criteria.Like;
import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.iana.criteria.And;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import org.testng.Assert;

import java.util.List;

/**
 * It tests find by criteria method from the AdminUserServiceBean.
 *
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true)
public class UserFindByCriteriaTest {

    private AdminUserService userService;

    private UserManager userManager;

    @BeforeClass
    public void setUp() {
        ApplicationContext ctx = SpringApplicationContext.getInstance().getContext();
        userService = (AdminUserService) ctx.getBean("GuardedAdminUserServiceBean");
        userManager = (UserManager) ctx.getBean("userManager");

        // create test data
        RZMUser admin = new RZMUser("an", "an", "aorg", "admin", "aemail", "apwd", false);
        admin.addRole(new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(admin);

        RZMUser user1 = new RZMUser("fn1", "ln1", "org1", "user1", "email1", "pwd1", false);
        user1.addRole(new SystemRole(SystemRole.SystemType.AC, "tld1"));
        user1.addRole(new SystemRole(SystemRole.SystemType.TC, "tld3"));
        userManager.create(user1);

        RZMUser user2 = new RZMUser("fn2", "ln2", "org2", "user2", "email2", "pwd2", false);
        user2.addRole(new SystemRole(SystemRole.SystemType.TC, "tld2"));
        userManager.create(user2);

        // set up the authenticated user
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(admin)).getAuthUser();
        userService.setUser(testAuthUser);
    }

    @AfterClass
    public void cleanUp() {
        userManager.deleteAll();
    }

    @Test
    public void testFindByLikeName() throws Exception {
        Criterion like = new Like(UserCriteria.FIRST_NAME, "f%");
        List<UserVO> users = userService.find(like);
        assert users.size() == 2;
    }

    @Test
    public void testFindByEqualRoleType() throws Exception {
        Criterion systemRole = new Equal(UserCriteria.ROLE, UserCriteria.SYSTEM_ROLE);
        Criterion systemType = new Equal(UserCriteria.ROLE_TYPE, SystemRoleVO.SystemType.AC);
        Criterion and = new And(systemRole, systemType);
        List<UserVO> users = userService.find(and);
        System.out.println(users.size());
        assert users.size() == 1;
        Assert.assertEquals("user1", users.get(0).getUserName());
    }

    @Test
    public void testFindByEqualDomainName() throws Exception {
        Criterion systemRole = new Equal(UserCriteria.ROLE, UserCriteria.SYSTEM_ROLE);
        Criterion systemDomain = new Equal(UserCriteria.ROLE_DOMAIN, "tld2");
        Criterion and = new And(systemRole, systemDomain);
        List<UserVO> users = userService.find(and);
        System.out.println(users.size());
        assert users.size() == 1;
        Assert.assertEquals("user2", users.get(0).getUserName());
    }
}
