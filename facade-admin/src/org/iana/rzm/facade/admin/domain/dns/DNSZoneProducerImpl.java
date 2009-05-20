package org.iana.rzm.facade.admin.domain.dns;

import org.iana.config.impl.ConfigException;
import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;
import org.iana.dns.DNSZone;
import org.iana.dns.RootServersProducer;
import org.iana.dns.obj.DNSZoneImpl;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The default implementation of DNSZoneProducer interface.
 * 
 * @author Patrycja Wegrzynowicz
 */
public class DNSZoneProducerImpl implements DNSZoneProducer {

    private long zoneTTL;

    private long zoneNameServersTTL;

    private long defaultTTL;

    private long refresh;

    private long retry;

    private long expire;

    private long minimum;

    private String adminAddress;

    private String description;

    private String dateOfSerial;

    private int noOfSerial;

    private DomainManager domainManager;

    private RootServersProducer rootServersProducer;

    public DNSZoneProducerImpl(DomainManager domainManager, RootServersProducer rootServersProducer) {
        CheckTool.checkNull(domainManager, "domain manager");
        CheckTool.checkNull(rootServersProducer, "root servers producer");
        this.domainManager = domainManager;
        this.rootServersProducer = rootServersProducer;
    }

    public void setZoneTTL(long zoneTTL) {
        this.zoneTTL = zoneTTL;
    }

    public void setZoneNameServersTTL(long TTL1) {
        this.zoneNameServersTTL = TTL1;
    }

    public void setDefaultTTL(long TTL2) {
        this.defaultTTL = TTL2;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateOfSerial(String dateOfSerial) {
        this.dateOfSerial = dateOfSerial;
    }

    public void setNoOfSerial(int noOfSerial) {
        this.noOfSerial = noOfSerial;
    }

    private List<DNSHost> getNameServers() throws ConfigException {
        return rootServersProducer.getRootServers();
    }

    public DNSZone getDNSZone() throws ConfigException {
        DNSZoneImpl ret = new DNSZoneImpl("");
        ret.setZoneTTL(zoneTTL);
        ret.setZoneNameServersTTL(zoneNameServersTTL);
        ret.setDefaultTTL(defaultTTL);
        ret.setRefresh(refresh);
        ret.setRetry(retry);
        ret.setExpire(expire);
        ret.setMinimum(minimum);
        ret.setAdminAddress(adminAddress);

        List<DNSHost> nameServers = getNameServers();

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
