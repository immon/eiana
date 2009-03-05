package org.iana.notifications.template.def;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface TemplateDefConfig {

    TemplateDef getTemplateDef(String name);

    void create(TemplateDef def);

    void update(TemplateDef def);

    void delete(String name);

    List<TemplateDef> getTemplateDefs();

}
