package org.iana.rzm.facade.admin;

import org.iana.rzm.facade.common.AbstractRZMStatefulService;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.criteria.Criterion;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 * @author: Piotr Tkaczyk
 */
public class GuardedAdminUserServiceBean  extends AdminFinderServiceBean<UserVO> implements AdminUserService {
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
        return findUsers(null);
    }

    public List<UserVO> findUsers(Criterion criteria) {
        isUserInRole();
        List<UserVO> userVOs = new ArrayList<UserVO>();
        for (RZMUser user : this.userManager.find(criteria))
            userVOs.add(UserConverter.convertSimple(user));

        return userVOs;
    }

    public int count(Criterion criteria) {
        isUserInRole();
        CheckTool.checkNull(criteria, "criteria");
        return userManager.count(criteria);
    }

    public List<UserVO> find(Criterion criteria, int offset, int limit) {
        isUserInRole();
        CheckTool.checkNull(criteria, "criteria");
        List<UserVO> userVOs = new ArrayList<UserVO>();
        for (RZMUser user : this.userManager.find(criteria, offset, limit))
            userVOs.add(UserConverter.convert(user));

        return userVOs;
    }
}
