package org.iana.rzm.user;

import java.util.List;

/**
 * This interface provides user management functionality.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface UserManager {

    public RZMUser get(long id) throws UserException;

    public RZMUser get(String loginName) throws UserException;

    public RZMUser create(RZMUser user) throws UserException;

    public List<RZMUser> findAll() throws UserException;

    public List<RZMUser> find(UserCriteria criteria) throws UserException;
}
