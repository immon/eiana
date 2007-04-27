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
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.springframework.context.ApplicationContext;

import java.util.List;

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
    public void testCreateDomain() {

        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminServ.setUser(testAuthUser);

        gAdminServ.createDomain(createDomainVO(DOMAIN_NAME));

        DomainVO retDomainVO = gAdminServ.getDomain(DOMAIN_NAME);

        assert DOMAIN_NAME.equals(retDomainVO.getName());

        retDomainVO = gAdminServ.getDomain(retDomainVO.getObjId());

        assert DOMAIN_NAME.equals(retDomainVO.getName());

        domainId = retDomainVO.getObjId();

        List<DomainVO> domVOList = gAdminServ.findDomains();
        assert domVOList.size() == 1;
        assert DOMAIN_NAME.equals(domVOList.iterator().next().getName());

        gAdminServ.close();

    }

    @Test (expectedExceptions = {AccessDeniedException.class},
            dependsOnMethods = {"testCreateDomain"})
    public void testCreateDomainByWrongUser() {
        try {
            AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(wrongUser)).getAuthUser();
            gAdminServ.setUser(testAuthUser);

            gAdminServ.createDomain(createDomainVO(DOMAIN_NAME));
        } catch (AccessDeniedException e) {
            gAdminServ.close();
            throw e;
        }
    }

    @Test (dependsOnMethods = {"testCreateDomainByWrongUser"})
    public void testUpdateDomain() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminServ.setUser(testAuthUser);

        DomainVO retDomainVO = gAdminServ.getDomain(DOMAIN_NAME);

        assert DOMAIN_NAME.equals(retDomainVO.getName());

        retDomainVO.setRegistryUrl("newregurl");
        gAdminServ.updateDomain(retDomainVO);

        retDomainVO = gAdminServ.getDomain(DOMAIN_NAME);

        assert DOMAIN_NAME.equals(retDomainVO.getName());
        assert "newregurl".equals(retDomainVO.getRegistryUrl());

        gAdminServ.close();
    }

    @Test (dependsOnMethods = {"testUpdateDomain"})
    public void testDeleteDomain() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminServ.setUser(testAuthUser);
        gAdminServ.deleteDomain(domainId);
        DomainVO retDomainVO = gAdminServ.getDomain(DOMAIN_NAME);
        assert retDomainVO == null;

        gAdminServ.createDomain(createDomainVO(DOMAIN_NAME));
        gAdminServ.deleteDomain(DOMAIN_NAME);
        retDomainVO = gAdminServ.getDomain(DOMAIN_NAME);
        assert retDomainVO == null;

        gAdminServ.close();
    }

    @AfterClass
    public void cleanUp() {
        userManager.delete(user);
        userManager.delete(wrongUser);
    }

    private DomainVO createDomainVO(String name) {
        Domain newDomain = new Domain(name);
        newDomain.setSupportingOrg(new Contact("supportOrg"));
        return ToVOConverter.toDomainVO(newDomain);
    }
}
