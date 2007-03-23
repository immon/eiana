package org.iana.rzm.trans.hibernate.test.accuracy;

import org.iana.rzm.trans.change.SimpleChange;
import org.iana.rzm.trans.hibernate.test.common.HibernateMappingUnitTest;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SimpleChangeHibernateMappingTest extends HibernateMappingUnitTest<SimpleChange> {

    protected SimpleChange create() throws Exception {
        return new SimpleChange("abc", "xyz");
    }

    protected SimpleChange change(SimpleChange o) throws Exception {
        return o;
    }

    protected Serializable getId(SimpleChange o) {
        return o.getObjId();
    }

    @Test
    public void testSimpleChange() throws Exception {
        super.test();
    }
}
