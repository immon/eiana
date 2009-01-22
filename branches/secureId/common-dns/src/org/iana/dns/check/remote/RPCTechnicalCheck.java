package org.iana.dns.check.remote;

import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.dns.validator.InvalidDomainNameException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The service to perform technical checks for a domain on a remote server.
 * <p/>
 * Note that JAX-RPC does not support Collection classes that's why a simple API is used.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface RPCTechnicalCheck extends Remote {

    /**
     * JAX-RPC does not support Collection classes that's why a simple API is used.
     *
     * @param domainName the name of the domain to be tested
     * @param nameServers the list of name servers of the domain
     * @param ipAddresses the list of IP addresses of the name servers
     * @return an array of answers to SOA query
     * @throws RemoteException
     * @throws InvalidDomainNameException thrown when domain or host name is invalid
     * @throws InvalidIPAddressException thrown when IP address is invalid
     * @throws IllegalArgumentException thrown when the length of nameServers and ipAddresses do not match
     * @throws RPCTechnicalCheckException
     */
    public SOA[] querySOA(String domainName, String[] nameServers, String[][] ipAddresses) throws RemoteException, RPCTechnicalCheckException;

    /**
     * JAX-RPC does not support Collection classes that's why a simple API is used.
     *
     * @param domainName the name of the domain to be tested
     * @param nameServers the list of name servers of the domain
     * @param ipAddresses the list of IP addresses of the name servers
     * @param dnsCheckRetries number of dns query retries
     * @return an array of answers to SOA query
     * @throws RemoteException
     * @throws InvalidDomainNameException thrown when domain or host name is invalid
     * @throws InvalidIPAddressException thrown when IP address is invalid
     * @throws IllegalArgumentException thrown when the length of nameServers and ipAddresses do not match
     * @throws RPCTechnicalCheckException
     */
    public SOA[] querySOA(String domainName, String[] nameServers, String[][] ipAddresses, int dnsCheckRetries) throws RemoteException, RPCTechnicalCheckException;
}
