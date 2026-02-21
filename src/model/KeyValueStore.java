package model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class KeyValueStore<K, V> {
    private final ConcurrentHashMap<K, Entry<V>> store;

    KeyValueStore() {
        store = new ConcurrentHashMap<>();
    }

    public Entry<V> put(K key, V value, long ttl, TimeUnit timeUnit) {
        long expiryTime = System.currentTimeMillis() + timeUnit.toMillis(ttl);
        return store.put(key, new Entry<>(value, expiryTime));
    }

    public Entry<V> remove(K key) {
        return store.remove(key);
    }

    public Entry<V> get(K key) {
        long now = System.currentTimeMillis();

        Entry<V> entry = store.get(key);

        if (entry != null && now > entry.expiryTime) {
            store.remove(key);
            return null;
        }

        return entry;
    }
}
