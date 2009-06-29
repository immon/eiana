package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSIPAddress;
import org.iana.dns.check.decorators.DomainDataDecorator;
import org.iana.dns.check.decorators.ExceptionDataDecorator;
import org.iana.dns.check.decorators.ValueDataDecorator;
import org.iana.dns.check.exceptions.*;
import pl.nask.xml.dynamic.DynaXMLParser;
import pl.nask.xml.dynamic.config.DPConfig;
import pl.nask.xml.dynamic.env.Environment;
import pl.nask.xml.dynamic.exceptions.DynaXMLException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class DNSExceptionXMLVisitor implements DNSTechnicalCheckExceptionVisitor {

    private final static String ENV_PROPERTIES = "xmlExport.properties";

    private final static String AS_NUMBER = "as";
    private final static String HOST = "host";
    private final static String PAYLOAD = "payload";
    private final static String NS = "ns";
    private final static String IP = "ip";
    private final static String SERIAL_NUMBER = "serial number";

    DomainDataDecorator domain;
    List<ExceptionDataDecorator> exceptions = new ArrayList<ExceptionDataDecorator>();

    public DNSExceptionXMLVisitor(DNSDomain domain) {
        this.domain = new DomainDataDecorator(domain); 
    }

    public DomainDataDecorator getDomain() {
        return domain;
    }

    public List<ExceptionDataDecorator> getExceptions() {
        return exceptions;
    }

    public void acceptMultipleDNSTechnicalCheckException(MultipleDNSTechnicalCheckException e) {
        for (DNSTechnicalCheckException subEx : e.getExceptions()) 
            subEx.accept(this);
    }

    public void acceptMinimumNetworkDiversityException(MinimumNetworkDiversityException e) {
        ExceptionDataDecorator exceptionDecorator = new ExceptionDataDecorator(getSimpleName(e));

        String asNumber = e.getASNumber();
        if (asNumber == null || asNumber.trim().length() == 0) asNumber = "no AS number";
        ValueDataDecorator valueDecorator = new ValueDataDecorator(AS_NUMBER, asNumber);
        exceptionDecorator.addReceived(valueDecorator);
        exceptions.add(exceptionDecorator);
    }

    public void acceptNotUniqueIPAddressException(NotUniqueIPAddressException e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));

        ValueDataDecorator expectedHostName = new ValueDataDecorator(HOST, e.getHostName());
        exceptionDataDecorator.addExpected(expectedHostName);

        ValueDataDecorator otherHostName = new ValueDataDecorator(HOST, e.getOtherHost().getName());
        exceptionDataDecorator.addOther(otherHostName);
        
        exceptions.add(exceptionDataDecorator);
    }

    public void acceptEmptyIPAddressListException(EmptyIPAddressListException e) {
        ExceptionDataDecorator exceptionDecorator = new ExceptionDataDecorator(getSimpleName(e));

        ValueDataDecorator valueDecorator = new ValueDataDecorator(HOST, e.getHostName());
        exceptionDecorator.addOther(valueDecorator);
        exceptions.add(exceptionDecorator);
    }

    public void acceptMaximumPayloadSizeExceededException(MaximumPayloadSizeExceededException e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));

        ValueDataDecorator received = new ValueDataDecorator(PAYLOAD, e.getEstimatedSize());
        ValueDataDecorator expected = new ValueDataDecorator(PAYLOAD, MaximumPayloadSizeCheck.MAX_SIZE);
        exceptionDataDecorator.addReceived(received);
        exceptionDataDecorator.addExpected(expected);
        exceptions.add(exceptionDataDecorator);
    }

    public void acceptNameServerCoherencyException(NameServerCoherencyException e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));
        exceptionDataDecorator.setHostName(e.getHostName());

        for (String receivedNS : e.getReceivedNameServers()) {
            ValueDataDecorator received = new ValueDataDecorator(NS, receivedNS);
            exceptionDataDecorator.addReceived(received);
        }
        for (String expectedNS: e.getExpectedNameServers()) {
            ValueDataDecorator expected = new ValueDataDecorator(NS, expectedNS);
            exceptionDataDecorator.addExpected(expected);
        }

        exceptions.add(exceptionDataDecorator);
    }

    public void acceptNameServerIPAddressesNotEqualException(NameServerIPAddressesNotEqualException e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));
        exceptionDataDecorator.setHostName(e.getHostName());

        for (DNSIPAddress ipAddress : e.getHost().getIPAddresses()) {
            ValueDataDecorator expected = new ValueDataDecorator(IP, ipAddress.getAddress());
            exceptionDataDecorator.addExpected(expected);
        }

        for (DNSIPAddress ipAddress : e.getReturnedIPAddresses()) {
            ValueDataDecorator returned = new ValueDataDecorator(IP, ipAddress.getAddress());
            exceptionDataDecorator.addReceived(returned);
        }

        exceptions.add(exceptionDataDecorator);
    }

    public void acceptNameServerUnreachableByTCPException(NameServerUnreachableByTCPException e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));
        exceptionDataDecorator.setHostName(e.getHostName());
        exceptions.add(exceptionDataDecorator);
    }

    public void acceptNameServerUnreachableByUDPException(NameServerUnreachableByUDPException e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));
        exceptionDataDecorator.setHostName(e.getHostName());
        exceptions.add(exceptionDataDecorator);
    }

    public void acceptNameServerUnreachableException(NameServerUnreachableException e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));
        exceptionDataDecorator.setHostName(e.getHostName());
        exceptions.add(exceptionDataDecorator);
    }

    public void acceptNoASNumberException(NoASNumberException e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));
        exceptionDataDecorator.setHostName(e.getHostName());
        DNSIPAddress ipAddress = e.getIpAddress();
        if (ipAddress != null) {
            ValueDataDecorator valueDecorator = new ValueDataDecorator(IP, ipAddress.getAddress());
            exceptionDataDecorator.addOther(valueDecorator);
        }

        exceptions.add(exceptionDataDecorator);
    }

    public void acceptNotAuthoritativeNameServerException(NotAuthoritativeNameServerException e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));
        exceptionDataDecorator.setHostName(e.getHostName());
        exceptions.add(exceptionDataDecorator);
    }

    public void acceptNotEnoughNameServersException(NotEnoughNameServersException e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));
        exceptionDataDecorator.addExpected(new ValueDataDecorator(NS, e.getExpected()));
        exceptionDataDecorator.addReceived(new ValueDataDecorator(NS, e.getReceived()));
        exceptions.add(exceptionDataDecorator);
    }

    public void acceptReservedIPv4Exception(ReservedIPv4Exception e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));
        exceptionDataDecorator.setHostName(e.getHostName());
        DNSIPAddress ipAddress = e.getIpAddress();
        if (ipAddress != null) {
            ValueDataDecorator valueDecorator = new ValueDataDecorator(IP, ipAddress.getAddress());
            exceptionDataDecorator.addOther(valueDecorator);
        }

        exceptions.add(exceptionDataDecorator);
    }

    public void acceptSerialNumberNotEqualException(SerialNumberNotEqualException e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));
        for (Long number : e.getSerialNumbers()) {
            if (number != null) {
                ValueDataDecorator valueDecorator = new ValueDataDecorator(SERIAL_NUMBER, number.intValue());
                exceptionDataDecorator.addOther(valueDecorator);
            }
        }

        exceptions.add(exceptionDataDecorator);
    }

    public void acceptWhoIsIOException(WhoIsIOException e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));
        exceptionDataDecorator.setHostName(e.getHostName());
        DNSIPAddress ipAddress = e.getIpAddress();
        if (ipAddress != null) {
            ValueDataDecorator valueDecorator = new ValueDataDecorator(IP, ipAddress.getAddress());
            exceptionDataDecorator.addOther(valueDecorator);
        }

        exceptions.add(exceptionDataDecorator);
    }

    public void acceptRadicalAlterationException(RadicalAlterationCheckException e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));

        exceptions.add(exceptionDataDecorator);
    }


    public void acceptInternalDNSCheckException(InternalDNSCheckException e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));
        exceptions.add(exceptionDataDecorator);
    }

    public void acceptDNSCheckIOException(DNSCheckIOException e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));
        exceptions.add(exceptionDataDecorator);
    }
    
    public void acceptRootServersPropagationException(RootServersPropagationException e) {
        ExceptionDataDecorator exceptionDataDecorator = new ExceptionDataDecorator(getSimpleName(e));
        exceptionDataDecorator.setHostName(e.getHostName());
        exceptions.add(exceptionDataDecorator);
    }

    public void acceptDomainTechnicalCheckException(DomainTechnicalCheckException e) {
        //unused
    }

    public void acceptNameServerTechnicalCheckException(NameServerTechnicalCheckException e) {
        //unused
    }

    public String getXML() {
        try {
            Environment env = DPConfig.getEnvironment(ENV_PROPERTIES);
            DynaXMLParser parser = new DynaXMLParser();
            return parser.toXML(this, env);
        } catch(DynaXMLException e) {
            return e.getMessage();   
        }
    }

    private String getSimpleName(Exception e) {
        return e.getClass().getSimpleName();
    }
}
