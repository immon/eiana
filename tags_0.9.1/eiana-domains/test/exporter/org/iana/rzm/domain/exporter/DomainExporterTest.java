package org.iana.rzm.domain.exporter;

import org.iana.rzm.domain.Address;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.conf.SpringDomainsApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"dao", "eiana-domains", "DomainExporterTest"})
public class DomainExporterTest {

    private DomainExporter domainExporter;

    private List<Domain> domains;

    String firstFile;

    @BeforeClass
    public void init() {
        domainExporter = (DomainExporter) SpringDomainsApplicationContext.getInstance().getContext().getBean("domainExporter");

        domains = new ArrayList<Domain>();

        Address address = new Address("street \n city \n state", "PL");
        Contact contact = new Contact("first name", "first org\ncity\nstate", address, "+48 1232", "+48 1234", "emil@none.com", true);

        Domain newDomain = new Domain("firstdomain");
        newDomain.setSupportingOrg(contact);
        newDomain.setAdminContact(contact);
        newDomain.setTechContact(contact);
        newDomain.setRegistryUrl("registry.url.pl");
        newDomain.setWhoisServer("whois.registry.url.pl");

        Host host = new Host("firstHost");
        host.addIPAddress("192.168.1.1");
        newDomain.addNameServer(host);
        host = new Host("secondHost");
        host.addIPAddress("192.168.1.1");
        host.addIPAddress("ffff::1af");
        newDomain.addNameServer(host);

        newDomain.setModified(new Timestamp(System.currentTimeMillis()));

        domains.add(newDomain);

        domainExporter.exportToXML(domains);
    }

    @Test
    public void checkFirstFileTest() throws Exception {
        firstFile = readXMLFromFile("exportedDomains.xml");
        assert firstFile != null;
        firstFile = cleanUpString(firstFile);
        String fromObject = cleanUpString(generateXML(domains));
        assert firstFile.equals(fromObject);
    }

    @Test(dependsOnMethods = "checkFirstFileTest")
    public void checkSecondFileTest() throws Exception {

        domains.add(new Domain("seconddomain"));
        domainExporter.exportToXML(domains);

        String newFile = readXMLFromFile("exportedDomains.xml");
        assert newFile != null;
        newFile = cleanUpString(newFile);
        String fromObject = cleanUpString(generateXML(domains));
        assert newFile.equals(fromObject);

        String oldFile = readXMLFromFile("exportedDomains.xml.old");
        assert oldFile != null;
        oldFile = cleanUpString(oldFile);
        assert firstFile.equals(oldFile);
    }

    private String generateXML(List<Domain> domains) throws Exception {
        StringWriter writer = new StringWriter();
        domainExporter.parseToXML(domains, writer);
        return writer.toString();
    }

    private String cleanUpString(String data) {
        return data.replaceAll("\\s", "");
    }

    private String readXMLFromFile(String fileName) throws Exception {
        File f = new File(fileName);

        FileReader input = new FileReader(f);
        BufferedReader buff = new BufferedReader(input);
        StringBuffer stringBuff = new StringBuffer();

        String line;
        while ((line = buff.readLine()) != null) {
            stringBuff.append(line);
        }
        return stringBuff.toString();
    }


}
