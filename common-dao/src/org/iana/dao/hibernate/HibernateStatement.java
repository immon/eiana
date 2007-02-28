package org.iana.dao.hibernate;

import org.iana.dao.ConfiguredStatement;
import org.iana.dao.Statement;
import org.iana.dao.statconfig.StatementConfigEntry;

/**
 * A hibernate-based implementation of <code>Statement</code>.
 *
 * @author Patrycja Wegrzynowicz
 */
abstract class HibernateStatement<T> extends ConfiguredStatement {

    protected HibernateDAO<T> dao;

    protected HibernateStatement(StatementConfigEntry configEntry, HibernateDAO<T> dao) {
        super(configEntry);
        this.dao = dao;
    }
}
