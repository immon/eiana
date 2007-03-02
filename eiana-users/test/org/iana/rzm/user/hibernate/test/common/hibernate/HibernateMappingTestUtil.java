package org.iana.rzm.user.hibernate.test.common.hibernate;

import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.user.MD5Password;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.User;

import java.sql.Timestamp;

/**
 * @author Jakub Laszkiewicz
 */
public class HibernateMappingTestUtil {
    public static TrackData setupTrackedObject(TrackData to, String prefix, Long id) {
        to.setCreated(new Timestamp(System.currentTimeMillis()));
        to.setCreatedBy(prefix + "-creator");
        to.setId(id);
        to.setModified(new Timestamp(System.currentTimeMillis()));
        to.setModifiedBy(prefix + "-modifier");
        return to;
    }

    public static Role setupRole(Role role, String prefix, boolean flag) throws InvalidNameException {
        role.setAcceptFrom(flag);
        role.setMustAccept(!flag);
        role.setName(prefix + "-role");
        role.setNotify(flag);
        role.setType(Role.Type.AC);
        return role;
    }

    public static User setupUser(User user, String prefix, boolean flag) {
        user.setEmail(prefix + "-user@nask.pl");
        user.setFirstName(prefix + " first name");
        user.setLastName(prefix + "last name");
        user.setLoginName(prefix + "-loginname");
        user.setOrganization(prefix + " organization");
        user.setPassword(new MD5Password(prefix + "password"));
        user.setSecurID(flag);
        return user;
    }
}
