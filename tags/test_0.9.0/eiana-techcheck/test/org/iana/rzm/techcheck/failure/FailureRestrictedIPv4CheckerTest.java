package org.iana.rzm.techcheck.failure;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
//import org.iana.dns.validator.SpecialIPAddressChecker;
import org.iana.rzm.techcheck.exceptions.RestrictedIPv4Exception;

import java.util.List;
import java.util.ArrayList;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"failure", "eiana-techcheck", "FailureTechCheckerTest"})
public class FailureRestrictedIPv4CheckerTest {

    List<String> addresses = new ArrayList<String>();

    @BeforeClass
    public void init() {
        addresses.add("0.230.10.15");
        addresses.add("10.230.10.15");
        addresses.add("14.230.10.15");
        addresses.add("24.230.10.15");
        addresses.add("39.230.10.15");
        addresses.add("127.230.10.15");
        addresses.add("128.0.10.15");
        addresses.add("169.254.10.15");
        addresses.add("172.16.10.15");
        addresses.add("172.25.10.15");
        addresses.add("172.31.10.15");
        addresses.add("191.255.10.15");
        addresses.add("192.0.0.15");
        addresses.add("192.0.2.15");
        addresses.add("192.88.99.15");
        addresses.add("192.168.10.15");
        addresses.add("198.18.10.15");
        addresses.add("198.19.10.15");
        addresses.add("223.255.255.15");
        addresses.add("224.230.10.15");
        addresses.add("230.230.10.15");
        addresses.add("239.230.10.15");
        addresses.add("240.230.10.15");
        addresses.add("250.230.10.15");
        addresses.add("255.230.10.15");
    }

    @Test
    public void testRestrictedIPs() {
//        int errorCount = 0;
//        for (String address : addresses) {
//            try {
////                if (SpecialIPAddressChecker.isAllocatedForSpecialUse(address))
////                    throw new RestrictedIPv4Exception(address);
////            } catch (RestrictedIPv4Exception e) {
////                assert e.getValue().equals(address);
//                errorCount++;
////            }
////        }
//        assert errorCount == addresses.size();
    }

}
