package org.iana.rzm.trans.hibernate;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public abstract class HibernateOperationStressTest extends org.iana.rzm.domain.hibernate.HibernateTest {
    abstract protected void operation(Object o) throws Exception;
    abstract protected List getList() throws Exception;

    protected void oneTransaction() throws Exception {
        begin();
        for (Object o : getList()) operation(o);
        close();
    }

    protected void manyTransactions() throws Exception {
        begin();
        for (Object o : getList()) {
            beginTx();
            operation(o);
            closeTx();
        }
        close();
    }

    protected void manySessions() throws Exception {
        for (Object o : getList()) {
            begin();
            operation(o);
            close();
        }
    }
}
