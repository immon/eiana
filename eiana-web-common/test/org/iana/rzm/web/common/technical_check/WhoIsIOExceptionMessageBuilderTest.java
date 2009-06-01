package org.iana.rzm.web.common.technical_check;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;


public class WhoIsIOExceptionMessageBuilderTest {

    @Test
    public void testBuildMessge() throws Exception {
        String xml = "<exception name=\"WhoIsIOException\">\n" +
                "\t\t<host>c.de.net</host>\n" +
                "\t\t<other>\n" +
                "\t\t\t<value name=\"ip\">208.48.81.43</value>\n" +
                "\t\t</other>\n" +
                "\t</exception>";
        Document document = new SAXBuilder().build(new StringReader(xml));
        Element element = document.getRootElement();
        WhoIsIOExceptionMessageBuilder builder = new WhoIsIOExceptionMessageBuilder();
        Assert.assertEquals(builder.build(element, "root"), "We couldn't determine the autonomous system for Name Server [c.de.net] IPV4 [208.48.81.43]");
    }
}
