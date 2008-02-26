package org.iana.dns;

import java.util.Set;

public interface DNSZone extends DNSDomain {

    public long getTTL1();

    public long getTTL2();

    public String getSerial();

    public long getRefresh();

    public long getRetry();

    public long getExpire();

    public long getMinimum();

    // todo: conformant to domain-name syntax???
    public String getAdminAddress();

    public DNSHost getPrimaryServer();

    public String getDescription();

    public Set<DNSDomain> getDomains();

}
