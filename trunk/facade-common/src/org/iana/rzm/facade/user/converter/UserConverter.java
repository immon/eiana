package org.iana.rzm.facade.user.converter;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminUser;
import org.iana.rzm.user.SystemUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.RoleVO;

import java.util.Set;
import java.util.HashSet;

/**
 * org.iana.rzm.facade.user.converter.UserConverter
 *
 * Performs conversion between user representation at DAO and facade level
 *
 * @author Marcin Zajaczkowski
 */
public class UserConverter {

    /**
     * Converts database user to facade user
     *
     * @param user DAO level user to be converter
     * @return facade level user
     * @throws ConverterException Currently not used
     */
    public static UserVO convert(RZMUser user) throws ConverterException {

        if (user == null) return null;

        UserVO userVO;

        //check is it safe with lazy user fetching?
        if (user instanceof SystemUser) {
            userVO = convertSystemUser((SystemUser)user);

        } else if (user instanceof AdminUser) {
            userVO = convertAdminUser((AdminUser)user);

        } else {
            throw new IllegalArgumentException("Unknown kind of user " + user.getClass().getName());
        }

        return userVO;
    }

    private static UserVO convertSystemUser(SystemUser user) {

        UserVO userVO = convertUser(user);

        //Note: in SystemUser there is a list, here a set. Possible loss of duplicated rolesVO.
        Set<RoleVO> rolesVO = new HashSet<RoleVO>();

        for (Role role : user.getRoles()) {
            rolesVO.add(RoleConverter.convertRole(role));
        }

        userVO.setRoles(rolesVO);

        return userVO;
    }

    private static UserVO convertAdminUser(AdminUser user) {

        UserVO userVO = convertUser(user);

        userVO.addRole(RoleConverter.convertAdminRole(user));

        return userVO;
    }

    private static UserVO convertUser(RZMUser user) {

        UserVO userVO = new UserVO();

        userVO.setCreated(user.getCreated());
        userVO.setCreatedBy(user.getCreatedBy());
        userVO.setFirstName(user.getFirstName());
        userVO.setLastName(user.getLastName());
        userVO.setModified(user.getModified());
        userVO.setModifiedBy(user.getModifiedBy());
        userVO.setObjId(user.getObjId());
        userVO.setOrganization(user.getOrganization());
        userVO.setUserName(user.getLoginName());

        return userVO;
    }

}
