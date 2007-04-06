package org.iana.rzm.domain.hibernate.test.accuracy;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.hibernate.test.common.HibernateMappingUnitTest;
import org.iana.rzm.domain.hibernate.test.common.HibernateMappingTestUtil;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"hibernate", "eiana-domains"})
public class ContactHibernateMappingTest extends HibernateMappingUnitTest<Contact> {

    protected Contact create() {
        return HibernateMappingTestUtil.setupContact(new Contact(), "created", true, "US");
    }

    protected Contact change(Contact o) {
        return HibernateMappingTestUtil.setupContact(o, "changed", false, "CC");
    }

    protected Serializable getId(Contact o) {
        return o.getObjId();
    }

    @Test
    public void testContact() throws Exception {
        super.test();
    }
}
