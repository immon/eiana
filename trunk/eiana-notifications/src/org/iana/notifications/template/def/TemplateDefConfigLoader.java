package org.iana.notifications.template.def;

import org.iana.rzm.common.validators.CheckTool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class TemplateDefConfigLoader implements TemplateDefConfig {

    List<TemplateDefConfig> loaders = new ArrayList<TemplateDefConfig>();

    public TemplateDefConfigLoader(List<TemplateDefConfig> loaders) {
        CheckTool.checkCollectionEmpty(loaders, "Template definition loader list is apmty or null.");
        this.loaders = loaders;
    }

    public TemplateDef getTemplateDef(String name) {
        for (TemplateDefConfig loader : loaders) {
            TemplateDef td = loader.getTemplateDef(name);
            if (td != null)
                return td;
        }

        return null;
    }

    public void create(TemplateDef def) {
        for (TemplateDefConfig loader : loaders)
            loader.create(def);
    }

    public void update(TemplateDef def) {
        for (TemplateDefConfig loader : loaders)
            loader.update(def);
    }

    public void delete(String name) {
        for (TemplateDefConfig loader : loaders)
            loader.delete(name);
    }

    public List<TemplateDef> getTemplateDefs() {
        List<TemplateDef> templateDefs = new ArrayList<TemplateDef>();
        for (TemplateDefConfig loader : loaders)
            templateDefs.addAll(loader.getTemplateDefs());

        return templateDefs;
    }
}
