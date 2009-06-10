package org.iana.rzm.facade.admin.users;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.user.UserVO;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public interface StatelessAdminUserService {

    public UserVO getUser(String userName, AuthenticatedUser authUser) throws AccessDeniedException;

    public UserVO getUser(long id, AuthenticatedUser authUser) throws AccessDeniedException;

    public void createUser(UserVO userVO, AuthenticatedUser authUser) throws AccessDeniedException;

    public void updateUser(UserVO userVO, AuthenticatedUser authUser) throws AccessDeniedException;

    public void deleteUser(String userName, AuthenticatedUser authUser) throws AccessDeniedException;

    public void deleteUser(long id, AuthenticatedUser authUser) throws AccessDeniedException;

    public List<UserVO> findUsers(AuthenticatedUser authUser) throws AccessDeniedException;

    public List<UserVO> findUsers(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException;

    public int count(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException;

    public List<UserVO> find(Criterion criteria, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException;


    public UserVO get(long id, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, NoObjectFoundException;
    
    public List<UserVO> find(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;
}
