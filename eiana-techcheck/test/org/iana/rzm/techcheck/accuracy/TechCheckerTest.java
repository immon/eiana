package org.iana.rzm.techcheck.accuracy;

import org.testng.annotations.Test;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.techcheck.TechChecker;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"accuracy", "eiana-techcheck", "TechCheckerTest"})
public class TechCheckerTest {

    Domain domain;
    Host host1, host2, host3;

    @Test
    public void testTechChecker() throws Exception {

        domain = new Domain("org");
        host1 = new Host("tld3.ultradns.org");
        host1.addIPAddress("199.7.66.1");
        domain.addNameServer(host1);
        host2 = new Host("tld4.ultradns.org");
        host2.addIPAddress("199.7.67.1");
        domain.addNameServer(host2);
        host3 = new Host("b0.org.afilias-nst.org");
        host3.addIPAddress("199.19.54.1");
        host3.addIPAddress("2001:500:c::1");
        domain.addNameServer(host3);
        
        TechChecker.checkDomain(domain);
    }

}
