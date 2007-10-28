package org.iana.rzm.facade.user.converter;

import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.common.TrackData;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

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
     */
    public static UserVO convert(RZMUser user) {

        if (user == null) return null;

        UserVO userVO = convertUser(user);

        //Note: in SystemUser there is a list, here a set. Possible loss of duplicated rolesVO.
        Set<RoleVO> rolesVO = new HashSet<RoleVO>();

        for (Role role : user.getRoles()) {
            rolesVO.add(RoleConverter.convertRole(role));
        }

        userVO.setRoles(rolesVO);

        return userVO;
    }

    public static UserVO convertSimple(RZMUser user) {
        if (user == null) return null;
        return convertUser(user);
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
        userVO.setEmail(user.getEmail());
        userVO.setPublicKey(user.getPublicKey());
        userVO.setSecurID(user.isSecurID());
        userVO.setPassword(user.getPassword());

        return userVO;
    }

    public static RZMUser convert(UserVO user) {
        if (user == null) return null;
        RZMUser rzmUser = new RZMUser();
        return convert(rzmUser, user);
    }

    public static RZMUser convert(RZMUser rzmUser, UserVO user) {
        if (user == null) return null;

        convertUser(rzmUser, user);

        List<Role> roles = new ArrayList<Role>();
        for (RoleVO roleVO : user.getRoles()) {
            roles.add(RoleConverter.convertRole(roleVO));
        }
        rzmUser.setRoles(roles);

        return rzmUser;
    }

    private static RZMUser convertUser(RZMUser rzmUser, UserVO userVO) {

        TrackData trackData = new TrackData();

        trackData.setCreated(userVO.getCreated());
        trackData.setCreatedBy(userVO.getCreatedBy());
        trackData.setModified(userVO.getModified());
        trackData.setModifiedBy(userVO.getModifiedBy());
        rzmUser.setTrackData(trackData);

        rzmUser.setFirstName(userVO.getFirstName());
        rzmUser.setLastName(userVO.getLastName());
        rzmUser.setObjId(userVO.getObjId());
        rzmUser.setOrganization(userVO.getOrganization());
        rzmUser.setLoginName(userVO.getUserName());
        rzmUser.setEmail(userVO.getEmail());
        rzmUser.setPublicKey(userVO.getPublicKey());
        // rzmUser.setPassword(userVO.getPassword());
        rzmUser.setSecurID(userVO.isSecurID());

        return rzmUser;
    }
}
