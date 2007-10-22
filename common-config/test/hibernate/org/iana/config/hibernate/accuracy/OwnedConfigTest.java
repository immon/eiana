package org.iana.config.hibernate.accuracy;

import org.iana.config.ConfigDAO;
import org.iana.config.ParameterManager;
import org.iana.config.conf.SpringConfigApplicationContext;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.ListParameter;
import org.iana.config.impl.OwnedConfig;
import org.iana.config.impl.SingleParameter;
import org.iana.test.spring.TransactionalSpringContextTests;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"hibernate", "common-config"})
public class OwnedConfigTest extends TransactionalSpringContextTests {

    private static final String OWNER = "me";

    protected ConfigDAO hibernateConfigDAO;
    protected ParameterManager parameterManager;

    public OwnedConfigTest() {
        super(SpringConfigApplicationContext.CONFIG_FILE_NAME);
    }

    public void init() {
    }

    @Test
    public void testSingleParam() throws ConfigException {
        SingleParameter singleParam = new SingleParameter("param1", "value1");
        singleParam.setOwner(OWNER);
        singleParam.setFromDate(System.currentTimeMillis() - 100000);
        singleParam.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(singleParam);

        OwnedConfig ownedConfig = new OwnedConfig(OWNER, parameterManager);
        String value = ownedConfig.getParameter("param1");
        assert "value1".equals(value);
    }

    @Test(dependsOnMethods = "testSingleParam")
    public void testListParam() throws ConfigException {
        List<String> values = new ArrayList<String>();
        values.add("firstValue");
        values.add("secondValue");

        ListParameter listParam = new ListParameter("param2", values);
        listParam.setOwner(OWNER);
        listParam.setFromDate(System.currentTimeMillis() - 100000);
        listParam.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(listParam);

        OwnedConfig ownedConfig = new OwnedConfig(OWNER, parameterManager);
        List<String> retValues = ownedConfig.getParameterList("param2");
        assert values.equals(retValues);
    }

    @Test(dependsOnMethods = "testListParam")
    public void testGetParameterNames() throws ConfigException {
        Set<String> paramNames = new HashSet<String>();
        paramNames.add("param1");
        paramNames.add("param2");

        OwnedConfig ownedConfig = new OwnedConfig("me", parameterManager);
        assert paramNames.equals(ownedConfig.getParameterNames());
    }

    @Test(dependsOnMethods = "testGetParameterNames", alwaysRun = true)
    public void testRemoveParameter() throws ConfigException {
        hibernateConfigDAO.removeParameter(OWNER, "param1");
        assert hibernateConfigDAO.getParameter(OWNER, "param1") == null;

        hibernateConfigDAO.removeParameter(OWNER, "param2");
        assert hibernateConfigDAO.getParameter(OWNER, "param2") == null;
    }

    public void cleanUp() {
    }
}