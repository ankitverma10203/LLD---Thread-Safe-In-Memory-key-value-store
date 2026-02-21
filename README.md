# Thread-Safe In-Memory Key-Value Store

A lightweight, thread-safe in-memory key-value store implementation in Java with support for Time-To-Live (TTL) expiration. This project demonstrates Low-Level Design (LLD) principles using concurrent data structures and generic programming.

## Overview

This project implements a generic key-value store that safely handles concurrent access from multiple threads while supporting automatic expiration of values based on a specified TTL (Time-To-Live). It's ideal for caching scenarios where entries should automatically expire after a certain time period.

## Features

- **Thread-Safe**: Uses `ConcurrentHashMap` for safe concurrent access without explicit locking
- **Generic Implementation**: Supports any key-value pair types using Java generics
- **TTL Support**: Values automatically expire based on a configurable time-to-live duration
- **Lazy Expiration**: Expired entries are removed when accessed (lazy deletion strategy)
- **Simple API**: Clean and intuitive methods for put, get, and remove operations

## Project Structure

```
src/
├── Main.java                 # Entry point of the application
└── model/
    ├── KeyValueStore.java   # Main key-value store implementation
    └── Entry.java           # Data structure for storing values with expiry time
```

## Classes

### KeyValueStore<K, V>

The main class that manages the in-memory key-value store with TTL support.

**Constructor:**
- `KeyValueStore()` - Creates a new empty key-value store using `ConcurrentHashMap`

**Methods:**

- **`put(K key, V value, long ttl, TimeUnit timeUnit)`**
  - Stores a key-value pair with an expiration time
  - Parameters:
    - `key`: The unique key to store the value under
    - `value`: The value to store
    - `ttl`: The time-to-live duration
    - `timeUnit`: The unit of the TTL (SECONDS, MINUTES, HOURS, etc.)
  - Returns: The previous `Entry` associated with the key, or `null` if none existed
  - Expiry time is calculated as: `System.currentTimeMillis() + timeUnit.toMillis(ttl)`

- **`get(K key)`**
  - Retrieves a value by its key
  - Parameters:
    - `key`: The key to look up
  - Returns: The `Entry` containing the value if it exists and hasn't expired, otherwise `null`
  - **Lazy Expiration**: If an entry has expired (current time > expiry time), it's automatically removed and `null` is returned

- **`remove(K key)`**
  - Removes a key-value pair from the store
  - Parameters:
    - `key`: The key to remove
  - Returns: The `Entry` that was removed, or `null` if the key didn't exist

### Entry<V>

A simple data structure that holds a value along with its expiration time.

**Fields:**
- `value`: The stored value of generic type `V`
- `expiryTime`: The timestamp (in milliseconds) when this entry expires

**Constructor:**
- `Entry(V value, long expiryTime)` - Creates an entry with a value and expiration time

## Technical Details

### Thread Safety

The store leverages Java's `ConcurrentHashMap` which provides:
- Atomic operations for all basic store operations
- No blocking on concurrent read operations
- Segment-based locking for write operations
- Better performance than synchronized collections in multi-threaded scenarios

### TTL and Expiration Strategy

- **Calculation**: `expiryTime = System.currentTimeMillis() + timeUnit.toMillis(ttl)`
- **Strategy**: Lazy deletion - expired entries are removed when accessed via `get()`, not proactively
- **Advantages**:
  - No background thread required for cleanup
  - Reduced memory overhead
  - Simple implementation

### Time Units Supported

The store accepts any `java.util.concurrent.TimeUnit`:
- `TimeUnit.MILLISECONDS`
- `TimeUnit.SECONDS`
- `TimeUnit.MINUTES`
- `TimeUnit.HOURS`
- `TimeUnit.DAYS`

## Usage Example

```java
// Create a store for String keys and String values
KeyValueStore<String, String> store = new KeyValueStore<>();

// Put a value that expires in 60 seconds
store.put("username", "john_doe", 60, TimeUnit.SECONDS);

// Retrieve the value
Entry<String> entry = store.get("username");
if (entry != null) {
    System.out.println(entry.value); // Output: john_doe
}

// Remove a value
store.remove("username");

// Try to get after removal
Entry<String> result = store.get("username"); // Returns null
```

## Design Patterns Used

1. **Generic Programming**: The store and entry classes use Java generics for type safety
2. **Lazy Initialization**: Store is initialized on first use
3. **Lazy Deletion**: Expired entries are cleaned up when accessed rather than proactively

## Concurrency Considerations

- Multiple threads can safely read and write simultaneously
- `ConcurrentHashMap` handles internal synchronization
- No external synchronization is required when using the store
- Each operation is atomic

## Potential Enhancements

- **Active Expiration**: Implement a background thread to proactively remove expired entries
- **Statistics**: Add metrics for cache hits, misses, and evictions
- **Capacity Limits**: Implement size limits with eviction policies (LRU, LFU)
- **Persistence**: Add serialization to save/load store state
- **Event Listeners**: Notify listeners when entries are added, removed, or expired
- **Custom Expiration Logic**: Allow custom TTL strategies beyond fixed time-based expiration
