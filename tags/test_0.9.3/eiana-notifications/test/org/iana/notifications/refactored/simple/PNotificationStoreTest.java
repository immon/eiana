package org.iana.notifications.refactored.simple;

import static org.easymock.EasyMock.*;
import org.iana.dao.DataAccessObject;
import org.iana.notifications.*;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test
public class PNotificationStoreTest {

    PNotificationStore store;

    NotificationSender senderMock;

    DataAccessObject<PNotification> daoMock;

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void setUp() {
        senderMock = createStrictMock(NotificationSender.class);
        daoMock = (DataAccessObject<PNotification>) createStrictMock(DataAccessObject.class);

        store = new PNotificationStore(senderMock, daoMock);
    }

    @BeforeMethod
    public void resetMocks() {
        reset(senderMock, daoMock);
    }

    @Test
    public void testSuccessSendOfPersistentNotification() throws Exception {
        PNotification tosend = createNotification(true);
        senderMock.send(tosend);
        daoMock.create(tosend);
        replay(senderMock, daoMock);
        store.send(tosend);
        verify(senderMock, daoMock);
        assertTrue(tosend.isSent());
        assertNotNull(tosend.getSentDate());
    }

    @Test
    public void testSuccessfulSendOfNotPersistentNotification() throws Exception {
        PNotification tosend = createNotification(false);
        senderMock.send(tosend);
        replay(senderMock, daoMock);
        store.send(tosend);
        verify(senderMock, daoMock);
        assertTrue(tosend.isSent());
        assertNotNull(tosend.getSentDate());
    }

    @Test (expectedExceptions = NotificationSenderException.class)
    public void testExceptionWhileSend() throws Exception {
        PNotification tosend = createNotification(false);
        NotificationSenderException exception = new NotificationSenderException();
        try {
            senderMock.send(tosend);
            expectLastCall().andStubThrow(exception);
            replay(senderMock, daoMock);
            store.send(tosend);
        } finally {
            verify(senderMock, daoMock);
            assertFalse(tosend.isSent());
            assertNull(tosend.getSentDate());
        }
    }

    private PNotification createNotification(boolean persistent) {
        Set<PAddressee> addrs = new HashSet<PAddressee>();
        addrs.add(new PAddressee("name", "email"));
        PContent cnt = new PContent("subject", "body");
        return new PNotification("type", addrs, cnt, persistent);
    }

}
