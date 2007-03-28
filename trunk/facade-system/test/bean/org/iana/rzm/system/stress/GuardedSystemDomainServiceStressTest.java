package org.iana.rzm.system.stress;


import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.iana.rzm.facade.system.domain.SystemDomainService;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.system.domain.SimpleDomainVO;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.system.conf.SpringSystemApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"stress",  "GuardedSystemDomainService"})
public class GuardedSystemDomainServiceStressTest {
    private static int NUMBER_OF_DOMAINS = 100; //must by changed also in TestSystemUserManagerStress class
    private SystemDomainService gsds;
    private List<Long> idList = new ArrayList<Long>();

    @BeforeClass
    public void init() throws Exception {
        gsds = (SystemDomainService) SpringSystemApplicationContext.getInstance().getContext().getBean("GuardedSystemDomainServiceStress");
        DomainDAO domainDAO = (DomainDAO) SpringSystemApplicationContext.getInstance().getContext().getBean("domainDAO");
        for(int i=0; i<NUMBER_OF_DOMAINS; i++) {
            Domain domainCreated = new Domain("stressfacadesystemiana"+i+".org");
            domainCreated.setWhoisServer("whoIsServer"+i);
            domainDAO.create(domainCreated);
            idList.add(domainCreated.getObjId());
        }
    }

    @Test
    public void testGetDomainByUserName() throws Exception {
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(generateUser());
        gsds.setUser(testAuthUser.getAuthUser());
        List<SimpleDomainVO> list = gsds.findUserDomains("test");
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
        for(Iterator i = idList.iterator(); i.hasNext();) {
            Long id = (Long) i.next();
            DomainVO domainVO = (DomainVO) gsds.getDomain(id.longValue());
        }
    }

    private UserVO generateUser() {
        UserVO user = new UserVO();
        user.setFirstName("Geordi");
        user.setLastName("LaForge");
        user.setUserName("test");
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
}
