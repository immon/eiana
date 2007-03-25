package org.iana.rzm.trans.change;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface ChangeVisitor {

    void visitCollection(CollectionChange change);

    void visitObject(ObjectChange change);

    void visitSimple(SimpleChange change);
}
