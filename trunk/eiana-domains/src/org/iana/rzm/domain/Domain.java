package org.iana.rzm.domain;

import org.hibernate.annotations.CollectionOfElements;
import org.iana.rzm.common.Name;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.EmailAddress;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.dns.validator.InvalidDomainNameException;

import javax.persistence.*;
import java.util.*;
import java.sql.Timestamp;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "status"}))
public class Domain implements TrackedObject, Cloneable {

    public static enum Breakpoint {
        SO_CHANGE_EXT_REVIEW,
        AC_CHANGE_EXT_REVIEW,
        TC_CHANGE_EXT_REVIEW,
        NS_CHANGE_EXT_REVIEW,
        ANY_CHANGE_EXT_REVIEW
    }

    public static enum Status {
        NEW,
        ACTIVE,
        CLOSED
    }

    public static enum State {
        NO_ACTIVITY,
        OPERATIONS_PENDING,
        THIRD_PARTY_PENDING
    }

    @Embedded
    private Name name;
    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name="name", column = @Column(name="so_name") ),
            @AttributeOverride(name="organization", column = @Column(name="so_org") ),
            @AttributeOverride(name="jobTitle", column = @Column(name="so_job_title") ),
            @AttributeOverride(name="address.textAddress", column = @Column(name="so_address") ),
            @AttributeOverride(name="address.countryCode.countryCode", column = @Column(name="so_cc") ),
            @AttributeOverride(name="phoneNumber", column = @Column(name="so_phone") ),
            @AttributeOverride(name="altPhoneNumber", column = @Column(name="so_alt_phone") ),
            @AttributeOverride(name="faxNumber", column = @Column(name="so_fax") ),
            @AttributeOverride(name="altFaxNumber", column = @Column(name="so_alt_fax") ),
            @AttributeOverride(name="publicEmail.email", column = @Column(name="so_pub_email") ),
            @AttributeOverride(name="privateEmail.email", column = @Column(name="so_priv_email") ),
            @AttributeOverride(name="role", column = @Column(name="so_role") ),
            @AttributeOverride(name="trackData.created", column = @Column(name="so_created") ),
            @AttributeOverride(name="trackData.createdBy", column = @Column(name="so_createdby") ),
            @AttributeOverride(name="trackData.modified", column = @Column(name="so_modified") ),
            @AttributeOverride(name="trackData.modifiedBy", column = @Column(name="so_modifiedby") )
     } )
    private Contact supportingOrg;
    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name="name", column = @Column(name="ac_name") ),
            @AttributeOverride(name="organization", column = @Column(name="ac_org") ),
            @AttributeOverride(name="jobTitle", column = @Column(name="ac_job_title") ),
            @AttributeOverride(name="address.textAddress", column = @Column(name="ac_address") ),
            @AttributeOverride(name="address.countryCode.countryCode", column = @Column(name="ac_cc") ),
            @AttributeOverride(name="phoneNumber", column = @Column(name="ac_phone") ),
            @AttributeOverride(name="altPhoneNumber", column = @Column(name="ac_alt_phone") ),
            @AttributeOverride(name="faxNumber", column = @Column(name="ac_fax") ),
            @AttributeOverride(name="altFaxNumber", column = @Column(name="ac_alt_fax") ),
            @AttributeOverride(name="publicEmail.email", column = @Column(name="ac_pub_email") ),
            @AttributeOverride(name="privateEmail.email", column = @Column(name="ac_priv_email") ),
            @AttributeOverride(name="role", column = @Column(name="ac_role") ),
            @AttributeOverride(name="trackData.created", column = @Column(name="ac_created") ),
            @AttributeOverride(name="trackData.createdBy", column = @Column(name="ac_createdby") ),
            @AttributeOverride(name="trackData.modified", column = @Column(name="ac_modified") ),
            @AttributeOverride(name="trackData.modifiedBy", column = @Column(name="ac_modifiedby") )
     } )
    private Contact adminContact;
    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name="name", column = @Column(name="tc_name") ),
            @AttributeOverride(name="organization", column = @Column(name="tc_org") ),
            @AttributeOverride(name="jobTitle", column = @Column(name="tc_job_title") ),
            @AttributeOverride(name="address.textAddress", column = @Column(name="tc_address") ),
            @AttributeOverride(name="address.countryCode.countryCode", column = @Column(name="tc_cc") ),
            @AttributeOverride(name="phoneNumber", column = @Column(name="tc_phone") ),
            @AttributeOverride(name="altPhoneNumber", column = @Column(name="tc_alt_phone") ),
            @AttributeOverride(name="faxNumber", column = @Column(name="tc_fax") ),
            @AttributeOverride(name="altFaxNumber", column = @Column(name="tc_alt_fax") ),
            @AttributeOverride(name="publicEmail.email", column = @Column(name="tc_pub_email") ),
            @AttributeOverride(name="privateEmail.email", column = @Column(name="tc_priv_email") ),
            @AttributeOverride(name="role", column = @Column(name="tc_role") ),
            @AttributeOverride(name="trackData.created", column = @Column(name="tc_created") ),
            @AttributeOverride(name="trackData.createdBy", column = @Column(name="tc_createdby") ),
            @AttributeOverride(name="trackData.modified", column = @Column(name="tc_modified") ),
            @AttributeOverride(name="trackData.modifiedBy", column = @Column(name="tc_modifiedby") )
     } )
    private Contact techContact;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Domain_NameServers",
            inverseJoinColumns = @JoinColumn(name = "Host_objId"))
    private List<Host> nameServers;
    @Basic
    private String registryUrl;
    @Embedded
    @AttributeOverride(name = "name",
            column = @Column(name = "whoisServer"))
    private Name whoisServer;
    @CollectionOfElements
    @JoinTable(name = "Domain_Breakpoints")
    @Column(name = "breakpoint", nullable = false)
    private Set<Breakpoint> breakpoints;
    @Basic
    private String specialInstructions;
    @Enumerated
    private Status status;
    @Basic
    private int openProcesses;
    @Basic
    private int thirdPartyPendingProcesses;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Embedded
    private TrackData trackData = new TrackData();

    protected Domain() {
    }

    public Domain(String name) throws InvalidDomainNameException {
        setName(name);
        this.nameServers = new ArrayList<Host>();
        this.breakpoints = new HashSet<Breakpoint>();
        this.status = Status.NEW;
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    final public String getName() {
        return name == null ? null : name.getName();
    }

    final public void setName(String name) throws InvalidDomainNameException {
        this.name = new Name(name);
    }

    final public Contact getSupportingOrg() {
        return supportingOrg;
    }

    final public void setSupportingOrg(Contact supportingOrg) {
        this.supportingOrg = supportingOrg;
    }

    final public Contact getAdminContact() {
        return adminContact;
    }

    final public List<Contact> getAdminContacts() {
        List<Contact> ret = new ArrayList<Contact>();
        if (adminContact != null) ret.add(adminContact);
        return ret;
    }

    final public void setAdminContact(Contact contact) {
        this.adminContact = contact;
    }

    final public List<Contact> getTechContacts() {
        List<Contact> ret = new ArrayList<Contact>();
        if (techContact != null) ret.add(techContact);
        return ret;
    }

    final public Contact getTechContact() {
        return techContact;
    }

    final public void setTechContact(Contact contact) {
        this.techContact = contact;
    }

    final public List<Host> getNameServers() {
        return Collections.unmodifiableList(nameServers);
    }

    final public void setNameServers(List<Host> nameServers) throws NameServerAlreadyExistsException {
        CheckTool.checkCollectionNull(nameServers, "nameServers");
        this.nameServers = new ArrayList<Host>();
        for (Host host : nameServers) {
            addNameServer(host);
        }
    }

    final public void removeNameServers() {
        List<Host> nss = new ArrayList<Host>(nameServers);
        for (Host ns : nss) {
            removeNameServer(ns);
        }
    }

    final public void addNameServer(Host host) throws NameServerAlreadyExistsException {
        CheckTool.checkNull(host, "nameServer");
        if (nameServers.contains(host)) throw new NameServerAlreadyExistsException(host.getName());
        nameServers.add(host);
        host.incDelegations();
    }

    final public Host getNameServer(String hostName) {
        CheckTool.checkNull(hostName, "hostName");
        for (Iterator<Host> i = nameServers.iterator(); i.hasNext();) {
            Host host = i.next();
            if (hostName.equals(host.getName())) {
                host.decDelegations();
                i.remove();
                return host;
            }
        }
        return null;
    }

    final public void setNameServer(Host host) {
        CheckTool.checkNull(host, "host");
        Host found = getNameServer(host.getName());
        if (found != null) {
            found.setAddresses(host.getAddresses());
        }
    }

    final public boolean removeNameServer(Host host) {
        if (host != null) {
            return removeNameServer(host.getName());
        }
        return false;
    }

    final public boolean removeNameServer(String hostName) {
        if (hostName != null) {
            for (Iterator<Host> i = nameServers.iterator(); i.hasNext();) {
                Host host = i.next();
                if (hostName.equals(host.getName())) {
                    host.decDelegations();
                    i.remove();
                    return true;
                }
            }
        }
        return false;
    }

    final public String getRegistryUrl() {
        return registryUrl;
    }

    final public void setRegistryUrl(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    final public String getWhoisServer() {
        return whoisServer == null ? null : whoisServer.getName();
    }

    final public void setWhoisServer(String whoisServer) throws InvalidDomainNameException {
        this.whoisServer = whoisServer == null ? null : new Name(whoisServer);
    }

    final public Set<Breakpoint> getBreakpoints() {
        return Collections.unmodifiableSet(breakpoints);
    }

    final public void setBreakpoints(Set<Breakpoint> breakpoints) {
        CheckTool.checkCollectionNull(breakpoints, "breakpoints");
        this.breakpoints = new HashSet<Breakpoint>();
        this.breakpoints.addAll(breakpoints);
    }

    final public void addBreakpoint(Breakpoint breakpoint) {
        this.breakpoints.add(breakpoint);
    }

    final public boolean removeBreakpoint(Breakpoint breakpoint) {
        return this.breakpoints.remove(breakpoint);
    }

    final public boolean hasBreakpoint(Breakpoint breakpoint) {
        return this.breakpoints.contains(breakpoint);
    }

    final public String getSpecialInstructions() {
        return specialInstructions;
    }

    final public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    final public Status getStatus() {
        return status;
    }

    final public void setStatus(Status status) {
        CheckTool.checkNull(status, "status");
        this.status = status;
    }

    final public void setStatus(String status) {
        CheckTool.checkNull(status, "status");
        this.status = Status.valueOf(status);
    }

    final public State getState() {
        return openProcesses > 0 ?
                thirdPartyPendingProcesses > 0 ? State.THIRD_PARTY_PENDING : State.OPERATIONS_PENDING
                : State.NO_ACTIVITY;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Domain domain = (Domain) o;

        if (openProcesses != domain.openProcesses) return false;
        if (thirdPartyPendingProcesses != domain.thirdPartyPendingProcesses) return false;
        if (adminContact != null ? !adminContact.equals(domain.adminContact) : domain.adminContact != null)
            return false;
        if (breakpoints != null ? !breakpoints.equals(domain.breakpoints) : domain.breakpoints != null) return false;
        if (name != null ? !name.equals(domain.name) : domain.name != null) return false;
        if (nameServers != null ? !nameServers.equals(domain.nameServers) : domain.nameServers != null) return false;
        if (registryUrl != null ? !registryUrl.equals(domain.registryUrl) : domain.registryUrl != null) return false;
        if (specialInstructions != null ? !specialInstructions.equals(domain.specialInstructions) : domain.specialInstructions != null)
            return false;
        if (status != domain.status) return false;
        if (supportingOrg != null ? !supportingOrg.equals(domain.supportingOrg) : domain.supportingOrg != null)
            return false;
        if (techContact != null ? !techContact.equals(domain.techContact) : domain.techContact != null) return false;
        if (whoisServer != null ? !whoisServer.equals(domain.whoisServer) : domain.whoisServer != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (supportingOrg != null ? supportingOrg.hashCode() : 0);
        result = 31 * result + (adminContact != null ? adminContact.hashCode() : 0);
        result = 31 * result + (techContact != null ? techContact.hashCode() : 0);
        result = 31 * result + (nameServers != null ? nameServers.hashCode() : 0);
        result = 31 * result + (registryUrl != null ? registryUrl.hashCode() : 0);
        result = 31 * result + (whoisServer != null ? whoisServer.hashCode() : 0);
        result = 31 * result + (breakpoints != null ? breakpoints.hashCode() : 0);
        result = 31 * result + (specialInstructions != null ? specialInstructions.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + openProcesses;
        result = 31 * result + thirdPartyPendingProcesses;
        return result;
    }

    public Timestamp getCreated() {
        return trackData.getCreated();
    }

    public Timestamp getModified() {
        return trackData.getModified();
    }

    public String getCreatedBy() {
        return trackData.getCreatedBy();
    }

    public String getModifiedBy() {
        return trackData.getModifiedBy();
    }

    public TrackData getTrackData() {
        return trackData;
    }

    public void setTrackData(TrackData trackData) {
        this.trackData = trackData;
    }

    public Domain clone() {
        try {
            Domain newDomain = (Domain) super.clone();

            if (name != null)
                newDomain.name = (Name) name.clone();

            List<Host> newHosts = new ArrayList<Host>();
            if (nameServers != null) {
                for (Host host : nameServers)
                    newHosts.add((Host) host.clone());
            }
            newDomain.nameServers = newHosts;

            newDomain.trackData = trackData == null ? new TrackData() : (TrackData) trackData.clone();
            if (adminContact != null)
                newDomain.adminContact = adminContact.clone();
            if (techContact != null)
                newDomain.techContact = techContact.clone();
            if (supportingOrg != null)
                newDomain.supportingOrg = (Contact) supportingOrg.clone();
            if (whoisServer != null)
                newDomain.whoisServer = (Name) whoisServer.clone();
            newDomain.registryUrl = registryUrl;

            Set<Breakpoint> newBreakpoints = new HashSet<Breakpoint>();
            if (breakpoints != null) {
                for (Breakpoint breakpoint : breakpoints)
                    newBreakpoints.add(breakpoint);
            }
            newDomain.breakpoints = newBreakpoints;

            newDomain.specialInstructions = specialInstructions;
            newDomain.status = status;
            newDomain.objId = objId;
            newDomain.openProcesses = openProcesses;
            newDomain.thirdPartyPendingProcesses = thirdPartyPendingProcesses;
            return newDomain;
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
        /*
        Domain domain = null;
        try {
            domain = (Domain) super.clone();
            Domain newDomain = new Domain(domain.getName());
            try {
                //domain.setDomainRegistryUrl(new URL(domain.getRegistryUrl().toString()));
                if (domain.getNameServers() != null) {
                    List<Host> newHosts = new ArrayList<Host>();
                    List<Host> oldHosts = domain.getNameServers();
                    for (Host host : oldHosts)
                        newHosts.add((Host) host.clone());
                    newDomain.setNameServers(newHosts);
                }
                if (domain.getTrackData() != null)
                    newDomain.setTrackData((TrackData) domain.getTrackData().clone());
                if (domain.getAdminContact() != null)
                    newDomain.setAdminContacts(copyListOfContacts(domain.getAdminContact()));
                if (domain.getTechContact() != null)
                    newDomain.setTechContacts(copyListOfContacts(domain.getTechContact()));
                if (domain.getSupportingOrg() != null)
                    newDomain.setSupportingOrg((Contact) domain.getSupportingOrg().clone());
                if (domain.getWhoisServer() != null)
                    newDomain.setWhoisServer(domain.getWhoisServer());
            } catch (NameServerAlreadyExistsException e) {
                e.printStackTrace();
            }
            return newDomain;
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
        */
    }

    private List<Contact> copyListOfContacts(List<Contact> oldContacts) throws CloneNotSupportedException {
        List<Contact> contactsList = new ArrayList<Contact>();
        if (oldContacts != null) {
            for (Contact cont : oldContacts)
                contactsList.add((Contact) cont.clone());
        }
        return contactsList;
    }


    public void setCreated(Timestamp created) {
        trackData.setCreated(created);
    }

    public void setModified(Timestamp modified) {
        trackData.setModified(modified);
    }

    public void setCreatedBy(String createdBy) {
        trackData.setCreatedBy(createdBy);
    }

    public void setModifiedBy(String modifiedBy) {
        trackData.setModifiedBy(modifiedBy);
    }

    public void incOpenProcesses() { ++openProcesses; }

    public void decOpenProcesses() { --openProcesses; }

    public void incThirdPartyPendingProcesses() { ++thirdPartyPendingProcesses; }

    public void decThirdPartyPendingProcesses() { --thirdPartyPendingProcesses; }
}
