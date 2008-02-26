package org.iana.dns.obj;

import org.iana.dns.DNSZone;
import org.iana.dns.DNSHost;
import org.iana.dns.DNSDomain;
import org.iana.dns.DNSVisitor;

import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DNSZoneImpl extends DNSDomainImpl implements DNSZone {

    long TTL1;

    long TTL2;

    String serial;

    long refresh;

    long retry;

    long expire;

    long minimum;

    String adminAddress;

    DNSHost primaryServer;

    String description;

    Set<DNSDomain> domains;

    public DNSZoneImpl(String name) {
        super(name);
    }

    public long getTTL1() {
        return TTL1;
    }

    public void setTTL1(long TTL1) {
        this.TTL1 = TTL1;
    }

    public long getTTL2() {
        return TTL2;
    }

    public void setTTL2(long TTL2) {
        this.TTL2 = TTL2;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public long getRefresh() {
        return refresh;
    }

    public void setRefresh(long refresh) {
        this.refresh = refresh;
    }

    public long getRetry() {
        return retry;
    }

    public void setRetry(long retry) {
        this.retry = retry;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public long getMinimum() {
        return minimum;
    }

    public void setMinimum(long minimum) {
        this.minimum = minimum;
    }

    public String getAdminAddress() {
        return adminAddress;
    }

    public void setAdminAddress(String adminAddress) {
        this.adminAddress = adminAddress;
    }

    public DNSHost getPrimaryServer() {
        return primaryServer;
    }

    public void setPrimaryServer(DNSHost primaryServer) {
        this.primaryServer = primaryServer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<DNSDomain> getDomains() {
        return domains;
    }

    public void setDomains(Set<DNSDomain> domains) {
        this.domains = domains;
    }

    public void accept(DNSVisitor visitor) {
        visitor.visitZone(this);
    }
}
