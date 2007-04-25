package org.iana.rzm.facade.admin;

import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.user.UserVO;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminUserService extends RZMStatefulService {

    public UserVO getUser(String userName);

    public UserVO getUser(long id);

    public void createUser(UserVO user);

    public void updateUser(UserVO user);

    public void deleteUser(String userName);

    public void deleteUser(long id);

    public List<UserVO> findUsers();

    public List<UserVO> findUsers(UserCriteria criteria);
   
}
