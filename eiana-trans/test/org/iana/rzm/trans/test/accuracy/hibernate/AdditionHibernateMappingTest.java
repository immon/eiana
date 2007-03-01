package org.iana.rzm.trans.test.accuracy.hibernate;

import org.iana.rzm.trans.change.Addition;
import org.iana.rzm.trans.test.common.hibernate.HibernateMappingUnitTest;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class AdditionHibernateMappingTest extends HibernateMappingUnitTest<Addition> {
    protected Addition create() throws Exception {
        return new Addition("created field name", "primitive value");
    }

    protected Addition change(Addition o) throws Exception {
        return o;
    }

    protected Serializable getId(Addition o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-trans"})
    public void testAddition() throws Exception {
        super.test();
    }
}
