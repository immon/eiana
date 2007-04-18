package org.iana.rzm.trans.transitionauth;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
@Entity
public class TransitionAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;

    public int hashCode() {
        return super.hashCode();
    }

    @OneToMany
    @JoinTable(name = "TransitionAuth_RZMUsers",
            inverseJoinColumns = @JoinColumn(name = "RZMUser_objId"))
    private Set<RZMUser> users = new HashSet<RZMUser>();
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "TransitionAuth_Roles",
            inverseJoinColumns = @JoinColumn(name = "Role_objId"))
    private Set<Role> roles = new HashSet<Role>();

    private TransitionAuth() {}

    public TransitionAuth(Set<RZMUser> users, Set<Role> roles) {
        this.users.addAll(users);
        this.roles.addAll(roles);
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public void addUser(RZMUser user) {
        users.add(user);
    }

    public void addRole(Role role) {
        roles.add(role);
    }
    
    public Set<RZMUser> getUsers() {
        return users;
    }

    public Set<Role> getRoles() {
        return roles;
    }
}
