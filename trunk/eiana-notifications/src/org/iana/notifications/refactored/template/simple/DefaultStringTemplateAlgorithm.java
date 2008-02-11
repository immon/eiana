package org.iana.notifications.refactored.template.simple;

import pl.nask.util.ReflectionTool;

import java.io.IOException;
import java.util.Map;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DefaultStringTemplateAlgorithm implements StringTemplateAlgorithm {

    public String instantiateString(String template, Object data) throws StringTemplateException {
        try {
            if (data instanceof Map) {
                return TemplateFiller.fill(template, (Map<String, ?>)data);
            } else {
                return ReflectionTool.replaceMethodChains(template, data);
            }
        } catch (IOException e) {
            throw new StringTemplateException(e);
        }
    }

}
