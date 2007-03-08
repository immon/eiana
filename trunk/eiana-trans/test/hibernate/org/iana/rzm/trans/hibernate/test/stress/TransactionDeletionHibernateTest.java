package org.iana.rzm.trans.hibernate.test.stress;

import org.iana.rzm.trans.hibernate.test.common.HibernateOperationStressTest;
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

    @Test(groups = {"hibernate", "eiana-trans","stress"})
    public void oneTransaction() throws Exception {
        super.oneTransaction();
    }
}
