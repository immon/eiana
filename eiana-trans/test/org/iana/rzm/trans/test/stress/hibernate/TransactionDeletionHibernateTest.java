package org.iana.rzm.trans.test.stress.hibernate;

import org.iana.rzm.trans.test.common.hibernate.HibernateOperationStressTest;
import org.iana.rzm.trans.Transaction;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionDeletionHibernateTest extends HibernateOperationStressTest {
    protected void operation(Object o) throws Exception {
        session.delete(o);
    }

    protected List getList() throws Exception {
        return session.createCriteria(Transaction.class).list();
    }

    @Test
    public void oneTransaction() throws Exception {
        super.oneTransaction();
    }
}
