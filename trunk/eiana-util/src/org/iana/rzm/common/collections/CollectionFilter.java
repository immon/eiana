package org.iana.rzm.common.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Patrycja Wegrzynowicz
 */
public class CollectionFilter {

    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> ret = new ArrayList<T>();
        for (T object : list) {
            if (predicate.isTrue(object)) ret.add(object);
        }
        return ret;
    }

    public static <T> Set<T> filter(Set<T> set, Predicate<T> predicate) {
        Set<T> ret = new HashSet<T>();
        for (T object : set) {
            if (predicate.isTrue(object)) ret.add(object);
        }
        return ret;
    }
    
    public static <T> T first(List<T> list, Predicate<T> predicate) {
        for (T object : list) {
            if (predicate.isTrue(object)) return object;
        }
        return null;
    }

    public static <T> T first(Set<T> set, Predicate<T> predicate) {
        for (T object : set) {
            if (predicate.isTrue(object)) return object;
        }
        return null;
    }

}
