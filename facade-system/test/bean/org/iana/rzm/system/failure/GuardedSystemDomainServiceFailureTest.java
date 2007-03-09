/**
 * org.iana.rzm.system.failure
 * (C) Research and Academic Computer Network - NASK
 * piotrt, 2007-03-09, 09:29:25
 */
package org.iana.rzm.system.failure;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.iana.rzm.facade.system.SystemDomainService;
import org.iana.rzm.facade.system.DomainVO;
import org.iana.rzm.facade.system.SimpleDomainVO;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Iterator;

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
    public void testGetDomainByWrongId() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain(domainId2);
    }

    @Test (expectedExceptions = {NoObjectFoundException.class}, groups = {"failure", "facade-system", "GuardedSystemDomainService"})
    public void testGetDomainByWrongName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain("wrongdomainname.org");
    }

    @Test (expectedExceptions = {AccessDeniedException.class}, groups = {"failure", "facade-system", "GuardedSystemDomainService"})
    public void testGetDomainByWrongUserName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        List<SimpleDomainVO> list = gsds.findUserDomains("anotherUser");
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
