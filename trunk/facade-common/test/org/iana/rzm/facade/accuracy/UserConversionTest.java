package org.iana.rzm.facade.accuracy;

import org.iana.rzm.user.hibernate.test.common.HibernateMappingTestUtil;
import org.iana.rzm.user.AdminUser;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.AdminRoleVO;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.TrackData;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.HashSet;
import java.sql.Timestamp;

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
    //role and trackData are ignored
    public void testAdminUserConversion() throws Exception {

        AdminUser user = new AdminUser();
        HibernateMappingTestUtil.setupUser(user, "t", false);
        user.setType(AdminUser.Type.GOV_OVERSIGHT);
        
        UserVO userVO = UserConverter.convert(user);
        compareRZMUser(userVO, user);
    }

    @Test
    public void testAdminUserTrackDataConversion() throws Exception {

        AdminUser user = new AdminUser();
        HibernateMappingTestUtil.setupUser(user, "t", false);
        user.setType(AdminUser.Type.GOV_OVERSIGHT);

        TrackData trackData = new TrackData();
        //created is set in a constructor
        trackData.setCreatedBy("Creator");
        trackData.setModified(new Timestamp(System.currentTimeMillis()));
        trackData.setModifiedBy("Modifier");

        user.setTrackData(trackData);

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

        //could be in some delegated method
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

    @Test
    public void testSystemUserWithoutRolesConversion() throws Exception {

        SystemUser user = new SystemUser();
        HibernateMappingTestUtil.setupUser(user, "t", false);

        UserVO userVO = UserConverter.convert(user);
        compareRZMUser(userVO, user);
    }

    @Test
    public void testSystemUserOneRoleConversion() throws Exception {

        SystemUser user = new SystemUser();
        HibernateMappingTestUtil.setupUser(user, "t", false);

        user.clearRoles();
        user.addRole(createRole(Role.Type.AC));
        UserVO userVO = UserConverter.convert(user);
        compareRZMUser(userVO, user);
        compareRoles(userVO, user);

        user.clearRoles();
        user.addRole(createRole(Role.Type.SO));
        userVO = UserConverter.convert(user);
        compareRZMUser(userVO, user);
        compareRoles(userVO, user);

        user.clearRoles();
        user.addRole(createRole(Role.Type.TC));
        userVO = UserConverter.convert(user);
        compareRZMUser(userVO, user);
        compareRoles(userVO, user);
    }

    //todo test with multiple roles could be added

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

        assert userVO.getCreated() == user.getCreated();
        assert (userVO.getCreatedBy() == null && user.getCreatedBy() == null) || userVO.getCreatedBy().equals(user.getCreatedBy());
        assert userVO.getModified() == user.getModified();
        assert (userVO.getModifiedBy() == null && user.getModifiedBy() == null) || userVO.getModifiedBy().equals(user.getModifiedBy());
    }

    private void compareRoles(UserVO userVO, SystemUser user) {

        if (userVO.getRoles() == null && user.getRoles() == null) return;

        Set<RoleVO> rolesVO = userVO.getRoles();
        Set<Role> roles = new HashSet<Role>(user.getRoles());

        assert userVO.getRoles() != null;
        assert user.getRoles() != null;
        assert rolesVO.size() == roles.size();

        //can be done more efficient?
        for (RoleVO roleVO : rolesVO) {
            //it was a SystemUser
            assert roleVO instanceof SystemRoleVO;

            for (Role role : roles) {
                compareRole((SystemRoleVO)roleVO, role);
            }
        }
    }

    private void compareRole(SystemRoleVO systemRoleVO, Role role) {

        assert systemRoleVO.getName().equals(role.getName());
        assert systemRoleVO.isNotify() == role.isNotify();
        assert systemRoleVO.isAcceptFrom() == role.isNotify();
        assert systemRoleVO.isMustAccept() == role.isMustAccept();

        compareType(systemRoleVO.getType(), role.getType());
    }

    private void compareType(SystemRoleVO.SystemType typeVO, Role.Type type) {

        //todo could be done using map
        if (!(type == Role.Type.AC && typeVO == SystemRoleVO.SystemType.AC))
            if (!(type == Role.Type.SO && typeVO == SystemRoleVO.SystemType.SO))
                if (!(type == Role.Type.TC && typeVO == SystemRoleVO.SystemType.TC))
                    assert false : "unmatched role type";
    }

    private Role createRole(Role.Type type) throws InvalidNameException {

        Role role = new Role();
        role.setAcceptFrom(true);
        role.setMustAccept(true);
        role.setName("roleName");
        role.setNotify(true);
        //without trackData and ObjID is not used in conversion
        role.setType(type);

        return role;
    }
}
