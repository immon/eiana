package org.iana.rzm.facade.system.domain.types;

import org.iana.codevalues.CodeValuesRetriever;
import org.iana.codevalues.Value;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DomainTypesImpl implements DomainTypes {

    public static final String CODE_DT = "domaintypes";

    private CodeValuesRetriever retriever;

    public DomainTypesImpl(CodeValuesRetriever retriever) {
        if (retriever == null) throw new IllegalArgumentException("null code values retriever");
        this.retriever = retriever;
    }

    public List<Value> getDomainTypes() {
        return retriever.getCodeValues(CODE_DT);
    }

    public boolean hasDomainType(String value) {
        return retriever.hasValue(CODE_DT, value);
    }
}
