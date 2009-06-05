package org.iana.rzm.web.common.technical_check;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;

public class NameServerCoherencyMessageBuilderTest {

    @Test
    public void testBuildMessage()throws Exception{

        String xml = "<exception name=\"NameServerCoherencyException\">\n" +
                "       <host>f.nic.de</host>\n" +
                "       <received>\n" +
                "           <value name=\"ns\">b.dns.net</value>\n" +
                "           <value name=\"ns\">a.dns.net</value>\n" +
                "       </received>\n" +
                "       <expected>\n" +
                "           <value name=\"ns\">c.dns.net</value>\n" +
                "           <value name=\"ns\">a.dns.net</value>\n" +
                "       </expected>\n" +
                "   </exception>";

        Document document = new SAXBuilder().build(new StringReader(xml));
        Element element = document.getRootElement();
        NameServerCoherencyMessageBuilder builder = new NameServerCoherencyMessageBuilder();
        String message = builder.build(element, "domain");
        Assert.assertEquals(message, "The NS RR-set returned by the authoritative name servers [b.dns.net, a.dns.net] " +
                "are not the same as the supplied ns records [c.dns.net, a.dns.net] for Name server: f.nic.de."  );

    }
}
