package org.iana.templates.def.parser;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.iana.templates.def.*;

import java.io.*;
import java.util.*;

/**
 * This class is a simple XML-based parser of template definition. The syntax of the template definition is as follows:
 * <code>
 * <template>
 * <section name="sectionName" label="sectionLabel" required="true|false">
 * <field name="fieldName" label="fieldLabel" required="true|false"/>
 * <list>...</list>
 * ...
 * </section>
 * </template>
 * </code>
 *
 * @author Jakub Laszkiewicz
 */
public class XmlTemplateDefParser implements TemplateDefParser {
    static final String TEMPLATE = "template";
    static final String SECTION = "section";
    static final String FIELD = "field";
    static final String LIST = "list";
    static final String NAME = "name";
    static final String LABEL = "label";
    static final String REQUIRED = "required";

    Logger logger = Logger.getLogger(getClass());

    public Map<String, SectionDef> toTemplateDef(String str) throws TemplateDefParseException {
        try {
            Document root = DocumentHelper.parseText(str);
            return toTemplateDef(root.getRootElement());
        } catch (DocumentException e) {
            throw new TemplateDefParseException(e);
        }
    }

    public Map<String, SectionDef> toTemplateDef(InputStream in) throws TemplateDefParseException, IOException {
        return toTemplateDef(new InputStreamReader(in));
    }

    public Map<String, SectionDef> toTemplateDef(Reader in) throws TemplateDefParseException, IOException {
        BufferedReader bin = new BufferedReader(in);
        StringBuffer buf = new StringBuffer();
        String line = bin.readLine();
        while (line != null) {
            buf.append(line).append('\n');
            line = bin.readLine();
        }
        return toTemplateDef(buf.toString());
    }

    public Map<String, SectionDef> toTemplateDef(Element element) throws TemplateDefParseException {
        Map<String, SectionDef> ret = new HashMap<String, SectionDef>();
        for (Iterator i = element.elementIterator(); i.hasNext();) {
            Element child = (Element) i.next();
            if (TEMPLATE.equals(child.getName())) {
                // new template
                SectionDef sectionDef = toSection(child);
                ret.put(sectionDef.getName(), sectionDef);
            } else
                logger.warn("skipping " + child + " element");
        }
        return ret;
    }

    ListDef toList(Element element) throws TemplateDefParseException {
        ElementDef elementDef = null;
        for (Iterator i = element.elementIterator(); i.hasNext();) {
            Element child = (Element) i.next();
            if (SECTION.equals(child.getName())) {
                elementDef = toSection(child);
                break;
            } else if (FIELD.equals(child.getName())) {
                elementDef = toField(child);
                break;
            } else {
                logger.warn("skipping " + child + " element");
            }
        }
        if (elementDef == null)
            throw new TemplateDefParseException("element type of list cannot be null");
        return new ListDef(elementDef);
    }

    SectionDef toSection(Element element) throws TemplateDefParseException {
        String name = element.attributeValue(NAME);
        if (name == null) throw new TemplateDefParseException(NAME + " of section cannot be null");
        String label = element.attributeValue(LABEL);
        if (label == null) label = name;

        return new SectionDef(name, label, toElements(element), toBool(element, REQUIRED, true));
    }

    FieldDef toField(Element element) throws TemplateDefParseException {
        String name = element.attributeValue(NAME);
        if (name == null) throw new TemplateDefParseException(NAME + " of field cannot be null");
        String label = element.attributeValue(LABEL);
        if (label == null) label = name;

        FieldDef fd = new FieldDef(name, label);
        fd.setRequired(toBool(element, REQUIRED, true));
        return fd;
    }

    List<TemplateObjectDef> toElements(Element element) throws TemplateDefParseException {
        List<TemplateObjectDef> ret = new ArrayList<TemplateObjectDef>();
        for (Iterator i = element.elementIterator(); i.hasNext();) {
            Element child = (Element) i.next();
            if (SECTION.equals(child.getName()))
                ret.add(toSection(child));
            else if (FIELD.equals(child.getName()))
                ret.add(toField(child));
            else if (LIST.equals(child.getName()))
                ret.add(toList(child));
            else
                logger.warn("skipping " + child + " element");
        }
        return ret;
    }

    int toInt(Element element, String attrName, Map<String, Integer> mapping, int defaultValue) {
        String value = element.attributeValue(attrName);
        if (!mapping.containsKey(value)) {
            logger.info("using default value for " + attrName + " " + defaultValue);
            return defaultValue;
        }
        return mapping.get(value);
    }

    static Map<String, Boolean> boolMapping = new HashMap<String, Boolean>();

    static {
        boolMapping.put("true", Boolean.TRUE);
        boolMapping.put("1", Boolean.TRUE);
        boolMapping.put("false", Boolean.FALSE);
        boolMapping.put("0", Boolean.FALSE);
    }

    boolean toBool(Element element, String attrName, boolean defaultValue) {
        String value = element.attributeValue(attrName);
        if (value != null) value = value.toLowerCase();
        if (!boolMapping.containsKey(value)) {
            logger.info("using default value for " + attrName + " " + defaultValue);
            return defaultValue;
        }
        return boolMapping.get(value);
    }
}
