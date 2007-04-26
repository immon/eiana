package org.iana.objectdiff;

import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TestDiffConfiguration extends DiffConfiguration {

    public TestDiffConfiguration() {
        List<String> af = new ArrayList<String>();
        af.add("name"); af.add("string1"); af.add("boolean1");
        ObjectConfiguration a = new ObjectConfiguration(af, "name");
        addObjectConfiguration(ObjectA.class, a);

        List<String> bf = new ArrayList<String>();
        bf.add("name"); bf.add("string1"); bf.add("string2"); bf.add("object");
        bf.add("strings"); bf.add("objects");
        ObjectConfiguration b = new ObjectConfiguration(bf, "name");
        b.addFieldClass("objects", ObjectA.class);
        b.addFieldClass("object", ObjectA.class);
        addObjectConfiguration(ObjectB.class, b);
    }
}
