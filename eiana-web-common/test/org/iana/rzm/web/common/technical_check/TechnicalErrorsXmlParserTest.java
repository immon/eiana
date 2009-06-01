package org.iana.rzm.web.common.technical_check;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TechnicalErrorsXmlParserTest {

    private Map<String, DNSTechnicalCheckErrorMessageBuilder> map = new HashMap<String, DNSTechnicalCheckErrorMessageBuilder>();

    @BeforeTest
    public void setUp() {
        map.put("MinimumNetworkDiversityException", new MinimumNetworkDiversityMessageBuilder());
        map.put("EmptyIPAddressListException", new EmptyIPAddressListMessageBuilder());
        map.put("MaximumPayloadSizeExceededException", new MaximumPayloadSizeMessageBuilder());
        map.put("NameServerUnreachableByTCPException", new NameServerUnreachableByTCPMessageBuilder());
        map.put("NameServerUnreachableByUDPException", new NameServerUnreachableByUDPMessageBuilder());
        map.put("NameServerUnreachableException", new NameServerUnreachableMessageBuilder());
        map.put("NotAuthoritativeNameServerException", new NotAuthoritativeNameServerMessageBuilder());
        map.put("NameServerIPAddressesNotEqualException", new NameServerIPAddressesNotEqualMessageBuilder());
        map.put("RadicalAlterationCheckException", new RadicalAlterationCheckMessageBuilder());
        map.put("NotUniqueIPAddressException", new NotUniqueIPAddressMessageBuilder());
        map.put("NotEnoughNameServersException", new NotEnoughNameServersMessageBuilder());
        map.put("SerialNumberNotEqualException", new SerialNumberNotEqualMessageBuilder());
        map.put("WhoIsIOException", new WhoIsIOExceptionMessageBuilder());
    }


    @Test
    public void testGetTechnicalCheckErrors_MinimumNetworkDiversityException() throws Exception {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<exceptions>\n" +
                "   <domain name=\"de\">\n" +
                "       <ns name=\"c.de.net\">\n" +
                "           <ip>208.48.81.43</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"f.nic.de\">\n" +
                "           <ip>81.91.164.5</ip>\n" +
                "           <ip>2001:608:6:6:0:0:0:10</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"g.nic.net\" />\n" +
                "   </domain>\n" +
                "   <exception name=\"MinimumNetworkDiversityException\">\n" +
                "       <received>\n" +
                "           <value name=\"as\">123</value>\n" +
                "       </received>\n" +
                "   </exception>\n" +
                "</exceptions>\n";

        DNSTechnicalCheckErrorsXmlParser parser = new DNSTechnicalCheckErrorsXmlParser(map);
        List<String> list = parser.getTechnicalCheckErrors(xml);
        Assert.assertTrue(list.size() == 1);
        Assert.assertEquals(list.get(0), "All name servers have the same AS number 123");
    }

    @Test
    public void testGetTechnicalCheckErrors_EmptyIpAddressListException() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<exceptions>\n" +
                "   <domain name=\"de\">\n" +
                "       <ns name=\"c.de.net\">\n" +
                "           <ip>208.48.81.43</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"f.nic.de\">\n" +
                "           <ip>81.91.164.5</ip>\n" +
                "           <ip>2001:608:6:6:0:0:0:10</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"g.nic.net\" />\n" +
                "   </domain>\n" +
                "   <exception name=\"EmptyIPAddressListException\">\n" +
                "       <other>\n" +
                "           <value name=\"host\">g.nic.net</value>\n" +
                "       </other>\n" +
                "   </exception>\n" +
                "</exceptions>\n";

        DNSTechnicalCheckErrorsXmlParser parser = new DNSTechnicalCheckErrorsXmlParser(map);
        List<String> list = parser.getTechnicalCheckErrors(xml);
        Assert.assertTrue(list.size() == 1);
        Assert.assertEquals(list.get(0), "The folowing host does not seem to have have an ip address: [g.nic.net]");
    }

    @Test
    public void testGetTechnicalCheckErrors_MaximumPayloadSizeExceededException() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<exceptions>\n" +
                "   <domain name=\"de\">\n" +
                "       <ns name=\"c.de.net\">\n" +
                "           <ip>208.48.81.43</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"f.nic.de\">\n" +
                "           <ip>81.91.164.5</ip>\n" +
                "           <ip>2001:608:6:6:0:0:0:10</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"g.nic.net\" />\n" +
                "   </domain>\n" +
                "   <exception name=\"MaximumPayloadSizeExceededException\">\n" +
                "      <received>\n" +
                "          <value name=\"payload\">623</value>\n" +
                "      </received>\n" +
                "      <expected>\n" +
                "          <value name=\"payload\">512</value>\n" +
                "      </expected>\n" +
                "   </exception>\n" +
                "</exceptions>\n";

        DNSTechnicalCheckErrorsXmlParser parser = new DNSTechnicalCheckErrorsXmlParser(map);
        List<String> list = parser.getTechnicalCheckErrors(xml);
        Assert.assertTrue(list.size() == 1);
        Assert.assertEquals(list.get(0), "The Response estimated size was 623 and it is greater than 512 bytes");
    }

    @Test
    public void testGetTechnicalCheckErrors_NameServerUnreachableByTCPException() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<exceptions>\n" +
                "   <domain name=\"de\">\n" +
                "       <ns name=\"c.de.net\">\n" +
                "           <ip>208.48.81.43</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"f.nic.de\">\n" +
                "           <ip>81.91.164.5</ip>\n" +
                "           <ip>2001:608:6:6:0:0:0:10</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"g.nic.net\" />\n" +
                "   </domain>\n" +
                "   <exception name=\"NameServerUnreachableByTCPException\">\n" +
                "       <host>f.nic.de</host>\n" +
                "   </exception>\n" +
                "</exceptions>\n";

        DNSTechnicalCheckErrorsXmlParser parser = new DNSTechnicalCheckErrorsXmlParser(map);
        List<String> list = parser.getTechnicalCheckErrors(xml);
        Assert.assertTrue(list.size() == 1);
        Assert.assertEquals(list.get(0), "The folowing host is not reachable by TCP: [f.nic.de]");
    }

    @Test
    public void testGetTechnicalCheckErrors_NameServerUnreachableByUDPException() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<exceptions>\n" +
                "   <domain name=\"de\">\n" +
                "       <ns name=\"c.de.net\">\n" +
                "           <ip>208.48.81.43</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"f.nic.de\">\n" +
                "           <ip>81.91.164.5</ip>\n" +
                "           <ip>2001:608:6:6:0:0:0:10</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"g.nic.net\" />\n" +
                "   </domain>\n" +
                "   <exception name=\"NameServerUnreachableByUDPException\">\n" +
                "       <host>f.nic.de</host>\n" +
                "   </exception>\n" +
                "</exceptions>\n";

        DNSTechnicalCheckErrorsXmlParser parser = new DNSTechnicalCheckErrorsXmlParser(map);
        List<String> list = parser.getTechnicalCheckErrors(xml);
        Assert.assertTrue(list.size() == 1);
        Assert.assertEquals(list.get(0), "The folowing host is not reachable by UDP: [f.nic.de]");
    }

    @Test
    public void testGetTechnicalCheckErrors_MultipleNameServerUnreachableByUDPException() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<exceptions>\n" +
                "   <domain name=\"de\">\n" +
                "       <ns name=\"c.de.net\">\n" +
                "           <ip>208.48.81.43</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"f.nic.de\">\n" +
                "           <ip>81.91.164.5</ip>\n" +
                "           <ip>2001:608:6:6:0:0:0:10</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"g.nic.net\" />\n" +
                "   </domain>\n" +
                "   <exception name=\"NameServerUnreachableByUDPException\">\n" +
                "       <host>f.nic.de</host>\n" +
                "   </exception>\n" +
                "  <exception name=\"NameServerUnreachableByUDPException\">\n" +
                "       <host>g.nic.de</host>\n" +
                "   </exception>\n" +
                "</exceptions>\n";

        DNSTechnicalCheckErrorsXmlParser parser = new DNSTechnicalCheckErrorsXmlParser(map);
        List<String> list = parser.getTechnicalCheckErrors(xml);
        Assert.assertTrue(list.size() == 2);
        Assert.assertEquals(list.get(0), "The folowing host is not reachable by UDP: [f.nic.de]");
        Assert.assertEquals(list.get(1), "The folowing host is not reachable by UDP: [g.nic.de]");
    }


    @Test
    public void testGetTechnicalCheckErrors_NameServerUnreachableException() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<exceptions>\n" +
                "   <domain name=\"de\">\n" +
                "       <ns name=\"c.de.net\">\n" +
                "           <ip>208.48.81.43</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"f.nic.de\">\n" +
                "           <ip>81.91.164.5</ip>\n" +
                "           <ip>2001:608:6:6:0:0:0:10</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"g.nic.net\" />\n" +
                "   </domain>\n" +
                "   <exception name=\"NameServerUnreachableException\">\n" +
                "       <host>f.nic.de</host>\n" +
                "   </exception>\n" +
                "</exceptions>\n";

        DNSTechnicalCheckErrorsXmlParser parser = new DNSTechnicalCheckErrorsXmlParser(map);
        List<String> list = parser.getTechnicalCheckErrors(xml);
        Assert.assertTrue(list.size() == 1);
        Assert.assertEquals(list.get(0), "The folowing host is not reachable: [f.nic.de]");
    }

    @Test
    public void testGetTechnicalCheckErrors_NotAuthoritativeNameServerException() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<exceptions>\n" +
                "   <domain name=\"de\">\n" +
                "       <ns name=\"c.de.net\">\n" +
                "           <ip>208.48.81.43</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"f.nic.de\">\n" +
                "           <ip>81.91.164.5</ip>\n" +
                "           <ip>2001:608:6:6:0:0:0:10</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"g.nic.net\" />\n" +
                "   </domain>\n" +
                "   <exception name=\"NotAuthoritativeNameServerException\">\n" +
                "       <host>f.nic.de</host>\n" +
                "   </exception>\n" +
                "</exceptions>\n";

        DNSTechnicalCheckErrorsXmlParser parser = new DNSTechnicalCheckErrorsXmlParser(map);
        List<String> list = parser.getTechnicalCheckErrors(xml);
        Assert.assertTrue(list.size() == 1);
        Assert.assertEquals(list.get(0), "The folowing host: [f.nic.de] is not Authoritative for the domain: de");
    }


    @Test
    public void testGetTechnicalCheckErrors_NameServerIPAddressesNotEqualException() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<exceptions>\n" +
                "   <domain name=\"de\">\n" +
                "       <ns name=\"c.de.net\">\n" +
                "           <ip>208.48.81.43</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"f.nic.de\">\n" +
                "           <ip>81.91.164.5</ip>\n" +
                "           <ip>2001:608:6:6:0:0:0:10</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"g.nic.net\" />\n" +
                "   </domain>\n" +
                "   <exception name=\"NameServerIPAddressesNotEqualException\">\n" +
                "       <host>c.de.net</host>\n" +
                "       <received>\n" +
                "           <value name=\"ip\">81.91.164.5</value>\n" +
                "           <value name=\"ip\">2001:608:6:6:0:0:0:10</value>\n" +
                "       </received>\n" +
                "       <expected>\n" +
                "           <value name=\"ip\">208.48.81.43</value>\n" +
                "       </expected>\n" +
                "   </exception>\n" +
                "</exceptions>\n";

        DNSTechnicalCheckErrorsXmlParser parser = new DNSTechnicalCheckErrorsXmlParser(map);
        List<String> list = parser.getTechnicalCheckErrors(xml);
        Assert.assertTrue(list.size() == 1);
        Assert.assertEquals(list.get(0), "The A and AAAA records [81.91.164.5, 2001:608:6:6:0:0:0:10]" +
                " returned from the authoritative name server [c.de.net] are not the same as the supplied glue" +
                " records [208.48.81.43]");
    }

    @Test
    public void testGetTechnicalCheckErrors_RadicalAlterationCheckException() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<exceptions>\n" +
                "   <domain name=\"de\">\n" +
                "       <ns name=\"c.de.net\">\n" +
                "           <ip>208.48.81.43</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"f.nic.de\">\n" +
                "           <ip>81.91.164.5</ip>\n" +
                "           <ip>2001:608:6:6:0:0:0:10</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"g.nic.net\" />\n" +
                "   </domain>\n" +
                "   <exception name=\"RadicalAlterationCheckException\" />" +
                "</exceptions>\n";

        DNSTechnicalCheckErrorsXmlParser parser = new DNSTechnicalCheckErrorsXmlParser(map);
        List<String> list = parser.getTechnicalCheckErrors(xml);
        Assert.assertTrue(list.size() == 1);
        Assert.assertEquals(list.get(0), "You are trying to change all name servers for domain " + "de" + " We prefer that you " +
                "stagger the request in two requests such that any unexpected faults might be mitigated.");
    }

    @Test
    public void testGetTechnicalCheckErrors_NotUniqueIPAddressException() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<exceptions>\n" +
                "   <domain name=\"de\">\n" +
                "       <ns name=\"c.de.net\">\n" +
                "           <ip>208.48.81.43</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"f.nic.de\">\n" +
                "           <ip>81.91.164.5</ip>\n" +
                "           <ip>2001:608:6:6:0:0:0:10</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"g.nic.net\" />\n" +
                "   </domain>\n" +
                "   <exception name=\"NotUniqueIPAddressException\">\n" +
                "       <expected>\n" +
                "           <value name=\"host\">a.nic.de</value>\n" +
                "       </expected>\n" +
                "       <other>\n" +
                "           <value name=\"host\">b.nic.de</value>\n" +
                "       </other>\n" +
                "   </exception>" +
                "</exceptions>\n";

        DNSTechnicalCheckErrorsXmlParser parser = new DNSTechnicalCheckErrorsXmlParser(map);
        List<String> list = parser.getTechnicalCheckErrors(xml);
        Assert.assertTrue(list.size() == 1);
        Assert.assertEquals(list.get(0), "The following name servers [a.nic.de, b.nic.de] share one or more IP Addresses");
    }

    @Test
    public void testGetTechnicalCheckErrors_NotEnoughNameServersException() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<exceptions>\n" +
                "   <domain name=\"de\">\n" +
                "       <ns name=\"c.de.net\">\n" +
                "           <ip>208.48.81.43</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"f.nic.de\">\n" +
                "           <ip>81.91.164.5</ip>\n" +
                "           <ip>2001:608:6:6:0:0:0:10</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"g.nic.net\" />\n" +
                "   </domain>\n" +
                "   <exception name=\"NotEnoughNameServersException\">\n" +
                "       <received>\n" +
                "           <value name=\"ns\">2</value>\n" +
                "       </received>\n" +
                "       <expected>\n" +
                "           <value name=\"ns\">3</value>\n" +
                "       </expected>\n" +
                "   </exception>" +
                "</exceptions>\n";

        DNSTechnicalCheckErrorsXmlParser parser = new DNSTechnicalCheckErrorsXmlParser(map);
        List<String> list = parser.getTechnicalCheckErrors(xml);
        Assert.assertTrue(list.size() == 1);
        Assert.assertEquals(list.get(0), "IANA Required a minimum of 3 name servers hosts you only have 2");
    }

    @Test
    public void testGetTechnicalCheckErrors_SerialNumberNotEqualException() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<exceptions>\n" +
                "   <domain name=\"de\">\n" +
                "       <ns name=\"c.de.net\">\n" +
                "           <ip>208.48.81.43</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"f.nic.de\">\n" +
                "           <ip>81.91.164.5</ip>\n" +
                "           <ip>2001:608:6:6:0:0:0:10</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"g.nic.net\" />\n" +
                "   </domain>\n" +
                    "<exception name=\"SerialNumberNotEqualException\">\n" +
                "       <other>\n" +
                "           <value name=\"serial number\">232</value>\n" +
                "           <value name=\"serial number\">9345</value>\n" +
                "       </other>\n" +
                "   </exception>"+
                "</exceptions>\n";

        DNSTechnicalCheckErrorsXmlParser parser = new DNSTechnicalCheckErrorsXmlParser(map);
        List<String> list = parser.getTechnicalCheckErrors(xml);
        Assert.assertTrue(list.size() == 1);
        Assert.assertEquals(list.get(0), "The Authoritative name servers SOA serial number does not match: [232, 9345]");
    }

    @Test
    public void testGetTechnicalCheckErrors_WhoIsIOException() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<exceptions>\n" +
                "   <domain name=\"de\">\n" +
                "       <ns name=\"c.de.net\">\n" +
                "           <ip>208.48.81.43</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"f.nic.de\">\n" +
                "           <ip>81.91.164.5</ip>\n" +
                "           <ip>2001:608:6:6:0:0:0:10</ip>\n" +
                "       </ns>\n" +
                "       <ns name=\"g.nic.net\" />\n" +
                "   </domain>\n" +
                "   <exception name=\"WhoIsIOException\">\n" +
                "      <host>c.de.net</host>\n" +
                "      <other>\n" +
                "          <value name=\"ip\">208.48.81.43</value>\n" +
                "      </other>\n" +
                "   </exception>\n"+
                "</exceptions>\n";

        DNSTechnicalCheckErrorsXmlParser parser = new DNSTechnicalCheckErrorsXmlParser(map);
        List<String> list = parser.getTechnicalCheckErrors(xml);
        Assert.assertTrue(list.size() == 1);
        Assert.assertEquals(list.get(0), "We couldn't determine the autonomous system for Name Server [c.de.net] IPV4 [208.48.81.43]");
    }


}
