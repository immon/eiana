package org.iana.rzm.domain;

import org.hibernate.annotations.Formula;
import org.iana.dns.obj.DNSHostImpl;
import org.iana.dns.validator.InvalidDomainNameException;
import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.dns.DNSHost;
import org.iana.rzm.common.Name;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * This class represents a computer machine available in the net which serves as a DNS name server to one or many domain names.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Host implements TrackedObject, Cloneable {

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
    @Formula("(select count(*) from Domain_NameServers as dns where dns.Host_objId = objId)")
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

    public Host(String name) throws InvalidDomainNameException {
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

    final public String getNameWithDot() {
        return name == null ? null : name.getNameWithDot();
    }

    final public void setName(String name) throws InvalidDomainNameException {
        this.name = new Name(name);
    }

    final public Set<IPAddress> getAddresses() {
        return Collections.unmodifiableSet(new HashSet<IPAddress>(addresses));
    }

    final public List<String> getIPv4Addresses() {
        List<String> retIPv4Address = new ArrayList<String>();
        for (IPAddress ipAddress : getAddresses())
            if (ipAddress.isIPv4()) retIPv4Address.add(ipAddress.getAddress());

        return retIPv4Address;
    }

    final public List<String> getIPv6Addresses() {
        List<String> retIPv6Address = new ArrayList<String>();
        for (IPAddress ipAddress : getAddresses())
            if (ipAddress.isIPv6()) retIPv6Address.add(ipAddress.getAddress());

        return retIPv6Address;
    }

    final public void setAddresses(Collection<IPAddress> addresses) {
        this.addresses = new HashSet<IPAddress>();
        CheckTool.checkCollectionNull(addresses, "addresses");
        CheckTool.addAllNoDup(this.addresses, addresses);
        touch();
    }

    final public void addIPv4Address(IPv4Address addr) {
        CheckTool.checkNull(addr, "IP address");
        this.addresses.add(addr);
        touch();
    }

    final public void addIPv4Address(String addr) throws InvalidIPAddressException {
        addIPv4Address(IPAddress.createIPv4Address(addr));
    }

    final public void addIPv6Address(IPv6Address addr) {
        CheckTool.checkNull(addr, "IP address");
        this.addresses.add(addr);
        touch();
    }

    final public void addIPv6Address(String addr) throws InvalidIPAddressException {
        addIPv6Address(IPAddress.createIPv6Address(addr));
    }

    final public void addIPAddress(IPAddress addr) {
        CheckTool.checkNull(addr, "IP address");
        this.addresses.add(addr);
        touch();
    }

    final public void addIPAddress(String addr) throws InvalidIPAddressException {
        CheckTool.checkNull(addr, "IP address");
        addIPAddress(IPAddress.createIPAddress(addr));
    }

    final public void removeIPAddress(IPAddress addr) {
        CheckTool.checkNull(addr, "IP address");
        this.addresses.remove(addr);
        touch();
    }

    final public void removeIPAddress(String addr) {
        CheckTool.checkNull(addr, "IP address");
        this.addresses.remove(IPAddress.createIPAddress(addr));
        touch();
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
        CheckTool.checkNull(trackData, "track data");
        this.trackData = trackData;
    }

    public Host clone() {
        try {
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
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
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

    void touch() {
        setModified(new Timestamp(System.currentTimeMillis()));
    }

    void checkModification(long timestamp, String modifiedBy) {
        Timestamp created = getCreated();
        Timestamp modified = getModified();
        if (created != null && created.getTime() >= timestamp) {
            setModified(null);
            setCreatedBy(modifiedBy);
        } else if (modified != null && modified.getTime() >= timestamp) {
            setModifiedBy(modifiedBy);
        }
    }

    public DNSHost toDNSHost() {
        DNSHostImpl ret = new DNSHostImpl(getName());
        for (IPAddress ip : getAddresses()) {
            ret.addIPAddress(ip.getAddress());
        }
        return ret;
    }
}
