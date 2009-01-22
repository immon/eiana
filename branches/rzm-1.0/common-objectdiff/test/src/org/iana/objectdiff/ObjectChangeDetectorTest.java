package org.iana.objectdiff;

import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(groups = {"change", "eiana-trans"})
public class ObjectChangeDetectorTest {

    private DiffConfiguration config = new TestDiffConfiguration();

    @Test
    public void testObjectAEqual() {
        ObjectA object = new ObjectA("a", "str1", Boolean.TRUE);
        Change change = ChangeDetector.diff(object, object, config);
        assert change == null;
    }
    
    @Test
    public void testObjectAEqual_IgnoreWhitespace() {
        ObjectA object1 = new ObjectA("a\ra", "str1\rx", Boolean.TRUE);
        ObjectA object2 = new ObjectA(" a  a", "str1\nx", Boolean.TRUE);
        Change change = ChangeDetector.diff(object1, object2, config);
        assert change == null;
    }

    @Test
    public void testObjectAAddition() {
        ObjectA object = new ObjectA("a", "str1", Boolean.TRUE);
        Change change = ChangeDetector.diff(null, object, config);
        assert change != null && change.isAddition();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> fieldChanges = objectChange.getFieldChanges();
        assert fieldChanges.size() == 3;

        SimpleChange simpleChange = simpleChange = (SimpleChange) fieldChanges.get("name");
        assert simpleChange.isAddition() && "a".equals(simpleChange.getNewValue());
        simpleChange = (SimpleChange) fieldChanges.get("string1");
        assert simpleChange.isAddition() && "str1".equals(simpleChange.getNewValue());
        simpleChange = (SimpleChange) fieldChanges.get("boolean1");
        assert simpleChange.isAddition() && Boolean.TRUE.toString().equals(simpleChange.getNewValue());
    }

    @Test
    public void testObjectARemoval() {
        ObjectA object = new ObjectA("a", "str1", Boolean.TRUE);
        Change change = ChangeDetector.diff(object, null, config);
        assert change != null && change.isRemoval();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> fieldChanges = objectChange.getFieldChanges();
        assert fieldChanges.size() == 3;

        SimpleChange simpleChange = simpleChange = (SimpleChange) fieldChanges.get("name");
        assert simpleChange.isRemoval() && "a".equals(simpleChange.getOldValue());
        simpleChange = (SimpleChange) fieldChanges.get("string1");
        assert simpleChange.isRemoval() && "str1".equals(simpleChange.getOldValue());
        simpleChange = (SimpleChange) fieldChanges.get("boolean1");
        assert simpleChange.isRemoval() && Boolean.TRUE.toString().equals(simpleChange.getOldValue());
    }

    @Test
    public void testObjectAStringModification() {
        ObjectA src = new ObjectA("a", "str1", Boolean.TRUE);
        ObjectA dst = new ObjectA("a", "str2", Boolean.TRUE);
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> fieldChanges = objectChange.getFieldChanges();
        assert fieldChanges.size() == 1;

        SimpleChange simpleChange = simpleChange = (SimpleChange) fieldChanges.get("string1");
        assert simpleChange.isModification() && "str1".equals(simpleChange.getOldValue()) && "str2".equals(simpleChange.getNewValue());
    }

    @Test
    public void testObjectABooleanModification() {
        ObjectA src = new ObjectA("a", "str1", Boolean.TRUE);
        ObjectA dst = new ObjectA("a", "str1", Boolean.FALSE);
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> fieldChanges = objectChange.getFieldChanges();
        assert fieldChanges.size() == 1;

        SimpleChange simpleChange = (SimpleChange) fieldChanges.get("boolean1");
        assert simpleChange.isModification() &&
                Boolean.TRUE.toString().equals(simpleChange.getOldValue()) &&
                Boolean.FALSE.toString().equals(simpleChange.getNewValue());
    }

    @Test
    public void testObjectACollectionAddition() {
        Set<ObjectA> src = new HashSet<ObjectA>();
        Set<ObjectA> dst = new HashSet<ObjectA>();
        dst.add(new ObjectA("a", "str1", Boolean.TRUE));
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isAddition();
        CollectionChange collChange = (CollectionChange) change;
        assert collChange.getAdded().size() == 1;
        assert collChange.getRemoved().isEmpty();
        assert collChange.getModified().isEmpty();
        // todo: assert if added correctly
    }

    @Test
    public void testObjectACollectionRemoval() {
        Set<ObjectA> src = new HashSet<ObjectA>();
        src.add(new ObjectA("a", "str1", Boolean.TRUE));
        Set<ObjectA> dst = new HashSet<ObjectA>();
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isRemoval();
        CollectionChange collChange = (CollectionChange) change;
        assert collChange.getRemoved().size() == 1;
        assert collChange.getAdded().isEmpty();
        assert collChange.getModified().isEmpty();
        // todo: assert if removed correctly
    }

    @Test
    public void testObjectACollectionModification() {
        Set<ObjectA> src = new HashSet<ObjectA>();
        src.add(new ObjectA("a", "str1", Boolean.TRUE));
        src.add(new ObjectA("b", "str1", Boolean.TRUE));
        src.add(new ObjectA("x", "str1", Boolean.TRUE));
        Set<ObjectA> dst = new HashSet<ObjectA>();
        dst.add(new ObjectA("a", "str2", Boolean.FALSE));
        dst.add(new ObjectA("c", "str1", Boolean.TRUE));
        dst.add(new ObjectA("x", "str1", Boolean.TRUE));
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        CollectionChange collChange = (CollectionChange) change;
        assert collChange.getRemoved().size() == 1;
        assert collChange.getAdded().size() == 1;
        assert collChange.getModified().size() == 1;
        // todo: assert if removed/added/modified correctly
    }

    public void testObjectBModification() {
        ObjectB src = new ObjectB("b", "str1", "str2", new ObjectA("a-name", "a-str1", null));
        src.strings.add("bbb");
        src.strings.add("xxx");
        ObjectB dst = new ObjectB("b", "str1-changed", "str2", new ObjectA("a-name", "a-str1-changed", Boolean.FALSE));
        dst.strings.add("bbb");
        dst.strings.add("yyy");
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> changes = objectChange.getFieldChanges();
        assert changes.size() == 3 && changes.containsKey("string1") && changes.containsKey("object") && changes.containsKey("strings");
        // todo: assert if modified correctly
    }

    public void testObjectBModificationObjectAAddition() {
        ObjectB src = new ObjectB("b", "str1", "str2", null);
        src.strings.add("bbb");
        src.strings.add("xxx");
        ObjectB dst = new ObjectB("b", "str1-changed", "str2", new ObjectA("a-name", "a-str1-changed", Boolean.FALSE));
        dst.strings.add("bbb");
        dst.strings.add("yyy");
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> changes = objectChange.getFieldChanges();
        assert changes.size() == 3 && changes.containsKey("string1") && changes.containsKey("object") && changes.containsKey("strings");
        // todo: assert if modified correctly
    }

    public void testObjectBModificationObjectARemoval() {
        ObjectB src = new ObjectB("b", "str1", "str2", new ObjectA("a-name", "a-str1-changed", Boolean.FALSE));
        src.strings.add("bbb");
        src.strings.add("xxx");
        ObjectB dst = new ObjectB("b", "str1-changed", "str2", null);
        dst.strings.add("bbb");
        dst.strings.add("yyy");
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> changes = objectChange.getFieldChanges();
        assert changes.size() == 3 && changes.containsKey("string1") && changes.containsKey("object") && changes.containsKey("strings");
        // todo: assert if modified correctly
    }
}
