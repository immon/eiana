package org.iana.dao.hibernate;

import org.testng.annotations.Test;
import org.iana.criteria.*;

import java.util.*;
import java.sql.Timestamp;

/**
 * HQLGenerator
 * @author Patrycja Wegrzynowicz
 */
@Test
public class HQLGeneratorTest {

    public void testNoWhere() {
        HQLBuffer buf = HQLGenerator.from(A.class, null);
        assert "from org.iana.dao.hibernate.A".equals(buf.getHQL().trim());
        assert buf.getParams().length == 0;
    }

    public void testEqual() {
        HQLBuffer buf = HQLGenerator.from(A.class, new Equal("astr", "value"));
        assert "from org.iana.dao.hibernate.A where astr = ?".equals(buf.getHQL().trim());
        assert buf.getParams().length == 1;
    }

    public void testGreater() {
        HQLBuffer buf = HQLGenerator.from(A.class, new Greater("astr", "value"));
        assert "from org.iana.dao.hibernate.A where astr > ?".equals(buf.getHQL().trim());
        assert buf.getParams().length == 1;
    }

    public void testGreaterEqual() {
        HQLBuffer buf = HQLGenerator.from(A.class, new GreaterEqual("astr", "value"));
        assert "from org.iana.dao.hibernate.A where astr >= ?".equals(buf.getHQL().trim());
        assert buf.getParams().length == 1;
    }

    public void testLower() {
        HQLBuffer buf = HQLGenerator.from(A.class, new Lower("astr", "value"));
        assert "from org.iana.dao.hibernate.A where astr < ?".equals(buf.getHQL().trim());
        assert buf.getParams().length == 1;
    }

    public void testLowerEqual() {
        HQLBuffer buf = HQLGenerator.from(A.class, new LowerEqual("astr", "value"));
        assert "from org.iana.dao.hibernate.A where astr <= ?".equals(buf.getHQL().trim());
        assert buf.getParams().length == 1;
    }

    public void testLike() {
        HQLBuffer buf = HQLGenerator.from(A.class, new Like("astr", "value"));
        assert "from org.iana.dao.hibernate.A where astr like ?".equals(buf.getHQL().trim());
        assert buf.getParams().length == 1;
    }

    public void testIn() {
        Set<Object> values = new TreeSet<Object>();
        values.add("val1"); values.add("val2");
        HQLBuffer buf = HQLGenerator.from(A.class, new In("astr", values));
        assert "from org.iana.dao.hibernate.A where astr in (?,?)".equals(buf.getHQL().trim());
        assert buf.getParams().length == 2;
    }

    public void testIsNull() {
        HQLBuffer buf = HQLGenerator.from(A.class, new IsNull("astr"));
        assert "from org.iana.dao.hibernate.A where astr is null".equals(buf.getHQL().trim());
        assert buf.getParams().length == 0;
    }

    public void testNot() {
        HQLBuffer buf = HQLGenerator.from(A.class, new Not(new IsNull("astr")));
        System.out.println(buf.getHQL());
        assert "from org.iana.dao.hibernate.A where not (astr is null )".equals(buf.getHQL().trim());
        assert buf.getParams().length == 0;
    }

    public void testAnd() {
        List<Criterion> args = new ArrayList<Criterion>();
        args.add(new IsNull("astr"));
        args.add(new Equal("atmsp", new Timestamp(System.currentTimeMillis())));
        HQLBuffer buf = HQLGenerator.from(A.class, new And(args));
        System.out.println(buf.getHQL());
        assert "from org.iana.dao.hibernate.A where (astr is null ) and (atmsp = ? )".equals(buf.getHQL().trim());
        assert buf.getParams().length == 1;
    }

    public void testOr() {
        List<Criterion> args = new ArrayList<Criterion>();
        args.add(new IsNull("astr"));
        args.add(new Equal("atmsp", new Timestamp(System.currentTimeMillis())));
        HQLBuffer buf = HQLGenerator.from(A.class, new Or(args));
        System.out.println(buf.getHQL());
        assert "from org.iana.dao.hibernate.A where (astr is null ) or (atmsp = ? )".equals(buf.getHQL().trim());
        assert buf.getParams().length == 1;
    }

    public void testSortSingleColumnAscNoWhere() {
        SortCriterion crit = new SortCriterion(null, new Order("astr", true));
        HQLBuffer buf = HQLGenerator.from(A.class, crit);
        assert "from org.iana.dao.hibernate.A order by astr asc".equals(buf.getHQL().trim());
        assert buf.getParams().length == 0;
    }

    public void testSortSingleColumnDescNoWhere() {
        SortCriterion crit = new SortCriterion(null, new Order("astr", false));
        HQLBuffer buf = HQLGenerator.from(A.class, crit);
        assert "from org.iana.dao.hibernate.A order by astr desc".equals(buf.getHQL().trim());
        assert buf.getParams().length == 0;
    }

    public void testSortMultipleColumnAscDescNoWhere() {
        SortCriterion crit = new SortCriterion(null, Arrays.asList(new Order("astr", true), new Order("atmsp", false)));
        HQLBuffer buf = HQLGenerator.from(A.class, crit);
        assert "from org.iana.dao.hibernate.A order by astr asc ,atmsp desc".equals(buf.getHQL().trim());
        assert buf.getParams().length == 0;
    }

    public void testSortSingleColumnAscWhere() {
        SortCriterion crit = new SortCriterion(new Equal("astr", "x"), new Order("astr", true));
        HQLBuffer buf = HQLGenerator.from(A.class, crit);
        assert "from org.iana.dao.hibernate.A where astr = ?   order by  ,astr asc".equals(buf.getHQL().trim());
        assert buf.getParams().length == 1;
    }

    public void testSortSingleColumnDescWhere() {
        SortCriterion crit = new SortCriterion(new Equal("astr", "x"), new Order("astr", false));
        HQLBuffer buf = HQLGenerator.from(A.class, crit);
        assert "from org.iana.dao.hibernate.A where astr = ?   order by  ,astr desc".equals(buf.getHQL().trim());
        assert buf.getParams().length == 1;
    }

    public void testSortMultipleColumnAscDescWhere() {
        SortCriterion crit = new SortCriterion(new Equal("astr", "x"), Arrays.asList(new Order("astr", true), new Order("atmsp", false)));
        HQLBuffer buf = HQLGenerator.from(A.class, crit);
        assert "from org.iana.dao.hibernate.A where astr = ?   order by  ,astr asc ,atmsp desc".equals(buf.getHQL().trim());
        assert buf.getParams().length == 1;
    }
}
