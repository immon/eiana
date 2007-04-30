package org.iana.rzm.facade.admin;

import org.iana.rzm.facade.common.AbstractRZMStatefulService;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.common.validators.CheckTool;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 * @author: Piotr Tkaczyk
 */
public class GuardedAdminUserServiceBean extends AbstractRZMStatefulService implements AdminUserService {
      private static Set<Role> allowedRoles = new HashSet<Role>();

    static {
        allowedRoles.add(new AdminRole(AdminRole.AdminType.IANA));
    }

    UserManager userManager;

    private void isUserInRole() throws AccessDeniedException {
        isUserInRole(allowedRoles);
    }

    public GuardedAdminUserServiceBean(UserManager userMgr) {
        super(userMgr);
        CheckTool.checkNull(userMgr, "user manager");
        this.userManager = userMgr;
    }

    public UserVO getUser(String userName) {
        isUserInRole();
        CheckTool.checkEmpty(userName, "user name");
        RZMUser retUser = this.userManager.get(userName);
        CheckTool.checkNull(retUser, "no such user: " + userName);
        return UserConverter.convert(retUser);
    }

    public UserVO getUser(long id) {
        isUserInRole();
        RZMUser retUser = this.userManager.get(id);
        CheckTool.checkNull(retUser, "no such user: " + id);
        return UserConverter.convert(retUser);
    }

    public void createUser(UserVO userVO) {
        isUserInRole();
        CheckTool.checkNull(userVO, "userVO");
        RZMUser newUser = UserConverter.convert(userVO);
        this.userManager.create(newUser);
    }

    public void updateUser(UserVO userVO) {
        isUserInRole();
        CheckTool.checkNull(userVO, "userVO");
        RZMUser updateUser = UserConverter.convert(userVO);
        this.userManager.update(updateUser);
    }

    public void deleteUser(String userName) {
        isUserInRole();
        CheckTool.checkEmpty(userName, "user Name");
        this.userManager.delete(userName);
    }

    public void deleteUser(long id) {
        isUserInRole();
        RZMUser retUser = this.userManager.get(id);
        CheckTool.checkNull(retUser, "no such user: " + id);
        this.userManager.delete(retUser);
    }

    public List<UserVO> findUsers() {
        isUserInRole();
        List<UserVO> usersVO = new ArrayList<UserVO>();
        for (RZMUser user : this.userManager.findAll())
            usersVO.add(UserConverter.convert(user));
        return usersVO;
    }

    public List<UserVO> findUsers(UserCriteria criteria) {
        isUserInRole();
        // todo
        return null;
    }
}
