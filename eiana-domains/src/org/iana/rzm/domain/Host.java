package org.iana.rzm.domain;

import org.iana.rzm.common.Name;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.*;
import java.util.*;
import java.sql.Timestamp;

/**
 * This class represents a computer machine available in the net which serves as a DNS name server to one or many domain names.
 */
@Entity
@Table (uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Host implements TrackedObject,Cloneable {

    /**
     * The name of this host.
     */
    @Embedded
    private Name name;
    /**
     * The set of IP addresses of this host, either IPv4 and IPv6.
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Host_IPAddresses",
            inverseJoinColumns = @JoinColumn(name = "IPAddress_objId"))
    private Set<IPAddress> addresses;
    /**
     * The number of domain names delegated to this host.
     */
    @Basic
    private int numDelegations;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Embedded
    private TrackData trackData = new TrackData();

    public Host() {
        this.addresses = new HashSet<IPAddress>();
        this.numDelegations = 0;
    }

    public Host(String name) throws InvalidNameException {
        setName(name);
        this.addresses = new HashSet<IPAddress>();
        this.numDelegations = 0;
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

    final public Set<IPAddress> getAddresses() {
        return Collections.unmodifiableSet(this.addresses);
    }

    final public void setAddresses(Collection<IPAddress> addresses) {
        this.addresses.clear();
        CheckTool.checkCollectionNull(addresses, "addresses");
        CheckTool.addAllNoDup(this.addresses, addresses);
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

    final void incDelegations() {
        ++numDelegations;
    }

    final void decDelegations() {
        --numDelegations;
    }

    final public boolean isShared() {
        return numDelegations > 1;
    }

    final public boolean isNameServer() {
        return numDelegations > 0;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Host host = (Host) o;

        if (addresses != null ? !addresses.equals(host.addresses) : host.addresses != null) return false;
        if (name != null ? !name.equals(host.name) : host.name != null) return false;

        return true;
    }

    public boolean hibernateEquals(Object o) {
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
        //result = 31 * result + numDelegations;
        //result = 31 * result + (trackData != null ? trackData.hashCode() : 0);
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

    protected Object clone() throws CloneNotSupportedException {
        Host newHost = (Host) super.clone();
        newHost.objId = objId;
        newHost.trackData = trackData == null ? new TrackData() : (TrackData) trackData.clone();

        Set<IPAddress> newAddresses = new HashSet<IPAddress>();
        if (addresses != null) {
            for (IPAddress ip : addresses)
                newAddresses.add(IPAddress.createIPAddress(ip.getAddress()));
        }
        newHost.addresses = newAddresses;
        
        return newHost;
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

    void setDelegations(int num) {
        this.numDelegations = num;
    }
}
