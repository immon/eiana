package org.iana.config.hibernate.accuracy;

import org.iana.config.ConfigDAO;
import org.iana.config.OwnedConfig;
import org.iana.config.SetParameter;
import org.iana.config.SingleParameter;
import org.iana.config.conf.SpringConfigApplicationContext;
import org.iana.test.spring.TransactionalSpringContextTests;
import org.testng.annotations.Test;

import java.util.HashSet;
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
    public void testSubConfig() {
        SingleParameter singleParam = new SingleParameter("sub1.sub2.param1", Boolean.toString(true));
        singleParam.setOwner(OWNER);
        singleParam.setFromDate(System.currentTimeMillis() - 100000);
        singleParam.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(singleParam);

        OwnedConfig ownedConfig = new OwnedConfig(OWNER, hibernateConfigDAO);
        Boolean retValue = ownedConfig.getSubConfig("sub1").getSubConfig("sub2").getBooleanParameter("param1");
        assert retValue;
    }

    @Test(dependsOnMethods = "testSubConfig")
    public void testSubConfigNames() {
        Set<String> values = new HashSet<String>();
        values.add("firstValue");
        values.add("secondValue");
        SetParameter setParameter = new SetParameter("sub1.param2", values);
        setParameter.setOwner(OWNER);
        setParameter.setFromDate(System.currentTimeMillis() - 100000);
        setParameter.setToDate(System.currentTimeMillis() + 100000);
        hibernateConfigDAO.addParameter(setParameter);

        OwnedConfig ownedConfig = new OwnedConfig(OWNER, hibernateConfigDAO);
        Set<String> retSubConfigNames = ownedConfig.getSubConfigNames();

        Set<String> subConfigNames = new HashSet<String>();
        subConfigNames.add("sub1.sub2");
        subConfigNames.add("sub1");
        assert subConfigNames.equals(retSubConfigNames);
    }

    @Test(dependsOnMethods = "testSubConfigNames")
    public void testRemoveNewParams() {
        hibernateConfigDAO.removeParameter(OWNER, "sub1.sub2.param1");
        hibernateConfigDAO.removeParameter(OWNER, "sub1.param2");
    }

    public void cleanUp() {
    }
}