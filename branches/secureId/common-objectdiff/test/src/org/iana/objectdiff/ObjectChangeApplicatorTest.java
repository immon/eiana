package org.iana.objectdiff;

import org.testng.annotations.Test;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(groups = {"change", "eiana-trans"})
public class ObjectChangeApplicatorTest {

    private DiffConfiguration config = new TestDiffConfiguration();

    @Test
    public void testObjectAModApplication() {
        ObjectA src = new ObjectA("a", "str1", Boolean.FALSE);
        ObjectA dst = new ObjectA("a", "str2", Boolean.TRUE);
        assert !src.equals(dst);
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null;
        ChangeApplicator.applyChange(src, (ObjectChange) change, config);
        assert src.equals(dst);
    }

    @Test
    public void testObjectARemApplication() {
        ObjectA src = new ObjectA("a", "str1", Boolean.FALSE);
        ObjectA dst = new ObjectA("a", null, null);
        assert !src.equals(dst);
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null;
        ChangeApplicator.applyChange(src, (ObjectChange) change, config);
        assert src.equals(dst);
    }

    @Test
    public void testObjectAAddApplication() {
        ObjectA src = new ObjectA("a", null, null);
        ObjectA dst = new ObjectA("a", "str1", Boolean.FALSE);
        assert !src.equals(dst);
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null;
        ChangeApplicator.applyChange(src, (ObjectChange) change, config);
        assert src.equals(dst);
    }

    @Test
    public void testObjectBModApplication() {
        ObjectB src = new ObjectB("b", "str1", "str2", null);
        src.strings.add("string-arr-1");
        src.strings.add("string-arr-2");
        src.objects.add(new ObjectA("a1", "a1-str", Boolean.FALSE));
        src.objects.add(new ObjectA("a2", "a2-str", Boolean.FALSE));
        ObjectB dst = new ObjectB("b", "str1", "str2", null);
        dst.strings.add("string-arr-1");
        dst.strings.add("string-arr-3");
        dst.objects.add(new ObjectA("a1", "a1-str-new", Boolean.TRUE));
        dst.objects.add(new ObjectA("a3", "a3-str", Boolean.FALSE));
        assert !src.equals(dst);
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null;
        ChangeApplicator.applyChange(src, (ObjectChange) change, config);
        assert src.equals(dst);
    }
}
