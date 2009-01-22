package org.iana.rzm.facade.admin.users;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.services.AbstractRZMStatelessService;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.user.UserManager;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class GuardedStatelessAdminUserServiceImpl extends AbstractRZMStatelessService implements StatelessAdminUserService {

    UserManager userManager;
    StatelessAdminUserService statelessAdminUserService;

    public GuardedStatelessAdminUserServiceImpl(UserManager userManager, StatelessAdminUserService statelessAdminUserService) {
        super(userManager);
        CheckTool.checkNull(userManager, "user manager");
        CheckTool.checkNull(statelessAdminUserService, "stateless admin user service");
        this.userManager = userManager;
        this.statelessAdminUserService = statelessAdminUserService;
    }

    public UserVO getUser(String userName, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminUserService.getUser(userName, authUser);
    }

    public UserVO getUser(long id, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminUserService.getUser(id, authUser);
    }

    public void createUser(UserVO userVO, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        statelessAdminUserService.createUser(userVO, authUser);
    }

    public void updateUser(UserVO userVO, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        statelessAdminUserService.updateUser(userVO, authUser);
    }

    public void deleteUser(String userName, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        statelessAdminUserService.deleteUser(userName, authUser);
    }

    public void deleteUser(long id, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        statelessAdminUserService.deleteUser(id, authUser);
    }

    public List<UserVO> findUsers(AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminUserService.findUsers(authUser);
    }

    public List<UserVO> findUsers(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminUserService.findUsers(criteria, authUser);
    }

    public int count(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return userManager.count(criteria);
    }

    public List<UserVO> find(Criterion criteria, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminUserService.find(criteria, offset, limit, authUser);
    }


    public UserVO get(long id, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        return getUser(id, authUser);
    }

    public List<UserVO> find(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        isIana(authUser);
        return statelessAdminUserService.find(criteria, authUser);
    }
}
