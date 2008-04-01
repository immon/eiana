package org.iana.dns.check;

import org.iana.dns.check.exceptions.*;

/**
 * @author Jakub Laszkiewicz
 */
public interface DNSTechnicalCheckExceptionVisitor {
    public void acceptMultipleDNSTechnicalCheckException(MultipleDNSTechnicalCheckException e);

    public void acceptDomainTechnicalCheckException(DomainTechnicalCheckException e);

    public void acceptMinimumNetworkDiversityException(MinimumNetworkDiversityException e);

    public void acceptNotUniqueIPAddressException(NotUniqueIPAddressException e);

    public void acceptEmptyIPAddressListException(EmptyIPAddressListException e);

    public void acceptMaximumPayloadSizeExceededException(MaximumPayloadSizeExceededException e);

    public void acceptNameServerCoherencyException(NameServerCoherencyException e);

    public void acceptNameServerIPAddressesNotEqualException(NameServerIPAddressesNotEqualException e);

    public void acceptNameServerTechnicalCheckException(NameServerTechnicalCheckException e);

    public void acceptNameServerUnreachableByTCPException(NameServerUnreachableByTCPException e);

    public void acceptNameServerUnreachableByUDPException(NameServerUnreachableByUDPException e);

    public void acceptNameServerUnreachableException(NameServerUnreachableException e);

    public void acceptNoASNumberException(NoASNumberException e);

    public void acceptNotAuthoritativeNameServerException(NotAuthoritativeNameServerException e);

    public void acceptNotEnoughNameServersException(NotEnoughNameServersException e);

    public void acceptReservedIPv4Exception(ReservedIPv4Exception e);

    public void acceptSerialNumberNotEqualException(SerialNumberNotEqualException e);

    public void acceptWhoIsIOException(WhoIsIOException e);

    public void acceptDNSCheckIOException(DNSCheckIOException e);
}
