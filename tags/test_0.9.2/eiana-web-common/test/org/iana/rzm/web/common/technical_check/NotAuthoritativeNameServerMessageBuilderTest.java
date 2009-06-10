package org.iana.rzm.web.common.technical_check;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;


public class NotAuthoritativeNameServerMessageBuilderTest {



     @Test
     public void testBuildMessage()throws Exception{
        String xml = "<exception name=\"NotAuthoritativeNameServerException\">\n" +
                "       <host>f.nic.de</host>\n" +
                "   </exception>";

        Document document = new SAXBuilder().build(new StringReader(xml));
        Element element = document.getRootElement();
        NotAuthoritativeNameServerMessageBuilder builder = new NotAuthoritativeNameServerMessageBuilder();
        String errorMessage = builder.build(element, "de");
        Assert.assertEquals(errorMessage, "The folowing host: [f.nic.de] is not Authoritative for the domain: de" );
    }
}
