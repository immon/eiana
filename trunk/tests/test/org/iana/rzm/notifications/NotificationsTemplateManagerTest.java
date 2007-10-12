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

    private NotificationTemplateManager notificationTemplateManager;
    private ConfigDAO hibernateConfigDAO;

    @BeforeClass
    protected void init() throws Exception {
        ApplicationContext ctx = SpringApplicationContext.getInstance().getContext();
        notificationTemplateManager = (NotificationTemplateManager) ctx.getBean("notificationTemplateManager");
        hibernateConfigDAO = (ConfigDAO) ctx.getBean("hibernateConfigDAO");
    }

    @Test
    public void doTest() throws Exception {
        String value = "<iana:template>\n" +
                "        <iana:type>contact-confirmation-remainder</iana:type>\n" +
                "        <iana:subject>{transactionId} | {stateName} | [RZM] | {domainName}</iana:subject>\n" +
                "        <iana:content>FROM DATABASE</iana:content>\n" +
                "    </iana:template>";
        String type = "contact-confirmation-remainder";
        String name = NotificationTemplateManager.class.getSimpleName() + "." + type;
        SingleParameter param = new SingleParameter(name, value);
        param.setOwner(Config.DEFAULT_OWNER);
        param.setFromDate(System.currentTimeMillis());
        hibernateConfigDAO.addParameter(param);

        NotificationTemplate template = notificationTemplateManager.getNotificationTemplate(type);

        assert template != null;
        assert template.getType().equals(type);
        assert template.getContent().equals("FROM DATABASE");

        hibernateConfigDAO.removeParameter(Config.DEFAULT_OWNER, name);

        template = notificationTemplateManager.getNotificationTemplate(type);

        assert template != null;
        assert template.getType().equals(type);
        assert !template.getContent().equals("FROM DATABASE");
    }
}
