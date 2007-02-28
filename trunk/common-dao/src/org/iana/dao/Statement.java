package org.iana.dao;

/**
 * <p>
 * This class represents a general statement regarding data retrieval or manipulation. It extends <code>Cloneable</code>
 * interface in order to facilitate an implementation of Prototype design pattern.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 */
public interface Statement extends Cloneable {

    void setObject(String name, Object object);

    Statement clone();    
}
