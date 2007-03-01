package org.iana.rzm.trans.test.accuracy.hibernate;

import org.iana.rzm.trans.State;
import org.iana.rzm.trans.Transition;
import org.iana.rzm.trans.test.common.hibernate.HibernateMappingTestUtil;
import org.iana.rzm.trans.test.common.hibernate.HibernateMappingUnitTest;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Jakub Laszkiewicz
 */
public class StateHibernateMappingTest extends HibernateMappingUnitTest<State> {
    protected State create() throws Exception {
        Set<Transition> transitions = new HashSet<Transition>();
        transitions.add(new Transition("1st transition"));
        transitions.add(new Transition("2nd transition"));
        return HibernateMappingTestUtil.setupState(new State(), State.Name.ADMIN_CLOSE, transitions);
    }

    protected State change(State o) throws Exception {
        Set<Transition> transitions = o.getAvailableTransitions();
        transitions.remove(transitions.iterator().next());
        transitions.add(new Transition("3rd transition"));
        return HibernateMappingTestUtil.setupState(o, State.Name.COMPLETED, transitions);
    }

    protected Serializable getId(State o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-trans"})
    public void testState() throws Exception {
        super.test();
    }
}
