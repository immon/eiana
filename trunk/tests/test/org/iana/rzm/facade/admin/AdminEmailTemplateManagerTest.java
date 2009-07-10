package org.iana.rzm.facade.admin;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.admin.config.EmailTemplateVO;
import org.iana.rzm.facade.admin.config.StatelessAdminEmailTemplateManager;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

@Test
public class AdminEmailTemplateManagerTest {

    StatelessAdminEmailTemplateManager statelessAdminEmailTemplateManager;

    @BeforeClass
    public void init() {
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
        statelessAdminEmailTemplateManager = (StatelessAdminEmailTemplateManager) appCtx.getBean("statelessAdminTemplatesManager");
    }

    @Test
    public void testGetTemplates() throws Exception {
        List<EmailTemplateVO> emailTemplates = statelessAdminEmailTemplateManager.getEmailTemplates();
        assert emailTemplates != null;
        assert !emailTemplates.isEmpty();
    }
}
