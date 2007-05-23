package org.iana.rzm.facade.system.domain;

import org.iana.rzm.common.Name;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

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

    public void addAdminContact(ContactVO contact) {
        if (adminContacts == null)
            adminContacts = new ArrayList<ContactVO>();
        adminContacts.add(contact);
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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DomainVO domainVO = (DomainVO) o;

        if (adminContacts != null ? !adminContacts.equals(domainVO.adminContacts) : domainVO.adminContacts != null)
            return false;
        if (breakpoints != null ? !breakpoints.equals(domainVO.breakpoints) : domainVO.breakpoints != null)
            return false;
        if (nameServers != null ? !nameServers.equals(domainVO.nameServers) : domainVO.nameServers != null)
            return false;
        if (registryUrl != null ? !registryUrl.equals(domainVO.registryUrl) : domainVO.registryUrl != null)
            return false;
        if (specialInstructions != null ? !specialInstructions.equals(domainVO.specialInstructions) : domainVO.specialInstructions != null)
            return false;
        if (state != domainVO.state) return false;
        if (status != domainVO.status) return false;
        if (supportingOrg != null ? !supportingOrg.equals(domainVO.supportingOrg) : domainVO.supportingOrg != null)
            return false;
        if (techContacts != null ? !techContacts.equals(domainVO.techContacts) : domainVO.techContacts != null)
            return false;
        if (whoisServer != null ? !whoisServer.equals(domainVO.whoisServer) : domainVO.whoisServer != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (supportingOrg != null ? supportingOrg.hashCode() : 0);
        result = 31 * result + (adminContacts != null ? adminContacts.hashCode() : 0);
        result = 31 * result + (techContacts != null ? techContacts.hashCode() : 0);
        result = 31 * result + (nameServers != null ? nameServers.hashCode() : 0);
        result = 31 * result + (registryUrl != null ? registryUrl.hashCode() : 0);
        result = 31 * result + (whoisServer != null ? whoisServer.hashCode() : 0);
        result = 31 * result + (breakpoints != null ? breakpoints.hashCode() : 0);
        result = 31 * result + (specialInstructions != null ? specialInstructions.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }
}
