package org.iana.config;

import org.iana.config.impl.*;
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
public class SubConfigTest extends RollbackableSpringContextTest {

    private static final String OWNER = "me";

    protected ConfigDAO hibernateConfigDAO;
    protected ParameterManager parameterManager;

    public SubConfigTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    public void init() throws ConfigException {
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

        SingleParameter subSingleParam = new SingleParameter("sub1.sub2_1.param13", Boolean.toString(true));
        subSingleParam.setOwner(OWNER);
        subSingleParam.setFromDate(System.currentTimeMillis() - 100000);
        subSingleParam.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(subSingleParam);

        subSingleParam = new SingleParameter("sub1.sub2_1.param14", Boolean.toString(true));
        subSingleParam.setOwner(OWNER);
        subSingleParam.setFromDate(System.currentTimeMillis() - 100000);
        subSingleParam.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(subSingleParam);

        SingleParameter subSingleParamAnotherUser = new SingleParameter("sub1.sub2_1.param15", Boolean.toString(true));
        subSingleParamAnotherUser.setOwner("Another_" + OWNER);
        subSingleParamAnotherUser.setFromDate(System.currentTimeMillis() - 100000);
        subSingleParamAnotherUser.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(subSingleParam);


        SingleParameter singleParam1 = new SingleParameter("param11", Boolean.toString(true));
        singleParam1.setOwner(OWNER);
        singleParam1.setFromDate(System.currentTimeMillis() - 100000);
        singleParam1.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(singleParam1);

        Set<String> setValues = new HashSet<String>();
        setValues.add("firstValue");
        setValues.add("secondValue");

        SetParameter setParameter = new SetParameter("sub1.param2", setValues);
        setParameter.setOwner(OWNER);
        setParameter.setFromDate(System.currentTimeMillis() - 100000);
        setParameter.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(setParameter);


    }

    @Test
    public void testSubConfig() throws ConfigException {
        List<String> values = new ArrayList<String>();
        values.add("20");
        values.add("80");

        OwnedConfig ownedConfig = new OwnedConfig(OWNER, parameterManager);
        Boolean retValue = ownedConfig.getSubConfig("sub1").getSubConfig("sub2").getBooleanParameter("param1");
        assert retValue;

        List<String> retValues = ownedConfig.getSubConfig("sub1").getSubConfig("sub2").getSubConfig("sub3").getParameterList("param4");
        assert values.equals(retValues);

    }

    @Test
    public void testParameterNames() throws ConfigException {
        OwnedConfig ownedConfig = new OwnedConfig(OWNER, parameterManager);

        Set<String> retNames = ownedConfig.getParameterNames();
        assert retNames != null && retNames.size() == 6;

        retNames = ownedConfig.getSubConfig("sub1").getParameterNames();
        assert retNames != null && retNames.size() == 5;

        retNames = ownedConfig.getSubConfig("sub1").getSubConfig("sub2").getParameterNames();
        assert retNames != null && retNames.size() == 2;

        retNames = ownedConfig.getSubConfig("sub1").getSubConfig("sub2_1").getParameterNames();
        assert retNames != null && retNames.size() == 2;
    }

    @Test
    public void testSubConfigNames() throws ConfigException {
        OwnedConfig ownedConfig = new OwnedConfig(OWNER, parameterManager);
        Set<String> retSubConfigNames = ownedConfig.getSubConfig("sub1").getSubConfigNames();

        Set<String> subConfigNames = new HashSet<String>();
        subConfigNames.add("sub2");
        subConfigNames.add("sub2_1");
        assert subConfigNames.equals(retSubConfigNames);

        retSubConfigNames = ownedConfig.getSubConfigNames();
        assert retSubConfigNames != null && retSubConfigNames.size() == 1 && retSubConfigNames.contains("sub1");
    }

    public void cleanUp() {
    }
}