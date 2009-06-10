package org.iana.rzm.common.collections;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Predicate<T> {

    boolean isTrue(T object);

}
