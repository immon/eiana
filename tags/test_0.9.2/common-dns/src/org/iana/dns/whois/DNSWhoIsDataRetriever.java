package org.iana.dns.whois;

import java.io.IOException;

/**
 * Interface for WHOIS data retriever service.
 *
 * @author Piotr Tkaczyk
 */
public interface DNSWhoIsDataRetriever {

    /**
     * Returns AS number for given IP address.
     *
     * @param IPAddress given ip address.
     * @return AS number or empty string if there is no AS number for given IP address.
     * @throws IOException when something goes wrong in service.
     */
    public String retrieveASNumber(String IPAddress) throws IOException;

}
