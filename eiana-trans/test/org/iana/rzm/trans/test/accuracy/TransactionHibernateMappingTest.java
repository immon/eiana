package org.iana.rzm.trans.test.accuracy;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.Action;
import org.iana.rzm.trans.State;
import org.iana.rzm.trans.test.common.HibernateMappingUnitTest;
import org.iana.rzm.trans.test.common.HibernateMappingTestUtil;
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
        List<Action> actions = new ArrayList<Action>();
        actions.add(HibernateMappingTestUtil.createAction(Action.Name.CREATE_NEW_TLD));
        actions.add(HibernateMappingTestUtil.createAction(Action.Name.MODIFY_NAMESERVER));
        return HibernateMappingTestUtil.setupTransaction(new Transaction(),
                "created", actions, getDomain("created"),
                HibernateMappingTestUtil.createState(State.Name.ADMIN_CLOSE));
    }

    protected Transaction change(Transaction o) throws Exception {
        List<Action> actions = o.getActions();
        actions.remove(actions.iterator().next());
        actions.add(HibernateMappingTestUtil.createAction(Action.Name.MODIFY_WHOIS_SERVER));
        return HibernateMappingTestUtil.setupTransaction(o,
                "changed", actions, getDomain("changed"),
                HibernateMappingTestUtil.createState(State.Name.COMPLETED));
    }

    protected Serializable getId(Transaction o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-trans"})
    public void testTransaction() throws Exception {
        super.test();
    }
}
