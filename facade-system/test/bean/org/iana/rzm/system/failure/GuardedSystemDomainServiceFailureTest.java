package org.iana.rzm.system.failure;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.iana.rzm.facade.system.domain.SystemDomainService;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.system.domain.SimpleDomainVO;
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

@Test(sequential=true, groups = {"failure", "facade-system", "GuardedSystemDomainService"})
public class GuardedSystemDomainServiceFailureTest {
    private SystemDomainService gsds;
    DomainDAO domainDAO;
    private long domainId1;
    private long domainId2;

    @BeforeClass
    public void init() throws Exception {
        gsds = (SystemDomainService) new ClassPathXmlApplicationContext("spring-facade-system.xml").getBean("GuardedSystemDomainService");
    }

    @Test
    public void initDB() {
        domainDAO = (DomainDAO) new ClassPathXmlApplicationContext("spring-facade-system.xml").getBean("domainDAO");
        Domain domainCreated = new Domain("facadesystemiana.org");
        domainCreated.setWhoisServer("whoIsServer");
        domainDAO.create(domainCreated);
        domainId1 = domainCreated.getObjId();
        domainCreated = new Domain("facadesystemiana1.org");
        domainCreated.setWhoisServer("whoIsServer2");
        domainDAO.create(domainCreated);
        domainId2 = domainCreated.getObjId();
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

    @Test (expectedExceptions = {NoObjectFoundException.class},
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
