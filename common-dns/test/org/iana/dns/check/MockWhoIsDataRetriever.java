package org.iana.dns.check;

import org.iana.dns.whois.DNSWhoIsDataRetriever;

/**
 * @author Piotr Tkaczyk
 */
class MockWhoIsDataRetriever implements DNSWhoIsDataRetriever {

    public String retrieveASNumber(String IPAddress) {
        return "AS1234";
    }
}
