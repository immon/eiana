package org.iana.dns.check;

import org.iana.dns.DNSHost;
import org.iana.dns.DNSIPAddress;
import org.iana.dns.check.exceptions.*;
import org.iana.dns.obj.DNSDomainImpl;
import org.iana.dns.obj.DNSHostImpl;
import org.iana.dns.obj.DNSIPAddressImpl;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"DNSExceptionXMLVisitor"})
public class DNSExceptionXMLVisitorTest {

    private static String example = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<exceptions>\n" +
            "\t<domain name=\"de\">\n" +
            "\t\t<ns name=\"a.nic.de\">\n" +
            "\t\t\t<ip>208.48.81.45</ip>\n" +
            "\t\t\t<ip>127.0.0.1</ip>\n" +
            "\t\t\t<ip>81.91.164.5</ip>\n" +
            "\t\t\t<ip>2001:608:6:6:0:0:0:10</ip>\n" +
            "\t\t</ns>\n" +
            "\t\t<ns name=\"b.nic.de\">\n" +
            "\t\t\t<ip>81.91.164.5</ip>\n" +
            "\t\t\t<ip>2001:608:6:6:0:0:0:10</ip>\n" +
            "\t\t</ns>\n" +
            "\t\t<ns name=\"c.de.net\">\n" +
            "\t\t\t<ip>208.48.81.43</ip>\n" +
            "\t\t</ns>\n" +
            "\t\t<ns name=\"f.nic.de\">\n" +
            "\t\t\t<ip>81.91.164.5</ip>\n" +
            "\t\t\t<ip>2001:608:6:6:0:0:0:10</ip>\n" +
            "\t\t</ns>\n" +
            "\t\t<ns name=\"g.nic.net\" />\n" +
            "\t</domain>\n" +
            "\t<exception name=\"EmptyIPAddressListException\">\n" +
            "\t\t<other>\n" +
            "\t\t\t<value name=\"host\">g.nic.net</value>\n" +
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
            "\t\t<host>c.de.net</host>\n" +
            "\t\t<received>\n" +
            "\t\t\t<value name=\"ip\">81.91.164.5</value>\n" +
            "\t\t\t<value name=\"ip\">2001:608:6:6:0:0:0:10</value>\n" +
            "\t\t</received>\n" +
            "\t\t<expected>\n" +
            "\t\t\t<value name=\"ip\">208.48.81.43</value>\n" +
            "\t\t</expected>\n" +
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
            "\t<exception name=\"RadicalAlterationCheckException\" />\n" +
            "\t<exception name=\"NotUniqueIPAddressException\">\n" +
            "\t\t<expected>\n" +
            "\t\t\t<value name=\"host\">a.nic.de</value>\n" +
            "\t\t</expected>\n" +
            "\t\t<other>\n" +
            "\t\t\t<value name=\"host\">b.nic.de</value>\n" +
            "\t\t</other>\n" +
            "\t</exception>\n" +
            "\t<exception name=\"NotEnoughNameServersException\">\n" +
            "\t\t<received>\n" +
            "\t\t\t<value name=\"ns\">2</value>\n" +
            "\t\t</received>\n" +
            "\t\t<expected>\n" +
            "\t\t\t<value name=\"ns\">3</value>\n" +
            "\t\t</expected>\n" +
            "\t</exception>\n" +
            "\t<exception name=\"ReservedIPv4Exception\">\n" +
            "\t\t<host>b.nic.de</host>\n" +
            "\t\t<other>\n" +
            "\t\t\t<value name=\"ip\">127.0.0.1</value>\n" +
            "\t\t</other>\n" +
            "\t</exception>\n" +
            "\t<exception name=\"SerialNumberNotEqualException\">\n" +
            "\t\t<other>\n" +
            "\t\t\t<value name=\"serial number\">232</value>\n" +
            "\t\t\t<value name=\"serial number\">9345</value>\n" +
            "\t\t</other>\n" +
            "\t</exception>\n" +
            "\t<exception name=\"WhoIsIOException\">\n" +
            "\t\t<host>c.de.net</host>\n" +
            "\t\t<other>\n" +
            "\t\t\t<value name=\"ip\">208.48.81.43</value>\n" +
            "\t\t</other>\n" +
            "\t</exception>\n" +
            "\t<exception name=\"InternalDNSCheckException\" />\n" +
            "</exceptions>";

