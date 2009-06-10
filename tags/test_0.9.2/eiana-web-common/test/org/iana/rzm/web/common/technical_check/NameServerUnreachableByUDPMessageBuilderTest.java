package org.iana.rzm.web.common.technical_check;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;


public class NameServerUnreachableByUDPMessageBuilderTest {

    @Test
    public void testBuildMessage()throws Exception{
        String xml = "<exception name=\"NameServerUnreachableByUDPException\">\n" +
                "       <host>f.nic.de</host>\n" +
                "   </exception>";

        Document document = new SAXBuilder().build(new StringReader(xml));
        Element element = document.getRootElement();
        NameServerUnreachableByUDPMessageBuilder builder = new NameServerUnreachableByUDPMessageBuilder();
        String errorMessage = builder.build(element, "domain");
        Assert.assertEquals(errorMessage, "The folowing host is not reachable by UDP: [f.nic.de]" );
    }
}
