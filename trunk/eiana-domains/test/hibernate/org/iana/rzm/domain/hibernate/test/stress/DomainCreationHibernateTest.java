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
public class DomainCreationHibernateTest extends HibernateOperationStressTest {
    protected void operation(Object o) throws Exception {
        session.save(HibernateMappingTestUtil.setupDomain(new Domain((String) o)));
    }


    protected List getList() throws Exception {
        List result = new ArrayList();
        for (int i = 0; i < 1000; i++) result.add("iana" + i + ".org");
        return result;
    }

    @Test
    public void oneTransaction() throws Exception {
        super.oneTransaction();
    }

    @Test
    public void manyTransactions() throws Exception {
        super.manyTransactions();
    }

    @Test
    public void manySessions() throws Exception {
        super.manySessions();
    }
}
