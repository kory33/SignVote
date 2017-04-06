package com.github.kory33.signvote.collection;

import java.util.HashMap;

public class BijectiveHashMap<K, V> extends HashMap<K, V> {
    private HashMap<V, K> inverse;
    
    public BijectiveHashMap() {
        this.inverse = new HashMap<V, K>();
    }

    private BijectiveHashMap(HashMap<K, V> map, HashMap<V, K> inverse) {
        super(map);
        this.inverse = new HashMap<>(inverse);
    }
    
    public BijectiveHashMap(BijectiveHashMap<K, V> bMap) {
        this(bMap, bMap.inverse);
    }

    @Override
    public V put(K key, V value) throws IllegalArgumentException {
        if(this.containsKey(key)) {
            this.inverse.remove(this.get(key));
        }
        
        if(this.inverse.containsKey(value)) {
            throw new IllegalArgumentException("There already exists a mapping to the given value.");
        }
        
        this.inverse.put(value, key);
        return super.put(key, value);
    }
    
    /**
     * Remove the mapping pair which has the given key.
     * @param key
     * @return
     */
    public V removeKey(K key) {
        V removed = this.remove(key);
        
        if (removed != null) {
            this.inverse.remove(removed);
        }
        
        return removed;
    }
    
    /**
     * Remove the mapping pair which has the given value.
     * @param key
     * @return
     */
    public K removeValue(V value) {
        K removed = this.inverse.remove(value);
        
        if (removed != null) {
            this.remove(removed);
        }
        
        return removed;
    }
    
    public BijectiveHashMap<V, K> getInverse() {
        return new BijectiveHashMap<V, K>(this.inverse, this);
    }
    
    private static final long serialVersionUID = 1L;
}
