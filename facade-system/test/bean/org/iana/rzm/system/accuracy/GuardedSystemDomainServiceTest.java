package org.iana.rzm.system.accuracy;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.iana.rzm.facade.system.*;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.AuthenticationService;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.SystemUser;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Address;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.TrackData;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * @author Piotr Tkaczyk
 */

@Test(groups = {"service", "facade-system"})
public class GuardedSystemDomainServiceTest {
    private SystemDomainService gsds;
    private long domainId;
    Set<String> domainNames = new HashSet<String>();

    @BeforeClass (groups = {"accuracy", "facade-system", "GuardedSystemDomainService"})
    public void init() throws Exception{
        gsds = (SystemDomainService) new ClassPathXmlApplicationContext("spring-facade-system.xml").getBean("GuardedSystemDomainService");
        DomainDAO domainDAO = (DomainDAO) new ClassPathXmlApplicationContext("spring-facade-system.xml").getBean("domainDAO");

        Domain domainCreated = new Domain("facadesystemiana.org");
            domainCreated.setWhoisServer("WhoIsServer");
            domainDAO.create(domainCreated);
            domainId = domainCreated.getObjId();
            domainNames.add(domainCreated.getName());

        domainCreated = new Domain("facadesystemiana1.org");
            domainCreated.setWhoisServer("WhoIsServer1");
            domainDAO.create(domainCreated);
            domainNames.add(domainCreated.getName());
    }

    @Test (groups = {"accuracy", "facade-system", "GuardedSystemDomainService"})
    public void testGetDomainById() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain(domainId);
        assert domainVO.getName().equals("facadesystemiana.org");
    }

    @Test (groups = {"accuracy", "facade-system", "GuardedSystemDomainService"})
    public void testGetDomainByName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain("facadesystemiana.org");
        assert domainVO.getName().equals("facadesystemiana.org");
    }

    @Test (groups = {"accuracy", "facade-system", "GuardedSystemDomainService"})
    public void testfindUserDomainsByUserName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        List<SimpleDomainVO> list = gsds.findUserDomains("test");
        assert !list.isEmpty();
        assert list.size() == 2;
        for (SimpleDomainVO simpleDomainVO : list)
            assert domainNames.contains(simpleDomainVO.getName());
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
        role.setName("facadesystemiana1.org");
        role.setType(SystemRoleVO.SystemType.AC);
        user.addRole(role);
        role = new SystemRoleVO();
        role.setName("facadesystemiana1.org");
        role.setType(SystemRoleVO.SystemType.TC);
        user.addRole(role);
        return user;
    }
}