    @Test
    public void xmlVisitorTest() throws Exception {
        DNSDomainImpl domain = new DNSDomainImpl("de");

        DNSHostImpl host1 = new DNSHostImpl("f.nic.de");
        host1.addIPAddress("81.91.164.5");
        host1.addIPAddress("2001:608:6:6::10");
        domain.addNameServer(host1);

        DNSHostImpl host2 = new DNSHostImpl("c.de.net");
        DNSIPAddress ipAddress2_2 = DNSIPAddressImpl.createIPAddress("208.48.81.43");
        host2.addIPAddress(ipAddress2_2);
        domain.addNameServer(host2);

        DNSHostImpl host3 = new DNSHostImpl("g.nic.net");
        domain.addNameServer(host3);

        DNSHostImpl host4 = new DNSHostImpl("a.nic.de");
        host4.addIPAddress("81.91.164.5");
        host4.addIPAddress("2001:608:6:6::10");
        host4.addIPAddress("208.48.81.45");
        host4.addIPAddress("127.0.0.1");
        domain.addNameServer(host4);

        DNSHostImpl host5 = new DNSHostImpl("b.nic.de");
        host5.addIPAddress("2001:608:6:6::10");
        host5.addIPAddress("81.91.164.5");
        domain.addNameServer(host5);

        MultipleDNSTechnicalCheckException multiEx = new MultipleDNSTechnicalCheckException();

        multiEx.addException(new EmptyIPAddressListException(domain, host3));

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

        multiEx.addException(new NameServerIPAddressesNotEqualException(host2, host1.getIPAddresses()));

        multiEx.addException(new NameServerUnreachableByTCPException(host1));
        multiEx.addException(new NameServerUnreachableByUDPException(host2));
        multiEx.addException(new NameServerUnreachableException(host1));

        multiEx.addException(new NoASNumberException(domain, host2, ipAddress2_2));

        multiEx.addException(new NotAuthoritativeNameServerException(domain, host1));

        multiEx.addException(new RadicalAlterationCheckException(domain));



        multiEx.addException(new NotUniqueIPAddressException(domain, host4, host5));

        multiEx.addException(new NotEnoughNameServersException(domain, 3, 2));

        multiEx.addException(new ReservedIPv4Exception(domain, host5, DNSIPAddressImpl.createIPAddress("127.0.0.1")));

        Map<Long, List<DNSHost>> serialNbrs = new HashMap<Long, List<DNSHost>>();

        List<DNSHost> firstHostsList = new ArrayList<DNSHost>();
        firstHostsList.add(host1);
        firstHostsList.add(host2);

        List<DNSHost> secondHostsList = new ArrayList<DNSHost>();
        secondHostsList.add(host3);

        serialNbrs.put(9345l, firstHostsList);
        serialNbrs.put(232l, secondHostsList);

        multiEx.addException(new SerialNumberNotEqualException(domain, serialNbrs));

        multiEx.addException(new NameServerTechnicalCheckException(host5));

        Exception e = new IOException("Socet timeout");

        multiEx.addException(new WhoIsIOException(host2, DNSIPAddressImpl.createIPAddress("208.48.81.43"), e));

        e = new Exception("DataAccessException");

        multiEx.addException(new InternalDNSCheckException(e));

        DNSExceptionXMLVisitor visitor = new DNSExceptionXMLVisitor(domain);
        multiEx.accept(visitor);

        String test = visitor.getXML();
        System.out.println(test);

        test = test.replaceAll("\\s", "");
        example = example.replaceAll("\\s", "");

        assert example.equals(test);
        
    }
}
