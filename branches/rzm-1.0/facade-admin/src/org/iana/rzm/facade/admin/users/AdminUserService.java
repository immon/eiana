package org.iana.rzm.facade.admin.users;

import org.iana.criteria.Criterion;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.services.FinderService;
import org.iana.rzm.facade.services.RZMStatefulService;
import org.iana.rzm.facade.user.UserVO;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminUserService extends RZMStatefulService, FinderService<UserVO> {

    public UserVO getUser(String userName) throws AccessDeniedException;

    public UserVO getUser(long id) throws AccessDeniedException;

    public void createUser(UserVO user) throws AccessDeniedException;

    public void updateUser(UserVO user) throws AccessDeniedException;

    public void deleteUser(String userName) throws AccessDeniedException;

    public void deleteUser(long id) throws AccessDeniedException;

    public List<UserVO> findUsers() throws AccessDeniedException;

    public List<UserVO> findUsers(Criterion criteria) throws AccessDeniedException;
}
