package org.iana.rzm.trans.change;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
abstract class AdditionOrRemoval extends Change {

    protected AdditionOrRemoval() {}

    protected AdditionOrRemoval(String fieldName, Type type) {
        super(fieldName, type);
    }
}
