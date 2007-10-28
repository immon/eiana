package org.iana.rzm.facade.admin.users;

import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.admin.users.AdminUserService;
import org.iana.rzm.facade.services.AbstractFinderService;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.user.*;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.criteria.Criterion;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;

/**
 * @author: Piotr Tkaczyk
 */
public class GuardedAdminUserServiceBean  extends AbstractFinderService<UserVO> implements AdminUserService {
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

    public UserVO getUser(String userName) throws AccessDeniedException {
        isUserInRole();
        CheckTool.checkEmpty(userName, "user name");
        RZMUser retUser = this.userManager.get(userName);
        CheckTool.checkNull(retUser, "no such user: " + userName);
        return UserConverter.convert(retUser);
    }

    public UserVO getUser(long id) throws AccessDeniedException {
        isUserInRole();
        RZMUser retUser = this.userManager.get(id);
        CheckTool.checkNull(retUser, "no such user: " + id);
        return UserConverter.convert(retUser);
    }

    public void createUser(UserVO userVO) throws AccessDeniedException {
        isUserInRole();
        CheckTool.checkNull(userVO, "userVO");
        RZMUser newUser = UserConverter.convert(userVO);
        for (Role role : newUser.getRoles()) {
            role.setObjId(null);
            if (role instanceof SystemRole) {
                SystemRole systemRole = (SystemRole) role;
                systemRole.setAccessToDomain(true);
            }
        }
        newUser.setPassword(userVO.getPassword());
        newUser.setCreated(new Timestamp(System.currentTimeMillis()));
        newUser.setCreatedBy(getAuthenticatedUser().getUserName());
        this.userManager.create(newUser);
    }

    public void updateUser(UserVO userVO) throws AccessDeniedException {
        isUserInRole();
        CheckTool.checkNull(userVO, "userVO");
        RZMUser user = userManager.get(userVO.getObjId());
        RZMUser updateUser = UserConverter.convert(user, userVO);
        updateUser.setTrackData(user.getTrackData());
        updateUser.setModified(new Timestamp(System.currentTimeMillis()));
        updateUser.setModifiedBy(getAuthenticatedUser().getUserName());
        this.userManager.update(updateUser);
    }

    public void deleteUser(String userName) throws AccessDeniedException {
        isUserInRole();
        CheckTool.checkEmpty(userName, "user Name");
        this.userManager.delete(userName);
    }

    public void deleteUser(long id) throws AccessDeniedException {
        isUserInRole();
        RZMUser retUser = this.userManager.get(id);
        CheckTool.checkNull(retUser, "no such user: " + id);
        this.userManager.delete(retUser);
    }

    public List<UserVO> findUsers() throws AccessDeniedException {
        isUserInRole();
        return findUsers(null);
    }

    public List<UserVO> findUsers(Criterion criteria) throws AccessDeniedException {
        isUserInRole();
        List<UserVO> userVOs = new ArrayList<UserVO>();
        for (RZMUser user : this.userManager.find(criteria))
            userVOs.add(UserConverter.convert(user));

        return userVOs;
    }

    public int count(Criterion criteria) throws AccessDeniedException {
        isUserInRole();
        return userManager.count(criteria);
    }

    public List<UserVO> find(Criterion criteria, int offset, int limit) throws AccessDeniedException {
        isUserInRole();
        List<UserVO> userVOs = new ArrayList<UserVO>();
        for (RZMUser user : this.userManager.find(criteria, offset, limit))
            userVOs.add(UserConverter.convert(user));

        return userVOs;
    }


    public UserVO get(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        return getUser(id);
    }

    public List<UserVO> find(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        List<UserVO> userVOs = new ArrayList<UserVO>();
        for (RZMUser user : this.userManager.find(criteria))
            userVOs.add(UserConverter.convert(user));

        return userVOs;
    }
}
