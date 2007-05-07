package org.iana.rzm.log.dao;

import org.iana.dao.hibernate.HibernateDAO;
import org.iana.rzm.log.LogEntry;

/**
 * @author Patrycja Wegrzynowicz
 */
public class HibernateLogDAO extends HibernateDAO<LogEntry> implements LogDAO {

    public HibernateLogDAO() {
        super(LogEntry.class);
    }
}
