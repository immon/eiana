package org.iana.rzm.trans.change;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface ValueVisitor {

    public void visitPrimitiveValue(PrimitiveValue value);

    public void visitModifiedPrimitiveValue(ModifiedPrimitiveValue value);

    public void visitObjectValue(ObjectValue value); 
}
