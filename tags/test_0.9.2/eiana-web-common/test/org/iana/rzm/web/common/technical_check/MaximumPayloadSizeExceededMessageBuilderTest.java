package org.iana.rzm.web.common.technical_check;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;


public class MaximumPayloadSizeExceededMessageBuilderTest {

    @Test
    public void testBuildMessage()throws Exception{

        String xml = "<exception name=\"MaximumPayloadSizeExceededException\">\n" +
                "       <received>\n" +
                "           <value name=\"payload\">623</value>\n" +
                "       </received>\n" +
                "       <expected>\n" +
                "           <value name=\"payload\">512</value>\n" +
                "       </expected>\n" +
                "   </exception>";

        Document document = new SAXBuilder().build(new StringReader(xml));
        Element element = document.getRootElement();
        MaximumPayloadSizeMessageBuilder builder = new MaximumPayloadSizeMessageBuilder();
        String errorMessage = builder.build(element, "domain");
        Assert.assertEquals(errorMessage, "The Response estimated size was 623 and it is greater than 512 bytes");
    }
}
