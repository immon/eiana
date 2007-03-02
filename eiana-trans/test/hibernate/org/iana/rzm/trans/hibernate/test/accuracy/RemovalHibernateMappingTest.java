package org.iana.rzm.trans.hibernate.test.accuracy;

import org.iana.rzm.trans.change.Removal;
import org.iana.rzm.trans.change.ObjectValue;
import org.iana.rzm.trans.hibernate.test.common.HibernateMappingUnitTest;
import org.iana.rzm.trans.hibernate.test.common.HibernateMappingTestUtil;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Jakub Laszkiewicz
 */
public class RemovalHibernateMappingTest extends HibernateMappingUnitTest<Removal> {
    protected Removal create() throws Exception {
        return new Removal("created field name", "primitive value");
    }

    protected Removal change(Removal o) throws Exception {
        List<Removal> changes = new ArrayList<Removal>();
        changes.add(new Removal("another created field name", "another primitive value"));
        o.setValue(HibernateMappingTestUtil.setupObjectValue(new ObjectValue<Removal>(changes), "changed"));
        return o;
    }

    protected Serializable getId(Removal o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-trans"})
    public void testRemoval() throws Exception {
        super.test();
    }
}
