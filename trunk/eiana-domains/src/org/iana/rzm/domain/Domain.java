package org.iana.rzm.domain;

import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.Name;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.common.exceptions.InvalidNameException;

import java.util.*;
import java.net.URL;

public class Domain extends TrackedObject {

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

    private Name name;
    private Contact supportingOrg;
    private List<Contact> adminContacts;
    private List<Contact> techContacts;
    private List<Host> nameServers;
    private URL registryUrl;
    private Name whoisServer;
    private Set<Breakpoint> breakpoints;
    private String specialInstructions;
    private Status status;
    private State state;

    public Domain(String name) throws InvalidNameException {
        setName(name);
        this.adminContacts = new ArrayList<Contact>();
        this.techContacts = new ArrayList<Contact>();
        this.nameServers = new ArrayList<Host>();
        this.breakpoints = new HashSet<Breakpoint>();
        this.status = Status.NEW;
        this.state = State.NO_ACTIVITY;
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

        if (name != null ? !name.equals(domain.name) : domain.name != null) return false;

        return true;
    }

    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }
}
