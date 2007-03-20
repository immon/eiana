package org.iana.rzm.trans.jbpm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jbpm.JbpmException;
import org.jbpm.db.*;
import org.jbpm.persistence.JbpmPersistenceException;
import org.jbpm.persistence.db.DbPersistenceService;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author Jakub Laszkiewicz
 */
public class SpringDbPersistenceService extends DbPersistenceService {

    private static final long serialVersionUID = 1L;

    public SpringDbPersistenceService(SpringDbPersistenceServiceFactory persistenceServiceFactory) {
        super(persistenceServiceFactory);
        this.persistenceServiceFactory = persistenceServiceFactory;
        this.isTransactionEnabled = persistenceServiceFactory.isTransactionEnabled();
    }

    SpringDbPersistenceServiceFactory persistenceServiceFactory = null;

    Connection connection = null;
    boolean mustConnectionBeClosed = false;

    Transaction transaction = null;
    boolean isTransactionEnabled = true;
    boolean isRollbackOnly = false;

    Session session;
    boolean mustSessionBeFlushed = false;
    boolean mustSessionBeClosed = false;

    GraphSession graphSession = null;
    TaskMgmtSession taskMgmtSession = null;
    SchedulerSession schedulerSession = null;
    MessagingSession messagingSession = null;
    ContextSession contextSession = null;
    LoggingSession loggingSession = null;

    public SessionFactory getSessionFactory() {
        return persistenceServiceFactory.getSessionFactory();
    }

    public Session getSession() {
        //if ((session == null || !session.isOpen())
        if ((session == null)
                && (getSessionFactory() != null)
                ) {
            Connection connection = getConnection(false);
            if (connection != null) {
                log.debug("creating hibernate session with connection " + connection);
                session = getSessionFactory().openSession(connection);
                mustSessionBeClosed = true;
                mustSessionBeFlushed = true;
                mustConnectionBeClosed = false;
            } else {
                log.debug("creating hibernate session using SessionFactoryUtils");
                session = SessionFactoryUtils.getSession(getSessionFactory(), true);
                mustSessionBeClosed = true;
                mustSessionBeFlushed = false;
                mustConnectionBeClosed = false;
            }

            isTransactionEnabled = !SessionFactoryUtils.isSessionTransactional(session, getSessionFactory(
            ));
            if (isTransactionEnabled) {
                log.debug("beginning hibernate transaction");
                transaction = session.beginTransaction();
            }
        }
        return session;
    }

    public Connection getConnection() {
        return getConnection(true);
    }

    Connection getConnection(boolean resolveSession) {
        if (connection == null) {
            if (persistenceServiceFactory.getDataSource() != null) {
                try {
                    log.debug("fetching jdbc connection from datasource");
                    connection = persistenceServiceFactory.getDataSource().getConnection();
                    mustConnectionBeClosed = true;
                } catch (Throwable t) {
                    throw new JbpmException("couldn't obtain connection from datasource", t);
                }
            } else {
                if (resolveSession) {
                    // initializes the session member
                    getSession();
                }
                //if (session != null && session.isOpen()) {
                if (session != null) {
                    log.debug("fetching connection from hibernate session. this transfers responsibility for closing the jdbc connection to the user!");
                    connection = session.connection();
                    mustConnectionBeClosed = false;
                }
            }
        }
        return connection;
    }

