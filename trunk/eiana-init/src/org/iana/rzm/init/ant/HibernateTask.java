package org.iana.rzm.init.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

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
        SessionFactory sf = null;
        try {
            Configuration cfg;
            if (annotationConfiguration == null) {
                cfg = new Configuration();
                if (configurationFile == null)
                    cfg.configure();
                else
                    cfg.configure(configurationFile);
            } else {
                cfg = new AnnotationConfiguration();
                cfg.configure(annotationConfiguration);
            }
            sf = cfg.buildSessionFactory();

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
        } finally {
            if (sf != null) sf.close();
        }
    }

    public abstract void doExecute(Session session) throws Exception;
}
