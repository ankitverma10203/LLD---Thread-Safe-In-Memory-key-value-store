package model;

public class Entry<V> {
    V value;
    long expiryTime;

    Entry(V value, long expiryTime) {
        this.value = value;
        this.expiryTime = expiryTime;
    }
}
