package org.iana.codevalues;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.iana.dao.hibernate.HibernateDAO;

import java.util.List;

/**
 * The hibernate-based implementation of <code>CodeValuesRetriever</code>.
 * 
 * @author Patrycja Wegrzynowicz
 */
public class HibernateCodeValuesRetriever extends HibernateDAO<Code> implements CodeValuesRetriever {

    public HibernateCodeValuesRetriever() {
        super(Code.class);
    }

    public List<Value> getCodeValues(String code) {
        Code codeValues = (Code) getHibernateTemplate().get(Code.class, code);
        return codeValues.getValues();
    }
}
