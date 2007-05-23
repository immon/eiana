package pl.nask.logs.dao;

import pl.nask.dao.hibernate.HibernateDAO;
import pl.nask.logs.LogEntry;

/**
 * @author Patrycja Wegrzynowicz
 */
public class HibernateLogDAO extends HibernateDAO<LogEntry> implements LogDAO {

    public HibernateLogDAO() {
        super(LogEntry.class);
    }
}
