package org.iana.rzm.web.common.technical_check;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;


public class MinimumNetworkDiversityMessageBuilderTest {

    @Test
    public void testBuildMessage()throws Exception{
        String xml = "<exception name=\"MinimumNetworkDiversityException\">\n" +
                "       <received>\n" +
                "           <value name=\"as\">123</value>\n" +
                "       </received>\n" +
                "   </exception>";

        Document document = new SAXBuilder().build(new StringReader(xml));
        Element element = document.getRootElement();
        MinimumNetworkDiversityMessageBuilder builder = new MinimumNetworkDiversityMessageBuilder();
        String errorMessage = builder.build(element, "domain");
        Assert.assertEquals(errorMessage, "All name servers have the same AS number 123");
    }
}
