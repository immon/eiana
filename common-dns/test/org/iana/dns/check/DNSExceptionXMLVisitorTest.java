package org.iana.dns.check;

import org.testng.annotations.Test;
import org.iana.dns.obj.DNSDomainImpl;
import org.iana.dns.obj.DNSHostImpl;
import org.iana.dns.obj.DNSIPAddressImpl;
import org.iana.dns.check.exceptions.*;
import org.iana.dns.DNSIPAddress;
import org.iana.dns.DNSHost;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class DNSExceptionXMLVisitorTest {

    private static String example = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<exceptions>\n" +
            "\t<domain name=\"de\">\n" +
            "\t\t<ns name=\"c.de.net\">\n" +
            "\t\t\t<ip>208.48.81.43</ip>\n" +
            "\t\t</ns>\n" +
            "\t\t<ns name=\"f.nic.de\">\n" +
            "\t\t\t<ip>81.91.164.5</ip>\n" +
            "\t\t\t<ip>2001:608:6:6:0:0:0:10</ip>\n" +
            "\t\t</ns>\n" +
            "\t</domain>\n" +
            "\t<exception name=\"EmptyIPAddressListException\">\n" +
            "\t\t<other>\n" +
            "\t\t\t<value name=\"host\">f.nic.de</value>\n" +
            "\t\t</other>\n" +
            "\t</exception>\n" +
            "\t<exception name=\"MaximumPayloadSizeExceededException\">\n" +
            "\t\t<received>\n" +
            "\t\t\t<value name=\"payload\">623</value>\n" +
            "\t\t</received>\n" +
            "\t\t<expected>\n" +
            "\t\t\t<value name=\"payload\">512</value>\n" +
            "\t\t</expected>\n" +
            "\t</exception>\n" +
            "\t<exception name=\"MinimumNetworkDiversityException\">\n" +
            "\t\t<received>\n" +
            "\t\t\t<value name=\"as\">123</value>\n" +
            "\t\t</received>\n" +
            "\t</exception>\n" +
            "\t<exception name=\"NameServerCoherencyException\">\n" +
            "\t\t<host>f.nic.de</host>\n" +
            "\t\t<received>\n" +
            "\t\t\t<value name=\"ns\">b.dns.net</value>\n" +
            "\t\t\t<value name=\"ns\">a.dns.net</value>\n" +
            "\t\t</received>\n" +
            "\t\t<expected>\n" +
            "\t\t\t<value name=\"ns\">c.dns.net</value>\n" +
            "\t\t\t<value name=\"ns\">a.dns.net</value>\n" +
            "\t\t</expected>\n" +
            "\t</exception>\n" +
            "\t<exception name=\"NameServerIPAddressesNotEqualException\">\n" +
            "\t\t<received>\n" +
            "\t\t\t<value name=\"ip\">81.91.164.5</value>\n" +
            "\t\t\t<value name=\"ip\">2001:608:6:6:0:0:0:10</value>\n" +
            "\t\t</received>\n" +
            "\t</exception>\n" +
            "\t<exception name=\"NameServerUnreachableByTCPException\">\n" +
            "\t\t<host>f.nic.de</host>\n" +
            "\t</exception>\n" +
            "\t<exception name=\"NameServerUnreachableByUDPException\">\n" +
            "\t\t<host>c.de.net</host>\n" +
            "\t</exception>\n" +
            "\t<exception name=\"NameServerUnreachableException\">\n" +
            "\t\t<host>f.nic.de</host>\n" +
            "\t</exception>\n" +
            "\t<exception name=\"NoASNumberException\">\n" +
            "\t\t<host>c.de.net</host>\n" +
            "\t\t<other>\n" +
            "\t\t\t<value name=\"ip\">208.48.81.43</value>\n" +
            "\t\t</other>\n" +
            "\t</exception>\n" +
            "\t<exception name=\"NotAuthoritativeNameServerException\">\n" +
            "\t\t<host>f.nic.de</host>\n" +
            "\t</exception>\n" +
            "</exceptions>";

    @Test
    public void doTest() throws Exception {
        DNSDomainImpl domain = new DNSDomainImpl("de");

        DNSHostImpl host1 = new DNSHostImpl("f.nic.de");
        host1.addIPAddress("81.91.164.5");
        host1.addIPAddress("2001:608:6:6::10");
        domain.addNameServer(host1);

        DNSHostImpl host2 = new DNSHostImpl("c.de.net");
        DNSIPAddress ipAddress2_2 = DNSIPAddressImpl.createIPAddress("208.48.81.43");
        host2.addIPAddress(ipAddress2_2);
        domain.addNameServer(host2);

        MultipleDNSTechnicalCheckException multiEx = new MultipleDNSTechnicalCheckException();

        multiEx.addException(new EmptyIPAddressListException(domain, host1));

        multiEx.addException(new MaximumPayloadSizeExceededException(domain, 623));

        List<DNSHost> ns = new ArrayList<DNSHost>();
        for (DNSHost host : domain.getNameServers())
            ns.add(host);

        multiEx.addException(new MinimumNetworkDiversityException(domain, "123", ns));

        Set<String> receivedNS = new HashSet<String>();
        receivedNS.add("a.dns.net");
        receivedNS.add("b.dns.net");

        Set<String> expectedNS = new HashSet<String>();
        expectedNS.add("a.dns.net");
        expectedNS.add("c.dns.net");

        multiEx.addException(new NameServerCoherencyException(domain, host1, expectedNS, receivedNS));

        multiEx.addException(new NameServerIPAddressesNotEqualException(null, host1.getIPAddresses()));

        multiEx.addException(new NameServerUnreachableByTCPException(host1));
        multiEx.addException(new NameServerUnreachableByUDPException(host2));
        multiEx.addException(new NameServerUnreachableException(host1));

        multiEx.addException(new NoASNumberException(domain, host2, ipAddress2_2));

        multiEx.addException(new NotAuthoritativeNameServerException(domain, host1));
        
        DNSExceptionXMLVisitor visitor = new DNSExceptionXMLVisitor(domain);
        multiEx.accept(visitor);

        String test = visitor.getXML();
        System.out.println(test);

        test = test.replaceAll("\n", "");
        test = test.replaceAll("\t", "");

        example = example.replaceAll("\n", "");
        example = example.replaceAll("\t", "");

        assert example.equals(test);
        
    }
}
