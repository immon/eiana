package org.iana.rzm.facade.system.domain;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.system.domain.vo.DomainVO;
import org.iana.rzm.facade.system.domain.vo.SimpleDomainVO;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.test.spring.RollbackableSpringContextTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"accuracy", "facade-system", "GuardedSystemDomainService"})
public class GuardedSystemDomainServiceTest extends RollbackableSpringContextTest {

    protected SystemDomainService GuardedSystemDomainService;
    protected DomainDAO domainDAO;
    protected UserManager userManager;

    private long domainId;
    private long domainId1;
    Set<String> domainNames = new HashSet<String>();

    Domain domain1, domain2;

    RZMUser user;

    public GuardedSystemDomainServiceTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() throws Exception{

        domain1 = new Domain("facadesystemiana.org");
        domainDAO.create(domain1);
        domainId = domain1.getObjId();
        domainNames.add(domain1.getName());

        domain2 = new Domain("facadesystemiana1.org");
        domainDAO.create(domain2);
        domainId1 = domain2.getObjId();
        domainNames.add(domain2.getName());

        user = generateUser();

        userManager.create(user);
    }

    @Test
    public void testGetFirstDomainById() throws Exception {
        GuardedSystemDomainService.setUser(new AuthenticatedUser(user.getObjId(), user.getLoginName(), true));
        DomainVO domainVO = (DomainVO) GuardedSystemDomainService.getDomain(domainId);
        assert domainVO.getName().equals("facadesystemiana.org");
        assert domainVO.getObjId() == domainId;
        Set<RoleVO.Type> roleType = new HashSet<RoleVO.Type>();
        roleType.add(SystemRoleVO.SystemType.AC);
        assert domainVO.getRoles().equals(roleType);
    }

    @Test
    public void testGetSecondDomainById() throws Exception {
        GuardedSystemDomainService.setUser(new AuthenticatedUser(user.getObjId(), user.getLoginName(), true));
        DomainVO domainVO = (DomainVO) GuardedSystemDomainService.getDomain(domainId1);
        assert domainVO.getName().equals("facadesystemiana1.org");
        assert domainVO.getObjId() == domainId1;
        Set<RoleVO.Type> roleType = new HashSet<RoleVO.Type>();
        roleType.add(SystemRoleVO.SystemType.SO);
        roleType.add(SystemRoleVO.SystemType.AC);
        assert domainVO.getRoles().equals(roleType);

    }

    @Test
    public void testGetFirstDomainByName() throws Exception {
        GuardedSystemDomainService.setUser(new AuthenticatedUser(user.getObjId(), user.getLoginName(), true));
        DomainVO domainVO = (DomainVO) GuardedSystemDomainService.getDomain("facadesystemiana.org");
        assert domainVO.getName().equals("facadesystemiana.org");
        assert domainVO.getObjId() == domainId;

        Set<RoleVO.Type> roleType = new HashSet<RoleVO.Type>();
        roleType.add(SystemRoleVO.SystemType.AC);
        assert domainVO.getRoles().equals(roleType);
    }

    @Test
    public void testGetSecondDomainByName() throws Exception {
        GuardedSystemDomainService.setUser(new AuthenticatedUser(user.getObjId(), user.getLoginName(), true));
        DomainVO domainVO = (DomainVO) GuardedSystemDomainService.getDomain("facadesystemiana1.org");
        assert domainVO.getName().equals("facadesystemiana1.org");
        assert domainVO.getObjId() == domainId1;

        Set<RoleVO.Type> roleType = new HashSet<RoleVO.Type>();
        roleType.add(SystemRoleVO.SystemType.SO);
        roleType.add(SystemRoleVO.SystemType.AC);
        assert domainVO.getRoles().equals(roleType);
    }

    @Test
    public void testFindUserDomainsByUserName() throws Exception {
        GuardedSystemDomainService.setUser(new AuthenticatedUser(user.getObjId(), user.getLoginName(), true));

        List<SimpleDomainVO> simpleDomainVOList = GuardedSystemDomainService.findUserDomains("test");

        assert !simpleDomainVOList.isEmpty();
        assert simpleDomainVOList.size() == 2;

        Set<String> retrivedNames = new HashSet<String>();
        for (SimpleDomainVO simpleDomainVO : simpleDomainVOList)
            assert retrivedNames.add(simpleDomainVO.getName());

        assert domainNames.equals(retrivedNames);
    }

    @AfterClass (alwaysRun = true)
    public void cleanUp() {
        for (Domain domain : domainDAO.findAll())
            domainDAO.delete(domain);
    }

    private RZMUser generateUser() {
        RZMUser user = new RZMUser();
        user.setObjId(1L);
        user.setFirstName("Geordi");
        user.setLastName("LaForge");
        user.setLoginName("test");
        SystemRole role = new SystemRole();
        role.setName("facadesystemiana.org");
        role.setType(SystemRole.SystemType.AC);
        user.addRole(role);
        role = new SystemRole();
        role.setName("facadesystemiana.org");
        role.setType(SystemRole.SystemType.TC);
        user.addRole(role);
        role.setName("facadesystemiana1.org");
        role.setType(SystemRole.SystemType.SO);
        user.addRole(role);
        role = new SystemRole();
        role.setName("facadesystemiana1.org");
        role.setType(SystemRole.SystemType.AC);
        user.addRole(role);
        return user;
    }
}
