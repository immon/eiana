package org.iana.rzm.init.ant;

import org.apache.tools.ant.*;
import org.hibernate.*;

/**
 * @author Jakub Laszkiewicz
 */
public abstract class HibernateTask extends Task {
    private String configurationFile = null;
    private String annotationConfiguration = null;

    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    public void setAnnotationConfiguration(String annotationConfiguration) {
        this.annotationConfiguration = annotationConfiguration;
    }

    public final void execute() throws BuildException {
        SessionFactory sf = (SessionFactory) SpringInitContext.getContext().getBean("sessionFactory");
        Session session = sf.openSession();
        try {
            Transaction transaction = session.beginTransaction();
            try {
                doExecute(session);
                transaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
                transaction.rollback();
            }
        } finally {
            session.close();
        }
    }

    public abstract void doExecute(Session session) throws Exception;
}
