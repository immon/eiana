package org.iana.rzm.common.validators;

import java.util.Collection;

/**
 * @author Patrycja Wegrzynowicz
 */
public class CheckTool {

    final public static void checkNull(Object o, String field) {
        if (o == null) throw new IllegalArgumentException(field);
    }

    final public static void checkEmpty(String s, String field) {
        if (s == null || s.trim().length() == 0) throw new IllegalArgumentException(field);
    }

    final public static <T> void checkCollectionNull(Collection<T> collection, String field) {
        checkNull(collection, field);
        for (T object : collection) checkNull(object, field);
    }

    final public static <T> void addAllNoDup(Collection<T> dst, Collection<T> src) {
        for (T object : src) {
            addNoDup(dst, object);
        }
    }

    final public static <T> void addNoDup(Collection<T> dst, T object) {
        if (!dst.contains(object)) dst.add(object);
    }
}
