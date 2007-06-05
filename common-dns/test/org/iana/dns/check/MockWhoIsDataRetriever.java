package org.iana.dns.check;

import org.iana.dns.DNSWhoIsData;

/**
 * @author: Piotr Tkaczyk
 */
class MockWhoIsDataRetriever implements DNSWhoIsData {

    public String retrieveASNumber(String IPAddress) {
        return "AS1234";
    }
}
