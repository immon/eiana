package org.iana.rzm.trans.test.accuracy;

import org.iana.rzm.trans.change.Modification;
import org.iana.rzm.trans.change.ModifiedPrimitiveValue;
import org.iana.rzm.trans.test.common.HibernateMappingTestUtil;
import org.iana.rzm.trans.test.common.HibernateMappingUnitTest;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class ModificationHibernateMappingTest extends HibernateMappingUnitTest<Modification> {
    protected Modification create() throws Exception {
        return new Modification("created field name",
                HibernateMappingTestUtil.setupMPV(new ModifiedPrimitiveValue(), "created"));
    }

    protected Modification change(Modification o) throws Exception {
        o.setValue(HibernateMappingTestUtil.setupMPV(new ModifiedPrimitiveValue(), "changed"));
        return o;
    }

    protected Serializable getId(Modification o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-trans"})
    public void testModification() throws Exception {
        super.test();
    }
}
