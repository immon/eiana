package org.iana.codevalues;

import pl.nask.cache.Cache;
import pl.nask.cache.NameNotFoundException;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
class CachedCodeValuesRetriever implements CodeValuesRetriever {

    private CodeValuesRetriever retriever;
    private Cache<List<Value>> cache;

    public CachedCodeValuesRetriever(CodeValuesRetriever retriever, Cache<List<Value>> cache) {
        if (retriever == null) throw new IllegalArgumentException("null retriever");
        if (cache == null) throw new IllegalArgumentException("null cache");
        this.retriever = retriever;
        this.cache = cache;
    }

    public List<Value> getCodeValues(String code) {
        synchronized (cache) {
            try {
                return cache.getElement(code);
            } catch (NameNotFoundException e) {
                List<Value> ret = retriever.getCodeValues(code);
                cache.putElement(code, ret);
                return ret;
            }
        }
    }
}
