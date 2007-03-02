package org.iana.rzm.trans.hibernate.test.accuracy;

import org.iana.rzm.trans.change.ModifiedPrimitiveValue;
import org.iana.rzm.trans.hibernate.test.common.HibernateMappingUnitTest;
import org.iana.rzm.trans.hibernate.test.common.HibernateMappingTestUtil;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class ModifiedPrimitiveValueHibernateMappingTest extends HibernateMappingUnitTest<ModifiedPrimitiveValue> {
    protected ModifiedPrimitiveValue create() throws Exception {
        return HibernateMappingTestUtil.setupMPV(new ModifiedPrimitiveValue(), "created");
    }

    protected ModifiedPrimitiveValue change(ModifiedPrimitiveValue o) throws Exception {
        return HibernateMappingTestUtil.setupMPV(o, "changed");
    }

    protected Serializable getId(ModifiedPrimitiveValue o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-trans"})
    public void testModifiedPrimitiveValue() throws Exception {
        super.test();
    }
}
