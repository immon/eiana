package org.iana.config;

import org.iana.config.impl.ConfigException;
import org.iana.config.impl.ListParameter;
import org.iana.config.impl.OwnedConfig;
import org.iana.config.impl.SingleParameter;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.test.spring.RollbackableSpringContextTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"hibernate", "common-config"})
public class OwnedConfigTest extends RollbackableSpringContextTest {

    private static final String OWNER = "me";

    protected ConfigDAO hibernateConfigDAO;
    protected ParameterManager parameterManager;

    public OwnedConfigTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    public void init() throws ConfigException {
        initFirstParam();

        List<String> values = new ArrayList<String>();
        values.add("firstValue");
        values.add("secondValue");

        initSecondParam(values);
    }

    @Test
    public void testSingleParam() throws ConfigException {
        OwnedConfig ownedConfig = new OwnedConfig(OWNER, parameterManager);
        String value = ownedConfig.getParameter("param1");
        assert "value1".equals(value);
    }

    @Test
    public void testListParam() throws ConfigException {
        List<String> values = new ArrayList<String>();
        values.add("firstValue");
        values.add("secondValue");

        OwnedConfig ownedConfig = new OwnedConfig(OWNER, parameterManager);
        List<String> retValues = ownedConfig.getParameterList("param2");
        assert values.equals(retValues);
    }

    @Test
    public void testGetParameterNames() throws ConfigException {
        Set<String> paramNames = new HashSet<String>();
        paramNames.add("param1");
        paramNames.add("param2");

        OwnedConfig ownedConfig = new OwnedConfig("me", parameterManager);
        assert paramNames.equals(ownedConfig.getParameterNames());
    }

    public void cleanUp() {
    }

    private void initFirstParam() throws ConfigException {
        SingleParameter singleParam = new SingleParameter("param1", "value1");
        singleParam.setOwner(OWNER);
        singleParam.setFromDate(System.currentTimeMillis() - 100000);
        singleParam.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(singleParam);
    }

    private void initSecondParam(List<String> values) throws ConfigException {
        ListParameter listParam = new ListParameter("param2", values);
        listParam.setOwner(OWNER);
        listParam.setFromDate(System.currentTimeMillis() - 100000);
        listParam.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(listParam);
    }
}