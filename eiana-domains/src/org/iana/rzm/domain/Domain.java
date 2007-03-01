package org.iana.rzm.domain;

import org.hibernate.annotations.CollectionOfElements;
import org.iana.rzm.common.Name;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.*;
import java.net.URL;
import java.util.*;
import java.sql.Timestamp;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "state"}))
public class Domain implements TrackedObject {

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
    @JoinColumn(name="supportingOrg_objId")
    private Contact supportingOrg;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Domain_AdminContacts",
            inverseJoinColumns = @JoinColumn(name = "Contact_objId"))
    private List<Contact> adminContacts;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Domain_TechContacts",
            inverseJoinColumns = @JoinColumn(name = "Contact_objId"))
    private List<Contact> techContacts;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Domain_NameServers",
            inverseJoinColumns = @JoinColumn(name = "Host_objId"))
    private List<Host> nameServers;
    @Basic
    private URL registryUrl;
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
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Embedded
    private TrackData trackData = new TrackData();

    protected Domain() {}

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

    @Transient
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

    final public void addNameServer(Host host) throws NameServerAlreadyExistsException {
        CheckTool.checkNull(host, "nameServer");
        if (nameServers.contains(host)) throw new NameServerAlreadyExistsException(host.getName());
        nameServers.add(host);
        host.incDelegations();
    }

    final public boolean removeNameServer(Host host) {
        if (host != null) {
            return removeNameServer(host.getName());
        }
        return false;
    }

    final public boolean removeNameServer(String hostName) {
        if (hostName != null) {
            for (Iterator<Host> i = nameServers.iterator(); i.hasNext(); ) {
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

    final public URL getRegistryUrl() {
        return registryUrl;
    }

    final public void setRegistryUrl(URL registryUrl) {
        this.registryUrl = registryUrl;
    }

    protected URL getDomainRegistryUrl() {
        return registryUrl;
    }

    protected void setDomainRegistryUrl(URL registryUrl) {
        this.registryUrl = registryUrl;
    }

    final public String getWhoisServer() {
        return whoisServer == null ? null : whoisServer.getName();
    }

    final public void setWhoisServer(String whoisServer) throws InvalidNameException {
        this.whoisServer = new Name(whoisServer);
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
        CheckTool.checkNull(status, "state");
        this.state = state;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Domain domain = (Domain) o;

        if (adminContacts != null ? !adminContacts.equals(domain.adminContacts) : domain.adminContacts != null)
            return false;
        if (breakpoints != null ? !breakpoints.equals(domain.breakpoints) : domain.breakpoints != null) return false;
        if (name != null ? !name.equals(domain.name) : domain.name != null) return false;
        if (nameServers != null ? !nameServers.equals(domain.nameServers) : domain.nameServers != null) return false;
        if (registryUrl != null ? !registryUrl.equals(domain.registryUrl) : domain.registryUrl != null) return false;
        if (specialInstructions != null ? !specialInstructions.equals(domain.specialInstructions) : domain.specialInstructions != null)
            return false;
        if (state != domain.state) return false;
        if (status != domain.status) return false;
        if (supportingOrg != null ? !supportingOrg.equals(domain.supportingOrg) : domain.supportingOrg != null)
            return false;
        if (techContacts != null ? !techContacts.equals(domain.techContacts) : domain.techContacts != null)
            return false;
        if (trackData != null ? !trackData.equals(domain.trackData) : domain.trackData != null) return false;
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

    public Long getId() {
        return trackData.getId();
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
}
