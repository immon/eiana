package org.iana.rzm.trans.change;

/**
 * @author Patrycja Wegrzynowicz
 */
abstract class AdditionOrRemoval extends Change {

    protected AdditionOrRemoval(String fieldName, Type type) {
        super(fieldName, type);
    }
}
