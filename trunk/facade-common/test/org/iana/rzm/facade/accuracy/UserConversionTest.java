package org.iana.rzm.facade.accuracy;

import org.iana.rzm.user.hibernate.test.common.HibernateMappingTestUtil;
import org.iana.rzm.user.AdminUser;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.AdminRoleVO;
import org.iana.rzm.facade.user.RoleVO;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Set;

/**
 * org.iana.rzm.facade.accuracy.UserConversionTest
 *
 * @author Marcin Zajaczkowski
 */
public class UserConversionTest {

    @BeforeClass
    public void init() {
        //nothing for now
    }

    @Test
    public void testAdminUserConversion() throws Exception {

        AdminUser user = new AdminUser();
        HibernateMappingTestUtil.setupUser(user, "t", false);
        user.setType(AdminUser.Type.GOV_OVERSIGHT);
        
        UserVO userVO = UserConverter.convert(user);
        compareRZMUser(userVO, user);
    }

    @Test
    public void testAdminUserTypesConversion() throws Exception {

        AdminUser user = new AdminUser();
        HibernateMappingTestUtil.setupUser(user, "t", false);
        user.setType(AdminUser.Type.GOV_OVERSIGHT);

        UserVO userVO = UserConverter.convert(user);
        compareRZMUser(userVO, user);
        compareAdminUserType(userVO, AdminRoleVO.AdminType.GOV_OVERSIGHT);

        user.setType(AdminUser.Type.IANA_STAFF);

        userVO = UserConverter.convert(user);
        compareRZMUser(userVO, user);
        compareAdminUserType(userVO, AdminRoleVO.AdminType.IANA);

        user.setType(AdminUser.Type.ZONE_PUBLISHER);

        userVO = UserConverter.convert(user);
        compareRZMUser(userVO, user);
        compareAdminUserType(userVO, AdminRoleVO.AdminType.ZONE_PUBLISHER);
    }

    private void compareAdminUserType(UserVO userVO, AdminRoleVO.Type type) {

        Set<RoleVO> roles = userVO.getRoles();
        //adminUser can have only one role
        assert roles.size() == 1;
        for (RoleVO role : roles) {
            //is there easier way to check it?
            assert role.getType() == type;
        }

    }

    private void compareRZMUser(UserVO userVO, RZMUser user) {

        assert userVO.getCreated() == user.getCreated();
        assert (userVO.getCreatedBy() == null && user.getCreatedBy() == null) || userVO.getCreatedBy().equals(user.getCreatedBy());
        assert (userVO.getFirstName() == null && user.getFirstName() == null) || userVO.getFirstName().equals(user.getFirstName());
        assert (userVO.getLastName() == null && user.getLastName() == null) || userVO.getLastName().equals(user.getLastName());
        assert userVO.getModified() == user.getModified();
        assert (userVO.getModified() == null && user.getModified() == null) || userVO.getModifiedBy().equals(user.getModifiedBy());
        assert userVO.getObjId() == user.getObjId();
        assert (userVO.getOrganization() == null && user.getOrganization() == null) || userVO.getOrganization().equals(user.getOrganization());
        assert (userVO.getUserName() == null && user.getLoginName() == null) || userVO.getUserName().equals(user.getLoginName());
    }

    //todo Add SystemUser roles comparison
}
