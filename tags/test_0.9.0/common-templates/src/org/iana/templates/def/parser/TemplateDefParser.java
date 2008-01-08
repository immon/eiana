package org.iana.templates.def.parser;

import org.iana.templates.def.SectionDef;

import java.util.Map;

/**
 * @author Jakub Laszkiewicz
 */
public interface TemplateDefParser {
    public Map<String, SectionDef> toTemplateDef(String str) throws TemplateDefParseException;
}
