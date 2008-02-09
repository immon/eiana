package org.iana.rzm.trans.hibernate.test.stress;

import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.NameServerAlreadyExistsException;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.hibernate.test.common.HibernateMappingTestUtil;
import org.iana.rzm.trans.hibernate.test.common.HibernateOperationStressTest;
import org.iana.dns.validator.InvalidDomainNameException;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"hibernate", "eiana-trans", "stress", "eiana-trans-stress-update"},
        dependsOnGroups = {"eiana-trans-stress-create"})
public class TransactionUpdateHibernateTest extends HibernateOperationStressTest {
    private Domain getDomain(String name) throws InvalidDomainNameException, MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException {
        Domain domain = HibernateMappingTestUtil.setupDomain(new Domain(name));
        session.save(domain);
        return domain;
    }

    protected void operation(Object o) throws Exception {
        /*
        Transaction trans = (Transaction) o;
        List<TransactionAction> actions = trans.getActions();
        actions.remove(actions.iterator().next());
        actions.add(HibernateMappingTestUtil.createAction(TransactionAction.Name.MODIFY_WHOIS_SERVER));
        session.save(HibernateMappingTestUtil.setupTransaction(trans,
                "changed", actions, getDomain("changed-" + trans.getCurrentDomain().getName()),
                HibernateMappingTestUtil.createState(TransactionState.Name.COMPLETED)));
        */
    }

    protected List getList() throws Exception {
        return session.createCriteria(Transaction.class).list();
    }

    @Test
    public void oneTransaction() throws Exception {
        super.oneTransaction();
    }
}
