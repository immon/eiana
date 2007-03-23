package org.iana.rzm.trans.change;

import org.testng.annotations.Test;
import org.iana.rzm.trans.change.ChangeDetector;
import org.iana.rzm.trans.change.DiffConfiguration;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test (groups = {"change", "eiana-trans"})
public class SimpleChangeDetectorTest {

    private DiffConfiguration config = new TestDiffConfiguration();

    @Test
    public void testNull() {
        Change change = ChangeDetector.diff(null, null, config);
        assert change == null;
    }

    @Test
    public void testSimpleStringAddition() {
        Change change = ChangeDetector.diff(null, "abc", config);
        assert change != null;
        SimpleChange simpleChange = (SimpleChange) change;
        assert simpleChange.isAddition() && "abc".equals(simpleChange.getNewValue());
    }

    @Test
    public void testSimpleStringRemoval() {
        Change change = ChangeDetector.diff("abc", null, config);
        assert change != null;
        SimpleChange simpleChange = (SimpleChange) change;
        assert simpleChange.isRemoval() && "abc".equals(simpleChange.getOldValue());        
    }

    @Test
    public void testSimpleStringModification() {
        Change change = ChangeDetector.diff("abc", "xyz", config);
        assert change != null;
        SimpleChange simpleChange = (SimpleChange) change;
        assert simpleChange.isModification() && "abc".equals(simpleChange.getOldValue()) && "xyz".equals(simpleChange.getNewValue());        
    }

    @Test
    public void testSimpleStringCollectionAddition() {
        Collection<String> src = new HashSet<String>();
        Collection<String> dst = new HashSet<String>();
        dst.add("abc");
        dst.add("xyz");
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null;
        CollectionChange collChange = (CollectionChange) change;
        assert collChange.isAddition();
        assert collChange.getModified().isEmpty();
        assert collChange.getRemoved().isEmpty();
        assert collChange.getAdded().size() == dst.size();
        for (Change addChange : collChange.getAdded()) {
            assert addChange != null;
            SimpleChange simpleAddChange = (SimpleChange) addChange;
            assert simpleAddChange.isAddition();
            assert dst.remove(simpleAddChange.getNewValue());
        }
        assert dst.isEmpty();
    }

    @Test
    public void testSimpleStringCollectionRemoval() {
        Collection<String> src = new HashSet<String>();
        Collection<String> dst = new HashSet<String>();
        src.add("abc");
        src.add("xyz");
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null;
        CollectionChange collChange = (CollectionChange) change;
        assert collChange.isRemoval();
        assert collChange.getModified().isEmpty();
        assert collChange.getAdded().isEmpty();
        assert collChange.getRemoved().size() == src.size();
        for (Change remChange : collChange.getRemoved()) {
            assert remChange != null;
            SimpleChange simpleRemChange = (SimpleChange) remChange;
            assert simpleRemChange.isRemoval();
            assert src.remove(simpleRemChange.getOldValue());
        }
        assert src.isEmpty();
    }

    @Test
    public void testSimpleStringCollectionModification() {
        Collection<String> src = new HashSet<String>();
        src.add("abc");
        src.add("xyz");
        Collection<String> dst = new HashSet<String>();
        dst.add("abc");
        dst.add("zyx");
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null;
        CollectionChange collChange = (CollectionChange) change;
        assert collChange.isModification();
        assert collChange.getModified().isEmpty();
        assert collChange.getAdded().size() == 1;
        for (Change addChange : collChange.getAdded()) {
            assert addChange != null;
            SimpleChange simpleAddChange = (SimpleChange) addChange;
            assert simpleAddChange.isAddition();
            assert dst.remove(simpleAddChange.getNewValue());
        }
        assert dst.size() == 1 && dst.contains("abc");
        assert collChange.getRemoved().size() == 1;
        for (Change remChange : collChange.getRemoved()) {
            assert remChange != null;
            SimpleChange simpleRemChange = (SimpleChange) remChange;
            assert simpleRemChange.isRemoval();
            assert src.remove(simpleRemChange.getOldValue());
        }
        assert src.size() == 1 && src.contains("abc");
    }
}
