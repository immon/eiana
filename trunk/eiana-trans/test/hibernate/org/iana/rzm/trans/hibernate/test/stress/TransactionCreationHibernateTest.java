package org.iana.rzm.trans.hibernate.test.stress;

import org.iana.dns.validator.InvalidDomainNameException;
import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.NameServerAlreadyExistsException;
import org.iana.rzm.trans.hibernate.test.common.HibernateMappingTestUtil;
import org.iana.rzm.trans.hibernate.test.common.HibernateOperationStressTest;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"hibernate", "eiana-trans", "stress", "eiana-trans-stress-create"})
public class TransactionCreationHibernateTest extends HibernateOperationStressTest {
    private Domain getDomain(String name) throws InvalidDomainNameException, MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException {
        Domain domain = HibernateMappingTestUtil.setupDomain(new Domain(name));
        session.save(domain);
        return domain;
    }

    protected void operation(Object o) throws Exception {
/*
        List<TransactionAction> actions = new ArrayList<TransactionAction>();
        actions.add(HibernateMappingTestUtil.createAction(TransactionAction.Name.CREATE_NEW_TLD));
        actions.add(HibernateMappingTestUtil.createAction(TransactionAction.Name.MODIFY_NAMESERVER));
*/
        /*
        session.save(HibernateMappingTestUtil.setupTransaction(new Transaction(),
                "" + o, actions, getDomain("created-" + o),
                HibernateMappingTestUtil.createState(TransactionState.Name.ADMIN_CLOSE)));
        */
    }

    protected List getList() throws Exception {
        List result = new ArrayList();
        for (int i = 0; i < 100; i++) result.add("transaction-" + i);
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
