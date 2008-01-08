package org.iana.rzm.web.util;

import java.util.*;

public final class ListUtil {

    public static interface Predicate<T>{
        public boolean evaluate(T object);
    }

    public static <T> void filter(List<T> list, Predicate<T> predicate) {
        Iterator<T> it = list.iterator();
        while(it.hasNext()){
            T t = it.next();
            if(!predicate.evaluate(t)){
                it.remove();
            }
        }
    }

    public static <T> T find(List<T> list, Predicate<T> predicate){
        for (T t : list) {
            if(predicate.evaluate(t)){
                return t;
            }
        }
        return null;
    }

    public static <T> T get(List<T>list, int index, T noneFound){
        if(list == null || list.size() <= index){
            return noneFound;
        }
        return list.get(index);
    }
    
}
