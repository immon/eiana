package org.iana.templates.inst.parser;

import org.iana.templates.inst.SectionInst;

/**
 * @author Jakub Laszkiewicz
 */
public interface TemplateParser {
    public SectionInst toTemplate(String str) throws TemplateParseException;
}
