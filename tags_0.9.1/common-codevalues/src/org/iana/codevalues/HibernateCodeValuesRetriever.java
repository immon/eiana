package org.iana.codevalues;

import org.iana.dao.hibernate.HibernateDAO;
import org.iana.criteria.Equal;

import java.util.List;

/**
 * The hibernate-based implementation of <code>CodeValuesRetriever</code>.
 * 
 * @author Patrycja Wegrzynowicz
 */
public class HibernateCodeValuesRetriever extends HibernateDAO<Code> implements CodeValuesManager {

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

    public boolean hasValueId(String code, String id) {
        throw new UnsupportedOperationException();
    }

    public boolean hasValue(String code, String value) {
        throw new UnsupportedOperationException();
    }

    public String getValueById(String codeId, String id) {
        List<Value> values = getCodeValues(codeId);
        if (values != null && id != null) {
            for (Value value : values) {
                if (id.equalsIgnoreCase(value.getValueId())) return value.getValueName();
            }
        }
        return null;
    }
}
