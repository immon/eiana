package org.iana.objectdiff;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface ObjectInstantiator {

    public Object instantiate(Change change);
}
