package org.iana.dns.check;

import org.iana.dns.check.exceptions.NotEnoughNameServersException;
import org.iana.dns.check.exceptions.NotUniqueIPAddressException;
import org.iana.dns.obj.DNSDomainImpl;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
@Test(sequential = true, groups = {"stress", "common-dns", "DNSTechnicalCheckTest"})
public class MinimumNameServersCheckTest extends AbstractTestHelper {

    DNSTechnicalCheck dnsTechnicalCheck = new DNSTechnicalCheck();

    @BeforeClass
    public void init() {
        List<DNSDomainTechnicalCheck> checks = new ArrayList<DNSDomainTechnicalCheck>();

        checks.add(new MinimumNameServersAndNoReservedIPsCheck());
        dnsTechnicalCheck.setDomainChecks(checks);
    }

    @Test
    public void testFailureDuplicatedIps() {
        try {
            DNSDomainImpl d = new DNSDomainImpl("de");
            d.addNameServer(createHost("z.nic.de", "87.124.123.1", "87.124.123.2"));
            d.addNameServer(createHost("a.nic.de", "87.124.123.1", "87.124.123.2", "87.124.123.3"));

            dnsTechnicalCheck.check(d);

        } catch(DNSTechnicalCheckException e) {
            MultipleDNSTechnicalCheckException ex = (MultipleDNSTechnicalCheckException) e;
            assert ex.getExceptions().size() == 1;
            DNSTechnicalCheckException techEx = ex.getExceptions().iterator().next();
            assert techEx.getClass().isAssignableFrom(NotUniqueIPAddressException.class);
            NotUniqueIPAddressException notUniqueEx = (NotUniqueIPAddressException) techEx;
            assert notUniqueEx.getHostName().equals("z.nic.de");

        }
    }

    @Test
    public void testFailureDuplicatedIpsBack() {
        try {
            DNSDomainImpl d = new DNSDomainImpl("de");
            d.addNameServer(createHost("a.nic.de", "87.124.123.1", "87.124.123.2", "2001:628:453:4905::53"));
            d.addNameServer(createHost("z.nic.de", "87.124.123.1", "87.124.123.2"));

            dnsTechnicalCheck.check(d);

        } catch(DNSTechnicalCheckException e) {
            MultipleDNSTechnicalCheckException ex = (MultipleDNSTechnicalCheckException) e;
            assert ex.getExceptions().size() == 2;
            for(DNSTechnicalCheckException techEx : ex.getExceptions()) {
                if (techEx.getClass().isAssignableFrom(NotEnoughNameServersException.class)) {
                    NotEnoughNameServersException innerEx = (NotEnoughNameServersException) techEx;
                    assert innerEx.getDomain().getNameServerNames().size() == 2;
                    assert innerEx.getDomain().getNameServerNames().contains("a.nic.de");
                    assert innerEx.getDomain().getNameServerNames().contains("z.nic.de");
                } else {
                    assert techEx.getClass().isAssignableFrom(NotUniqueIPAddressException.class);
                    NotUniqueIPAddressException notUniqueEx = (NotUniqueIPAddressException) techEx;
                    assert notUniqueEx.getHostName().equals("z.nic.de");
                }
            }
        }
    }

    @Test
    public void testFailureAtLeastTwoNsWithUniqueIPv4() {
        try {
            DNSDomainImpl d = new DNSDomainImpl("de");
            d.addNameServer(createHost("a.nic.de", "2001:628:453:4905::53"));
            d.addNameServer(createHost("z.nic.de", "87.124.123.1", "87.124.123.2"));
            
            dnsTechnicalCheck.check(d);

        } catch(DNSTechnicalCheckException e) {
            MultipleDNSTechnicalCheckException ex = (MultipleDNSTechnicalCheckException) e;
            assert ex.getExceptions().size() == 1;
            DNSTechnicalCheckException singleEx = ex.getExceptions().iterator().next();
            assert singleEx.getClass().isAssignableFrom(NotEnoughNameServersException.class);
        }
    }

    @Test
    public void testAtLeastTwoNsWithUniqueIPv4() {
        try {
            DNSDomainImpl d = new DNSDomainImpl("de");
            d.addNameServer(createHost("a.nic.de", "87.124.123.1", "87.124.123.3"));
            d.addNameServer(createHost("z.nic.de", "87.124.123.1", "87.124.123.2"));

            dnsTechnicalCheck.check(d);

        } catch(DNSTechnicalCheckException e) {
            MultipleDNSTechnicalCheckException ex = (MultipleDNSTechnicalCheckException) e;
            assert ex.getExceptions().size() == 1;
            DNSTechnicalCheckException singleEx = ex.getExceptions().iterator().next();
            assert singleEx.getClass().isAssignableFrom(NotEnoughNameServersException.class);
        }
    }
}
