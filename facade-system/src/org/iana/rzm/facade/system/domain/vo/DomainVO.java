package org.iana.rzm.facade.system.domain.vo;

import java.io.*;
import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DomainVO extends SimpleDomainVO implements IDomainVO, Serializable {

    private ContactVO supportingOrg;
    private ContactVO adminContact;
    private ContactVO techContact;
    private List<HostVO> nameServers;
    private String registryUrl;
    private String whoisServer;
    private Set<Breakpoint> breakpoints;
    private String specialInstructions;
    private Status status;
    private State state;
    private boolean enableEmails;
    private String description;
    private String type;
    private String ianaCode;

    public ContactVO getSupportingOrg() {
        return supportingOrg;
    }

    public void setSupportingOrg(ContactVO supportingOrg) {
        this.supportingOrg = supportingOrg;
    }

    public List<ContactVO> getAdminContacts() {
        return null;
    }

    public ContactVO getAdminContact() {
        return adminContact;
    }

    public void setAdminContact(ContactVO adminContact) {
        this.adminContact = adminContact;
    }

    public List<ContactVO> getTechContacts() {
        return null;
    }

    public ContactVO getTechContact() {
        return techContact;
    }

    public void setTechContact(ContactVO techContact) {
        this.techContact = techContact;
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

    public String getWhoisServer() {
        return whoisServer;
    }

    public void setWhoisServer(String whoisServer) {
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

    public boolean isEnableEmails() {
        return enableEmails;
    }

    public void setEnableEmails(boolean enableEmails) {
        this.enableEmails = enableEmails;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIanaCode() {
        return ianaCode;
    }

    public void setIanaCode(String ianaCode) {
        this.ianaCode = ianaCode;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DomainVO domainVO = (DomainVO) o;

        if (adminContact != null ? !adminContact.equals(domainVO.adminContact) : domainVO.adminContact != null)
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
        if (techContact != null ? !techContact.equals(domainVO.techContact) : domainVO.techContact != null)
            return false;
        if (whoisServer != null ? !whoisServer.equals(domainVO.whoisServer) : domainVO.whoisServer != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (supportingOrg != null ? supportingOrg.hashCode() : 0);
        result = 31 * result + (adminContact != null ? adminContact.hashCode() : 0);
        result = 31 * result + (techContact != null ? techContact.hashCode() : 0);
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
