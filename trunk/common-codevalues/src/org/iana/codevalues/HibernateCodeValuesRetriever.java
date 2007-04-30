package org.iana.codevalues;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
class HibernateCodeValuesRetriever extends HibernateDaoSupport implements CodeValuesRetriever {

    public List<Value> getCodeValues(String code) {
        Code codeValues = (Code) getHibernateTemplate().get(Code.class, code);
        return codeValues.getValues();
    }
}
