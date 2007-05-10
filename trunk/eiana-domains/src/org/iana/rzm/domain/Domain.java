package org.iana.rzm.domain;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Cascade;
import org.iana.rzm.common.Name;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.*;
import java.util.*;
import java.sql.Timestamp;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "state"}))
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
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "supportingOrg_objId")
    private Contact supportingOrg;
    @OneToMany(cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinTable(name = "Domain_AdminContacts",
            inverseJoinColumns = @JoinColumn(name = "Contact_objId"))
    private List<Contact> adminContacts;
    @OneToMany(cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinTable(name = "Domain_TechContacts",
            inverseJoinColumns = @JoinColumn(name = "Contact_objId"))
    private List<Contact> techContacts;
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
    @Enumerated
    private State state;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Embedded
    private TrackData trackData = new TrackData();

    protected Domain() {
    }

    public Domain(String name) throws InvalidNameException {
        setName(name);
        this.adminContacts = new ArrayList<Contact>();
        this.techContacts = new ArrayList<Contact>();
        this.nameServers = new ArrayList<Host>();
        this.breakpoints = new HashSet<Breakpoint>();
        this.status = Status.NEW;
        this.state = State.NO_ACTIVITY;
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

    final public void setName(String name) throws InvalidNameException {
        this.name = new Name(name);
    }

    final public Contact getSupportingOrg() {
        return supportingOrg;
    }

    final public void setSupportingOrg(Contact supportingOrg) {
        CheckTool.checkNull(supportingOrg, "supportingOrg");
        // hibernate trick; note it only works when no-null arg is required
        if (this.supportingOrg != null) {
            supportingOrg.setObjId(this.supportingOrg.getObjId());
        }
        this.supportingOrg = supportingOrg;
    }

    final public List<Contact> getAdminContacts() {
        return Collections.unmodifiableList(adminContacts);
    }

    final public void setAdminContacts(Collection<Contact> adminContacts) {
        CheckTool.checkCollectionNull(adminContacts, "adminContacts");
        this.adminContacts.clear();
        CheckTool.addAllNoDup(this.adminContacts, adminContacts);
    }

    final public void addAdminContact(Contact contact) {
        CheckTool.checkNull(contact, "adminContact");
        CheckTool.addNoDup(this.adminContacts, contact);
    }

    final public boolean removeAdminContact(Contact contact) {
        return adminContacts.remove(contact);
    }

    final public List<Contact> getTechContacts() {
        return Collections.unmodifiableList(techContacts);
    }

    final public void setTechContacts(List<Contact> techContacts) {
        CheckTool.checkCollectionNull(techContacts, "techContacts");
        this.techContacts.clear();
        CheckTool.addAllNoDup(this.techContacts, techContacts);
    }

    final public void addTechContact(Contact contact) {
        CheckTool.checkNull(contact, "techContact");
        CheckTool.addNoDup(this.techContacts, contact);
    }

    final public boolean removeTechContact(Contact contact) {
        return techContacts.remove(contact);
    }

    final public List<Host> getNameServers() {
        return Collections.unmodifiableList(nameServers);
    }

    final public void setNameServers(List<Host> nameServers) throws NameServerAlreadyExistsException {
        CheckTool.checkCollectionNull(nameServers, "nameServers");
        this.nameServers.clear();
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

    final public void setWhoisServer(String whoisServer) throws InvalidNameException {
        this.whoisServer = whoisServer == null ? null : new Name(whoisServer);
    }

    final public Set<Breakpoint> getBreakpoints() {
        return Collections.unmodifiableSet(breakpoints);
    }

    final public void setBreakpoints(Set<Breakpoint> breakpoints) {
        CheckTool.checkCollectionNull(breakpoints, "breakpoints");
        this.breakpoints.clear();
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

    final public State getState() {
        return state;
    }

    final public void setState(State state) {
        CheckTool.checkNull(state, "state");
        this.state = state;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Domain domain = (Domain) o;

        if (adminContacts != null ? !adminContacts.equals(domain.adminContacts) : domain.adminContacts != null)
            return false;
        //System.out.println("1: breakpoints");
        if (breakpoints != null ? !breakpoints.equals(domain.breakpoints) : domain.breakpoints != null) return false;
        //System.out.println("2: name");
        if (name != null ? !name.equals(domain.name) : domain.name != null) return false;
        //System.out.println("3: name servers");
        if (nameServers != null ? !nameServers.equals(domain.nameServers) : domain.nameServers != null) return false;
        //System.out.println("4: registry url");
        if (registryUrl != null ? !registryUrl.equals(domain.registryUrl) : domain.registryUrl != null) return false;
        //System.out.println("5: special instructions");
        if (specialInstructions != null ? !specialInstructions.equals(domain.specialInstructions) : domain.specialInstructions != null)
            return false;
        //System.out.println("6: state");
        if (state != domain.state) return false;
        //System.out.println("7: status");
        if (status != domain.status) return false;
        //System.out.println("8: so");
        if (supportingOrg != null ? !supportingOrg.equals(domain.supportingOrg) : domain.supportingOrg != null)
            return false;
        //System.out.println("9: tc");
        if (techContacts != null ? !techContacts.equals(domain.techContacts) : domain.techContacts != null)
            return false;
        //System.out.println("10: td");
        //if (trackData != null ? !trackData.equals(domain.trackData) : domain.trackData != null) return false;
        //System.out.println("11: whois " + whoisServer);
        if (whoisServer != null ? !whoisServer.equals(domain.whoisServer) : domain.whoisServer != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (supportingOrg != null ? supportingOrg.hashCode() : 0);
        result = 31 * result + (adminContacts != null ? adminContacts.hashCode() : 0);
        result = 31 * result + (techContacts != null ? techContacts.hashCode() : 0);
        result = 31 * result + (nameServers != null ? nameServers.hashCode() : 0);
        result = 31 * result + (registryUrl != null ? registryUrl.hashCode() : 0);
        result = 31 * result + (whoisServer != null ? whoisServer.hashCode() : 0);
        result = 31 * result + (breakpoints != null ? breakpoints.hashCode() : 0);
        result = 31 * result + (specialInstructions != null ? specialInstructions.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (trackData != null ? trackData.hashCode() : 0);
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

    public Domain clone() throws CloneNotSupportedException {
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
        newDomain.adminContacts = copyListOfContacts(adminContacts);
        newDomain.techContacts = copyListOfContacts(techContacts);
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
        newDomain.state = state;
        newDomain.status = status;
        newDomain.objId = objId;
        return newDomain;
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
                if (domain.getAdminContacts() != null)
                    newDomain.setAdminContacts(copyListOfContacts(domain.getAdminContacts()));
                if (domain.getTechContacts() != null)
                    newDomain.setTechContacts(copyListOfContacts(domain.getTechContacts()));
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
}
