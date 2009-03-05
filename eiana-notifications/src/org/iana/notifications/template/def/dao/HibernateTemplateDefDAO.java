package org.iana.notifications.template.def.dao;

import org.iana.dao.DataAccessObject;
import org.iana.dao.hibernate.HibernateDAO;
import org.iana.notifications.template.def.TemplateDef;
import org.iana.notifications.template.def.TemplateDefConfig;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class HibernateTemplateDefDAO extends HibernateDAO<TemplateDef> implements DataAccessObject<TemplateDef>, TemplateDefConfig {

    public HibernateTemplateDefDAO() {
        super(TemplateDef.class);
    }

    public TemplateDef getTemplateDef(String name) {
        return (TemplateDef) getHibernateTemplate().get(TemplateDef.class, name);
    }

    public void delete(String name) {
        TemplateDef def = getTemplateDef(name);
        if (def != null) delete(def);
    }

    public List<TemplateDef> getTemplateDefs() {
        return find();
    }

}
