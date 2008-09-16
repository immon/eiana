package org.iana.rzm.facade.admin.users;

import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.user.*;
import org.iana.criteria.Criterion;

import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Piotr Tkaczyk
 */
public class StatelessAdminUserServiceImpl implements StatelessAdminUserService {

    UserManager userManager;

    public static final Role IANA = new AdminRole(AdminRole.AdminType.IANA);

    public StatelessAdminUserServiceImpl(UserManager userManager) {
        CheckTool.checkNull(userManager, "user manager");
        this.userManager = userManager;
    }

    public UserVO getUser(String userName, AuthenticatedUser authUser) throws AccessDeniedException {
        CheckTool.checkEmpty(userName, "user name");
        RZMUser retUser = this.userManager.get(userName);
        CheckTool.checkNull(retUser, "no such user: " + userName);
        return UserConverter.convert(retUser);
    }

    public UserVO getUser(long id, AuthenticatedUser authUser) throws AccessDeniedException {
        RZMUser retUser = this.userManager.get(id);
        CheckTool.checkNull(retUser, "no such user: " + id);
        return UserConverter.convert(retUser);
    }

    public void createUser(UserVO userVO, AuthenticatedUser authUser) throws AccessDeniedException {
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
        newUser.setCreatedBy(authUser.getUserName());
        this.userManager.create(newUser);
    }

    public void updateUser(UserVO userVO, AuthenticatedUser authUser) throws AccessDeniedException {
        CheckTool.checkNull(userVO, "userVO");
        RZMUser user = userManager.get(userVO.getObjId());
        RZMUser updateUser = UserConverter.convert(user, userVO);
        updateUser.setTrackData(user.getTrackData());
        updateUser.setModified(new Timestamp(System.currentTimeMillis()));
        updateUser.setModifiedBy(authUser.getUserName());
        this.userManager.update(updateUser);
    }

    public void deleteUser(String userName, AuthenticatedUser authUser) throws AccessDeniedException {
        CheckTool.checkEmpty(userName, "user Name");
        this.userManager.delete(userName);
    }

    public void deleteUser(long id, AuthenticatedUser authUser) throws AccessDeniedException {
        RZMUser retUser = this.userManager.get(id);
        CheckTool.checkNull(retUser, "no such user: " + id);
        this.userManager.delete(retUser);
    }

    public List<UserVO> findUsers(AuthenticatedUser authUser) throws AccessDeniedException {
        return findUsers(null, authUser);
    }

    public List<UserVO> findUsers(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException {
        List<UserVO> userVOs = new ArrayList<UserVO>();
        for (RZMUser user : this.userManager.find(criteria))
            userVOs.add(UserConverter.convert(user));

        return userVOs;
    }

    public int count(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException {
        return userManager.count(criteria);
    }

    public List<UserVO> find(Criterion criteria, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException {
        List<UserVO> userVOs = new ArrayList<UserVO>();
        for (RZMUser user : this.userManager.find(criteria, offset, limit))
            userVOs.add(UserConverter.convert(user));

        return userVOs;
    }


    public UserVO get(long id, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        return getUser(id, authUser);
    }

    public List<UserVO> find(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        List<UserVO> userVOs = new ArrayList<UserVO>();
        for (RZMUser user : this.userManager.find(criteria))
            userVOs.add(UserConverter.convert(user));

        return userVOs;
    }
}
