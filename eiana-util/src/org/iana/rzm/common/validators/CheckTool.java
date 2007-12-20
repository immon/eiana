package org.iana.rzm.common.validators;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * <p/>
 * A simple helper tool that validates given objects against null or empty values.
 * In case of nulliness or emptiness an IllegalArgumentException is raised.
 * </p>
 *
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

    final public static <T> void checkCollectionEmpty(Collection<T> collection, String field) {
        checkNull(collection, field);
        if (collection.isEmpty()) throw new IllegalArgumentException(field);
    }

    final public static <T> void checkCollectionNullOrEmpty(Collection<T> collection, String field) {
        checkCollectionNull(collection, field);
        checkCollectionEmpty(collection, field);
    }

    final public static <T> void addAllNoDup(Collection<T> dst, Collection<T> src) {
        for (T object : src) {
            addNoDup(dst, object);
        }
    }

    final public static <T> void addNoDup(Collection<T> dst, T object) {
        if (!dst.contains(object)) dst.add(object);
    }

    final public static void checkNoNegative(long value, String field) {
        if (value < 0) throw new IllegalArgumentException(field);
    }

    final public static boolean isCorrectEmali(String email) {
        if (email == null || email.trim().length() == 0) return false;
        return Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+", email);
    }
}
