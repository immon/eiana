package org.iana.rzm.facade.system.trans;

import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class SharedNameServersCollisionException extends TransactionServiceException {

    Set<String> nameServers;

    public SharedNameServersCollisionException(Set<String> nameServers) {
        this.nameServers = nameServers;
    }

    public Set<String> getNameServers() {
        return nameServers;
    }
}
