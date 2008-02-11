package org.iana.dns;

public interface DNSZone extends DNSDomain {

    public String getTTL();

    public long getSerial();

    public long getRefresh();

    public long getRetry();

    public long getExpire();

    public long getMinimum();

    public String getAdminAddress();

    public String getPrimaryServer();

    public String getDescription();

}
