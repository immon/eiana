package org.iana.templates;

import org.iana.templates.inst.SectionInst;

/**
 * @author Jakub Laszkiewicz
 */
public interface TemplatesService {
    public SectionInst parseTemplate(String str) throws TemplatesServiceException;
}
