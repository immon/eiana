package org.iana.rzm.web.common.technical_check;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;


public class NotEnoughNameServersMessageBuilderTest {

    @Test
    public void testBuildMessage()throws Exception{
        String xml =
                "<exception name=\"NotEnoughNameServersException\">\n" +
                "   <received>\n" +
                "       <value name=\"ns\">2</value>\n" +
                "   </received>\n" +
                "   <expected>\n" +
                "       <value name=\"ns\">3</value>\n" +
                "   </expected>\n" +
                "</exception>";

        Document document = new SAXBuilder().build(new StringReader(xml));
        Element element = document.getRootElement();
        NotEnoughNameServersMessageBuilder builder = new NotEnoughNameServersMessageBuilder();
        Assert.assertEquals(builder.build(element, "root"), "IANA Required a minimum of 3 name servers hosts you only have 2.");
    }
}
