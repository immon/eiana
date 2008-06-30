package org.iana.config;

import org.iana.config.impl.ListParameter;
import org.iana.config.impl.OwnedConfig;
import org.iana.rzm.conf.SpringApplicationContext;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"common-config"})
public class LazyInitializationTest {

    private ConfigDAO dao;

    private static final String LIST_PARAMETER = "list";
    private static final String OWNER = "owner";

    private Config config;

    private List<String> values;

    @BeforeClass
    public void init() throws Exception {
        ApplicationContext ctx = SpringApplicationContext.getInstance().getContext();
        dao = (ConfigDAO) ctx.getBean("hibernateConfigDAO");
        ParameterManager manager = (ParameterManager) ctx.getBean("parameterManager");

        config = new OwnedConfig(OWNER, manager);

        values = new ArrayList<String>();
        values.add("value1");
        values.add("value2");
        values.add("value2");

        ListParameter listParam = new ListParameter(LIST_PARAMETER, values);
        listParam.setOwner(OWNER);
        listParam.setFromDate(System.currentTimeMillis());
        dao.addParameter(listParam);
    }

    @Test
    public void doListTest() throws Exception {
        List<String> retList = config.getParameterList(LIST_PARAMETER);
        assert retList != null;
        assert values.equals(retList);
    }

    @Test(dependsOnMethods = "doListTest")
    public void doSetTest() throws Exception {
        Set<String> retSet = config.getParameterSet(LIST_PARAMETER);
        assert retSet != null;
        assert new HashSet<String>(values).equals(retSet);
    }

    @AfterClass
    public void cleanUp() throws Exception {
        dao.removeParameter(OWNER, LIST_PARAMETER);
    }
}
