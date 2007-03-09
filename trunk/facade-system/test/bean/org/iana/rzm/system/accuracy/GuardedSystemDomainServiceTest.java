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
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.SystemUser;
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
public class GuardedSystemDomainServiceTest {
    private SystemDomainService gsds;
    private long domainId;

    @BeforeClass
    public void init() throws InvalidNameException {
            gsds = (SystemDomainService) new ClassPathXmlApplicationContext("spring-facade-system.xml").getBean("GuardedSystemDomainService");
            DomainDAO domainDAO = (DomainDAO) new ClassPathXmlApplicationContext("spring-facade-system.xml").getBean("domainDAO");
            Domain domainCreated = new Domain("iana.org");
            domainCreated.setWhoisServer("whoIsServer");
            domainDAO.create(domainCreated);
            domainId = domainCreated.getObjId();

    }

    @Test
    public void testGetDomainById() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        DomainVO domainVO = (DomainVO) gsds.getDomain(domainId);
        assert domainVO.getName().equals("iana.org");
    }
    
    @Test
    public void testGetDomainByUserName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        List<SimpleDomainVO> list = gsds.findUserDomains("test");
        for(Iterator iterator = list.iterator(); iterator.hasNext();) {
            SimpleDomainVO simpleDomainVO = (SimpleDomainVO) iterator.next();
            System.out.print("User Domain retrived by findUserDomains: " + simpleDomainVO.getName() + "\n");
        }
    }

    private UserVO generateUser() {
        UserVO user = new UserVO();
        user.setFirstName("Geordi");
        user.setLastName("LaForge");
        user.setUserName("test");
        SystemRoleVO role = new SystemRoleVO();
        role.setName("iana.org");
        role.setType(SystemRoleVO.SystemType.AC);
        user.addRole(role);
        role = new SystemRoleVO();
        role.setName("iana.org");
        role.setType(SystemRoleVO.SystemType.TC);
        user.addRole(role);
        return user;
    }
}
