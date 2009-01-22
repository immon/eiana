package org.iana.config;

import org.iana.config.impl.CompositeConfig;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.OwnedConfig;
import org.iana.config.impl.SingleParameter;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.test.spring.RollbackableSpringContextTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"hibernate", "common-config"})
public class CompositeConfigTest extends RollbackableSpringContextTest {

    private static final String OWNER = "me";

    protected ConfigDAO hibernateConfigDAO;

    protected ParameterManager parameterManager;

    public CompositeConfigTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    public void init() throws ConfigException {
        initFirstParam();
        initSecondParam();
    }

    @Test
    public void testCompositeConfig() throws ConfigException {

        List<Config> configs = new ArrayList<Config>();
        OwnedConfig ownedConfig = new OwnedConfig(OWNER, parameterManager);

        configs.add(ownedConfig.getSubConfig("sub1"));
        configs.add(ownedConfig);


        Config compositeConfig = new CompositeConfig(configs);
        Long retLong = compositeConfig.getLongParameter("param1");
        assert 10 == retLong;
    }

    @Test
    public void testCompositeConfig2() throws ConfigException {
        
        List<Config> configs = new ArrayList<Config>();
        OwnedConfig ownedConfig = new OwnedConfig(OWNER, parameterManager);

        configs.add(ownedConfig);
        configs.add(ownedConfig.getSubConfig("sub1"));

        Config compositeConfig = new CompositeConfig(configs);
        Long retLong = compositeConfig.getLongParameter("param1");
        assert 100 == retLong;
    }

    public void cleanUp() {
    }

    private void initFirstParam() throws ConfigException {
        SingleParameter singleParam = new SingleParameter("param1", Long.toString(100));
        singleParam.setOwner(OWNER);
        singleParam.setFromDate(System.currentTimeMillis() - 100000);
        singleParam.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(singleParam);
    }

    private void initSecondParam() throws ConfigException {
        SingleParameter singleParam2 = new SingleParameter("sub1.param1", Long.toString(10));
        singleParam2.setOwner(OWNER);
        singleParam2.setFromDate(System.currentTimeMillis() - 100000);
        singleParam2.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(singleParam2);
    }
}