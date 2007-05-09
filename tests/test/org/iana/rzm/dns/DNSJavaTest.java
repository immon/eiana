package org.iana.rzm.dns;

import org.testng.annotations.Test;
import org.testng.annotations.AfterTest;
import org.xbill.DNS.*;

import java.net.InetAddress;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"dns", "DNSJavaTest"})
public class DNSJavaTest {

    @Test
    public void testDNS() throws Exception {

        String TLD = "org.";

        Record [] records = new Lookup(TLD, Type.SOA).run();
        assert records != null;
        assert records.length == 1;

        Record [] nsRecords = new Lookup(TLD, Type.NS).run();
        assert nsRecords != null;
        assert records.length == 1;

        for (int i=0; i<nsRecords.length; i++) {

            assert nsRecords[i] instanceof NSRecord;

            Record question = Record.newRecord(new Name(TLD), Type.SOA, DClass.IN);
            Message query = Message.newQuery(question);
            Message response = null;
            String nsName = nsRecords[i].rdataToString();

            response = new SimpleResolver(nsName).send(query);

            assert response.getHeader() != null;
            assert response.getHeader().getFlag(Flags.AA);

        }
    }
}
