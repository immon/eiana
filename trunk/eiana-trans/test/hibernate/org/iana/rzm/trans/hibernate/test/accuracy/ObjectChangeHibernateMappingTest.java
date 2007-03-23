package org.iana.rzm.trans.hibernate.test.accuracy;

import org.iana.rzm.trans.change.ObjectChange;
import org.iana.rzm.trans.change.Change;
import org.iana.rzm.trans.change.SimpleChange;
import org.iana.rzm.trans.hibernate.test.common.HibernateMappingUnitTest;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ObjectChangeHibernateMappingTest extends HibernateMappingUnitTest<ObjectChange> {

    protected ObjectChange create() throws Exception {
        Map<String, Change> fieldChanges = new HashMap<String, Change>();
        fieldChanges.put("field1", new SimpleChange("old-1", "new-1"));
        fieldChanges.put("field2", new SimpleChange("old-2", "new-2"));
        Map<String, Change> subfieldChanges = new HashMap<String, Change>();
        subfieldChanges.put("subfield1", new SimpleChange(null, "newold-2"));
        fieldChanges.put("field3", new ObjectChange(Change.Type.ADDITION, "subid", subfieldChanges));
        return new ObjectChange(Change.Type.UPDATE, "id", fieldChanges);
    }

    protected ObjectChange change(ObjectChange o) throws Exception {
        o.getFieldChanges().remove("field2");
        return o;
    }

    protected Serializable getId(ObjectChange o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-trans"})
    public void testObjectChange() throws Exception {
        super.test();
    }
}
