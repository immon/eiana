package org.iana.rzm.techcheck;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.IPAddress;
import org.iana.rzm.techcheck.exceptions.NotEnoughHostsException;
import org.iana.rzm.techcheck.exceptions.DuplicatedIPAddressException;
import org.iana.rzm.techcheck.exceptions.DomainValidationException;
import org.iana.rzm.techcheck.exceptions.EmptyIPAddressListException;

import java.util.List;
import java.util.ArrayList;

/**
 * @author: Piotr Tkaczyk
 */

public class TechChecker {

    public static void checkDomain(Domain domain) throws DomainValidationException {
        checkNameServers(domain.getNameServers());
    }

    private static void checkNameServers(List<Host> nameServers) throws NotEnoughHostsException, DuplicatedIPAddressException, EmptyIPAddressListException {
        if (nameServers.size() < 2) throw new NotEnoughHostsException();
        List<IPAddress> ipAddresses = new ArrayList<IPAddress>();
        for (Host nameServer : nameServers) {
            if (nameServer.getAddresses().isEmpty()) throw new EmptyIPAddressListException();
            if (ipAddresses.containsAll(nameServer.getAddresses())) {
                for (IPAddress ipAddress : nameServer.getAddresses()){
                    if (ipAddresses.contains(ipAddress)) throw new DuplicatedIPAddressException(ipAddress.getAddress());
                }
            }
            ipAddresses.addAll(nameServer.getAddresses());
        }
    }
}
