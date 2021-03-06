package org.iana.rzm.facade.system.domain;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.system.domain.vo.DomainVO;
import org.iana.rzm.facade.system.domain.vo.SimpleDomainVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.user.UserManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"failure", "facade-system", "GuardedSystemDomainService"})
public class GuardedSystemDomainServiceFailureTest {
    private SystemDomainService gsds;
    DomainDAO domainDAO;
    private long domainId1;
    private long domainId2;

    Domain domain1, domain2;
    UserManager userManager;

    @BeforeClass
    public void init() throws Exception {
        gsds = (SystemDomainService) SpringApplicationContext.getInstance().getContext().getBean("GuardedSystemDomainService");
        userManager = (UserManager) SpringApplicationContext.getInstance().getContext().getBean("userManager");
    }

    @Test
    public void initDB() {
        domainDAO = (DomainDAO) SpringApplicationContext.getInstance().getContext().getBean("domainDAO");
        domain1 = new Domain("failurefacadesystemiana.org");
        domain1.setWhoisServer("whoIsServer");
        domainDAO.create(domain1);
        domainId1 = domain1.getObjId();
        domain2 = new Domain("failurefacadesystemiana1.org");
        domain2.setWhoisServer("whoIsServer2");
        domainDAO.create(domain2);
        domainId2 = domain2.getObjId();

        userManager.create(UserConverter.convert(generateUser()));
    }

    @Test (expectedExceptions = {AccessDeniedException.class},
            dependsOnMethods = {"initDB"})
    public void testGetDomainByUserNameWhenUserNotSetInService() throws Exception {
        DomainVO domainVO = (DomainVO) gsds.findUserDomains("someUser");
    }

    @Test (expectedExceptions = {AccessDeniedException.class},
            dependsOnMethods = {"testGetDomainByUserNameWhenUserNotSetInService"})
    public void testGetDomainByNameWhenUserNotSetInService() throws Exception {
        DomainVO domainVO = (DomainVO) gsds.getDomain(domainId1);
    }

    @Test (expectedExceptions = {AccessDeniedException.class},
        dependsOnMethods = {"testGetDomainByNameWhenUserNotSetInService"})
    public void testGetDomainByIdWhenUserNotSetInService() throws Exception {
        DomainVO domainVO = (DomainVO) gsds.getDomain(domainId1);
    }

    @Test (expectedExceptions = {AccessDeniedException.class},
            dependsOnMethods = {"testGetDomainByIdWhenUserNotSetInService"})
    public void testGetDomainByWrongId() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain(domainId2);
    }

    @Test (expectedExceptions = {AccessDeniedException.class},
            dependsOnMethods = {"testGetDomainByWrongId"})
    public void testGetDomainByWrongName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain("wrongdomainname.org");
    }

    @Test (expectedExceptions = {AccessDeniedException.class},
            dependsOnMethods = {"testGetDomainByWrongName"})
    public void testFindUserDomainsByWrongUserName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        List<SimpleDomainVO> list = gsds.findUserDomains("anotherUser");
    }

    @Test (expectedExceptions = {IllegalArgumentException.class},
            dependsOnMethods = {"testFindUserDomainsByWrongUserName"})
    public void testGetDomainByNullId() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain(null);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class},
            dependsOnMethods = {"testGetDomainByNullId"})
    public void testGetDomainByNullName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain(null);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class},
            dependsOnMethods = {"testGetDomainByNullName"})
    public void testGetDomainByOutOfRangeId() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain(-1);
    }

    @AfterClass (alwaysRun = true)
    public void cleanUp() {
        for (Domain domain : domainDAO.findAll())
            domainDAO.delete(domain);
    }

    private UserVO generateUser() {
        UserVO user = new UserVO();
        user.setObjId(1L);
        user.setFirstName("Geordi");
        user.setLastName("LaForge");
        user.setUserName("test");
        SystemRoleVO role = new SystemRoleVO();
        role.setName("failurefacadesystemiana.org");
        role.setType(SystemRoleVO.SystemType.AC);
        user.addRole(role);
        role = new SystemRoleVO();
        role.setName("failurefacadesystemiana.org");
        role.setType(SystemRoleVO.SystemType.TC);
        user.addRole(role);
        return user;
    }
}
