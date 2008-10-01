package org.iana.rzm.facade.admin.domain.dns;

import org.iana.dns.DNSZone;
import org.iana.dns.DNSHost;
import org.iana.dns.DNSDomain;
import org.iana.dns.obj.DNSZoneImpl;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.common.validators.CheckTool;

import java.util.*;
import java.text.SimpleDateFormat;

/**
 * The default implementation of DNSZoneProducer interface.
 * 
 * @author Patrycja Wegrzynowicz
 */
public class DNSZoneProducerImpl implements DNSZoneProducer {

    private long TTL1;

    private long TTL2;

    private long refresh;

    private long retry;

    private long expire;

    private long minimum;

    private String adminAddress;

    private List<DNSHost> nameServers;

    private String description;

    private String dateOfSerial;

    private int noOfSerial;

    private DomainManager domainManager;

    public DNSZoneProducerImpl(DomainManager domainManager) {
        CheckTool.checkNull(domainManager, "domain manager");
        this.domainManager = domainManager;
    }

    public void setTTL1(long TTL1) {
        this.TTL1 = TTL1;
    }

    public void setTTL2(long TTL2) {
        this.TTL2 = TTL2;
    }

    public void setRefresh(long refresh) {
        this.refresh = refresh;
    }

    public void setRetry(long retry) {
        this.retry = retry;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public void setMinimum(long minimum) {
        this.minimum = minimum;
    }

    public void setAdminAddress(String adminAddress) {
        this.adminAddress = adminAddress;
    }

    public void setNameServers(List<DNSHost> nameServers) {
        this.nameServers = nameServers;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateOfSerial(String dateOfSerial) {
        this.dateOfSerial = dateOfSerial;
    }

    public void setNoOfSerial(int noOfSerial) {
        this.noOfSerial = noOfSerial;
    }

    public DNSZone getDNSZone() {
        DNSZoneImpl ret = new DNSZoneImpl("");
        ret.setZoneNameServersTTL(TTL1);
        ret.setDefaultTTL(TTL2);
        ret.setRefresh(refresh);
        ret.setRetry(retry);
        ret.setExpire(expire);
        ret.setMinimum(minimum);
        ret.setAdminAddress(adminAddress);
        if (nameServers != null && nameServers.size() > 0) {
            ret.setPrimaryServer(nameServers.get(0));
        }
        ret.setDescription(description);
        ret.setSerial(getSerial());
        ret.setNameServers(new HashSet<DNSHost>(nameServers));
        ret.setDomains(getDomains());
        return ret;
    }

    private synchronized String getSerial() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String curDateOfSerial = sdf.format(new Date());
        if (curDateOfSerial.equals(dateOfSerial)) {
            noOfSerial++;
        } else {
            dateOfSerial = curDateOfSerial;
            noOfSerial = 0;
        }
        String zeroPaddedNoOfSerial = (noOfSerial < 10) ? "0"+noOfSerial : ""+noOfSerial;
        return dateOfSerial+zeroPaddedNoOfSerial;
    }

    private Set<DNSDomain> getDomains() {
        Set<DNSDomain> ret = new TreeSet<DNSDomain>();
        // todo: only active domains
        for (Domain domain : domainManager.findAll()) {
            ret.add(domain.toDNSDomain());
        }
        return ret;
    }
}
