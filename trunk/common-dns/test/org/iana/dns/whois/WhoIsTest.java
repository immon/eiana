package org.iana.dns.whois;

import org.testng.annotations.Test;

/**
 * @author: Piotr Tkaczyk
 */
@Test(sequential = true, groups = {"common-dns", "WhoIsTest"})
public class WhoIsTest {

    @Test
    public void testRetrieveASNumbers() {
        WhoIsDataRetriever retriever = new WhoIsDataRetriever();
        assert "AS8763".equals(retriever.retrieveASNumber("81.91.164.5"));
        assert "AS3333".equals(retriever.retrieveASNumber("193.0.7.3"));
        assert "AS31529".equals(retriever.retrieveASNumber("194.246.96.1"));
    }
}
