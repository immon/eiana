package org.iana.rzm.user.hibernate.test.common;

import org.iana.rzm.common.TrackData;
import org.iana.rzm.user.MD5Password;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.dns.validator.InvalidDomainNameException;

import java.sql.Timestamp;

/**
 * @author Jakub Laszkiewicz
 */
public class HibernateMappingTestUtil {
    public static TrackData setupTrackedObject(TrackData to, String prefix, Long id) {
        to.setCreated(new Timestamp(System.currentTimeMillis()));
        to.setCreatedBy(prefix + "-creator");
        to.setModified(new Timestamp(System.currentTimeMillis()));
        to.setModifiedBy(prefix + "-modifier");
        return to;
    }

    public static SystemRole setupRole(SystemRole role, String prefix, boolean flag) throws InvalidDomainNameException {
        role.setAcceptFrom(flag);
        role.setMustAccept(!flag);
        role.setName(prefix + "-role");
        role.setNotify(flag);
        role.setType(SystemRole.SystemType.AC);
        return role;
    }

    public static RZMUser setupUser(RZMUser user, String prefix, boolean flag) {
        user.setEmail(prefix + "-user@nask.pl");
        user.setFirstName(prefix + " first name");
        user.setLastName(prefix + "last name");
        user.setLoginName(prefix + "-loginname");
        user.setOrganization(prefix + " organization");
        user.setPassword(prefix + "password");
        user.setSecurID(flag);
        return user;
    }
}
