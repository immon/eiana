package org.iana.rzm.mail.parser;

import org.iana.templates.inst.SectionInst;

/**
 * @author Jakub Laszkiewicz
 */
public class TemplateMailData extends AbstractMailData {
    private SectionInst template;

    public TemplateMailData() {
    }

    public TemplateMailData(String originalSubject, String originalBody, SectionInst template) {
        super(originalSubject, originalBody);
        this.template = template;
    }

    public SectionInst getTemplate() {
        return template;
    }

    public void setTemplate(SectionInst template) {
        this.template = template;
    }
}
