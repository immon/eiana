package org.iana.rzm.facade.system.domain;

import org.iana.rzm.common.Name;
import org.iana.rzm.facade.auth.AccessDeniedException;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * This class is used as a wrapper around DomainVO to limit access to domain attributes.
 *
 * @author Patrycja Wegrzynowicz
 */
class SystemDomainVO implements IDomainVO {

    private IDomainVO domain;

    public SystemDomainVO(IDomainVO domain) {
        this.domain = domain;    
    }

    public Long getObjId() {
        return domain.getObjId();
    }

    public void setObjId(Long id) {
        domain.setObjId(id);
    }

    public ContactVO getSupportingOrg() {
        return domain.getSupportingOrg();
    }

    public void setSupportingOrg(ContactVO supportingOrg) {
        domain.setSupportingOrg(supportingOrg);
    }

    public List<ContactVO> getAdminContacts() {
        return domain.getAdminContacts();
    }

    public void setAdminContacts(List<ContactVO> adminContacts) {
        domain.setAdminContacts(adminContacts);
    }

    public List<ContactVO> getTechContacts() {
        return domain.getTechContacts();
    }

    public void setTechContacts(List<ContactVO> techContacts) {
        domain.setTechContacts(techContacts);
    }

    public List<HostVO> getNameServers() {
        return domain.getNameServers();
    }

    public void setNameServers(List<HostVO> nameServers) {
        domain.setNameServers(nameServers);
    }

    public String getRegistryUrl() {
        return domain.getRegistryUrl();
    }

    public void setRegistryUrl(String registryUrl) {
        domain.setRegistryUrl(registryUrl);
    }

    public Name getWhoisServer() {
        return domain.getWhoisServer();
    }

    public void setWhoisServer(Name whoisServer) {
        domain.setWhoisServer(whoisServer);
    }

    public Set<Breakpoint> getBreakpoints() {
        throw new AccessDeniedException("system user is not allowed to access special breakpoints");
    }

    public void setBreakpoints(Set<Breakpoint> breakpoints) {
        throw new AccessDeniedException("system user is not allowed to createDomainModificationTransaction breakpoints");
    }

    public String getSpecialInstructions() {
        throw new AccessDeniedException("system user is not allowed to access special instructions");
    }

    public void setSpecialInstructions(String specialInstructions) {
        throw new AccessDeniedException("system user is not allowed to createDomainModificationTransaction special instructions");
    }

    public Status getStatus() {
        return domain.getStatus();
    }

    public void setStatus(Status status) {
        domain.setStatus(status);
    }

    public State getState() {
        return domain.getState();
    }

    public void setState(State state) {
        domain.setState(state);
    }

    public String getName() {
        return domain.getName();
    }

    public Timestamp getCreated() {
        return domain.getCreated();
    }

    public Timestamp getModified() {
        return domain.getModified();
    }

    public String getCreatedBy() {
        return domain.getCreatedBy();
    }

    public String getModifiedBy() {
        return domain.getModifiedBy();
    }
}
