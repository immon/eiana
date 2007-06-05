package org.iana.dns.whois;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author: Piotr Tkaczyk
 */
@Test(sequential = true, groups = {"common-dns", "WhoIsTest"})
public class WhoIsTest {

    WhoIsDataRetriever retriever;

    @BeforeClass
    public void init() {
        retriever = new WhoIsDataRetriever();
    }

    @Test
    public void testRetrieveFirstASNumber() throws Exception {
        assert "AS8763".equals(retriever.retrieveASNumber("81.91.164.5"));
    }

    @Test
    public void testRetriveSecondASNumber() throws Exception {
        assert "AS3333".equals(retriever.retrieveASNumber("193.0.7.3"));
    }

    @Test
    public void testRetriveThirdASNumber() throws Exception {
        assert "AS31529".equals(retriever.retrieveASNumber("194.246.96.1"));
    }
}
