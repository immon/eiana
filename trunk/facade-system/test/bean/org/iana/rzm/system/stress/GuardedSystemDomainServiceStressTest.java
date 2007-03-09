/**
 * org.iana.rzm.system.stress
 * (C) Research and Academic Computer Network - NASK
 * piotrt, 2007-03-09, 09:46:38
 */
package org.iana.rzm.system.stress;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.iana.rzm.facade.system.SystemDomainService;
import org.iana.rzm.facade.system.DomainVO;
import org.iana.rzm.facade.system.SimpleDomainVO;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.SystemRoleVO;
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
public class GuardedSystemDomainServiceStressTest {
    private static int NUMBER_OF_DOMAINS = 100; //must by changed also in TestSystemUserManagerStress class
    private SystemDomainService gsds;

    @BeforeClass
    public void init() throws Exception {
        gsds = (SystemDomainService) new ClassPathXmlApplicationContext("spring-facade-system.xml").getBean("GuardedSystemDomainServiceStress");
        DomainDAO domainDAO = (DomainDAO) new ClassPathXmlApplicationContext("spring-facade-system.xml").getBean("domainDAO");
        for(int i=0; i<NUMBER_OF_DOMAINS; i++) {
            Domain domainCreated = new Domain("facadesystemiana"+i+".org");
            domainCreated.setWhoisServer("whoIsServer"+i);
            domainDAO.create(domainCreated);
        }
    }

    @Test
    public void testGetDomainByUserName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        List<SimpleDomainVO> list = gsds.findUserDomains("test");
        assert list.size() == NUMBER_OF_DOMAINS;
    }

    private UserVO generateUser() {
        UserVO user = new UserVO();
        user.setFirstName("Geordi");
        user.setLastName("LaForge");
        user.setUserName("test");
        for(int i=0; i<NUMBER_OF_DOMAINS; i++) {
            SystemRoleVO role = new SystemRoleVO();
            role.setName("facadesystemiana"+i+".org");
            role.setType(SystemRoleVO.SystemType.AC);
            user.addRole(role);
            role = new SystemRoleVO();
            role.setName("facadesystemiana"+i+".org");
            role.setType(SystemRoleVO.SystemType.TC);
            user.addRole(role);
        }
        return user;
    }
}
