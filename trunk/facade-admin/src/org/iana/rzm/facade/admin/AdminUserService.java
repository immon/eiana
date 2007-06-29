package org.iana.rzm.facade.admin;

import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.criteria.Criterion;
import org.iana.criteria.Order;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminUserService extends RZMStatefulService, AdminFinderService<UserVO> {

    public UserVO getUser(String userName) throws AccessDeniedException;

    public UserVO getUser(long id) throws AccessDeniedException;

    public void createUser(UserVO user) throws AccessDeniedException;

    public void updateUser(UserVO user) throws AccessDeniedException;

    public void deleteUser(String userName) throws AccessDeniedException;

    public void deleteUser(long id) throws AccessDeniedException;

    public List<UserVO> findUsers() throws AccessDeniedException;

    public List<UserVO> findUsers(Criterion criteria) throws AccessDeniedException;
}
