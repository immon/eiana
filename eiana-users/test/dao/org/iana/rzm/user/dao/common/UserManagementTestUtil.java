package org.iana.rzm.user.dao.common;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.common.TrackData;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Jakub Laszkiewicz
 */
public class UserManagementTestUtil {
    public static RZMUser createUser(String suffix, Role role) {
        RZMUser user = new RZMUser();
        user.setEmail("user-" + suffix + "@post.net");
        user.setFirstName("fn" + suffix);
        user.setLastName("ln" + suffix);
        user.setLoginName("user-" + suffix);
        user.setOrganization("org" + suffix);
        user.setPassword("passwd-" + suffix);
        user.setSecurID(false);
        TrackData td = new TrackData();
        td.setCreated(new Timestamp(new Date().getTime() - 1000L));
        td.setCreatedBy("cr-" + suffix);
        td.setModified(new Timestamp(new Date().getTime()));
        td.setModifiedBy("mo-" + suffix);
        user.setTrackData(td);
        user.addRole(role);
        return user;
    }

    public static SystemRole createSystemRole(String name, boolean af, boolean ma, SystemRole.SystemType type) {
        SystemRole role = new SystemRole(type);
        role.setAcceptFrom(af);
        role.setMustAccept(ma);
        role.setName(name);
        role.setNotify(true);
        return role;
    }
}
