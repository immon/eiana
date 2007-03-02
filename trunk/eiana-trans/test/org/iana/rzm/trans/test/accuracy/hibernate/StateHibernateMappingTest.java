package org.iana.rzm.trans.test.accuracy.hibernate;

import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.StateTransition;
import org.iana.rzm.trans.test.common.hibernate.HibernateMappingTestUtil;
import org.iana.rzm.trans.test.common.hibernate.HibernateMappingUnitTest;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Jakub Laszkiewicz
 */
public class StateHibernateMappingTest extends HibernateMappingUnitTest<TransactionState> {
    protected TransactionState create() throws Exception {
        Set<StateTransition> transitions = new HashSet<StateTransition>();
        transitions.add(new StateTransition("1st transition"));
        transitions.add(new StateTransition("2nd transition"));
        return HibernateMappingTestUtil.setupState(new TransactionState(), TransactionState.Name.ADMIN_CLOSE, transitions);
    }

    protected TransactionState change(TransactionState o) throws Exception {
        Set<StateTransition> transitions = o.getAvailableTransitions();
        transitions.remove(transitions.iterator().next());
        transitions.add(new StateTransition("3rd transition"));
        return HibernateMappingTestUtil.setupState(o, TransactionState.Name.COMPLETED, transitions);
    }

    protected Serializable getId(TransactionState o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-trans"})
    public void testState() throws Exception {
        super.test();
    }
}
