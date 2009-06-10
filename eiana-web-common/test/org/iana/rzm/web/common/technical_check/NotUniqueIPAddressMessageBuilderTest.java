package org.iana.rzm.web.common.technical_check;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;


public class NotUniqueIPAddressMessageBuilderTest {

    @Test
    public void testBuildMessage()throws Exception{
        String xml ="<exception name=\"NotUniqueIPAddressException\">\n" +
                        "<expected>\n" +
                            "<value name=\"host\">a.nic.de</value>\n" +
                        "</expected>\n" +
                        "<other>\n" +
                            "<value name=\"host\">b.nic.de</value>\n" +
                        "</other>\n" +
                    "</exception>";

        Document document = new SAXBuilder().build(new StringReader(xml));
        Element element = document.getRootElement();
        NotUniqueIPAddressMessageBuilder builder = new NotUniqueIPAddressMessageBuilder();
        Assert.assertEquals(builder.build(element, "root"), "The following name servers [a.nic.de, b.nic.de] share one or more IP Addresses.");
    }

}
