package org.iana.rzm.trans;

import org.iana.notifications.Notification;
import org.iana.notifications.NotificationManager;
import org.iana.notifications.TextContent;
import org.iana.rzm.trans.conf.DefinedReSenderProcess;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.jbpm.JbpmConfiguration;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.scheduler.impl.SchedulerThread;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;

import java.util.List;

/**
 * @author: piotrt
 */
@Test(sequential = true, groups = {"eiana-trans", "notificationsReSender"})
public class NotificationsReSenderTest {
    ApplicationContext appCtx;
    TransactionManager transMgr;
    ProcessDAO processDAO;
    UserManager userManagerBean;
    NotificationManager notificationManagerBean;

    SchedulerThread schedulerThread;
    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();
    private Long testProcessInstanceId;

    RZMUser user;
    Notification notification;

    @BeforeClass
    public void init() {
        appCtx = SpringTransApplicationContext.getInstance().getContext();
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        txMgr = (PlatformTransactionManager) appCtx.getBean("transactionManager");
        schedulerThread = new SchedulerThread((JbpmConfiguration) appCtx.getBean("jbpmConfiguration"));
        userManagerBean = (UserManager) appCtx.getBean("userManager");
        notificationManagerBean = (NotificationManager) appCtx.getBean("NotificationManagerBean");

        try {
            processDAO.deploy(DefinedReSenderProcess.getDefinition());
        } finally {
            processDAO.close();
        }

        user = new RZMUser();
        user.setLoginName("reSenderUserTest");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("email@some.com");
        userManagerBean.create(user);

        TextContent content = new TextContent("subject", "body");

        notification = new Notification();
        notification.addAddressee(user);
        notification.setSent(false);
        notification.setContent(content);
        notificationManagerBean.create(notification);

    }

    @Test
    public void testReSender() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            ProcessInstance pi = processDAO.newProcessInstance("notifications resender");
            testProcessInstanceId = pi.getId();
            Token token = pi.getRootToken();
            token.signal();
            assert token.getNode().getName().equals("TRY_SEND");

            txMgr.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test (dependsOnMethods = {"testReSender"})
    public void testNotificatioUp() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            ProcessInstance pi = processDAO.getProcessInstance(testProcessInstanceId);
            processDAO.delete(pi);
            List<Notification> notifList = notificationManagerBean.findUnSentNotifications(40L);
            assert notifList.size() == 1;
            assert notifList.get(0).getSentFailures() == 1;

            txMgr.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @AfterClass
    public void cleanUp() {
        userManagerBean.delete(user);
    }
}
