/**
 * org.iana.rzm.trans.test.common.HibernateMappingUnitTest
 * (C) NASK 2006
 * jakubl, 2007-02-28 16:25:40
 */
package org.iana.rzm.trans.test.common;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
abstract public class HibernateMappingUnitTest<T> extends HibernateTest {

    abstract protected T create() throws Exception;
    abstract protected T change(T o) throws Exception;
    abstract protected Serializable getId(T o);

    protected void test() throws Exception {
        begin();
        T object = create();
        session.save(object);
        closeTx();

        beginTx();
        T object1 = (T) session.get(object.getClass(), getId(object));
        assert object.equals(object1) : object.getClass() + " creation test failed";
        closeTx();

        beginTx();
        T object2 = (T) session.get(object.getClass(), getId(object));
        object2 = change(object2);
        session.update(object2);
        closeTx();

        beginTx();
        T object3 = (T) session.get(object.getClass(), getId(object));
        assert object2.equals(object3) : object.getClass() + " modification test failed";
        closeTx();

        beginTx();
        session.delete(object);
        closeTx();

        beginTx();
        T object4 = (T) session.get(object.getClass(), getId(object));
        assert object4 == null : object.getClass() + " deletion test failed";
        close();
    }
}
