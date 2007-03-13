package org.iana.rzm.system.failure;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.iana.rzm.facade.system.domain.SystemDomainService;
import org.iana.rzm.facade.system.DomainVO;
import org.iana.rzm.facade.system.SimpleDomainVO;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.domain.Domain;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

@Test(groups = {"service", "facade-system"})
public class GuardedSystemDomainServiceFailureTest {
    private SystemDomainService gsds;
    private long domainId1;
    private long domainId2;

    @BeforeClass (groups = {"failure", "facade-system", "GuardedSystemDomainService"})
    public void init() throws Exception {
        gsds = (SystemDomainService) new ClassPathXmlApplicationContext("spring-facade-system.xml").getBean("GuardedSystemDomainService");
            DomainDAO domainDAO = (DomainDAO) new ClassPathXmlApplicationContext("spring-facade-system.xml").getBean("domainDAO");
            Domain domainCreated = new Domain("facadesystemiana.org");
            domainCreated.setWhoisServer("whoIsServer");
            domainDAO.create(domainCreated);
            domainId1 = domainCreated.getObjId();
            domainCreated = new Domain("facadesystemiana2.org");
            domainCreated.setWhoisServer("whoIsServer2");
            domainDAO.create(domainCreated);
            domainId2 = domainCreated.getObjId();
    }

    @Test (expectedExceptions = {AccessDeniedException.class}, groups = {"failure", "facade-system", "GuardedSystemDomainService"})
    public void testGetDomainByUserNameWhenUserNotSetInService() throws Exception {
        DomainVO domainVO = (DomainVO) gsds.findUserDomains("someUser");
    }

    @Test (expectedExceptions = {AccessDeniedException.class}, groups = {"failure", "facade-system", "GuardedSystemDomainService"},
            dependsOnMethods = {"testGetDomainByUserNameWhenUserNotSetInService"})
    public void testGetDomainByNameWhenUserNotSetInService() throws Exception {
        DomainVO domainVO = (DomainVO) gsds.getDomain(domainId1);
    }

    @Test (expectedExceptions = {AccessDeniedException.class}, groups = {"failure", "facade-system", "GuardedSystemDomainService"},
        dependsOnMethods = {"testGetDomainByNameWhenUserNotSetInService"})
    public void testGetDomainByIdWhenUserNotSetInService() throws Exception {
        DomainVO domainVO = (DomainVO) gsds.getDomain(domainId1);
    }

    @Test (expectedExceptions = {AccessDeniedException.class}, groups = {"failure", "facade-system", "GuardedSystemDomainService"},
            dependsOnMethods = {"testGetDomainByIdWhenUserNotSetInService"})
    public void testGetDomainByWrongId() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain(domainId2);
    }

    @Test (expectedExceptions = {NoObjectFoundException.class}, groups = {"failure", "facade-system", "GuardedSystemDomainService"},
            dependsOnMethods = {"testGetDomainByWrongId"})
    public void testGetDomainByWrongName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain("wrongdomainname.org");
    }

    @Test (expectedExceptions = {AccessDeniedException.class}, groups = {"failure", "facade-system", "GuardedSystemDomainService"},
            dependsOnMethods = {"testGetDomainByWrongName"})
    public void testFindUserDomainsByWrongUserName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        List<SimpleDomainVO> list = gsds.findUserDomains("anotherUser");
    }

    @Test (expectedExceptions = {IllegalArgumentException.class}, groups = {"failure", "facade-system", "GuardedSystemDomainService"},
            dependsOnMethods = {"testFindUserDomainsByWrongUserName"})
    public void testGetDomainByNullId() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain(null);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class}, groups = {"failure", "facade-system", "GuardedSystemDomainService"},
            dependsOnMethods = {"testGetDomainByNullId"})
    public void testGetDomainByNullName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain(null);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class}, groups = {"failure", "facade-system", "GuardedSystemDomainService"},
            dependsOnMethods = {"testGetDomainByNullName"})
    public void testGetDomainByOutOfRangeId() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain(-1);
    }
    
    private UserVO generateUser() {
        UserVO user = new UserVO();
        user.setFirstName("Geordi");
        user.setLastName("LaForge");
        user.setUserName("test");
        SystemRoleVO role = new SystemRoleVO();
        role.setName("facadesystemiana.org");
        role.setType(SystemRoleVO.SystemType.AC);
        user.addRole(role);
        role = new SystemRoleVO();
        role.setName("facadesystemiana.org");
        role.setType(SystemRoleVO.SystemType.TC);
        user.addRole(role);
        return user;
    }
}
