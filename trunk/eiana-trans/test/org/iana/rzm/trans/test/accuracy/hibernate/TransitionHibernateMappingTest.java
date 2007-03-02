package org.iana.rzm.trans.test.accuracy.hibernate;

import org.iana.rzm.trans.StateTransition;
import org.iana.rzm.trans.test.common.hibernate.HibernateMappingUnitTest;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class TransitionHibernateMappingTest extends HibernateMappingUnitTest<StateTransition> {
    protected StateTransition create() throws Exception {
        return new StateTransition("created transition");
    }

    protected StateTransition change(StateTransition o) throws Exception {
        o.setName("changed transition");
        return o;
    }

    protected Serializable getId(StateTransition o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-trans"})
    public void testTransition() throws Exception {
        super.test();
    }
}
