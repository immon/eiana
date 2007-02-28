package org.iana.dao.hibernate;

import org.iana.dao.Update;
import org.iana.dao.ConfiguredStatement;
import org.iana.dao.DataAccessException;
import org.iana.dao.statconfig.StatementConfigEntry;
import org.hibernate.Query;

/**
 * <p>
 * A hibernate-based implementation of <code>Update</code> interface.
 * </p> 
 *
 * @author Patrycja Wegrzynowicz
 */
public class HibernateUpdate<T> extends ConfiguredStatement implements Update<T> {

    public HibernateUpdate(StatementConfigEntry configEntry) {
        super(configEntry);
    }

    public void execute() throws DataAccessException {
            
    }

    public Update<T> clone() {
        return new HibernateUpdate<T>(configEntry);
    }
}
