package org.iana.objectdiff;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ClassBasedInstantiator implements ObjectInstantiator {

    private Class clazz;

    public ClassBasedInstantiator(Class clazz) {
        this.clazz = clazz;
    }

    public Object instantiate(Change change) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("cannot instantiate class " + clazz.getName(), e);
        }
    }
}
