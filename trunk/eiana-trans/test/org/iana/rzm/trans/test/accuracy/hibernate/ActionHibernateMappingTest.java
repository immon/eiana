package org.iana.rzm.trans.test.accuracy.hibernate;

import org.iana.rzm.trans.Action;
import org.iana.rzm.trans.test.common.hibernate.HibernateMappingUnitTest;
import org.iana.rzm.trans.test.common.hibernate.HibernateMappingTestUtil;
import org.iana.rzm.trans.change.Change;
import org.iana.rzm.trans.change.Modification;
import org.iana.rzm.trans.change.ModifiedPrimitiveValue;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Jakub Laszkiewicz
 */
public class ActionHibernateMappingTest extends HibernateMappingUnitTest<Action> {
    protected Action create() throws Exception {
        List<Change> change = new ArrayList<Change>();
        change.add(new Modification("1st modification",
                HibernateMappingTestUtil.setupMPV(new ModifiedPrimitiveValue(), "created")));
        change.add(new Modification("2nd modification",
                HibernateMappingTestUtil.setupMPV(new ModifiedPrimitiveValue(), "created")));
        return HibernateMappingTestUtil.setupAction(new Action(), Action.Name.CREATE_NEW_TLD, change);
    }

    protected Action change(Action o) throws Exception {
        return HibernateMappingTestUtil.setupAction(o, Action.Name.MODIFY_CONTACT, o.getChange());
    }

    protected Serializable getId(Action o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-trans"})
    public void testAction() throws Exception {
        super.test();
    }
}
