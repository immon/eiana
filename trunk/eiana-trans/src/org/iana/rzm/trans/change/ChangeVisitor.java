package org.iana.rzm.trans.change;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface ChangeVisitor {

    public void visitAddition(Addition add);

    public void visitRemoval(Removal rem);

    public void visitModification(Modification mod);
}
