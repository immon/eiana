package org.iana.codevalues;

import org.iana.criteria.Criterion;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface CodeValuesManager extends CodeValuesRetriever {

    public void create(final Code object);

    public void update(final Code object);

    public void delete(Code object);

    public List<Code> find();

    public List<Code> find(Criterion criteria);

    public List<Code> find(final Criterion criteria, final int offset, final int limit);

    public int count(final Criterion criteria);
}
