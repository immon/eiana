package org.iana.rzm.trans.hibernate.test.stress.hibernate;

import org.iana.rzm.trans.hibernate.test.common.hibernate.HibernateOperationStressTest;
import org.iana.rzm.trans.hibernate.test.common.hibernate.HibernateMappingTestUtil;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionAction;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.NameServerAlreadyExistsException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.testng.annotations.Test;

import java.util.List;
import java.util.ArrayList;
import java.net.MalformedURLException;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionCreationHibernateTest extends HibernateOperationStressTest {
    private Domain getDomain(String name) throws InvalidNameException, MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException {
        Domain domain = HibernateMappingTestUtil.setupDomain(new Domain(name));
        session.save(domain);
        return domain;
    }

    protected void operation(Object o) throws Exception {
        List<TransactionAction> actions = new ArrayList<TransactionAction>();
        actions.add(HibernateMappingTestUtil.createAction(TransactionAction.Name.CREATE_NEW_TLD));
        actions.add(HibernateMappingTestUtil.createAction(TransactionAction.Name.MODIFY_NAMESERVER));
        session.save(HibernateMappingTestUtil.setupTransaction(new Transaction(),
                "" + o, actions, getDomain("created-" + o),
                HibernateMappingTestUtil.createState(TransactionState.Name.ADMIN_CLOSE)));
    }

    protected List getList() throws Exception {
        List result = new ArrayList();
        for (int i = 0; i < 1000; i++) result.add("transaction-" + i);
        return result;
    }

    @Test
    public void oneTransaction() throws Exception {
        super.oneTransaction();
    }

    @Test
    public void manyTransactions() throws Exception {
        super.manyTransactions();
    }

    @Test
    public void manySessions() throws Exception {
        super.manySessions();
    }
}
