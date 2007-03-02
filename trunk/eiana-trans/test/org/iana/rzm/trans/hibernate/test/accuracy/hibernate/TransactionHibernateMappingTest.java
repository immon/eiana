package org.iana.rzm.trans.hibernate.test.accuracy.hibernate;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionAction;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.hibernate.test.common.hibernate.HibernateMappingUnitTest;
import org.iana.rzm.trans.hibernate.test.common.hibernate.HibernateMappingTestUtil;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.NameServerAlreadyExistsException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.net.MalformedURLException;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionHibernateMappingTest extends HibernateMappingUnitTest<Transaction> {
    private Domain getDomain(String name) throws InvalidNameException, MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException {
        Domain domain = HibernateMappingTestUtil.setupDomain(new Domain(name));
        session.save(domain);
        return domain;
    }

    protected Transaction create() throws Exception {
        List<TransactionAction> actions = new ArrayList<TransactionAction>();
        actions.add(HibernateMappingTestUtil.createAction(TransactionAction.Name.CREATE_NEW_TLD));
        actions.add(HibernateMappingTestUtil.createAction(TransactionAction.Name.MODIFY_NAMESERVER));
        return HibernateMappingTestUtil.setupTransaction(new Transaction(),
                "created", actions, getDomain("created1"),
                HibernateMappingTestUtil.createState(TransactionState.Name.ADMIN_CLOSE));
    }

    protected Transaction change(Transaction o) throws Exception {
        List<TransactionAction> actions = o.getActions();
        actions.remove(actions.iterator().next());
        actions.add(HibernateMappingTestUtil.createAction(TransactionAction.Name.MODIFY_WHOIS_SERVER));
        return HibernateMappingTestUtil.setupTransaction(o,
                "changed", actions, getDomain("changed2"),
                HibernateMappingTestUtil.createState(TransactionState.Name.COMPLETED));
    }

    protected Serializable getId(Transaction o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-trans"})
    public void testTransaction() throws Exception {
        super.test();
    }
}
