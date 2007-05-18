package org.iana.templates.def.parser;

import org.iana.templates.def.SectionDef;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Jakub Laszkiewicz
 */
public class TemplatesManager {

    private Map<String, SectionDef> templates;

    public TemplatesManager(String configFileName) throws TemplatesManagerException {
        try {
            XmlTemplateDefParser parser = new XmlTemplateDefParser();
            InputStream in = getClass().getClassLoader().getResourceAsStream(configFileName);
            if (in == null) throw new FileNotFoundException(configFileName);
            templates = parser.toTemplateDef(in);
        } catch (TemplateDefParseException e) {
            throw new TemplatesManagerException("template manager initialization failed", e);
        } catch (IOException e) {
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
