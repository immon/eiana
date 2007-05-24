package org.iana.rzm.facade.admin;

import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.criteria.Criterion;
import org.iana.criteria.Order;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminUserService extends RZMStatefulService, AdminFinderService<UserVO> {

    public UserVO getUser(String userName);

    public UserVO getUser(long id);

    public void createUser(UserVO user);

    public void updateUser(UserVO user);

    public void deleteUser(String userName);

    public void deleteUser(long id);

    public List<UserVO> findUsers();

    public List<UserVO> findUsers(Criterion criteria);
}
