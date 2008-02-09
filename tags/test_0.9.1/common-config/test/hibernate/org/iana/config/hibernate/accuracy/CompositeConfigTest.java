package org.iana.config.hibernate.accuracy;

import org.iana.config.Config;
import org.iana.config.ConfigDAO;
import org.iana.config.ParameterManager;
import org.iana.config.conf.SpringConfigApplicationContext;
import org.iana.config.impl.CompositeConfig;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.OwnedConfig;
import org.iana.config.impl.SingleParameter;
import org.iana.test.spring.TransactionalSpringContextTests;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"hibernate", "common-config"})
public class CompositeConfigTest extends TransactionalSpringContextTests {

    private static final String OWNER = "me";

    protected ConfigDAO hibernateConfigDAO;

    protected ParameterManager parameterManager;

    public CompositeConfigTest() {
        super(SpringConfigApplicationContext.CONFIG_FILE_NAME);
    }

    public void init() {
    }

    @Test
    public void testCompositeConfig() throws ConfigException {
        SingleParameter singleParam = new SingleParameter("param1", Long.toString(100));
        singleParam.setOwner(OWNER);
        singleParam.setFromDate(System.currentTimeMillis() - 100000);
        singleParam.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(singleParam);

        SingleParameter singleParam2 = new SingleParameter("sub1.param1", Long.toString(10));
        singleParam2.setOwner(OWNER);
        singleParam2.setFromDate(System.currentTimeMillis() - 100000);
        singleParam2.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(singleParam2);

        List<Config> configs = new ArrayList<Config>();
        OwnedConfig ownedConfig = new OwnedConfig(OWNER, parameterManager);

        configs.add(ownedConfig.getSubConfig("sub1"));
        configs.add(ownedConfig);


        Config compositeConfig = new CompositeConfig(configs);
        Long retLong = compositeConfig.getLongParameter("param1");
        assert 10 == retLong;
    }

    @Test(dependsOnMethods = "testCompositeConfig")
    public void testCompositeConfig2() throws ConfigException {
        List<Config> configs = new ArrayList<Config>();
        OwnedConfig ownedConfig = new OwnedConfig(OWNER, parameterManager);

        configs.add(ownedConfig);
        configs.add(ownedConfig.getSubConfig("sub1"));

        Config compositeConfig = new CompositeConfig(configs);
        Long retLong = compositeConfig.getLongParameter("param1");
        assert 100 == retLong;
    }

    @Test(dependsOnMethods = "testCompositeConfig2", alwaysRun = true)
    public void testRemove() throws ConfigException {
        hibernateConfigDAO.removeParameter(OWNER, "param1");
        hibernateConfigDAO.removeParameter(OWNER, "sub1.param1");
    }

    public void cleanUp() {
    }
}