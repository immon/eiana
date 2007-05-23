package org.iana.rzm.log.dao;

import org.iana.rzm.log.LogEntry;
import org.iana.dao.hibernate.HibernateDAO;


/**
 * @author Patrycja Wegrzynowicz
 */
public class HibernateLogDAO extends HibernateDAO<LogEntry> implements LogDAO {

    public HibernateLogDAO() {
        super(LogEntry.class);
    }
}
