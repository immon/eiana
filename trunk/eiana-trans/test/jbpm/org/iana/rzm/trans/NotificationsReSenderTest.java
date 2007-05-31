package org.iana.rzm.trans;

import org.iana.notifications.Notification;
import org.iana.notifications.NotificationManager;
import org.iana.notifications.TextContent;
import org.iana.rzm.trans.conf.DefinedReSenderProcess;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.iana.test.spring.TransactionalSpringContextTests;
import org.jbpm.JbpmConfiguration;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.scheduler.impl.SchedulerThread;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author: piotrt
 */
@Test(sequential = true, groups = {"eiana-trans", "notificationsReSender"})
public class NotificationsReSenderTest extends TransactionalSpringContextTests {
    protected ProcessDAO processDAO;
    protected UserManager userManager;
    protected NotificationManager NotificationManagerBean;
    protected JbpmConfiguration jbpmConfiguration;

    private Long testProcessInstanceId;

    public NotificationsReSenderTest() {
        super(SpringTransApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() {
        new SchedulerThread(jbpmConfiguration);
        try {
            processDAO.deploy(DefinedReSenderProcess.getDefinition());
        } finally {
            processDAO.close();
        }

        RZMUser user = new RZMUser();
        user.setLoginName("reSenderUserTest");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("email@some.com");
        userManager.create(user);

        TextContent content = new TextContent("subject", "body");

        Notification notification = new Notification();
        notification.addAddressee(user);
        notification.setSent(false);
        notification.setContent(content);
        NotificationManagerBean.create(notification);
    }

    @Test
    public void testReSender() throws Exception {
        try {
            ProcessInstance pi = processDAO.newProcessInstance("notifications resender");
            testProcessInstanceId = pi.getId();
            Token token = pi.getRootToken();
            token.signal();
            assert token.getNode().getName().equals("TRY_SEND");
        } finally {
            processDAO.close();
        }
    }

    @Test (dependsOnMethods = {"testReSender"})
    public void testNotificatioUp() throws Exception {
        try {
            ProcessInstance pi = processDAO.getProcessInstance(testProcessInstanceId);
            processDAO.delete(pi);
            List<Notification> notifList = NotificationManagerBean.findUnSentNotifications(40L);
            assert notifList.size() == 1;
            assert notifList.get(0).getSentFailures() == 1;
        } finally {
            processDAO.close();
        }
    }

    protected void cleanUp() {
        List<RZMUser> users = userManager.findAll();
        for (RZMUser user : users) {
            userManager.delete(user);
        }
    }
}
