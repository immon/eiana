package org.iana.codevalues;

import pl.nask.cache.Cache;
import pl.nask.cache.NameNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * The implementation of <code>CodeValuesRetriever</code> that caches retrieved  values.
 *
 * @author Patrycja Wegrzynowicz
 */
class CachedCodeValuesRetriever implements CodeValuesRetriever {

    private CodeValuesRetriever retriever;
    private Cache<CodeValuesEntry> cache;

    static class CodeValuesEntry {
        List<Value> valueList;
        Map<String, String> valueMap;

        CodeValuesEntry(List<Value> valueList) {
            this.valueList = valueList;
            this.valueMap = new HashMap<String, String>();
            for (Value value : valueList) {
                this.valueMap.put(value.getValueId(), value.getValueName());
            }
        }
    }

    public CachedCodeValuesRetriever(CodeValuesRetriever retriever, Cache<CodeValuesEntry> cache) {
        if (retriever == null) throw new IllegalArgumentException("null retriever");
        if (cache == null) throw new IllegalArgumentException("null cache");
        this.retriever = retriever;
        this.cache = cache;
    }

    public List<Value> getCodeValues(String code) {
        return getCodeValuesEntry(code).valueList;
    }

    public String getCodeValue(String code, String id) {
        return getCodeValuesEntry(code).valueMap.get(id);
    }

    private CodeValuesEntry getCodeValuesEntry(String code) {
        synchronized (cache) {
            try {
                return cache.getElement(code);
            } catch (NameNotFoundException e) {
                CodeValuesEntry ret = new CodeValuesEntry(retriever.getCodeValues(code));
                cache.putElement(code, ret);
                return ret;
            }
        }
    }
}
