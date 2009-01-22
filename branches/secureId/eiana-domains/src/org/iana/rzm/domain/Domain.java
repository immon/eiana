package org.iana.rzm.domain;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.iana.dns.DNSDomain;
import org.iana.dns.obj.DNSDomainImpl;
import org.iana.dns.validator.InvalidDomainNameException;
import org.iana.rzm.common.Name;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

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

    @OneToMany(mappedBy = "domain", cascade = CascadeType.ALL)
    @MapKey(name = "domainRole")
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    Map<Contact.Role,  Contact> contacts = new HashMap<Contact.Role, Contact>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Domain_NameServers",
            inverseJoinColumns = @JoinColumn(name = "Host_objId"))
    private List<Host> nameServers;
    @Basic
    private String registryUrl;
    @Embedded
    @AttributeOverride(name = "name",
            column = @Column(name = "whoisServer"))
    private WhoisName whoisServer;
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
    @Basic
    private String description;
    @Basic
    private boolean enableEmails = false;
    @Basic
    private String type;
    @Basic
    private String ianaCode;
    @Basic
    private boolean specialReview;

    protected Domain() {
        this.nameServers = new ArrayList<Host>();
        this.breakpoints = new HashSet<Breakpoint>();
        this.specialReview = false;
    }

    public Domain(String name) throws InvalidDomainNameException {
        setName(name);
        this.nameServers = new ArrayList<Host>();
        this.breakpoints = new HashSet<Breakpoint>();
        this.status = Status.ACTIVE;
        this.ianaCode = getName();
        this.specialReview = false;
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

    final public String getFqdnName() {
        return name == null ? null : name.getFqdnName();
    }

    public boolean isSpecialReview() {
        return specialReview;
    }

    public boolean getSpecialReview() {
        return specialReview;
    }

    public void setSpecialReview(boolean specialReview) {
        this.specialReview = specialReview;
    }

    final public void setName(String name) throws InvalidDomainNameException {
        this.name = new Name(name);
    }

    final public Contact getSupportingOrg() {
        return contacts.get(Contact.Role.SO);
    }

    final public void setSupportingOrg(Contact supportingOrg) {
        setContact(supportingOrg, Contact.Role.SO);
    }

    final public Contact getAdminContact() {
        return contacts.get(Contact.Role.AC);
    }

    final public void setAdminContact(Contact contact) {
        setContact(contact, Contact.Role.AC);
    }

    final public Contact getTechContact() {
        return contacts.get(Contact.Role.TC);
    }

    final public void setTechContact(Contact contact) {
        setContact(contact, Contact.Role.TC);
    }

    private void setContact(Contact contact, Contact.Role role) {
        Contact found = contacts.get(role);
        if (contact == null && found == null) {
            // nothing to do: return
            return;
        }
        if (contact != null && found == null) {
            contact.setDomainRole(role);
            contact.setDomain(this);
            contacts.put(role, contact);
            return;
        }
        if (contact != null && found != null) {
            contact.setDomainRole(role);
            contact.setDomain(this);
            contact.setId(found.getObjId());
            contacts.put(role, contact);
            return;
        }
        // contact == null && found != null
        found.setDomain(null);
        contacts.remove(role);
    }

    final public List<Host> getNameServers() {
        return Collections.unmodifiableList(nameServers);
    }

    final public NameServersDecorator getNameServersToXMLExport() {
        return new NameServersDecorator(getNameServers());
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
            if (hostName.equals(host.getName())) return host;
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
        this.whoisServer = whoisServer == null ? null : new WhoisName(whoisServer);
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


    public Timestamp getCreated() {
        return trackData.getCreated();
    }

    public String getCreatedToXMLExport() {
        return trackData.getCreatedToXMLExport();
    }

    public Timestamp getModified() {
        return trackData.getModified();
    }

    public String getModifiedToXMLExport() {
        return trackData.getModifiedToXMLExport();
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
        CheckTool.checkNull(trackData, "track data");
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
            if (contacts != null) {
                newDomain.contacts = new HashMap<Contact.Role, Contact>();
                for (Contact.Role role : contacts.keySet()) {
                    newDomain.contacts.put(role, contacts.get(role).clone());
                }
            }
            if (whoisServer != null)
                newDomain.whoisServer = whoisServer.clone();
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
                contactsList.add(cont.clone());
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

    public void modify(long timestamp, String modifiedBy) {
        setModified(new Timestamp(timestamp));
        setModifiedBy(modifiedBy);
        for (Contact contact : contacts.values()) {
            contact.checkModification(timestamp, modifiedBy);
        }
        for (Host host : nameServers) {
            host.checkModification(timestamp, modifiedBy);
        }
    }

    public void incOpenProcesses() {
        ++openProcesses;
    }

    public void decOpenProcesses() {
        --openProcesses;
    }

    protected int getOpenProcesses() {
        return openProcesses;
    }

    protected void setOpenProcesses(int value) {
        openProcesses = value;
    }

    public void incThirdPartyPendingProcesses() {
        ++thirdPartyPendingProcesses;
    }

    public void decThirdPartyPendingProcesses() {
        --thirdPartyPendingProcesses;
    }

    protected int getThirdPartyPendingProcesses() {
        return thirdPartyPendingProcesses;
    }

    protected void setThirdPartyPendingProcesses(int value) {
        thirdPartyPendingProcesses = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnableEmails() {
        return enableEmails;
    }

    public boolean getEnableEmails() {
        return enableEmails;
    }

    public void setEnableEmails(boolean enableEmails) {
        this.enableEmails = enableEmails;
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

        Domain domain = (Domain) o;

        if (name != null ? !name.equals(domain.name) : domain.name != null) return false;

        return true;
    }

    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }

    public boolean equalsTotal(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Domain domain = (Domain) o;

        if (enableEmails != domain.enableEmails) return false;
        if (openProcesses != domain.openProcesses) return false;
        if (thirdPartyPendingProcesses != domain.thirdPartyPendingProcesses) return false;
        if (breakpoints != null ? !breakpoints.equals(domain.breakpoints) : domain.breakpoints != null) return false;
        if (contacts != null ? !contacts.equals(domain.contacts) : domain.contacts != null) return false;
        if (description != null ? !description.equals(domain.description) : domain.description != null) return false;
        if (ianaCode != null ? !ianaCode.equals(domain.ianaCode) : domain.ianaCode != null) return false;
        if (name != null ? !name.equals(domain.name) : domain.name != null) return false;
        if (nameServers != null ? !nameServers.equals(domain.nameServers) : domain.nameServers != null) return false;
        if (registryUrl != null ? !registryUrl.equals(domain.registryUrl) : domain.registryUrl != null) return false;
        if (specialInstructions != null ? !specialInstructions.equals(domain.specialInstructions) : domain.specialInstructions != null)
            return false;
        if (status != domain.status) return false;
        if (type != null ? !type.equals(domain.type) : domain.type != null) return false;
        if (whoisServer != null ? !whoisServer.equals(domain.whoisServer) : domain.whoisServer != null) return false;

        return true;
    }

    public DNSDomain toDNSDomain() {
        DNSDomainImpl ret = new DNSDomainImpl(getName());
        for (Host ns : getNameServers()) {
            ret.addNameServer(ns.toDNSHost());
        }
        return ret;
    }
}
