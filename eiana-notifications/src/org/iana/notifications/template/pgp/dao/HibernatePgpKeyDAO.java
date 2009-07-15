package org.iana.notifications.template.pgp.dao;

import org.iana.dao.DataAccessObject;
import org.iana.dao.hibernate.HibernateDAO;
import org.iana.notifications.template.pgp.PgpKey;
import org.iana.notifications.template.pgp.PgpKeyConfig;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class HibernatePgpKeyDAO extends HibernateDAO<PgpKey> implements DataAccessObject<PgpKey>, PgpKeyConfig {

    public HibernatePgpKeyDAO() {
        super(PgpKey.class);
    }

    public PgpKey getPgpKey(String name) {
        return (PgpKey) getHibernateTemplate().get(PgpKey.class, name);
    }

    public void delete(String name) {
        PgpKey def = getPgpKey(name);
        if (def != null) delete(def);
    }

    public List<PgpKey> getPgpKeys() {
        return find();
    }
}