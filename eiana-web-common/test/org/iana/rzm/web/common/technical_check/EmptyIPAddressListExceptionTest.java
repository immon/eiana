package org.iana.rzm.web.common.technical_check;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;


public class EmptyIPAddressListExceptionTest {


    @Test
    public void testBuildMessage_oneHost()throws Exception{

        String xml = "<exception name=\"EmptyIPAddressListException\">\n" +
                "       <other>\n" +
                "           <value name=\"host\">g.nic.net</value>\n" +
                "       </other>\n" +
                "   </exception>";
        Document document = new SAXBuilder().build(new StringReader(xml));
        Element element = document.getRootElement();
        EmptyIPAddressListMessageBuilder builder = new EmptyIPAddressListMessageBuilder();
        String message = builder.build(element, "domain");
        Assert.assertEquals(message, "The folowing host does not seem to have have an ip address: [g.nic.net]" );
    }

}
