/**
 * @author Piotr Tkaczyk
 */
package org.iana.notifications.refactored.template.def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLTemplateDefConfig implements TemplateDefConfig {

    private Map<String, TemplateDef> templates = new HashMap<String, TemplateDef>();

    public TemplateDef getNotificationTemplate(String type) {
        return templates.get(type);
    }

    Map<String, TemplateDef> getTemplates() {
        return templates;
    }

    void setTemplates(Map<String, TemplateDef> templates) {
        this.templates = templates;
    }

    void addTemplate(String type, TemplateDef template) {
        if (template != null) addTemplate(type, template);
    }

    public List<TemplateDef> getTemplatesList() {
        if (templates == null) return null;
        List<TemplateDef> templateList = new ArrayList<TemplateDef>();
        //no elements - return empty list
        if (templates.size() == 0) return templateList;

        for (TemplateDef template : templates.values()) {
            templateList.add(template);
        }

        return templateList;
    }

    public void setTemplatesList(List<TemplateDef> templates) {
        if (templates == null) {
            this.templates = null;
        } else if (templates.size() == 0) {
            this.templates = new HashMap<String, TemplateDef>();
        } else {
            if (this.templates == null) this.templates = new HashMap<String, TemplateDef>();
            this.templates.clear();
            for (TemplateDef template : templates) {
                this.templates.put(template.getType(), template);
            }
        }
    }

    public void addTemplateList(TemplateDef template) {
        if (template != null) addTemplate(template.getType(), template);
    }

    public TemplateDef getTemplateDef(String name) {
        return templates.get(name);
    }
}
