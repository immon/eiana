package org.iana.rzm.user;

import java.util.List;

public interface UserManager {

    public User get(long id) throws UserException;

    public User get(String loginName) throws UserException;

    public User create(User user) throws UserException;

    public User update(User user) throws UserException;

    public List<User> findAll() throws UserException;

    public List<User> find(UserCriteria criteria) throws UserException;
}
