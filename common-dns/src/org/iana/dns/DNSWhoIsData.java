package org.iana.dns;

import java.io.IOException;

/**
 * @author: Piotr Tkaczyk
 */
public interface DNSWhoIsData {

    public String retrieveASNumber(String IPAddress) throws IOException;

}
