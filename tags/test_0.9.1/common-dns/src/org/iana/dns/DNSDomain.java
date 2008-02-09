package org.iana.dns;

import java.util.Map;
import java.util.Set;

/**
 * This interface models a domain configured in DNS.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface DNSDomain {

    /**
     * Returns the name of this domain. The returned name is lower-cased and is a valid name according to RFC 1034.
     * Examples: <code>com</code> <code>nask.pl</code>
     *
     * @return the name of this domain.
     */
    String getName();

    /**
     * Returns the name of this domain with a trailing dot. The returned name is lower-cased.
     * Examples: <code>com.</code> <code>nask.pl.</code>
     *
     * @return the name of this domain with a trailing dot.
     */
    String getNameWithDot();

    /**
     * Returns an array of labels of this domain name.
     * Example: <code>[com]</code> <code>[nask,pl]<code>
     * Java notation (valid Java arrays): <code>{"com"}</code> <code>{"nask", "pl"}</code>
     *
     * @return an array of labels of this domain name.
     */
    String[] getLabels();

    /**
     * Returns a set of name servers to which this domain is delegated. Each host is uniquely
     * identified by its name.
     *
     * @return the name servers of this domain.
     */
    Set<DNSHost> getNameServers();

    /**
     * Returns a map of name server names to name server objects to which this domain is delegated to.
     *
     * @return the map of name server names to name server objects of this domain.
     */
    Map<String, DNSHost> getNameServerMap();

    /**
     * Returns a set of names of name servers to which this domain is delegated. Names are lower-cased.
     *
     * @return the set of names of the name servers of this domain.
     */
    Set<String> getNameServerNames();

    /**
     * Returns a name server object that represents a name server with a given name to which this domain is delegated.
     *
     * @param name the name of the name server to be found
     * @return the name server of this domain identified by the given name; null if not found.
     */
    DNSHost getNameServer(String name);
}
