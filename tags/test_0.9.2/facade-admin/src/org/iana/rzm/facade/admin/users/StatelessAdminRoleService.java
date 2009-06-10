package org.iana.rzm.facade.admin.users;

import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.converter.RoleConverter;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.user.Role;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.criteria.Criterion;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Piotr Tkaczyk
 */
public interface StatelessAdminRoleService {

    public RoleVO getRole(long id, AuthenticatedUser authUser) throws AccessDeniedException;

    public long createRole(RoleVO roleVO, AuthenticatedUser authUser) throws AccessDeniedException;

    public void updateRole(RoleVO roleVO, AuthenticatedUser authUser) throws AccessDeniedException;

    public void deleteRole(long id, AuthenticatedUser authUser) throws AccessDeniedException;

    public List<RoleVO> findRoles(AuthenticatedUser authUser) throws AccessDeniedException;

    public List<RoleVO> findRoles(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException;

    public int count(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException;

    public List<RoleVO> find(Criterion criteria, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException;

    public List<RoleVO> find(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public RoleVO get(long id, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, NoObjectFoundException;
}
