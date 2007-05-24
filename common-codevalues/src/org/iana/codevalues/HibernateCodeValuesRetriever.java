package org.iana.codevalues;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.iana.dao.hibernate.HibernateDAO;
import org.iana.criteria.Equal;

import java.util.List;
import java.util.ArrayList;

/**
 * The hibernate-based implementation of <code>CodeValuesRetriever</code>.
 * 
 * @author Patrycja Wegrzynowicz
 */
public class HibernateCodeValuesRetriever extends HibernateDAO<Code> implements CodeValuesRetriever {

    public HibernateCodeValuesRetriever() {
        super(Code.class);
    }

    public List<Value> getCodeValues(String codeId) {
        if (codeId == null) return null;
        Code code = getCode(codeId);
        return code != null ? code.getValues() : null;
    }

    public Code getCode(String codeId) {
        List<Code> code = find(new Equal("code", codeId));
        return code != null && code.size() > 0 ? code.get(0) : null;
    }
}
