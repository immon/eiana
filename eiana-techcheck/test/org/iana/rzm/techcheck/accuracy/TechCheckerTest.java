package org.iana.rzm.techcheck.accuracy;

import org.testng.annotations.Test;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.techcheck.TechChecker;
import org.iana.rzm.techcheck.exceptions.DomainCheckException;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"accuracy", "eiana-techcheck", "TechCheckerTest"})
public class TechCheckerTest {

    Domain domain;
    Host host1, host2, host3;

    @Test
    public void testTechChecker() throws DomainCheckException {

        try {
            domain = new Domain("de");
            host1 = new Host("f.nic.de");
            host1.addIPAddress("81.91.164.5");
            host1.addIPAddress("2001:608:6::5");
            domain.addNameServer(host1);
            host2 = new Host("a.nic.de");
            host2.addIPAddress("193.0.7.3");
            domain.addNameServer(host2);
            host3 = new Host("z.nic.de");
            host3.addIPAddress("194.246.96.1");
            host3.addIPAddress("2001:628:453:4905::53");
            domain.addNameServer(host3);

            TechChecker.checkDomain(domain);
        } catch (DomainCheckException e) {
            System.out.print(e.getExceptionTypes());
            throw e; //for debug
        }

    }
}
