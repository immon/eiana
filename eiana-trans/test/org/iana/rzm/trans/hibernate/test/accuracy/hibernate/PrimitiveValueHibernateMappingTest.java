package org.iana.rzm.trans.hibernate.test.accuracy.hibernate;

import org.iana.rzm.trans.change.PrimitiveValue;
import org.iana.rzm.trans.hibernate.test.common.hibernate.HibernateMappingUnitTest;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class PrimitiveValueHibernateMappingTest extends HibernateMappingUnitTest<PrimitiveValue> {
    protected PrimitiveValue create() throws Exception {
        return new PrimitiveValue("created value");
    }

    protected PrimitiveValue change(PrimitiveValue o) throws Exception {
        o.setValue("changed value");
        return o;
    }

    protected Serializable getId(PrimitiveValue o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-trans"})
    public void testPrimitiveValue() throws Exception {
        super.test();
    }
}
