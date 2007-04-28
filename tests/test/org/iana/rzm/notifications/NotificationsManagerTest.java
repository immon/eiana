package org.iana.rzm.notifications;

import org.iana.notifications.Addressee;
import org.iana.notifications.Notification;
import org.iana.notifications.NotificationManager;
import org.iana.notifications.TemplateContent;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"excluded", "tests", "notificationManager"})
public class NotificationsManagerTest {
    private UserManager                userManager;
    private NotificationManager notificationManager;

    private RZMUser      firstUser, secondUser;
    private Notification notification, secondNotification, thirdNotification;
    private Long         notificationId;
    private Long         secondUserId;
    private List<Long> notificationIds = new ArrayList<Long>();

    @BeforeClass
    public void init() {
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
        userManager = (UserManager) appCtx.getBean("userManager");
        notificationManager = (NotificationManager) appCtx.getBean("NotificationManagerBean");
    }

    @Test
    public void testNotificationsDAO() throws Exception {
        firstUser = new RZMUser("John", "Do", "Organization", "john345", "john@do.com", "magic", false);
        userManager.create(firstUser);

        secondUser = new RZMUser("temp","temp", "temp", "temp", "temp@temp.com", "temp", false);
        userManager.create(secondUser);
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
        notificationIds.add(notificationId);

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
        notificationIds.add(secondNotification.getObjId());

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
    }

    @Test(dependsOnMethods = {"testNotificationsDAO"})
    public void testNotificationsDAO_Update() throws Exception {
        notification = notificationManager.get(notificationId);
        notification.addAddressee(secondUser);
        notificationManager.update(notification);

        notification = notificationManager.get(notificationId);
        assert notification.getAddressee().size() == 2;
    }

    @Test(dependsOnMethods = {"testNotificationsDAO_Update"})
    public void testNotificationsDAO_TempContent() throws Exception {
        TempContent tempContent = new TempContent();
        tempContent.setBody("temp body");
        tempContent.setSubject("temp subject");
        thirdNotification = new Notification();
        thirdNotification.setContent(tempContent);
        thirdNotification.addAddressee(firstUser);
        thirdNotification.setSent(false);
        notificationManager.create(thirdNotification);
        notificationIds.add(thirdNotification.getObjId());

        Notification retrivedNotification = notificationManager.get(thirdNotification.getObjId());
        assert retrivedNotification.getContent().getBody().equals("temp body");
        assert retrivedNotification.getContent().getSubject().equals("temp subject");
    }

    @AfterClass
    public void cleanUp() throws Exception {
        userManager.delete(firstUser);

        notificationManager.deleteUserNotifications(secondUser);
        userManager.delete(secondUser);
    }
}
