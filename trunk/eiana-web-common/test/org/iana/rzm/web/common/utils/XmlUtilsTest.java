package org.iana.rzm.web.common.utils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;
import java.util.List;


public class XmlUtilsTest {

    @Test
    public void testFindFirstElementByName()throws Exception{
        String xml = "<exception name=\"MinimumNetworkDiversityException\">\n" +
                "       <received>\n" +
                "           <value name=\"as\">123</value>\n" +
                "       </received>\n" +
                "   </exception>";

        Document document = new SAXBuilder().build(new StringReader(xml));
        Element element = XmlUtils.findFirstElementByName(document.getRootElement(), "value");
        Assert.assertNotNull(element);
        Assert.assertNotNull(element.getAttribute("name"));
        Assert.assertEquals(element.getAttributeValue("name"), "as");
        Assert.assertEquals(element.getValue(), "123");

        Element element1 = XmlUtils.findFirstElementByName(document.getRootElement(), "exception");
        Assert.assertNotNull(element1);
        Assert.assertNotNull(element1.getAttribute("name"));
        Assert.assertEquals(element1.getAttributeValue("name"), "MinimumNetworkDiversityException");
    }


    @Test
    public void testFindElementByName()throws Exception{
        String xml = "<exception name=\"EmptyIPAddressListException\">\n" +
                "       <other>\n" +
                "           <value name=\"host\">g.nic.net</value>\n" +
                "           <value name=\"host\">h.nic.net</value>\n" +
                "       </other>\n" +
                "   </exception>";

        Document document = new SAXBuilder().build(new StringReader(xml));
        List<Element> elements = XmlUtils.findElementByName(document.getRootElement(), "value");
        Assert.assertEquals(elements.size(), 2);
        Assert.assertEquals(elements.get(0).getValue(), "g.nic.net");
        Assert.assertEquals(elements.get(1).getValue(), "h.nic.net");
    }

}
