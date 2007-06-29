package org.iana.rzm.facade.system.domain;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.user.UserVO;

import java.util.List;

/**
 * <p>This is a stateful domain facade service providing the basic operations on domains for the eIANA RZM end-users.</p>
 *
 * <p>Note that this service is stateful and designed to be called in a user context. Thus all the methods
 * isAllocatedForSpecialUse whether the user has been granted respective permissions. Often the permission checks are performed
 * in a context of a given resource, like, a domain object.</p>
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public interface SystemDomainService extends RZMStatefulService {

    /**
     * <p>Returns a domain object with a given id. It checks whether the user has been granted access
     * to the specified domain. For example, IANA stuff is allowed to access all domains while system users
     * are permitted to access only those domain objects for which they are in one of the roles: AC, TC, SO.</p>
     *
     * @param id the identifier of the domain to be found
     * @return the domain object if found and the user is allowed to access it
     * @throws AccessDeniedException when the user is not allowed to access this method or the given domain object
     * @throws NoObjectFoundException when the domain is not found
     * @throws InfrastructureException when a low-level exception happened
     */
    IDomainVO getDomain(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException;

    /**
     * <p>Returns a domain object with a given name. It checks whether the user has been granted access
     * to the specified domain. For example, IANA stuff is allowed to access all domains while system users
     * are permitted to access only those domain objects for which they are in one of the roles: AC, TC, SO.</p>
     *
     * @param name the name of the domain to be found
     * @return the domain object if found and the user is allowed to access it
     * @throws AccessDeniedException when the user is not allowed to access this method or the given domain object
     * @throws NoObjectFoundException when the domain is not found
     * @throws InfrastructureException when a low-level exception happened
     */
    IDomainVO getDomain(String name) throws AccessDeniedException, InfrastructureException, NoObjectFoundException;

    /**
     * <p>Returns a list of the domains objects that an authenticated user is granted access to.
     * A shortcut to <code>findUserDomains(authenticatedUser.getUserName())</code>.</p>
     *
     * @return the list of the domains managed/administered by a given user, an empty list if no domains found.
     * @throws AccessDeniedException when the user is not allowed to access this method
     * @throws InfrastructureException when a low-level exception happened
     */
    List<SimpleDomainVO> findUserDomains() throws AccessDeniedException, InfrastructureException;

    /**
     * <p>Returns a list of the domains objects that the provided user is granted access to. For example,
     * IANA stuff is allowed to access all domains thus this method returns all existing domain objects. While
     * system users are allowed to access only the domains for which they are in one of the roles: AC, TC, SO, thus
     * for them the returned list is limited only to those domains that they manage/administer at the moment of time.</p>
     *
     * @return the list of the domains managed/administered by a given user, an empty list if no domains found.
     * @throws AccessDeniedException when the user is not allowed to access this method
     * @throws InfrastructureException when a low-level exception happened
     */
    List<SimpleDomainVO> findUserDomains(String userName) throws AccessDeniedException, InfrastructureException;

    /**
     * <p>Enables or disables access to the domain identified by <code>domainId</code> for the user identified by
     * <code>userId</code>.</p>
     * @param userId user identifier
     * @param domainId domain identifier
     * @param access access to be set
     */
    void setAccessToDomain(long userId, long domainId, boolean access) throws AccessDeniedException;

    /**
     * <p>Returns a list of users, who have a role for the domain <code>domainName</code>,
     * when <code>havingAccessOnly</code> is set to <code>false</code>.
     * Result may be narrowed to users, who have access to this domain, by setting
     * <code>havingAccessOnly</code> to <code>true</code>.</p>
     * @param domainName name of the domain
     * @param havingAccessOnly <code>false</code> when a list of users, who has a role
     * for the domain <code>domainName</code> has to be returned or <code>true</code>
     * when a list of users, who have access to this domain has to be returned.
     * @return the list of users, who have a role for the domain <code>domainName</code>,
     * when <code>havingAccessOnly</code> is set to <code>false</code> or the list of users,
     * who have access to this domain when <code>havingAccessOnly</code> is set to <code>true</code>.  
     */
    List<UserVO> findDomainUsers(String domainName, boolean havingAccessOnly) throws AccessDeniedException;
}
