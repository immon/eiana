package org.iana.rzm.web.common.technical_check;

import org.apache.log4j.Logger;
import org.iana.rzm.web.common.utils.XmlUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;


public class DNSTechnicalCheckErrorsXmlParser implements DNSTechnicalCheckErrorMessageBuilder {

    private static final String EXCEPTION_ELEMENT = "exception";
    private Map<String, DNSTechnicalCheckErrorMessageBuilder> messageBuilders = null;
    private static final String DOMAIN_ELEMENT = "domain";
    private static final String NAME_ATTRIBUTE = "name";

    public DNSTechnicalCheckErrorsXmlParser(Map<String, DNSTechnicalCheckErrorMessageBuilder> messageBuilders) {
        this.messageBuilders = messageBuilders;
    }

    @SuppressWarnings("unchecked")
    public List<String>getTechnicalCheckErrors(String xml){

        Set<String> result = new HashSet<String>();

        try {
            Document doc = new SAXBuilder().build(new StringReader(xml));
            Element root = doc.getRootElement();
            Element domain = root.getChild(DOMAIN_ELEMENT);

            if(domain == null){
                return new ArrayList<String>();
            }

            List<Element> exceptions = root.getChildren(EXCEPTION_ELEMENT);
            for (Element exception : exceptions) {
                String message = build(exception, domain.getAttributeValue(NAME_ATTRIBUTE));
                if(message != null){
                    result.add(message);
                }
            }
        } catch (JDOMException e) {
            Logger.getLogger(getClass().getName()).error("XML Parsing error", e);
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).error("IOExcaption error", e);
        }

        return new ArrayList<String>(result);
    }

    public String build(Element elm, String domain) {
        Element element = XmlUtils.findFirstElementByName(elm, EXCEPTION_ELEMENT);
        if (element == null) {
            return null;
        }

        DNSTechnicalCheckErrorMessageBuilder builder = messageBuilders.get(element.getAttributeValue(NAME_ATTRIBUTE));
        if(builder == null){
            return null;
        }
        
        return builder.build(elm, domain);
    }
}
