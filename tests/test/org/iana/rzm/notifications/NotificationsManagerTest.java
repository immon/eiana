package org.iana.rzm.notifications;

import org.iana.notifications.*;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.iana.test.spring.TransactionalSpringContextTests;
import org.iana.criteria.*;
import org.testng.annotations.Test;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"test", "NotificationManagerBean"})
public class NotificationsManagerTest extends TransactionalSpringContextTests {
    protected UserManager userManager;
    protected NotificationManager NotificationManagerBean;

    private RZMUser firstUser, secondUser;
    private Notification notification;
    private Notification thirdNotification;
    private Long notificationId;

    public NotificationsManagerTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() {
    }

    @Test
    public void testNotificationsDAO() throws Exception {
        firstUser = new RZMUser("John", "Do", "Organization", "john345", "john@do.com", "magic", false);
        userManager.create(firstUser);

        secondUser = new RZMUser("temp", "temp", "temp", "temp", "temp@temp.com", "temp", false);
        userManager.create(secondUser);

        Map<String, String> values = new HashMap<String, String>();
        values.put("test", "something");
        values.put("name", "anyName");

        TemplateContent tc = new TemplateContent("SAMPLE_TEMPLATE3", values);

        notification = new Notification(1L);
        notification.addAddressee(firstUser);
        notification.setContent(tc);
        notification.setSent(false);
        notification.setType(tc.getTemplateName());

        NotificationManagerBean.create(notification);
        notificationId = notification.getObjId();

        Map<String, String> valuesNew = new HashMap<String, String>();
        valuesNew.put("newKey1", "somethingNew");
        valuesNew.put("newKey2", "anyNameNew");

        TemplateContent tcNew = new TemplateContent("SAMPLE_TEMPLATE3", valuesNew);
        Notification secondNotification = new Notification(2L);
        secondNotification.addAddressee(firstUser);
        secondNotification.addAddressee(secondUser);
        secondNotification.setContent(tcNew);
        secondNotification.setSent(false);
        secondNotification.setType(tcNew.getTemplateName());
        NotificationManagerBean.create(secondNotification);

        Notification retrivedNotif = NotificationManagerBean.get(notification.getObjId());

        Set<Addressee> retrivedAddressee = retrivedNotif.getAddressee();

        assert retrivedAddressee.size() == 1;

        for (Addressee addressee : retrivedAddressee) {
            assert addressee.getEmail().equals(firstUser.getEmail());
            assert addressee.getName().equals(firstUser.getName());
        }

        retrivedNotif = NotificationManagerBean.get(secondNotification.getObjId());
        retrivedAddressee = retrivedNotif.getAddressee();

        assert retrivedAddressee.size() == 2;

        List<Notification> userNotifications = NotificationManagerBean.findUserNotifications(secondUser);

        assert userNotifications.size() == 1;

        userNotifications = NotificationManagerBean.findUserNotifications(firstUser);

        assert userNotifications.size() == 2;

        for (Notification notification : userNotifications) {
            assert notification.getContent().equals(tc) || notification.getContent().equals(tcNew);
            assert !notification.isSent();
        }
    }

    @Test(dependsOnMethods = {"testNotificationsDAO"})
    public void testNotificationsDAO_Update() throws Exception {
        notification = NotificationManagerBean.get(notificationId);
        notification.addAddressee(secondUser);
        NotificationManagerBean.update(notification);
        notification = NotificationManagerBean.get(notificationId);
        assert notification.getAddressee().size() == 2;
    }

    @Test(dependsOnMethods = {"testNotificationsDAO_Update"})
    public void testNotificationsDAO_TextContent() throws Exception {
        TextContent tempContent = new TextContent();
        tempContent.setBody("temp body");
        tempContent.setSubject("temp subject");
        thirdNotification = new Notification(3L);
        thirdNotification.setContent(tempContent);
        thirdNotification.addAddressee(firstUser);
        thirdNotification.setSent(false);
        thirdNotification.setType("text-content");
        NotificationManagerBean.create(thirdNotification);
        Notification retrivedNotification = NotificationManagerBean.get(thirdNotification.getObjId());
        assert retrivedNotification.getContent().getBody().equals("temp body");
        assert retrivedNotification.getContent().getSubject().equals("temp subject");
    }

    @Test(dependsOnMethods = {"testNotificationsDAO_TextContent"})
    public void testNotificationsDAO_EmailAddresse() throws Exception {
        TextContent tempContent = new TextContent();
        tempContent.setBody("temp body");
        tempContent.setSubject("temp subject");
        thirdNotification = new Notification(4L);
        thirdNotification.setContent(tempContent);

        EmailAddressee emailAddressee = new EmailAddressee("some@emial.com", "someusername");
        emailAddressee.setEmail("some@emial.com");
        emailAddressee.setName("someusername");
        thirdNotification.addAddressee(emailAddressee);
        thirdNotification.setSent(false);
        thirdNotification.setType("text-content");
        NotificationManagerBean.create(thirdNotification);
        Notification retrivedNotification = NotificationManagerBean.get(thirdNotification.getObjId());
        assert retrivedNotification.getContent().getBody().equals("temp body");
        assert retrivedNotification.getContent().getSubject().equals("temp subject");
        assert retrivedNotification.getAddressee().iterator().next().getEmail().equals("some@emial.com");
        assert retrivedNotification.getAddressee().iterator().next().getName().equals("someusername");
    }

    @Test(dependsOnMethods = {"testNotificationsDAO_EmailAddresse"})
    public void testFindNotifications() {
        //List<Criterion> criteria = new ArrayList<Criterion>();
        //criteria.add(new Lower(NotificationCriteriaFields.CREATED, new Date()));
        //criteria.add(new Equal(NotificationCriteriaFields.PERSISTENT, false));
        //List<Notification> found =  NotificationManagerBean.find(new And(criteria));
        List<Notification> found =  NotificationManagerBean.findAll();
        System.out.println("#### " + found);
    }

    protected void cleanUp() throws Exception {
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Notification notification : NotificationManagerBean.findAll())
            NotificationManagerBean.delete(notification);
    }
}
