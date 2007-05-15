package org.iana.rzm.facade.system.domain;

import org.iana.rzm.facade.common.TrackDataVO;
import org.iana.rzm.facade.common.Trackable;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.common.Name;

import java.util.Set;
import java.util.List;
import java.sql.Timestamp;
import java.io.Serializable;

/**
 * A simplified version of DomainVO used with lists of domains. 
 *
 * @author Patrycja Wegrzynowicz
 */
public class SimpleDomainVO implements IDomainVO, Trackable, Serializable {

    private String name;
    private Set<RoleVO.Type> roles;

    private Long objId;
    private TrackDataVO trackData = new TrackDataVO();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<RoleVO.Type> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleVO.Type> roles) {
        this.roles = roles;
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public Timestamp getCreated() {
        return trackData.getCreated();
    }

    public void setCreated(Timestamp created) {
        trackData.setCreated(created);
    }

    public Timestamp getModified() {
        return trackData.getModified();
    }

    public void setModified(Timestamp modified) {
        trackData.setModified(modified);
    }

    public String getCreatedBy() {
        return trackData.getCreatedBy();
    }

    public void setCreatedBy(String createdBy) {
        trackData.setCreatedBy(createdBy);
    }

    public String getModifiedBy() {
        return trackData.getModifiedBy();
    }

    public void setModifiedBy(String modifiedBy) {
        trackData.setModifiedBy(modifiedBy);
    }

//------------------

    public ContactVO getSupportingOrg() {
        throw new UnsupportedOperationException();
    }

    public void setSupportingOrg(ContactVO supportingOrg) {
        throw new UnsupportedOperationException();
    }

    public List<ContactVO> getAdminContacts() {
        throw new UnsupportedOperationException();
    }

    public void setAdminContacts(List<ContactVO> adminContacts) {
        throw new UnsupportedOperationException();
    }

    public List<ContactVO> getTechContacts() {
        throw new UnsupportedOperationException();
    }

    public void setTechContacts(List<ContactVO> techContacts) {
        throw new UnsupportedOperationException();
    }

    public List<HostVO> getNameServers() {
        throw new UnsupportedOperationException();
    }

    public void setNameServers(List<HostVO> nameServers) {
        throw new UnsupportedOperationException();
    }

    public String getRegistryUrl() {
        throw new UnsupportedOperationException();
    }

    public void setRegistryUrl(String registryUrl) {
        throw new UnsupportedOperationException();
    }

    public Name getWhoisServer() {
        throw new UnsupportedOperationException();
    }

    public void setWhoisServer(Name whoisServer) {
        throw new UnsupportedOperationException();
    }

    public Set<Breakpoint> getBreakpoints() {
        throw new UnsupportedOperationException();
    }

    public void setBreakpoints(Set<Breakpoint> breakpoints) {
        throw new UnsupportedOperationException();
    }

    public String getSpecialInstructions() {
        throw new UnsupportedOperationException();
    }

    public void setSpecialInstructions(String specialInstructions) {
        throw new UnsupportedOperationException();
    }

    public Status getStatus() {
        throw new UnsupportedOperationException();
    }

    public void setStatus(Status status) {
        throw new UnsupportedOperationException();
    }

    public State getState() {
        throw new UnsupportedOperationException();
    }

    public void setState(State state) {
        throw new UnsupportedOperationException();
    }
}
