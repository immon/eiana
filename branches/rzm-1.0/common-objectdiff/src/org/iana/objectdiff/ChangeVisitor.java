package org.iana.objectdiff;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface ChangeVisitor {

    void visitCollection(CollectionChange change);

    void visitObject(ObjectChange change);

    void visitSimple(SimpleChange change);
}
