package org.iana.rzm.domain.hibernate.test.stress;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.hibernate.test.common.HibernateMappingTestUtil;
import org.iana.rzm.domain.hibernate.test.common.HibernateOperationStressTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"hibernate", "eiana-domains","stress", "eiana-domains-stress-create"})
public class DomainCreationHibernateTest extends HibernateOperationStressTest {
    private String prefix;
    protected void operation(Object o) throws Exception {
        session.save(HibernateMappingTestUtil.setupDomain(new Domain((String) o)));
    }


    protected List getList() throws Exception {
        List result = new ArrayList();
        for (int i = 0; i < 100; i++) result.add("iana" + prefix + i + ".org");
        return result;
    }

    @Test
    public void oneTransaction() throws Exception {
        prefix = "OT";
        super.oneTransaction();
    }

    @Test(dependsOnMethods = {"oneTransaction"})
    public void manyTransactions() throws Exception {
        prefix = "MT";
        super.manyTransactions();
    }

    @Test(dependsOnMethods = {"manyTransactions"})
    public void manySessions() throws Exception {
        prefix = "MS";
        super.manySessions();
    }
}
