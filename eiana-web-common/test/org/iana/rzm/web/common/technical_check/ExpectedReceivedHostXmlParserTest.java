package org.iana.rzm.web.common.technical_check;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: May 26, 2009
 * Time: 11:46:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class ExpectedReceivedHostXmlParserTest {

    @Test
    public void testxmlParsing()throws Exception{

        String xml =
                "<exception name=\"NameServerIPAddressesNotEqualException\">\n" +
                    "<host>c.de.net</host>\n" +
                    "<received>\n" +
                        "<value name=\"ip\">81.91.164.5</value>\n" +
                        "<value name=\"ip\">2001:608:6:6:0:0:0:10</value>\n" +
                    "</received>\n" +
                    "<expected>\n" +
                        "<value name=\"ip\">208.48.81.43</value>\n" +
                    "</expected>\n" +
                "</exception>";

        Document document = new SAXBuilder().build(new StringReader(xml));
        Element element = document.getRootElement();
        ExpectedReceivedHostXmlParser parser = new ExpectedReceivedHostXmlParser(element);
        Assert.assertEquals(parser.getExpected(), Arrays.asList("208.48.81.43"));
        Assert.assertEquals(parser.getReceived(), Arrays.asList("81.91.164.5", "2001:608:6:6:0:0:0:10"));
        Assert.assertEquals(parser.getHost(), "c.de.net");
    }
}
