package org.iana.rzm.notifications;

import org.iana.config.Config;
import org.iana.config.ConfigDAO;
import org.iana.config.impl.SingleParameter;
import org.iana.notifications.NotificationTemplate;
import org.iana.notifications.NotificationTemplateManager;
import org.iana.rzm.conf.SpringApplicationContext;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"test", "NotificationsTemplateManagerTest"})
public class NotificationsTemplateManagerTest {

    private static final String NOTIFICATION_TYPE = "contact-confirmation-remainder";

    private NotificationTemplateManager notificationTemplateManager;
    private ConfigDAO hibernateConfigDAO;

    @BeforeClass
    protected void init() throws Exception {
        ApplicationContext ctx = SpringApplicationContext.getInstance().getContext();
        notificationTemplateManager = (NotificationTemplateManager) ctx.getBean("notificationTemplateManager");
        hibernateConfigDAO = (ConfigDAO) ctx.getBean("hibernateConfigDAO");

        //create notification template in config
        String value = "<iana:template>\n" +
                "        <iana:type>contact-confirmation-remainder</iana:type>\n" +
                "        <iana:subject>{transactionId} | {stateName} | [RZM] | {domainName}</iana:subject>\n" +
                "        <iana:content>FROM DATABASE</iana:content>\n" +
                "       </iana:template>";
        String name = NotificationTemplateManager.class.getSimpleName() + "." + NOTIFICATION_TYPE;
//        List<String> values = new ArrayList<String>(1);
//        values.add(value);
        SingleParameter param = new SingleParameter(name, value);
        param.setOwner(Config.DEFAULT_OWNER);
        param.setFromDate(System.currentTimeMillis());
        hibernateConfigDAO.addParameter(param);
    }

    @Test
    public void doTest() throws Exception {

        NotificationTemplate template = notificationTemplateManager.getNotificationTemplate(NOTIFICATION_TYPE);

        assert template != null;
        assert template.getType().equals(NOTIFICATION_TYPE);
        assert template.getContent().equals("FROM DATABASE");

        //remove notification template from config
        String name = NotificationTemplateManager.class.getSimpleName() + "." + NOTIFICATION_TYPE;
        hibernateConfigDAO.removeParameter(Config.DEFAULT_OWNER, name);

        //notification taken from mapper
        template = notificationTemplateManager.getNotificationTemplate(NOTIFICATION_TYPE);

        assert template != null;
        assert template.getType().equals(NOTIFICATION_TYPE);
        assert !template.getContent().equals("FROM DATABASE");
    }
}
