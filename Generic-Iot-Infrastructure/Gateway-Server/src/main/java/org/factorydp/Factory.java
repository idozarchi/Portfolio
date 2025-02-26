package org.factorydp;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Factory <K, D, T> {
    private final Map<K, Function<D, T>> hashmap;

    {
        hashmap = new HashMap<>();
    }

    public void add(K key, Function<D, T> v){
        hashmap.put(key, v);
    }

    public T create(K key, D data){
        Function<D, T> newV = hashmap.get(key);
        return newV == null ? null : newV.apply(data);
    }
}
