package edu.cmu.cs.cs214.rec08.map;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.jcip.annotations.ThreadSafe;

/**
 * A broken thread-safe HashMap: it is not yet thread-safe. Your task is to use
 * primitive Java synchronization to make the HashMap thread-safe while allowing
 * concurrent access. Use a different lock to synchronize access to
 * each bucket in the hash table.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
@ThreadSafe
public class SimpleHashMap<K, V> {
    /**
     * We store the hash table as a list of lists. Each element of the outer
     * list is a bucket in the hash table, and each bucket (an inner list)
     * expands as needed to accommodate the entries in that bucket.
     */
    private final List<List<Entry<K, V>>> table;

    /**
     * Array of lock objects - one lock per bucket to allow concurrent access
     * to different buckets while ensuring thread safety within each bucket.
     */
    private final Object[] bucketLocks;

    /**
     * Number of buckets in the hash table. This is immutable after construction.
     */
    private final int numBuckets;

    /**
     * Constructs a new hash map with a given number of buckets.
     */
    public SimpleHashMap(int numBuckets) {
        if (numBuckets <= 0) {
            throw new IllegalArgumentException("Illegal number of buckets: "
                    + numBuckets);
        }

        this.numBuckets = numBuckets;
        table = new ArrayList<>(this.numBuckets);
        bucketLocks = new Object[this.numBuckets];
        for (int i = 0; i < numBuckets; i++) {
            table.add(new LinkedList<>());
            bucketLocks[i] = new Object(); // Initialize lock for each bucket
        }
    }

    /**
     * Puts a new key-value pair into the map.
     * 
     * @param key The key to add to the map.
     * @param value The value to add to the map for the key.
     * @return The previous value for the given key, or null 
     *         if the given key was not previously in the map.
     *
     */
    public V put(K key, V value) {
        if (key == null)
            throw new NullPointerException("Key can't be null.");

        int bucketIndex = hash(key);
        synchronized (bucketLocks[bucketIndex]) {
            List<Entry<K,V>> bucket = table.get(bucketIndex);
            for (Entry<K, V> e : bucket) {
                if (e.key.equals(key)) {
                    V result = e.value;
                    e.value = value;
                    return result;
                }
            }

            bucket.add(new Entry<>(key, value));
            return null;
        }
    }

    /**
     * Returns value for the given key, or null if the key is not present.
     *
     * @param key The key for which to return the value.
     * @return The value for the given key, or null if the key is not present.
     */
    public V get(K key) {
        int bucketIndex = hash(key);
        synchronized (bucketLocks[bucketIndex]) {
            List<Entry<K,V>> bucket = table.get(bucketIndex);
            for (Entry<K, V> e : bucket) {
                if (e.key.equals(key)) {
                    return e.value;
                }
            }
            return null;
        }
    }

    /**
     * Returns a hash code for an object, bound to the
     * number of buckets in the hash table.
     * 
     * @param o The object to hash.
     * @return The hash code for o, bound to the number
     * of buckets in the table.
     */
    private int hash(Object o) {
        if (o == null)
            return 0;
        return Math.abs(o.hashCode() % numBuckets);
    }

    private static class Entry<K, V> {
        final K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
