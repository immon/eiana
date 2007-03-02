package org.iana.rzm.trans.test.stress.hibernate;

import org.iana.rzm.trans.test.common.hibernate.HibernateOperationStressTest;
import org.iana.rzm.trans.test.common.hibernate.HibernateMappingTestUtil;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionAction;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.NameServerAlreadyExistsException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.testng.annotations.Test;

import java.util.List;
import java.net.MalformedURLException;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionUpdateHibernateTest extends HibernateOperationStressTest {
    private Domain getDomain(String name) throws InvalidNameException, MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException {
        Domain domain = HibernateMappingTestUtil.setupDomain(new Domain(name));
        session.save(domain);
        return domain;
    }

    protected void operation(Object o) throws Exception {
        Transaction trans = (Transaction) o;
        List<TransactionAction> actions = trans.getActions();
        actions.remove(actions.iterator().next());
        actions.add(HibernateMappingTestUtil.createAction(TransactionAction.Name.MODIFY_WHOIS_SERVER));
        session.save(HibernateMappingTestUtil.setupTransaction(trans,
                "changed", actions, getDomain("changed-" + trans.getCurrentDomain().getName()),
                HibernateMappingTestUtil.createState(TransactionState.Name.COMPLETED)));
    }

    protected List getList() throws Exception {
        return session.createCriteria(Transaction.class).list();
    }

    @Test
    public void oneTransaction() throws Exception {
        super.oneTransaction();
    }
}
