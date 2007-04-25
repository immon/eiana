package org.iana.objectdiff;

import pl.nask.util.ReflectionTool;
import pl.nask.util.StringTool;

import java.util.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * todo: check if old value == current value
 *
 * @author Patrycja Wegrzynowicz
 */
public class ChangeApplicator {

    static public void applyChange(final Object object, final ObjectChange change, final DiffConfiguration config) {
        if (change.isAddition() || change.isModification()) {
            Map<String, Change> fieldChanges = change.getFieldChanges();
            for (final String field : fieldChanges.keySet()) {
                Change fieldChange = fieldChanges.get(field);
                fieldChange.accept(new ChangeVisitor() {
                    public void visitCollection(CollectionChange change) {
                        setCollection(object, field, change, config);
                    }

                    public void visitObject(ObjectChange change) {
                        setObject(object, field, change, config);
                    }

                    public void visitSimple(SimpleChange change) {
                        setField(object, field, change.getNewValue());
                    }
                });
            }
        }
    }

    static private void setField(Object object, String field, Object value) {
        Class clazz = object.getClass();
        String methodName = "set" + StringTool.toFirstUpperCase(field);
        Method method = null;
        if (value != null) {
            try {
                method = clazz.getMethod(methodName, value.getClass());
            } catch (NoSuchMethodException e) {
            }
        }
        if (method == null) {
            for (Method m : clazz.getMethods()) {
                if (methodName.equals(m.getName())) {
                    method = m;
                    break;
                }
            }
        }
        try {
            if (method != null) method.invoke(object, new Object[]{value});
            else throw new RuntimeException("cannot update " + field);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("cannot update " + field);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("cannot update " + field);
        }
    }

    static private Object getVariable(Object object, String field) {
        try {
            return ReflectionTool.invokeGetter(object, field);
        } catch (Exception e) {
            throw new RuntimeException("cannot get " + field, e);
        }
    }

    static private void setObject(Object object, String field, ObjectChange change, DiffConfiguration config) {
        try {
            if (change.isRemoval()) {
                setField(object, field, null);
            } else if (change.isAddition()) {
                Object value = createObject(config, object, field);
                if (value != null) {
                    setField(object, field, value);
                    applyChange(value, change, config);
                }
            } else if (change.isModification()) {
                Object value = getVariable(object, field);
                applyChange(value, change, config);
            }
        } catch (Exception e) {
            throw new RuntimeException("cannot set object " + field, e);
        }
    }

    private static Object createObject(DiffConfiguration config, Object object, String field) throws InstantiationException, IllegalAccessException {
        ObjectConfiguration objectConfig = config.getObjectConfig(object.getClass());
        if (objectConfig != null) {
            Class clazz = objectConfig.getFieldClass(field);
            if (clazz != null) {
                return clazz.newInstance();
            }
        }
        return null;
    }

    static private void setCollection(Object object, String field, CollectionChange change, DiffConfiguration config) {
        try {
            Map<String, ObjectChange> toMod = new HashMap<String, ObjectChange>();
            for (Change mod : change.getModified()) {
                if (mod instanceof ObjectChange) {
                    ObjectChange objectMod = (ObjectChange) mod;
                    toMod.put(objectMod.getId(), objectMod);
                }
            }

            List toAdd = new ArrayList();
            Class clazz = config.getObjectConfig(object.getClass()).getFieldClass(field);
            for (Change add : change.getAdded()) {
                if (add instanceof SimpleChange) {
                    SimpleChange simpleAdd = (SimpleChange) add;
                    toAdd.add(simpleAdd.getNewValue());
                } else if (add instanceof ObjectChange) {
                    Object objectToAdd = clazz.newInstance();
                    applyChange(objectToAdd, (ObjectChange) add, config);
                    toAdd.add(objectToAdd);
                }
            }

            Set<String> toRemove = new HashSet<String>();
            for (Change rem : change.getRemoved()) {
                if (rem instanceof SimpleChange) {
                    SimpleChange simpleRem = (SimpleChange) rem;
                    toRemove.add(simpleRem.getOldValue());
                } else if (rem instanceof ObjectChange) {
                    ObjectChange objectRem = (ObjectChange) rem;
                    toRemove.add(objectRem.getId());
                }
            }

            List list = new ArrayList();
            Collection coll = (Collection) getVariable(object, field);
            for (Object o : coll) {
                if (o != null) {
                    if (config.isSimple(o.getClass())) {
                        if (!toRemove.contains(o)) list.add(o);
                    } else if (config.isObject(o.getClass())) {
                        ObjectConfiguration objectConfig = config.getObjectConfig(o.getClass());
                        String id = ChangeDetector.getId(o, objectConfig.getFieldId());
                        if (toMod.containsKey(id)) {
                            ObjectChange objectMod = toMod.get(id);
                            applyChange(o, objectMod, config);
                        }
                        if (!toRemove.contains(id)) list.add(o);
                    }
                }
            }
            list.addAll(toAdd);
            setField(object, field, list);
        } catch (Exception e) {
            throw new RuntimeException("cannot set collection " + field, e);
        }
    }


}
