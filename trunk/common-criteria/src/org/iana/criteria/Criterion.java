package org.iana.criteria;

import java.io.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Criterion extends Serializable {

    public void accept(CriteriaVisitor visitor);
}
