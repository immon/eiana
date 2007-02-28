package org.iana.rzm.domain;

import org.iana.rzm.common.Name;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.sql.Timestamp;

/**
 * This class represents a computer machine available in the net which serves as a DNS name server to one or many domain names.
 *
 */
@Entity
public class Host implements TrackedObject {

    /**
     * The name of this host.
     */
    private Name name;
    /**
     * The set of IP addresses of this host, either IPv4 and IPv6.
     */
    private Set<IPAddress> addresses;
    /**
     * The number of domain names delegated to this host.
     */
    private int numDelegations;
    private Long objId;
    private TrackData trackData = new TrackData();

    protected Host() {}

    public Host(String name) throws InvalidNameException {
        setName(name);
        this.addresses = new HashSet<IPAddress>();
        this.numDelegations = 0;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Embedded
    @AttributeOverride(name = "nameStr",
            column = @Column(name = "name"))
    protected Name getHostName() {
        return name;
    }

    protected void setHostName(Name name) {
        this.name = name;
    }

    @Transient
    final public Set<IPAddress> getAddresses() {
        return Collections.unmodifiableSet(this.addresses);
    }

    final public void setAddresses(Collection<IPAddress> addresses) {
        CheckTool.checkCollectionNull(addresses, "IP addresses");
        this.addresses.clear();
        this.addresses.addAll(addresses);
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Host_IPAddresses",
            inverseJoinColumns = @JoinColumn(name = "IPAddress_objId"))
    protected Set<IPAddress> getHostAddresses() {
        return addresses;
    }

    protected void setHostAddresses(Set<IPAddress> addresses) {
        this.addresses = addresses;
    }

    final public void addIPv4Address(IPv4Address addr) {
        CheckTool.checkNull(addr, "IP address");
        this.addresses.add(addr);
    }

    final public void addIPv4Address(String addr) throws InvalidIPAddressException {
        addIPv4Address(IPAddress.createIPv4Address(addr));
    }

    final public void addIPv6Address(IPv6Address addr) {
        CheckTool.checkNull(addr, "IP address");
        this.addresses.add(addr);
    }

    final public void addIPv6Address(String addr) throws InvalidIPAddressException {
        addIPv6Address(IPAddress.createIPv6Address(addr));
    }

    final public void addIPAddress(IPAddress addr) {
        CheckTool.checkNull(addr, "IP address");
        this.addresses.add(addr);
    }

    final public void addIPAddress(String addr) throws InvalidIPAddressException {
        CheckTool.checkNull(addr, "IP address");
        addIPAddress(IPAddress.createIPAddress(addr));
    }

    final void incDelegations() { ++numDelegations; }

    final void decDelegations() { --numDelegations; }

    @Transient
    final public boolean isShared() { return numDelegations > 1; }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Host host = (Host) o;

        if (numDelegations != host.numDelegations) return false;
        if (addresses != null ? !addresses.equals(host.addresses) : host.addresses != null) return false;
        if (name != null ? !name.equals(host.name) : host.name != null) return false;
        if (trackData != null ? !trackData.equals(host.trackData) : host.trackData != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (addresses != null ? addresses.hashCode() : 0);
        result = 31 * result + numDelegations;
        result = 31 * result + (trackData != null ? trackData.hashCode() : 0);
        return result;
    }

    @Transient
    public Long getId() {
        return trackData.getId();
    }

    @Transient
    public Timestamp getCreated() {
        return trackData.getCreated();
    }

    @Transient
    public Timestamp getModified() {
        return trackData.getModified();
    }

    @Transient
    public String getCreatedBy() {
        return trackData.getCreatedBy();
    }

    @Transient
    public String getModifiedBy() {
        return trackData.getModifiedBy();
    }

    @Embedded
    public TrackData getTrackData() {
        return trackData;
    }

    public void setTrackData(TrackData trackData) {
        this.trackData = trackData;
    }
}
