package org.iana.notifications.refactored.simple;

import org.iana.notifications.PAddressee;
import org.iana.notifications.PContent;
import org.iana.notifications.PNotification;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test
public class PNotificationTest {

    @Test
    public void testPNotificationCtr() {
        Set<PAddressee> addrs = new HashSet<PAddressee>();
        addrs.add(new PAddressee("name", "email"));
        PContent cnt = new PContent("subject", "body");
        PNotification src = new PNotification("type", addrs, cnt, false);
        // todo: test
/*
        assertEquals("type", src.getType());
        assertEquals(dst.getAddressees(), src.getAddressees());
        assertEquals(dst.getContent(), src.getContent());
        assertEquals(dst.isPersistent(), src.isPersistent());
        assertEquals(dst.isSent(), false);
        assertNull(dst.getSentDate());
        assertNotNull(dst.getCreateDate());
*/
    }

}
