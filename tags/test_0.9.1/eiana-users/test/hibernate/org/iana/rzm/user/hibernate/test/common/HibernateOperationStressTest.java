package org.iana.rzm.user.hibernate.test.common;

import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
@Test
public abstract class HibernateOperationStressTest extends HibernateTest {
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
