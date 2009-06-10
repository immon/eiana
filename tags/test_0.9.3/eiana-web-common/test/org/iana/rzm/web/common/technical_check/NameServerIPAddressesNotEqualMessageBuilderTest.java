package org.iana.rzm.web.common.technical_check;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: May 26, 2009
 * Time: 11:18:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class NameServerIPAddressesNotEqualMessageBuilderTest {

    @Test
    public void testBuildMessage()throws Exception{

        String xml =
                "<exception name=\"NameServerIPAddressesNotEqualException\">\n" +
                    "<host>c.de.net</host>\n" +
                    "<received>\n" +
                        "<value name=\"ip\">81.91.164.5</value>\n" +
                        "<value name=\"ip\">2001:608:6:6:0:0:0:10</value>\n" +
                    "</received>\n" +
                    "<expected>\n" +
                        "<value name=\"ip\">208.48.81.43</value>\n" +
                    "</expected>\n" +
                "</exception>";

        Document document = new SAXBuilder().build(new StringReader(xml));
        Element element = document.getRootElement();
        NameServerIPAddressesNotEqualMessageBuilder builder = new NameServerIPAddressesNotEqualMessageBuilder();
        Assert.assertEquals(builder.build(element, "root"),"The A and AAAA records [81.91.164.5, 2001:608:6:6:0:0:0:10]" +
                " returned from the authoritative name server [c.de.net] are not the same as the supplied glue" +
                " records [208.48.81.43].");
    }
}
