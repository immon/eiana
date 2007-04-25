package org.iana.objectdiff;

import org.iana.rzm.common.validators.CheckTool;

import java.util.*;

import pl.nask.util.ReflectionTool;

/**
 * This class detects changes between two entities.
 *
 * @author Patrycja Wegrzynowicz
 */
@SuppressWarnings("unchecked")
public class ChangeDetector {

    /**
     * Determines a difference between source and destination objects.
     *
     * @param src the source object
     * @param dst the destination object
     * @param config the configuration for diff
     * @return a change that needs to be applied to the source object to make it equal the destination object,
     * or null if the objects are equal.
     */
    static public Change diff(Object src, Object dst, DiffConfiguration config) {
        CheckTool.checkNull(config, "diff configuration");

        if (src == null && dst == null) {
            return null;
        }

        if (src != null && dst != null &&
            !src.getClass().isAssignableFrom(dst.getClass()) &&
            !dst.getClass().isAssignableFrom(src.getClass())) {
            throw new IllegalArgumentException("src and dst objects need to be of the same type");
        }

        Class clazz = getClass(src, dst);

        // if simple, use equal and construct an appropriate change
        if (config.isSimple(clazz)) {
            return diffSimple(src, dst);
        }

        // if collection, diff collection
        if (Collection.class.isAssignableFrom(clazz)) {
            return diffCollection((Collection) src, (Collection) dst, config);
        }

        // otherwise, diff objects' fields
        return diffObjects(src, dst, config, clazz);
    }

    static private Change diffSimple(Object src, Object dst) {
        if (src == null || dst == null) return new SimpleChange(src, dst);
        return src.equals(dst) ? null : new SimpleChange(src, dst);
    }

    static private Change diffCollection(Collection src, Collection dst, DiffConfiguration config) {
        boolean srcEmpty = src == null || src.isEmpty();
        boolean dstEmpty = dst == null || dst.isEmpty();
        CollectionChange ret = null;
        if (!srcEmpty || !dstEmpty) {
            if (srcEmpty) {
                CheckTool.checkCollectionNull(dst, "dst collection");
                ret = new CollectionChange(Change.Type.ADDITION);
                ret.addAdded(diffAdded(dst, config));
            } else if (dstEmpty) {
                CheckTool.checkCollectionNull(src, "src collection");
                ret = new CollectionChange(Change.Type.REMOVAL);
                ret.addRemoved(diffRemoved(src, config));
            } else {
                CheckTool.checkCollectionNull(src, "src collection");
                CheckTool.checkCollectionNull(dst, "dst collection");
                ret = new CollectionChange(Change.Type.UPDATE);
                Class clazz = src.iterator().next().getClass();
                if (config.isSimple(clazz)) {
                    diffSimpleCollection(src, dst, config, ret);
                } else if (config.isObject(clazz)) {
                    diffObjectCollection(src, dst, config, clazz, ret);
                }
           }
        }
        return ret == null || ret.isEmpty() ? null : ret;
    }

    static private List<Change> diffAdded(Collection c, DiffConfiguration config) {
        List<Change> ret = new ArrayList<Change>();
        for (Object object : c) {
            Change change = diff(null, object, config);
            if (change != null) ret.add(change);
        }
        return ret;
    }

    static private List<Change> diffRemoved(Collection c, DiffConfiguration config) {
        List<Change> ret = new ArrayList<Change>();
        for (Object object : c) {
            Change change = diff(object, null, config);
            if (change != null) ret.add(change);
        }
        return ret;
    }

    static private void diffSimpleCollection(Collection src, Collection dst, DiffConfiguration config, CollectionChange ret) {
        Collection added = new HashSet(dst);
        added.removeAll(src);
        ret.addAdded(diffAdded(added, config));

        Collection removed = new HashSet(src);
        removed.removeAll(dst);
        ret.addRemoved(diffRemoved(removed, config));
    }

    static private void diffObjectCollection(Collection src, Collection dst, DiffConfiguration config, Class clazz, CollectionChange ret) {
        ObjectConfiguration objectConfig = config.getObjectConfig(clazz);
        Map<String, Object> srcMap = initObjectMap(src, objectConfig.getFieldId());
        Map<String, Object> dstMap = initObjectMap(dst, objectConfig.getFieldId());

        Collection<String> addedIds = new HashSet(dstMap.keySet());
        addedIds.removeAll(srcMap.keySet());
        List added = new ArrayList();
        for (String id : addedIds) added.add(dstMap.get(id));
        ret.addAdded(diffAdded(added, config));

        Collection<String> removedIds = new HashSet(srcMap.keySet());
        removedIds.removeAll(dstMap.keySet());
        List removed = new ArrayList();
        for (String id : removedIds) removed.add(srcMap.get(id));
        ret.addRemoved(diffRemoved(removed, config));

        Collection<String> commonIds = new HashSet(srcMap.keySet());
        commonIds.retainAll(dstMap.keySet());
        for (String id : commonIds) {
            Change change = diff(srcMap.get(id), dstMap.get(id), config);
            if (change != null) ret.addModified(change);
        }
    }

    static private Map<String, Object> initObjectMap(Collection c, String fieldId) {
        Map<String, Object> ret = new HashMap<String, Object>();
        for (Object o : c) {
            String id = getId(o, fieldId);
            ret.put(id, o);
        }
        return ret;
    }

    static private Change diffObjects(Object src, Object dst, DiffConfiguration config, Class clazz) {
        ObjectConfiguration objectConfig = config.getObjectConfig(clazz);
        if (objectConfig != null) {
            List<String> fields = objectConfig.getFields();
            if (fields != null && !fields.isEmpty()) {
                Map<String, Change> changes = new HashMap<String, Change>();
                for (String field : fields) {
                    Object srcVar = getVariable(src, field);
                    Object dstVar = getVariable(dst, field);
                    Change change = diff(srcVar, dstVar, config);
                    if (change != null) changes.put(field, change);
                }
                if (!changes.isEmpty()) {
                    Change.Type type = null;
                    String id = null;
                    if (src != null && dst != null) {
                        type = Change.Type.UPDATE;
                        id = getId(src, objectConfig.getFieldId());
                    } else if (src == null) {
                        type = Change.Type.ADDITION;
                        id = getId(dst, objectConfig.getFieldId());
                    } else {
                        type = Change.Type.REMOVAL;
                        id = getId(src, objectConfig.getFieldId());
                    }
                    return new ObjectChange(type, id, changes);
                }
            }
        }
        return null;
    }

    static String getId(Object dst, String fieldId) {
        if (dst == null) return null;
        Object id = getVariable(dst, fieldId);
        return id == null ? null : id.toString();
    }

    static private Object getVariable(Object object, String field) {
        try {
            return object == null ? null : ReflectionTool.invokeGetter(object, field);
        } catch (Exception e) {
            return null;
        }
    }

    static private Class getClass(Object src, Object dst) {
        if (src == null && dst == null) return null;
        return src != null ? src.getClass() : dst.getClass();
    }
}
