package org.iana.rzm.domain.test.accuracy.hibernate;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.test.common.hibernate.HibernateMappingUnitTest;
import org.iana.rzm.domain.test.common.hibernate.HibernateMappingTestUtil;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class ContactHibernateMappingTest extends HibernateMappingUnitTest<Contact> {
    protected Contact create() {
        return HibernateMappingTestUtil.setupContact(new Contact(), "created", true);
    }

    protected Contact change(Contact o) {
        return HibernateMappingTestUtil.setupContact(o, "changed", false);
    }

    protected Serializable getId(Contact o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-domains"})
    public void testContact() throws Exception {
        super.test();
    }
}
