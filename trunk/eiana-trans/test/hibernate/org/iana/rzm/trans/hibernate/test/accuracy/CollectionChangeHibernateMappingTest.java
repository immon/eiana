package org.iana.rzm.trans.hibernate.test.accuracy;

import org.iana.rzm.trans.change.CollectionChange;
import org.iana.rzm.trans.change.Change;
import org.iana.rzm.trans.change.SimpleChange;
import org.iana.rzm.trans.hibernate.test.common.HibernateMappingUnitTest;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Patrycja Wegrzynowicz
 */
public class CollectionChangeHibernateMappingTest extends HibernateMappingUnitTest<CollectionChange> {

    protected CollectionChange create() throws Exception {
        CollectionChange ret = new CollectionChange(Change.Type.UPDATE);
        ret.getAdded().add(new SimpleChange(null, "added"));
        ret.getRemoved().add(new SimpleChange("removed", null));
        ret.getModified().add(new SimpleChange("old", "new"));
        return ret;
    }

    protected CollectionChange change(CollectionChange o) throws Exception {
        o.getAdded().add(new SimpleChange(null, "added-2"));
        o.getRemoved().add(new SimpleChange("removed-2", null));
        o.getModified().clear();
        return o;
    }

    protected Serializable getId(CollectionChange o) {
        return o.getObjId();
    }

    @Test
    public void testCollectionChange() throws Exception {
        super.test();
    }
}
