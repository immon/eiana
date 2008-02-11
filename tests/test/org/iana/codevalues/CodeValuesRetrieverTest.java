package org.iana.codevalues;

import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.iana.rzm.conf.SpringApplicationContext;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true)
public class CodeValuesRetrieverTest {

    private CodeValuesRetriever retriever;
    private CodeValuesManager dao;

    @BeforeClass
    public void init() {
        ApplicationContext ctx = SpringApplicationContext.getInstance().getContext();
        retriever = (CodeValuesRetriever) ctx.getBean("cachedCodeValues");
        dao = (CodeValuesManager) ctx.getBean("codeValues");
        dao.create(createCode(1, 1));
        dao.create(createCode(2, 2));
    }

    @Test
    public void testValuesRetrieval1() {
        List<Value> values = retriever.getCodeValues("code1");
        assert values.size() == 1;
    }

    @Test
    public void testValuesRetrieval2() {
        List<Value> values = retriever.getCodeValues("code2");
        assert values.size() == 2;
    }

    @Test(dependsOnMethods={"testValuesRetrieval1"})
    public void testCachedRetrieval1() {
        Code code = findByCode("code1");
        dao.update(createCode(1, 5, code.getObjId()));
        List<Value> notCachedValues = dao.getCodeValues("code1");
        assert notCachedValues.size() == 5;
        List<Value> cachedValues = retriever.getCodeValues("code1");
        assert cachedValues.size() == 1;
    }

    @AfterClass (alwaysRun = true)
    public void cleanup() {
        for (String codeId : new String[]{"code1", "code2"}) {
            Code code = findByCode(codeId);
            dao.delete(code);            
        }
    }

    private Code createCode(int nr, int vals) {
        return createCode(nr, vals, null);    
    }

    private Code createCode(int nr, int vals, Long id) {
        List<Value> values = new ArrayList<Value>();
        for (int i = 0; i < vals; ++i) {
            values.add(new Value("v"+nr+"-"+i, "value"+nr+"-"+i));
        }
        return new Code(id, "code"+nr,values);
    }

    private Code findByCode(String code) {
        Criterion codeCriterion = new Equal("code", code);
        return dao.find(codeCriterion).get(0);
    }
}
