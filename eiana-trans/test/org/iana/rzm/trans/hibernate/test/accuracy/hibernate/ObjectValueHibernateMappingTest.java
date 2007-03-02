package org.iana.rzm.trans.hibernate.test.accuracy.hibernate;

import org.iana.rzm.trans.change.ObjectValue;
import org.iana.rzm.trans.change.Change;
import org.iana.rzm.trans.hibernate.test.common.hibernate.HibernateMappingTestUtil;
import org.iana.rzm.trans.hibernate.test.common.hibernate.HibernateMappingUnitTest;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class ObjectValueHibernateMappingTest extends HibernateMappingUnitTest<ObjectValue> {
    protected ObjectValue create() throws Exception {
        List<Change> changes = new ArrayList<Change>();
        changes.add(HibernateMappingTestUtil.createPrimitiveModification("created1"));
        changes.add(HibernateMappingTestUtil.createPrimitiveModification("created2"));
        return HibernateMappingTestUtil.setupObjectValue(new ObjectValue(changes), "created");
    }

    protected ObjectValue change(ObjectValue o) throws Exception {
        HibernateMappingTestUtil.setupObjectValue(o, "changed");
        o.getChanges().remove(o.getChanges().iterator().next());
        o.getChanges().add(HibernateMappingTestUtil.createPrimitiveModification("created3"));
        return o;
    }

    protected Serializable getId(ObjectValue o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-trans"})
    public void testObjectValue() throws Exception {
        super.test();
    }
}
