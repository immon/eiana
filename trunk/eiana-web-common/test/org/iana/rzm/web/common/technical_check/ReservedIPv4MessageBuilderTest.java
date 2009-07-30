package org.iana.rzm.web.common.technical_check;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;


public class ReservedIPv4MessageBuilderTest {

    @Test
    public void testBuildMessage() throws Exception {

        String xml = "<exception name=\"ReservedIPException\">\n" +
                "\t\t<host>b.nic.de</host>\n" +
                "\t\t<other>\n" +
                "\t\t\t<value name=\"ip\">127.0.0.1</value>\n" +
                "\t\t</other>\n" +
                "\t</exception>";

        Document document = new SAXBuilder().build(new StringReader(xml));
        Element element = document.getRootElement();
        ReservedIPv4MessageBuilder builder = new ReservedIPv4MessageBuilder();
        Assert.assertEquals(builder.build(element, "root"), "The following Name Server [b.nic.de] is using a reserved IPV4 Address [127.0.0.1]");
    }

}
