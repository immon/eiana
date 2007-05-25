package org.iana.rzm.facade.admin;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.SimpleDomainVO;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.criteria.Equal;
import org.iana.criteria.Criterion;
import org.iana.criteria.Or;
import org.iana.criteria.Not;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author: Piotr Tkaczyk
 */
@Test (sequential = true, groups = {"test", "GuardedAdminDomainServiceTest"})
public class GuardedAdminDomainServiceTest {

    ApplicationContext appCtx;
    AdminDomainService gAdminServ;

    UserManager userManager;
    RZMUser user, wrongUser;
    long domainId;

    final static String DOMAIN_NAME = "gadmindomain.org";
    final static String SECOND_DOMAIN = "gadminsecong.org";

    @BeforeClass
    public void init() {
        appCtx = SpringApplicationContext.getInstance().getContext();
        gAdminServ = (AdminDomainService) appCtx.getBean("GuardedAdminDomainServiceBean");
        userManager = (UserManager) appCtx.getBean("userManager");

        user = new RZMUser();
        user.setLoginName("gadstestuser");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("email@some.com");
        user.addRole(new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(user);

        wrongUser = new RZMUser();
        wrongUser.setLoginName("gadswronguser");
        wrongUser.setFirstName("firstName");
        wrongUser.setLastName("lastName");
        wrongUser.setEmail("email@some.com");
        wrongUser.addRole(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        userManager.create(wrongUser);
    }

    @Test
    public void testFacadeCreateDomain() {

        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminServ.setUser(testAuthUser);

        gAdminServ.createDomain(createDomainVO(DOMAIN_NAME));

        IDomainVO retDomainVO = gAdminServ.getDomain(DOMAIN_NAME);

        assert DOMAIN_NAME.equals(retDomainVO.getName());

        retDomainVO = gAdminServ.getDomain(retDomainVO.getObjId());

        assert DOMAIN_NAME.equals(retDomainVO.getName());

        domainId = retDomainVO.getObjId();

        List<IDomainVO> domVOList = gAdminServ.findDomains();
        assert domVOList.size() == 1;
        assert DOMAIN_NAME.equals(domVOList.iterator().next().getName());

        gAdminServ.createDomain(createDomainVO(SECOND_DOMAIN));

        gAdminServ.close();

    }

    @Test (dependsOnMethods = {"testFacadeCreateDomain"})
    public void testCount() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminServ.setUser(testAuthUser);

        assert gAdminServ.count(new Not(new Equal("name.name", "existno.org"))) == 2;

        gAdminServ.close();
    }

    @Test (dependsOnMethods = {"testCount"})
    public void testFacadeFindDomainByCriteria_Offset_Limit() throws Exception {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminServ.setUser(testAuthUser);

        List<IDomainVO> retDomainVOs = gAdminServ.find(new Not(new Equal("name.name", "existno.org")), 0, 1);

        assert retDomainVOs.size() == 1 : "################# " + retDomainVOs.size();
        assert retDomainVOs.iterator().next().getName().equals(DOMAIN_NAME);

        retDomainVOs = gAdminServ.find(new Not(new Equal("name.name", "existno.org")), 1, 1);
        assert retDomainVOs.size() == 1;
        assert retDomainVOs.iterator().next().getName().equals(SECOND_DOMAIN);

        retDomainVOs = gAdminServ.find(new Not(new Equal("name.name", "existno.org")), 0, 5);
        assert retDomainVOs.size() == 2;
        Iterator iterator = retDomainVOs.iterator();
        assert ((SimpleDomainVO)iterator.next()).getName().equals(DOMAIN_NAME);
        assert ((SimpleDomainVO)iterator.next()).getName().equals(SECOND_DOMAIN);

        gAdminServ.close();
    }

    @Test (dependsOnMethods = {"testFacadeFindDomainByCriteria_Offset_Limit"})
    public void testFacadeFindDomainByCriteria() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminServ.setUser(testAuthUser);

        List<IDomainVO> retDomainVOs = gAdminServ.findDomains(new Equal("name.name", DOMAIN_NAME));
        assert retDomainVOs.size() == 1;
        assert DOMAIN_NAME.equals(retDomainVOs.iterator().next().getName());

        List<Criterion> criterias = new ArrayList<Criterion>();
        criterias.add(new Equal("name.name", DOMAIN_NAME));
        criterias.add(new Equal("name.name", SECOND_DOMAIN));
        
        retDomainVOs = gAdminServ.findDomains(new Or(criterias));
        assert retDomainVOs.size() == 2;

        List<String> names = new ArrayList<String>();
        names.add(DOMAIN_NAME);
        names.add(SECOND_DOMAIN);

        List<String> retNames = new ArrayList<String>();
        for (IDomainVO domainVO : retDomainVOs)
            retNames.add(domainVO.getName());

        assert names.equals(retNames);
        

        gAdminServ.close();
    }

    @Test (expectedExceptions = {AccessDeniedException.class},
            dependsOnMethods = {"testFacadeFindDomainByCriteria"})
    public void testFacadeCreateDomainByWrongUser() {
        try {
            AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(wrongUser)).getAuthUser();
            gAdminServ.setUser(testAuthUser);

            gAdminServ.createDomain(createDomainVO(DOMAIN_NAME));
        } catch (AccessDeniedException e) {
            gAdminServ.close();
            throw e;
        }
    }

    @Test (dependsOnMethods = {"testFacadeCreateDomainByWrongUser"})
    public void testFacadeDeleteDomain() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminServ.setUser(testAuthUser);

        gAdminServ.deleteDomain(domainId); //first domain
        
        gAdminServ.deleteDomain(SECOND_DOMAIN); //second domain

        gAdminServ.close();
    }

    @AfterClass
    public void cleanUp() {
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
    }

    private DomainVO createDomainVO(String name) {
        Domain newDomain = new Domain(name);
        newDomain.setSupportingOrg(new Contact("supportOrg"));
        return ToVOConverter.toDomainVO(newDomain);
    }
}
