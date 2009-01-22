package org.iana.codevalues;

import pl.nask.cache.Cache;
import pl.nask.cache.NameNotFoundException;

import java.util.*;

/**
 * The implementation of <code>CodeValuesRetriever</code> that caches retrieved  values.
 *
 * @author Patrycja Wegrzynowicz
 */
class CachedCodeValuesRetriever implements CodeValuesRetriever {

    private CodeValuesRetriever retriever;
    private Cache<CodeValuesEntry> cache;

    static class CodeValuesEntry {
        Code code;
        Map<String, String> valueMap;
        Set<String> valueSet;

        CodeValuesEntry(Code code) {
            this.code = code;
            this.valueMap = new HashMap<String, String>();
            this.valueSet = new HashSet<String>();
            if (code != null && code.getValues() != null) {
                for (Value value : code.getValues()) {
                    this.valueMap.put(value.getValueId(), value.getValueName());
                    this.valueSet.add(value.getValueName());
                }
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
        return getCodeValuesEntry(code).code.getValues();
    }

    public boolean hasValueId(String code, String id) {
        return getCodeValuesEntry(code).valueMap.containsKey(id);
    }

    public boolean hasValue(String code, String value) {
        return getCodeValuesEntry(code).valueSet.contains(value);
    }

    public String getValueById(String code, String id) {
        return getCodeValuesEntry(code).valueMap.get(id);
    }

    public Code getCode(String code) {
        return getCodeValuesEntry(code).code;
    }

    private CodeValuesEntry getCodeValuesEntry(String code) {
        synchronized (cache) {
            try {
                return cache.getElement(code);
            } catch (NameNotFoundException e) {
                CodeValuesEntry ret = new CodeValuesEntry(retriever.getCode(code));
                cache.putElement(code, ret);
                return ret;
            }
        }
    }

    public void refresh() {
        cache.clear();
    }
}
