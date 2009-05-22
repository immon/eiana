package org.iana.rzm.facade.admin;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.admin.config.StatelessAdminConfigManager;
import org.iana.rzm.facade.admin.config.binded.Pop3Config;
import org.iana.rzm.facade.admin.config.binded.SmtpConfig;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.iana.test.spring.RollbackableSpringContextTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test
public class AdminConfigManagerTest extends RollbackableSpringContextTest {

    protected StatelessAdminConfigManager adminConfigManager;
    protected UserManager userManager;

    private AuthenticatedUser authUser;

    public AdminConfigManagerTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    @BeforeClass
    protected void init() throws Exception {

        RZMUser root = new RZMUser();
        root.setLoginName("root");
        AdminRole adminRole = new AdminRole(AdminRole.AdminType.ROOT);
        root.addRole(adminRole);
        userManager.create(root);
        authUser = new AuthenticatedUser(root.getObjId(), root.getLoginName(), true);

    }

    protected void cleanUp() throws Exception {
    }

    @Test
    public void testCreatePop3Config() throws Exception {
        Pop3Config expectedConfig = createPop3Config();
        Pop3Config actualConfig = adminConfigManager.getPop3Config(authUser);
        Assert.assertEquals(actualConfig, expectedConfig);
    }

    @Test
    public void testUpdatePop3Config() throws Exception {
        Pop3Config expectedConfig = createPop3Config();
        expectedConfig.setHost("host2");
        adminConfigManager.setPop3Config(expectedConfig, authUser);
        Pop3Config actualConfig = adminConfigManager.getPop3Config(authUser);
        Assert.assertEquals(actualConfig, expectedConfig);
    }

    @Test
    public void testResetPop3Config() throws Exception {
        Pop3Config expectedConfig = createPop3Config();
        expectedConfig.setHost(null);
        expectedConfig.setPort(null);
        adminConfigManager.setPop3Config(expectedConfig, authUser);
        Pop3Config actualConfig = adminConfigManager.getPop3Config(authUser);
        Assert.assertEquals(actualConfig, expectedConfig);
    }

    @Test
    public void testCreateSmtpConfig() throws Exception {
        SmtpConfig expectedConfig = createSmtpConfig();
        SmtpConfig actualConfig = adminConfigManager.getSmtpConfig(authUser);
        Assert.assertEquals(actualConfig, expectedConfig);
    }

    private Pop3Config createPop3Config() throws InfrastructureException {
        Pop3Config expectedConfig = new Pop3Config();
        expectedConfig.setHost("host");
        expectedConfig.setPort(22);
        adminConfigManager.setPop3Config(expectedConfig, authUser);
        return expectedConfig;
    }

    private SmtpConfig createSmtpConfig() throws InfrastructureException {
        SmtpConfig expectedConfig = new SmtpConfig();
        expectedConfig.setHost("host");
        expectedConfig.setPort(22);
        adminConfigManager.setSmtpConfig(expectedConfig, authUser);
        return expectedConfig;
    }

}
