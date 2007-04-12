package org.iana.notifications;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.context.ApplicationContext;
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.conf.SpringApplicationContext;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"tests", "notificationManager"})
public class NotificationsManagerTest {
    private PlatformTransactionManager txMgr;
    private UserDAO                    userDAO;
    private NotificationManager        notificationManager;
    private TransactionDefinition      txDef = new DefaultTransactionDefinition();
    TransactionStatus                  txStatus;

    private RZMUser      firstUser, secondUser;
    private Notification notification, secondNotification, thirdNotification;
    private Long         notificationId;
    private Long         secondUserId;

    @BeforeClass
    public void init() {
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
        txMgr = (PlatformTransactionManager) appCtx.getBean("transactionManager");
        userDAO = (UserDAO) appCtx.getBean("userDAO");
        notificationManager = (NotificationManager) appCtx.getBean("NotificationManagerBean");
    }

    @Test
    public void testNotificationsDAO() {
        txStatus = txMgr.getTransaction(txDef);

        firstUser = new RZMUser("John", "Do", "Organization", "john345", "john@do.com", "magic", false);
        userDAO.create(firstUser);

        secondUser = new RZMUser("temp","temp", "temp", "temp", "temp@temp.com", "temp", false);
        userDAO.create(secondUser);
        secondUserId = secondUser.getObjId();

        Map<String, String> values = new HashMap<String, String>();
        values.put("test", "something");
        values.put("name", "anyName");

        TemplateContent tc = new TemplateContent("SAMPLE_TEMPLATE3", values);

        notification = new Notification();
        notification.addAddressee(firstUser);
        notification.setContent(tc);
        notification.setSent(false);

        notificationManager.create(notification);
        notificationId = notification.getObjId();

        Map<String, String> valuesNew = new HashMap<String, String>();
        valuesNew.put("newKey1", "somethingNew");
        valuesNew.put("newKey2", "anyNameNew");

        TemplateContent tcNew = new TemplateContent("SAMPLE_TEMPLATE3", valuesNew);
        secondNotification = new Notification();
        secondNotification.addAddressee(firstUser);
        secondNotification.addAddressee(secondUser);
        secondNotification.setContent(tcNew);
        secondNotification.setSent(false);
        notificationManager.create(secondNotification);

        Notification retrivedNotif = notificationManager.get(notification.getObjId());

        Set<Addressee> retrivedAddressee = retrivedNotif.getAddressee();

        assert retrivedAddressee.size() == 1;

        for(Addressee addressee : retrivedAddressee) {
            assert addressee.getEmail().equals(firstUser.getEmail());
            assert addressee.getName().equals(firstUser.getName());
        }

        retrivedNotif = notificationManager.get(secondNotification.getObjId());
        retrivedAddressee = retrivedNotif.getAddressee();

        assert retrivedAddressee.size() == 2;

        List<Notification> userNotifications = notificationManager.findUserNotifications(secondUser);

        assert userNotifications.size() == 1;

        userNotifications = notificationManager.findUserNotifications(firstUser);

        assert userNotifications.size() == 2;

        for(Notification notification : userNotifications) {
            assert notification.getContent().equals(tc) || notification.getContent().equals(tcNew);
            assert !notification.isSent();
        }
        txMgr.commit(txStatus);
    }

    @Test(dependsOnMethods = {"testNotificationsDAO"})
    public void testNotificationsDAO_Update() {
        txStatus = txMgr.getTransaction(txDef);
        notification = notificationManager.get(notificationId);
        notification.addAddressee(secondUser);
        notificationManager.update(notification);
        txMgr.commit(txStatus);
        txStatus = txMgr.getTransaction(txDef);
        notification = notificationManager.get(notificationId);
        assert notification.getAddressee().size() == 2;
        txMgr.commit(txStatus);
    }

    @Test(dependsOnMethods = {"testNotificationsDAO_Update"})
    public void testNotificationsDAO_DeleteUser() {
        txStatus = txMgr.getTransaction(txDef);
        List<Notification> notifList = notificationManager.findUserNotifications(secondUser);
        assert notifList.size() == 2;
        for(Notification notif : notifList) {
            Set<Addressee> newAddressee = new HashSet<Addressee>();
            for(Addressee addressee : notif.getAddressee()) {
                if (!addressee.getObjId().equals(secondUserId))
                    newAddressee.add(addressee);
            }
            notif.setAddressee(newAddressee);
            notificationManager.update(notif);
        }
        txMgr.commit(txStatus);    
        txStatus = txMgr.getTransaction(txDef);
        notifList = notificationManager.findUserNotifications(secondUser);
        assert notifList.size() == 0;
        txMgr.commit(txStatus);
        userDAO.delete(secondUser);
    }

    @Test(dependsOnMethods = {"testNotificationsDAO_DeleteUser"})
    public void testNotificationsDAO_TempContent() {
        txStatus = txMgr.getTransaction(txDef);
        TempContent tempContent = new TempContent();
        tempContent.setBody("temp body");
        tempContent.setSubject("temp subject");
        thirdNotification = new Notification();
        thirdNotification.setContent(tempContent);
        thirdNotification.addAddressee(firstUser);
        thirdNotification.setSent(false);
        notificationManager.create(thirdNotification);
        txMgr.commit(txStatus);
        txStatus = txMgr.getTransaction(txDef);
        Notification retrivedNotification = notificationManager.get(thirdNotification.getObjId());
        assert retrivedNotification.getContent().getBody().equals("temp body");
        assert retrivedNotification.getContent().getSubject().equals("temp subject");
        txMgr.commit(txStatus);
    }

    @AfterClass
    public void cleanUp() {
        notification = notificationManager.get(notification.getObjId());
        secondNotification = notificationManager.get(secondNotification.getObjId());
        notificationManager.delete(notification);
        notificationManager.delete(secondNotification);
        notificationManager.delete(thirdNotification);
        userDAO.delete(firstUser);
    }
}
