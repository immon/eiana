/**
 * org.iana.rzm.user.test.common.HibernateMappingTestUtil
 * (C) NASK 2006
 * jakubl, 2007-02-28 16:29:02
 */
package org.iana.rzm.user.test.common;

import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.user.MD5Password;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.User;

import java.sql.Timestamp;

/**
 * @author Jakub Laszkiewicz
 */
public class HibernateMappingTestUtil {
    public static TrackedObject setupTrackedObject(TrackedObject to, String prefix, Long id) {
        to.setCreated(new Timestamp(System.currentTimeMillis()));
        to.setCreatedBy(prefix + "-creator");
        to.setId(id);
        to.setModified(new Timestamp(System.currentTimeMillis()));
        to.setModifiedBy(prefix + "-modifier");
        return to;
    }

    public static Role setupRole(Role role, String prefix, boolean flag) throws InvalidNameException {
        HibernateMappingTestUtil.setupTrackedObject(role, prefix, System.currentTimeMillis());
        role.setAcceptFrom(flag);
        role.setMustAccept(!flag);
        role.setName(prefix + "-role");
        role.setNotify(flag);
        role.setType(Role.Type.AC);
        return role;
    }

    public static User setupUser(User user, String prefix, boolean flag) {
        HibernateMappingTestUtil.setupTrackedObject(user, prefix, System.currentTimeMillis());
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
