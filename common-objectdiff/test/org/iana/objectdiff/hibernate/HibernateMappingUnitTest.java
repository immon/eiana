package org.iana.rzm.trans.hibernate.test.common;

import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */

@Test(groups = {"hibernate", "common-objectdiff"})
abstract public class HibernateMappingUnitTest<T> extends HibernateTest {

    abstract protected T create() throws Exception;

    abstract protected T change(T o) throws Exception;

    abstract protected Serializable getId(T o);

    protected void test() throws Exception {
        begin();
        T object = null;
        try {
            object = create();
            session.save(object);
        } finally {
            closeTx();
        }

        beginTx();
        try {
            T object1 = (T) session.get(object.getClass(), getId(object));
            assert object.equals(object1) : object.getClass() + " creation test failed";
        } finally {
            closeTx();
        }

        beginTx();
        T object2 = null;
        try {
            object2 = (T) session.get(object.getClass(), getId(object));
            object2 = change(object2);
            session.update(object2);
        } finally {
            closeTx();
        }

        beginTx();
        try {
            T object3 = (T) session.get(object.getClass(), getId(object));
            assert object2.equals(object3) : object.getClass() + " modification test failed";
        } finally {
            closeTx();
        }

        beginTx();
        try {
            session.delete(object);
        } finally {
            closeTx();
        }

        beginTx();
        try {
            T object4 = (T) session.get(object.getClass(), getId(object));
            assert object4 == null : object.getClass() + " deletion test failed";
        } finally {
            close();
        }
    }
}
