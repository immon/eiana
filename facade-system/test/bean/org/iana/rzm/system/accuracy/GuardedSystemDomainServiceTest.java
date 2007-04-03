package org.iana.rzm.system.accuracy;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.iana.rzm.facade.system.domain.SystemDomainService;
import org.iana.rzm.facade.system.domain.SimpleDomainVO;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.system.conf.SpringSystemApplicationContext;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"accuracy", "facade-system", "GuardedSystemDomainService"})
public class GuardedSystemDomainServiceTest {
    private SystemDomainService gsds;
    DomainDAO domainDAO;
    private long domainId;
    private long domainId1;
    Set<String> domainNames = new HashSet<String>();

    Domain domain1, domain2;

    @BeforeClass
    public void init() throws Exception{
        gsds = (SystemDomainService) SpringSystemApplicationContext.getInstance().getContext().getBean("GuardedSystemDomainService");
        domainDAO = (DomainDAO) SpringSystemApplicationContext.getInstance().getContext().getBean("domainDAO");
              
        domain1 = new Domain("facadesystemiana.org");
        domainDAO.create(domain1);
        domainId = domain1.getObjId();
        domainNames.add(domain1.getName());

        domain2 = new Domain("facadesystemiana1.org");
        domainDAO.create(domain2);
        domainId1 = domain2.getObjId();
        domainNames.add(domain2.getName());
    }

    @Test
    public void testGetFirstDomainById() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain(domainId);
        assert domainVO.getName().equals("facadesystemiana.org");
        assert domainVO.getObjId() == domainId;
        Set<RoleVO.Type> roleType = new HashSet<RoleVO.Type>();
        roleType.add(SystemRoleVO.SystemType.AC);
        roleType.add(SystemRoleVO.SystemType.TC);
        assert domainVO.getRoles().equals(roleType);
    }

    @Test
    public void testGetSecondDomainById() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain(domainId1);
        assert domainVO.getName().equals("facadesystemiana1.org");
        assert domainVO.getObjId() == domainId1;
        Set<RoleVO.Type> roleType = new HashSet<RoleVO.Type>();
        roleType.add(SystemRoleVO.SystemType.SO);
        roleType.add(SystemRoleVO.SystemType.AC);
        assert domainVO.getRoles().equals(roleType);

    }

    @Test
    public void testGetFirstDomainByName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain("facadesystemiana.org");
        assert domainVO.getName().equals("facadesystemiana.org");
        assert domainVO.getObjId() == domainId;

        Set<RoleVO.Type> roleType = new HashSet<RoleVO.Type>();
        roleType.add(SystemRoleVO.SystemType.AC);
        roleType.add(SystemRoleVO.SystemType.TC);
        assert domainVO.getRoles().equals(roleType);
    }

    @Test
    public void testGetSecondDomainByName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain("facadesystemiana1.org");
        assert domainVO.getName().equals("facadesystemiana1.org");
        assert domainVO.getObjId() == domainId1;

        Set<RoleVO.Type> roleType = new HashSet<RoleVO.Type>();
        roleType.add(SystemRoleVO.SystemType.SO);
        roleType.add(SystemRoleVO.SystemType.AC);
        assert domainVO.getRoles().equals(roleType);
    }

    @Test
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

    @AfterClass
    public void cleanUp() {
        domainDAO.delete(domain1);
        domainDAO.delete(domain2);
    }

    private UserVO generateUser() {
        UserVO user = new UserVO();
        user.setObjId(1L);
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
        role.setType(SystemRoleVO.SystemType.SO);
        user.addRole(role);
        role = new SystemRoleVO();
        role.setName("facadesystemiana1.org");
        role.setType(SystemRoleVO.SystemType.AC);
        user.addRole(role);
        return user;
    }
}
