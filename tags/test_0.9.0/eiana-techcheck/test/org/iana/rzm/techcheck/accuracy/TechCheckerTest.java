package org.iana.rzm.techcheck.accuracy;

import org.testng.annotations.Test;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.techcheck.TechChecker;
import org.iana.rzm.techcheck.exceptions.DomainCheckException;
import org.iana.rzm.techcheck.exceptions.ExceptionMessage;

import java.util.Set;

/**
 * @author: Piotr Tkaczyk
 */


@Test(sequential=true, groups = {"excluded", "eiana-techcheck", "TechCheckerTest"})
public class TechCheckerTest {

    Domain domain;
    Host host;

    @Test
    public void testTechCheckerDE() throws DomainCheckException {

        try {
            domain = new Domain("de");

            host = new Host("f.nic.de");
            host.addIPAddress("81.91.164.5");
            host.addIPAddress("2001:608:6::5");
            domain.addNameServer(host);

            host = new Host("a.nic.de");
            host.addIPAddress("193.0.7.3");
            domain.addNameServer(host);

            host = new Host("z.nic.de");
            host.addIPAddress("194.246.96.1");
            host.addIPAddress("2001:628:453:4905::53");
            domain.addNameServer(host);

            TechChecker.checkDomain(domain);
        } catch (DomainCheckException e) {
            printError(e);
            throw e; //for debug
        }
    }

    @Test
    public void testTechCheckerUS() throws DomainCheckException {
        try {
            domain = new Domain("us");

            host = new Host("a.gtld.biz");
            host.addIPAddress("209.173.53.162");
            domain.addNameServer(host);

            host = new Host("b.gtld.biz");
            host.addIPAddress("209.173.57.162");
            domain.addNameServer(host);

            host = new Host("c.gtld.biz");
            host.addIPAddress("209.173.60.65");
            domain.addNameServer(host);

            host = new Host("i.gtld.biz");
            host.addIPAddress("156.154.96.126");
            host.addIPAddress("2001:503:d1ae:ffff:ffff:ffff:ffff:ff7e");
            domain.addNameServer(host);

            host = new Host("j.gtld.biz");
            host.addIPAddress("2001:503:a124:ffff:ffff:ffff:ffff:ff7e");
            domain.addNameServer(host);

            host = new Host("k.gtld.biz");
            host.addIPAddress("156.154.72.65");
            domain.addNameServer(host);

            TechChecker.checkDomain(domain);
        } catch (DomainCheckException e) {
            printError(e);
            throw e; //for debug
        }

    }

    private void printError(DomainCheckException e) {
        Set<String> exceptions = e.getExceptionTypes();
        for(String exception : exceptions) {
            System.out.println("Exception: " + exception);
            for (ExceptionMessage exceptionMessage : e.getErrorsByExceptionType(exception))
                System.out.println("\t" + exceptionMessage.getOwner() + " " + exceptionMessage.getValue() + "\n");
        }
    }
}
