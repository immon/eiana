package org.iana.rzm.facade.system.domain;

import org.iana.rzm.common.Name;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DomainVO extends SimpleDomainVO implements IDomainVO, Serializable {

    private ContactVO supportingOrg;
    private List<ContactVO> adminContacts;
    private List<ContactVO> techContacts;
    private List<HostVO> nameServers;
    private String registryUrl;
    private Name whoisServer;
    private Set<Breakpoint> breakpoints;
    private String specialInstructions;
    private Status status;
    private State state;

    public ContactVO getSupportingOrg() {
        return supportingOrg;
    }

    public void setSupportingOrg(ContactVO supportingOrg) {
        this.supportingOrg = supportingOrg;
    }

    public List<ContactVO> getAdminContacts() {
        return adminContacts;
    }

    public void setAdminContacts(List<ContactVO> adminContacts) {
        this.adminContacts = adminContacts;
    }

    public List<ContactVO> getTechContacts() {
        return techContacts;
    }

    public void setTechContacts(List<ContactVO> techContacts) {
        this.techContacts = techContacts;
    }

    public List<HostVO> getNameServers() {
        return nameServers;
    }

    public void setNameServers(List<HostVO> nameServers) {
        this.nameServers = nameServers;
    }

    public String getRegistryUrl() {
        return registryUrl;
    }

    public void setRegistryUrl(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    public Name getWhoisServer() {
        return whoisServer;
    }

    public void setWhoisServer(Name whoisServer) {
        this.whoisServer = whoisServer;
    }

    public Set<Breakpoint> getBreakpoints() {
        return breakpoints;
    }

    public void setBreakpoints(Set<Breakpoint> breakpoints) {
        this.breakpoints = breakpoints;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
