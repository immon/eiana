package org.iana.rzm.web.common.technical_check;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;


public class SerialNumberNotEqualmessageBuilderTest {

    @Test
    public void testBuildMessage() throws Exception {
        String xml = "<exception name=\"SerialNumberNotEqualException\">\n" +
                "\t\t<other>\n" +
                "\t\t\t<value name=\"serial number\">232</value>\n" +
                "\t\t\t<value name=\"serial number\">9345</value>\n" +
                "\t\t</other>\n" +
                "\t</exception>";

        Document document = new SAXBuilder().build(new StringReader(xml));
        Element element = document.getRootElement();
        SerialNumberNotEqualMessageBuilder builder = new SerialNumberNotEqualMessageBuilder();
        Assert.assertEquals(builder.build(element, "root"), "The Authoritative name servers SOA serial number does not match: [232, 9345]");

    }


}
