package org.iana.rzm.system.stress;


import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.iana.rzm.facade.system.domain.SystemDomainService;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.system.domain.SimpleDomainVO;
import org.iana.rzm.facade.system.converter.FromVOConverter;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.system.conf.SpringSystemApplicationContext;
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"stress",  "facade-system"})
public class GuardedSystemDomainServiceStressTest {
    private static int NUMBER_OF_DOMAINS = 100; //must by changed also in TestSystemUserManagerStress class
    private SystemDomainService gsds;
    private List<Domain> domainsList = new ArrayList<Domain>();
    private DomainDAO domainDAO;

    @BeforeClass
    public void init() throws Exception {
        gsds = (SystemDomainService) SpringSystemApplicationContext.getInstance().getContext().getBean("GuardedSystemDomainServiceStress");
        domainDAO = (DomainDAO) SpringSystemApplicationContext.getInstance().getContext().getBean("domainDAO");
        for(int i=0; i<NUMBER_OF_DOMAINS; i++) {
            Domain domainCreated = new Domain("stressfacadesystemiana"+i+".org");
            domainCreated.setWhoisServer("whoIsServer"+i);
            domainDAO.create(domainCreated);
            domainsList.add(domainCreated);
        }
    }

    @Test
    public void testGetDomainByUserName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        List<SimpleDomainVO> list = gsds.findUserDomains("testSystemStress");
        assert list.size() == NUMBER_OF_DOMAINS;
    }

    @Test
    public void testGetDomainByName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        for(int i=0; i<NUMBER_OF_DOMAINS; i++) {
            DomainVO domainVO = (DomainVO) gsds.getDomain("stressfacadesystemiana"+i+".org");
        }
    }

    @Test
    public void testGetDomainById() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        for(Domain domain : domainsList) {
            DomainVO domainVO = (DomainVO) gsds.getDomain(domain.getObjId());
        }
    }

    private UserVO generateUser() {
        UserVO user = new UserVO();
        user.setFirstName("Geordi");
        user.setLastName("LaForge");
        user.setUserName("testSystemStress");
        user.setObjId(2l);
        for(int i=0; i<NUMBER_OF_DOMAINS; i++) {
            SystemRoleVO role = new SystemRoleVO();
            role.setName("stressfacadesystemiana"+i+".org");
            role.setType(SystemRoleVO.SystemType.AC);
            user.addRole(role);
            role = new SystemRoleVO();
            role.setName("stressfacadesystemiana"+i+".org");
            role.setType(SystemRoleVO.SystemType.TC);
            user.addRole(role);
        }
        return user;
    }

    @AfterClass
    public void cleanUp() {
        for(Domain domain : domainsList)
            domainDAO.delete(domain);
    }
}
