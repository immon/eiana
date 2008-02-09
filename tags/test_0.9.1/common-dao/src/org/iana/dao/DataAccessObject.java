package org.iana.dao;

import org.iana.criteria.Criterion;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface DataAccessObject<T> {

    T get(long id);

    void create(T object);

    void update(T object);

    void delete(T object);

    List<T> find();

    List<T> find(Criterion criteria);

    List<T> find(Criterion criteria, int offset, int limit);

    int count(Criterion criteria);
    
}
