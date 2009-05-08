package org.iana.rzm.facade.admin;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.admin.config.AdminEmailTemplateManager;
import org.iana.rzm.facade.admin.config.EmailTemplateVO;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

@Test
public class AdminEmailTemplateManagerTest {

    AdminEmailTemplateManager adminEmailTemplateManager;

    @BeforeClass
    public void init() {
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
        adminEmailTemplateManager = (AdminEmailTemplateManager) appCtx.getBean("adminTemplatesManager");
    }

    @Test
    public void testGetTemplates() throws Exception {
        List<EmailTemplateVO> emailTemplates = adminEmailTemplateManager.getEmailTemplates();
        assert emailTemplates != null;
        assert emailTemplates.size() == 25;
    }
}
