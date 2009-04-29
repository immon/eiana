package org.iana.rzm.facade.admin;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.admin.config.AdminConfigManager;
import org.iana.rzm.facade.admin.config.ConfigParameter;
import org.iana.rzm.facade.admin.config.binded.Pop3Config;
import org.iana.rzm.facade.admin.config.binded.SmtpConfig;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.test.spring.RollbackableSpringContextTest;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test
public class AdminConfigManagerTest extends RollbackableSpringContextTest {

    protected AdminConfigManager adminConfigManager;

    public AdminConfigManagerTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    @BeforeClass
    protected void init() throws Exception {
    }

    protected void cleanUp() throws Exception {
    }

    @Test
    public void testCreatePop3Config() throws Exception {
        Pop3Config expectedConfig = createPop3Config();
        Pop3Config actualConfig = adminConfigManager.getPop3Config();
        Assert.assertEquals(actualConfig, expectedConfig);
    }

    @Test
    public void testUpdatePop3Config() throws Exception {
        Pop3Config expectedConfig = createPop3Config();
        expectedConfig.setHost("host2");
        adminConfigManager.setPop3Config(expectedConfig);
        Pop3Config actualConfig = adminConfigManager.getPop3Config();
        Assert.assertEquals(actualConfig, expectedConfig);
    }

    @Test
    public void testResetPop3Config() throws Exception {
        Pop3Config expectedConfig = createPop3Config();
        expectedConfig.setHost(null);
        expectedConfig.setPort(null);
        adminConfigManager.setPop3Config(expectedConfig);
        Pop3Config actualConfig = adminConfigManager.getPop3Config();
        Assert.assertEquals(actualConfig, expectedConfig);
    }

    @Test
    public void testCreateSmtpConfig() throws Exception {
        SmtpConfig expectedConfig = createSmtpConfig();
        SmtpConfig actualConfig = adminConfigManager.getSmtpConfig();
        Assert.assertEquals(actualConfig, expectedConfig);
    }

    @Test
    public void testGetConfigParameters() throws Exception {
        createPop3Config();
        createSmtpConfig();
        List<ConfigParameter> params = adminConfigManager.getParameters();
        Assert.assertTrue(params.size() > 0);
    }

    private Pop3Config createPop3Config() throws InfrastructureException {
        Pop3Config expectedConfig = new Pop3Config();
        expectedConfig.setHost("host");
        expectedConfig.setPort(22);
        adminConfigManager.setPop3Config(expectedConfig);
        return expectedConfig;
    }

    private SmtpConfig createSmtpConfig() throws InfrastructureException {
        SmtpConfig expectedConfig = new SmtpConfig();
        expectedConfig.setHost("host");
        expectedConfig.setPort(22);
        adminConfigManager.setSmtpConfig(expectedConfig);
        return expectedConfig;
    }
    
}
