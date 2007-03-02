package org.iana.rzm.facade.auth;

import javax.mail.AuthenticationFailedException;


/**
 * <p>This interface defines an authentication operation available to the UI-tier. It constitues a common
 * interface for all the authentication mechanisms and thus allows to define a system-wide authentication policy
 * which may involve a useage of several authentication mechanisms in order to get fully identified by the system.</p>
 *
 * @author Patrycja Wegrzynowicz
 */
public interface AuthenticationService {

    /**
     * <p>Authenticates a user in the system. A user provided data containing a user name and an additional
     * part specific to am authentication method (for example, password for a simple password-based authentication).</p>
     *
     * <p>Note that in case of a successful authentication it's possible for this method to not return
     * an <code>AuthenticatedUser</code> instance. Instead an additional authentication may be requested by throwing
     * <code>AuthenticationRequiredException</code>. In that case the exception contains information
     * what type of authentication is required together with the <code>AuthenticationToken</code>instance.
     * Thus the UI-tier is able to process this exception appropriately and eventually issueing next
     * request to authenticate by providing the obtained <code>AuthenticationToken</code> instance and additional
     * authentication data.</p>
     *
     * @param data the data allowing to authenticate a user; specific to the authentication type
     * @return the authenticated user in case of successful authentication and when no additional authentication is required
     * @throws AuthenticationFailedException thrown when a provided data do not form a valid credential
     * @throws AuthenticationRequiredException thrown when this authentication was successful but an additional authentication is required
     */
    public AuthenticatedUser authenticate(AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException;

    /**
     * <p>Continues an authentication of a user in the system. The authentication process is represented by a given
     * authentication token while <code>data</code> contains an additional authentication data.
     * This data should match a requested authentication mechanism.</p>
     *
     * @param token
     * @param data the data allowing to authenticate a user; specific to the authentication type
     * @return the authenticated user in case of successful authentication
     * @throws AuthenticationFailedException thrown when a provided data do not form a valid credential
     * @throws AuthenticationRequiredException thrown when an additional authentication is required
     */
    public AuthenticatedUser authenticate(AuthenticationToken token, AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException;
}
