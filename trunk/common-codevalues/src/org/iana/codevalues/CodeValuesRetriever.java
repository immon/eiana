package org.iana.codevalues;

import org.iana.criteria.Criterion;
import org.iana.dao.hibernate.HQLBuffer;
import org.iana.dao.hibernate.HQLGenerator;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.hibernate.Session;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import java.util.List;
import java.sql.SQLException;

/**
 * It provides a method to retrieve values associated with a given code. It forms a sort
 * of an application dictionary which can be used to populate, for example, combo boxes in user interface.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface CodeValuesRetriever {

    public List<Value> getCodeValues(String code);

    public String getCodeValue(String code, String id);

    public Code getCode(String codeId);

    public Code get(final long id);
    public void create(final Code object);
    public void update(final Code object);
    public void delete(Code object);
    public List<Code> find();
    public List<Code> find(Criterion criteria);
    public List<Code> find(final Criterion criteria, final int offset, final int limit);
    public int count(final Criterion criteria);
}
