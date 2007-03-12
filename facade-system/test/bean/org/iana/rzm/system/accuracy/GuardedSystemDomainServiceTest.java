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
    private long domainId1;
    Set<String> domainNames = new HashSet<String>();

    @BeforeClass (groups = {"accuracy", "facade-system", "GuardedSystemDomainService"})
    public void init() throws Exception{
        gsds = (SystemDomainService) new ClassPathXmlApplicationContext("spring-facade-system.xml").getBean("GuardedSystemDomainService");
        DomainDAO domainDAO = (DomainDAO) new ClassPathXmlApplicationContext("spring-facade-system.xml").getBean("domainDAO");

        Domain domainCreated = new Domain("facadesystemiana.org");
            domainDAO.create(domainCreated);
            domainId = domainCreated.getObjId();
            domainNames.add(domainCreated.getName());

        domainCreated = new Domain("facadesystemiana1.org");
            domainDAO.create(domainCreated);
            domainId1 = domainCreated.getObjId();
            domainNames.add(domainCreated.getName());
    }

    @Test (groups = {"accuracy", "facade-system", "GuardedSystemDomainService"})
    public void testGetFirstDomainById() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain(domainId);
        assert domainVO.getName().equals("facadesystemiana.org");
        assert domainVO.getObjId() == domainId;
    }

    @Test (groups = {"accuracy", "facade-system", "GuardedSystemDomainService"})
    public void testGetSecondDomainById() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain(domainId1);
        assert domainVO.getName().equals("facadesystemiana1.org");
        assert domainVO.getObjId() == domainId1;
    }

    @Test (groups = {"accuracy", "facade-system", "GuardedSystemDomainService"})
    public void testGetFirstDomainByName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain("facadesystemiana.org");
        assert domainVO.getName().equals("facadesystemiana.org");
        assert domainVO.getObjId() == domainId;
    }

    @Test (groups = {"accuracy", "facade-system", "GuardedSystemDomainService"})
    public void testGetSecondDomainByName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain("facadesystemiana1.org");
        assert domainVO.getName().equals("facadesystemiana1.org");
        assert domainVO.getObjId() == domainId1;
    }

    @Test (groups = {"accuracy", "facade-system", "GuardedSystemDomainService"})
    public void testFindUserDomainsByUserName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());

        List<SimpleDomainVO> simpleDomainVOList = gsds.findUserDomains("test");

        assert !simpleDomainVOList.isEmpty();
        assert simpleDomainVOList.size() == 2;

        Set<String> retrivedNames = new HashSet<String>();
        for (SimpleDomainVO simpleDomainVO : simpleDomainVOList)
            assert retrivedNames.add(simpleDomainVO.getName());

        assert domainNames.equals(retrivedNames);
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