    public void close() {
        if ((session != null)
                && (transaction == null)
                && (isRollbackOnly)
                ) {
            throw new JbpmException("User provided session was combined with setRollbackOnly.  With user provided hibernate sessions, the user is responsible for managing transactions");
        }
        if (messagingSession != null) {
            messagingSession.closeOpenIterators();
        }
        if (schedulerSession != null) {
            schedulerSession.closeOpenIterators();
        }
        if ((isTransactionEnabled)
                && (transaction != null)
                ) {
            if (isRollbackOnly) {
                try {
                    log.debug("rolling back hibernate transaction");
                    mustSessionBeFlushed = false; // flushing updates that will be rolled back is not very clever :-)
                    transaction.rollback();
                } catch (Throwable t) {
                    throw new JbpmPersistenceException("couldn't rollback hibernate session", t);
                }
            } else {
                try {
                    log.debug("committing hibernate transaction");
                    mustSessionBeFlushed = false; // commit does a flush anyway
                    transaction.commit();
                } catch (Throwable t) {
                    try {
                        // if the commit fails, we must do a rollback
                        transaction.rollback();
                    } catch (Throwable t2) {
                        // if the rollback fails, we did what we could and you're in
                        // deep shit :-(
                        log.error("problem rolling back after failed commit", t2);
                    }
                    throw new JbpmPersistenceException("couldn't commit hibernate session", t);
                }
            }
        }

        if (mustSessionBeFlushed) {
            try {
                log.debug("flushing hibernate session");
                session.flush();
            } catch (Throwable t) {
                throw new JbpmPersistenceException("couldn't flush hibernate session", t);
            }
        }

        if (mustSessionBeClosed) {
            try {
                log.debug("closing hibernate session using SessionFactoryUtils");
                SessionFactoryUtils.releaseSession(session, getSessionFactory());
            } catch (Throwable t) {
                throw new JbpmPersistenceException("couldn't close hibernate session", t);
            }
        }

        if (mustConnectionBeClosed) {
            try {
                log.debug("closing jdbc connection");
                connection.close();
            } catch (Throwable t) {
                throw new JbpmPersistenceException("couldn't close jdbc connection", t);
            }
        }
    }

    public void assignId(Object object) {
        try {
            getSession().save(object);
        } catch (Throwable t) {
            throw new JbpmPersistenceException("couldn't assign id to " + object, t);
        }
    }

    // getters and setters //////////////////////////////////////////////////////

    public GraphSession getGraphSession() {
        if (graphSession == null) {
            Session session = getSession();
            if (session != null) {
                graphSession = new GraphSession(session);
            }
        }
        return graphSession;
    }

    public LoggingSession getLoggingSession() {
        if (loggingSession == null) {
            Session session = getSession();
            if (session != null) {
                loggingSession = new LoggingSession(session);
            }
        }
        return loggingSession;
    }

    public MessagingSession getMessagingSession() {
        if (messagingSession == null) {
            Session session = getSession();
            if (session != null) {
                messagingSession = new MessagingSession(session);
            }
        }
        return messagingSession;
    }

    public SchedulerSession getSchedulerSession() {
        if (schedulerSession == null) {
            Session session = getSession();
            if (session != null) {
                schedulerSession = new SchedulerSession(session);
            }
        }
        return schedulerSession;
    }

    public ContextSession getContextSession() {
        if (contextSession == null) {
            Session session = getSession();
            if (session != null) {
                contextSession = new ContextSession(session);
            }
        }
        return contextSession;
    }

    public TaskMgmtSession getTaskMgmtSession() {
        if (taskMgmtSession == null) {
            Session session = getSession();
            if (session != null) {
                taskMgmtSession = new TaskMgmtSession(session);
            }
        }
        return taskMgmtSession;
    }

    public DataSource getDataSource() {
        return persistenceServiceFactory.getDataSource();
    }

    public boolean isRollbackOnly() {
        return isRollbackOnly;
    }

    public void setRollbackOnly(boolean isRollbackOnly) {
        this.isRollbackOnly = isRollbackOnly;
    }

    public void setRollbackOnly() {
        isRollbackOnly = true;
    }

    public void setSession(Session session) {
        this.session = session;
        log.debug("injecting a session disables transaction");
        isTransactionEnabled = false;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setContextSession(ContextSession contextSession) {
        this.contextSession = contextSession;
    }

    public void setDataSource(DataSource dataSource) {
        this.persistenceServiceFactory.setDataSource(dataSource);
    }

    public void setGraphSession(GraphSession graphSession) {
        this.graphSession = graphSession;
    }

    public void setLoggingSession(LoggingSession loggingSession) {
        this.loggingSession = loggingSession;
    }

    public void setMessagingSession(MessagingSession messagingSession) {
        this.messagingSession = messagingSession;
    }

    public void setSchedulerSession(SchedulerSession schedulerSession) {
        this.schedulerSession = schedulerSession;
    }

    public void setTaskMgmtSession(TaskMgmtSession taskMgmtSession) {
        this.taskMgmtSession = taskMgmtSession;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.persistenceServiceFactory.setSessionFactory(sessionFactory);
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public boolean isTransactionEnabled() {
        return isTransactionEnabled;
    }

    public void setTransactionEnabled(boolean isTransactionEnabled) {
        this.isTransactionEnabled = isTransactionEnabled;
    }

    private static Log log = LogFactory.getLog(SpringDbPersistenceService.class);
}
