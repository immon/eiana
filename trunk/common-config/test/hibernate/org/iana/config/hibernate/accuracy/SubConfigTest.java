package org.iana.config.hibernate.accuracy;

import org.iana.config.ConfigDAO;
import org.iana.config.conf.SpringConfigApplicationContext;
import org.iana.config.impl.*;
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
public class SubConfigTest extends TransactionalSpringContextTests {

    private static final String OWNER = "me";

    protected ConfigDAO hibernateConfigDAO;

    public SubConfigTest() {
        super(SpringConfigApplicationContext.CONFIG_FILE_NAME);
    }

    public void init() {
    }

    @Test
    public void testSubConfig() throws ConfigException {
        SingleParameter singleParam = new SingleParameter("sub1.sub2.param1", Boolean.toString(true));
        singleParam.setOwner(OWNER);
        singleParam.setFromDate(System.currentTimeMillis() - 100000);
        singleParam.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(singleParam);

        List<String> values = new ArrayList<String>();
        values.add("20");
        values.add("80");

        ListParameter listParam = new ListParameter("sub1.sub2.sub3.param4", values);
        listParam.setOwner(OWNER);
        listParam.setFromDate(System.currentTimeMillis() - 100000);
        listParam.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(listParam);

        OwnedConfig ownedConfig = new OwnedConfig(OWNER, hibernateConfigDAO);
        Boolean retValue = ownedConfig.getSubConfig("sub1").getSubConfig("sub2").getBooleanParameter("param1");
        assert retValue;

        List<String> retValues = ownedConfig.getSubConfig("sub1").getSubConfig("sub2").getSubConfig("sub3").getParameterList("param4");
        assert values.equals(retValues);

    }

    @Test(dependsOnMethods = "testSubConfig")
    public void testParameterNames() throws ConfigException {
        SingleParameter singleParam = new SingleParameter("sub1.sub2_1.param13", Boolean.toString(true));
        singleParam.setOwner(OWNER);
        singleParam.setFromDate(System.currentTimeMillis() - 100000);
        singleParam.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(singleParam);

        SingleParameter singleParam1 = new SingleParameter("param11", Boolean.toString(true));
        singleParam1.setOwner(OWNER);
        singleParam1.setFromDate(System.currentTimeMillis() - 100000);
        singleParam1.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(singleParam1);

        OwnedConfig ownedConfig = new OwnedConfig(OWNER, hibernateConfigDAO);

        Set<String> retNames = ownedConfig.getParameterNames();
        assert retNames != null && retNames.size() == 1 && retNames.contains("param11");

        retNames = ownedConfig.getSubConfig("sub1").getSubConfig("sub2_1").getParameterNames();
        assert retNames != null && retNames.size() == 1 && retNames.contains("param13");
    }

    @Test(dependsOnMethods = "testParameterNames")
    public void testSubConfigNames() throws ConfigException {
        Set<String> values = new HashSet<String>();
        values.add("firstValue");
        values.add("secondValue");
        SetParameter setParameter = new SetParameter("sub1.param2", values);
        setParameter.setOwner(OWNER);
        setParameter.setFromDate(System.currentTimeMillis() - 100000);
        setParameter.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(setParameter);

        OwnedConfig ownedConfig = new OwnedConfig(OWNER, hibernateConfigDAO);
        Set<String> retSubConfigNames = ownedConfig.getSubConfig("sub1").getSubConfigNames();

        Set<String> subConfigNames = new HashSet<String>();
        subConfigNames.add("sub2");
        subConfigNames.add("sub2_1");
        assert subConfigNames.equals(retSubConfigNames);

        retSubConfigNames = ownedConfig.getSubConfigNames();
        assert retSubConfigNames != null && retSubConfigNames.size() == 1 && retSubConfigNames.contains("sub1");
    }

    @Test(dependsOnMethods = "testSubConfigNames", alwaysRun = true)
    public void testRemoveNewParams() throws ConfigException {
        hibernateConfigDAO.removeParameter(OWNER, "sub1.sub2.param1");
        hibernateConfigDAO.removeParameter(OWNER, "sub1.param2");
        hibernateConfigDAO.removeParameter(OWNER, "sub1.sub2.sub3.param4");
        hibernateConfigDAO.removeParameter(OWNER, "sub1.sub2_1.param13");
        hibernateConfigDAO.removeParameter(OWNER, "param11");
    }

    public void cleanUp() {
    }
}