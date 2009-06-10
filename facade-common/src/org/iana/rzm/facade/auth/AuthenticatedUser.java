package org.iana.rzm.facade.auth;

import java.io.*;

/**
 * <p>This class represents an authenticated user of the system i.e. an individual who provided valid credentials
 * and got identified by the system.</p>
 *
 * <p>Note that to obtain an <code>AuthenticatedUser</code> instance <code>AuthenticationService</code>
 * may be required to be called several times, like, the system policy may force a user to get identified
 * via both a password and SecurID token.</p>
 *
 * @author Patrycja Wegrzynowicz
 */
public class AuthenticatedUser implements Serializable {

    /**
     * A identifier of a user who got authenticated.
     */
    private long objId;
    /**
     * A name of a user who got authenticated.
     */
    private String userName;

    private boolean admin;
    /**
     * A flag representing whether the user has been invalidated.
     */
    private boolean invalidated;

    /**
     * <p>A package-accessible constructor to protect creation of this class outside of the package,
     * thus one is forced to perform authentication in order to get access to the system services.
     * (Tough it's still possible for a developer to inject a class into this package and then obtain
     * a valid instance of an <code>AuthenticatedUser</code>, however this would mean a deliberate
     * action taken by a developer.)</p>
     *
     * @param objId the identifier of the authenticated user.
     * @param userName the user name of the authenticated user.
     * @param admin the boolean flag indicating whether the authenticated user is an administrator or not.
     * @throws IllegalArgumentException when user is null
     */
    public AuthenticatedUser(long objId, String userName, boolean admin) {
        this.objId = objId;
        this.userName = userName;
        this.admin = admin;
    }


    /**
     * Invalidates this user object. After the invalidation happens the object can not be used as a valid
     * authenticated object. Every call to <code>checkPermission</code> will raise a
     * <code>UserInvalidatedException</code>.
     */
    public void invalidate() {
        invalidated = true;
    }

    public long getObjId() {
        return objId;
    }

    public String getUserName() {
        return userName;
    }


    public boolean isAdmin() {
        return admin;
    }

    public boolean isInvalidated() {
        return invalidated;
    }
}
