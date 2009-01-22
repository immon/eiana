package org.iana.templates.def.parser;

import org.apache.log4j.*;
import org.iana.templates.def.*;

import java.io.*;
import java.util.*;

/**
 * @author Jakub Laszkiewicz
 */
public class TemplatesManager {

    private Map<String, SectionDef> templates;

    public TemplatesManager(String configFileName) throws TemplatesManagerException {
        try {
            XmlTemplateDefParser parser = new XmlTemplateDefParser();
            InputStream in = getClass().getClassLoader().getResourceAsStream(configFileName);
            if (in == null) {
                FileNotFoundException e = new FileNotFoundException(configFileName);
                Logger.getLogger(getClass().getName()).error("Template not found " + configFileName, e);
                throw e;
            }
            templates = parser.toTemplateDef(in);
        } catch (TemplateDefParseException e) {
            Logger.getLogger(getClass().getName()).error("template manager initialization failed",e);
            throw new TemplatesManagerException("template manager initialization failed", e);
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).error("template manager initialization failed",e);
            throw new TemplatesManagerException("template manager initialization failed", e);

        }
    }

    public SectionDef getTemplate(String name) {
        return templates.get(name);
    }

    public Map<String, SectionDef> getTemplates() {
        return templates;
    }
}
