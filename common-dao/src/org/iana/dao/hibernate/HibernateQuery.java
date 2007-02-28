package org.iana.dao.hibernate;

import org.iana.dao.ConfiguredStatement;
import org.iana.dao.Query;
import org.iana.dao.DataAccessException;
import org.iana.dao.statconfig.StatementConfigEntry;

import java.util.List;

/**
 * <p>
 * A hibernate-based implementation of <code>Update</code> interface.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 */
public class HibernateQuery<T> extends ConfiguredStatement implements Query<T> {

    public HibernateQuery(StatementConfigEntry configEntry) {
        super(configEntry);
    }

    public Query<T> clone() {
        return new HibernateQuery<T>(configEntry);
    }

    public T executeAndFetchFirst() throws DataAccessException {
        return null;
    }

    public List<T> execute() throws DataAccessException {
        return null;
    }
}
