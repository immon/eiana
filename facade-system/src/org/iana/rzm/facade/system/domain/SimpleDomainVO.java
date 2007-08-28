package org.iana.rzm.facade.system.domain;

import org.iana.rzm.common.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.user.*;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A simplified version of DomainVO used with lists of domains.
 *
 * @author Patrycja Wegrzynowicz
 */
public class SimpleDomainVO implements IDomainVO, Trackable, Serializable {

    private String name;
    private Set<RoleVO.Type> roles;
    private String specialInstructions;

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

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
       this.specialInstructions = specialInstructions;
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

    public void setAdminContact(List<ContactVO> adminContacts) {
        throw new UnsupportedOperationException();
    }

    public List<ContactVO> getTechContacts() {
        throw new UnsupportedOperationException();
    }

    public ContactVO getAdminContact() {
        throw new UnsupportedOperationException();
    }

    public void setAdminContact(ContactVO adminContacts) {
        throw new UnsupportedOperationException();
    }

    public ContactVO getTechContact() {
        throw new UnsupportedOperationException();
    }

    public void setTechContact(ContactVO techContacts) {
        throw new UnsupportedOperationException();
    }

    public void setTechContact(List<ContactVO> techContacts) {
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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleDomainVO that = (SimpleDomainVO) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (objId != null ? !objId.equals(that.objId) : that.objId != null) return false;
        if (roles != null ? !roles.equals(that.roles) : that.roles != null) return false;
        if (trackData != null ? !trackData.equals(that.trackData) : that.trackData != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        result = 31 * result + (objId != null ? objId.hashCode() : 0);
        result = 31 * result + (trackData != null ? trackData.hashCode() : 0);
        return result;
    }
}
