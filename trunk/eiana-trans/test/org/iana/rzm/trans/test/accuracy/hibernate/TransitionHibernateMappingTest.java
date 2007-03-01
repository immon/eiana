package org.iana.rzm.trans.test.accuracy.hibernate;

import org.iana.rzm.trans.Transition;
import org.iana.rzm.trans.test.common.hibernate.HibernateMappingUnitTest;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class TransitionHibernateMappingTest extends HibernateMappingUnitTest<Transition> {
    protected Transition create() throws Exception {
        return new Transition("created transition");
    }

    protected Transition change(Transition o) throws Exception {
        o.setName("changed transition");
        return o;
    }

    protected Serializable getId(Transition o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-trans"})
    public void testTransition() throws Exception {
        super.test();
    }
}
