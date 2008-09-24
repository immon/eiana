package org.iana.rzm.facade.admin.users;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractFinderService;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.user.UserManager;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public class GuardedAdminUserServiceBean  extends AbstractFinderService<UserVO> implements AdminUserService {

    StatelessAdminUserService statelessAdminUserService;

    public GuardedAdminUserServiceBean(UserManager userManager, StatelessAdminUserService statelessAdminUserService) {
        super(userManager);
        this.statelessAdminUserService = statelessAdminUserService;
    }

    public UserVO getUser(String userName) throws AccessDeniedException {
        return statelessAdminUserService.getUser(userName, getAuthenticatedUser());
    }

    public UserVO getUser(long id) throws AccessDeniedException {
        return statelessAdminUserService.getUser(id, getAuthenticatedUser());
    }

    public void createUser(UserVO userVO) throws AccessDeniedException {
        statelessAdminUserService.createUser(userVO, getAuthenticatedUser());
    }

    public void updateUser(UserVO userVO) throws AccessDeniedException {
        statelessAdminUserService.updateUser(userVO, getAuthenticatedUser());
    }

    public void deleteUser(String userName) throws AccessDeniedException {
        statelessAdminUserService.deleteUser(userName, getAuthenticatedUser());
    }

    public void deleteUser(long id) throws AccessDeniedException {
        statelessAdminUserService.deleteUser(id, getAuthenticatedUser());
    }

    public List<UserVO> findUsers() throws AccessDeniedException {
        return statelessAdminUserService.findUsers(getAuthenticatedUser());
    }

    public List<UserVO> findUsers(Criterion criteria) throws AccessDeniedException {
        return statelessAdminUserService.findUsers(criteria, getAuthenticatedUser());
    }

    public int count(Criterion criteria) throws AccessDeniedException {
        return statelessAdminUserService.count(criteria, getAuthenticatedUser());
    }

    public List<UserVO> find(Criterion criteria, int offset, int limit) throws AccessDeniedException {
        return statelessAdminUserService.find(criteria, offset, limit, getAuthenticatedUser());
    }


    public UserVO get(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        return statelessAdminUserService.get(id, getAuthenticatedUser());
    }

    public List<UserVO> find(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        return statelessAdminUserService.find(criteria, getAuthenticatedUser());
    }
}
