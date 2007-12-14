package org.iana.rzm.web.model;

import org.iana.rzm.facade.user.*;
import org.iana.rzm.web.util.*;

import java.util.*;

public class UserVOWrapper extends ValueObject implements PaginatedEntity {

    private UserVO vo;

    public UserVOWrapper() {
        this(new UserVO());
    }

    public UserVOWrapper(UserVO vo) {
        this.vo = vo;
    }

    public UserVO getVo(){
        return vo;
    }

    public long getId() {
        return vo.getObjId();
    }

    public String getUserName() {
        return vo.getUserName();
    }

    public void setUserName(String userName) {
        vo.setUserName(userName);
    }

    public String getFirstName() {
        return vo.getFirstName();
    }

    public void setFirstName(String name){
        vo.setFirstName(name);
    }

    public String getModified() {
        Date d = vo.getModified();
        return d == null ?
                DateUtil.formatDate(vo.getCreated()) :
                DateUtil.formatDate(d);
    }

    public void setEmail(String email) {
        vo.setEmail(email);
    }

    public String getEmail() {
        return vo.getEmail();
    }

    public String getPublickey() {
        return vo.getPublicKey();
    }

    public void setPublicKey(String key) {
        vo.setPublicKey(key);
    }

    public boolean isUseSecureId() {
        return vo.isSecurID();
    }

    public void setUseSecureId(boolean value) {
        vo.setSecurID(value);
    }

    public boolean isAdmin(){
        return vo.isAdmin();
    }

    public void setPassword(String pass){
        vo.setPassword(pass);
    }
    
    public String getLastName() {
        return vo.getLastName();
    }
    
    public void setLastName(String lastName) {
        vo.setLastName(lastName);
    }

    public String getOrganization() {
        return vo.getOrganization();
    }

    public void setOrganization(String org) {
        vo.setOrganization(org);
    }

    public List<String> listSystemUserRoles() {
        List<String> rolesNames = new ArrayList<String>();
        List<SystemRoleVOWrapper> list = getSystemRoles();
        for (SystemRoleVOWrapper systemRoleVOWrapper : list) {
            rolesNames.add(systemRoleVOWrapper.getTypeAsString());
        }
        return rolesNames;
    }

    public List<String> listSystemRolesForDomain(String domain){
        List<String> rolesNames = new ArrayList<String>();
        List<SystemRoleVOWrapper> list = getSystemRoles();
        for (SystemRoleVOWrapper systemRoleVOWrapper : list) {
            if(systemRoleVOWrapper.getDomainName().equals(domain)){
                rolesNames.add(systemRoleVOWrapper.getTypeAsString());
            }
        }

        return rolesNames;
    }


    public boolean isAc() {
        return isInRole(SystemRoleVOWrapper.SystemType.AC);
    }

    public boolean isTc() {
        return isInRole(SystemRoleVOWrapper.SystemType.TC);
    }

    public List<AdminRoleVOWrapper> getAdminRoles() {
        Set<RoleVO> roleVOs = vo.getRoles();
        List<AdminRoleVOWrapper> roles = new ArrayList<AdminRoleVOWrapper>();
        for (RoleVO roleVO : roleVOs) {
            if (roleVO.isAdmin()) {
                roles.add(new AdminRoleVOWrapper((AdminRoleVO) roleVO));
            }
        }
        return roles;
    }

    public List<SystemRoleVOWrapper> getSystemRoles() {
        Set<RoleVO> roleVOs = vo.getRoles();
        List<SystemRoleVOWrapper> roles = new ArrayList<SystemRoleVOWrapper>();
        for (RoleVO roleVO : roleVOs) {
            if (roleVO.isAdmin()) {
                continue;
            }
            roles.add(new SystemRoleVOWrapper((SystemRoleVO) roleVO));
        }
        return roles;
    }

    public boolean isAccessEnabled(String domainName) {
        return vo.hasAccessToDomain(domainName);
    }

    public List<RoleUserDomain> getUserDomains() {
        List<SystemRoleVOWrapper> list = getSystemRoles();
        List<RoleUserDomain> result = new ArrayList<RoleUserDomain>();
        int id = 0;
        for (SystemRoleVOWrapper systemRoleVOWrapper : list) {
            RoleUserDomain o = new RoleUserDomain(
                id, systemRoleVOWrapper.getDomainName(), systemRoleVOWrapper.getTypeAsString(), null, systemRoleVOWrapper.getId());
            o.setAcceptFrom(systemRoleVOWrapper.isAcceptFrom());
            o.setNotify(systemRoleVOWrapper.isNotify());
            o.setMustAccept(systemRoleVOWrapper.isMustAccept());
            result.add(o);
            id++;
        }
        return result;
    }

    public boolean containsRole(SystemRoleVOWrapper role) {
        for (SystemRoleVOWrapper systemRoleVOWrapper : getSystemRoles()) {
            if(systemRoleVOWrapper.getId() == role.getId()){
                return true;
            }
        }

        return false;
    }


    public void addRole(RoleVOWrapper role) {
        vo.addRole(role.getVo());
    }

    public void setRoles(List<RoleVOWrapper> roles) {
        vo.setRoles(new HashSet<RoleVO>());
        for (RoleVOWrapper role : roles) {
            vo.addRole(role.getVo());
        }
    }

    private boolean isInRole(RoleVOWrapper.Type type) {
        List<SystemRoleVOWrapper> list = getSystemRoles();
        for (SystemRoleVOWrapper role : list) {
            if (role.getType().equals(type)) {
                return true;
            }
        }

        return false;
    }
}
