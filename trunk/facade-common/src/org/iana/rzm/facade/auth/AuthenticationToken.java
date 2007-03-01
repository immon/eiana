package org.iana.rzm.facade.auth;

import java.util.Set;
import java.util.HashSet;

/**
 * <p>This class represents an authentication process and holds a set of valid credentials obtained so far.</p>
 *
 * @author Patrycja Wegrzynowicz
 */
public class AuthenticationToken {

    private String userName;
    final private Set<Authentication> credentials = new HashSet<Authentication>();

    AuthenticationToken(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    void addCredential(Authentication credential) {
        credentials.add(credential);
    }

    boolean hasCredential(Authentication credential) {
        return credentials.contains(credential);
    }
}
