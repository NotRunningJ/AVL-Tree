package lvc.cds;

/**
 * A scaled-down version of the Java map interface. This supplies only the fundamental
 * map operations.
 *
 * @param <K> the type of the search keys.
 * @param <V> the value type associated with our keys.
 */
public interface Map<K, V> {

    /**
     * Add an entry to this map. If the key is already present, return the
     * previous value and replace it. If the key is not present, return null.
     * @param key The key, used for searching.
     * @param value the payload value.
     * @return previous value, or null.
     */
    V put(K key, V value);

    /**
     * Remove the entry with this key from the map.
     *
     * @param key the key of the entry to remove
     * @return the value stored with that key, or null if the key is not present.
     */
    V remove(K key);

    /**
     * Look up key, and return the associated value, or null.
     * @param key the search key.
     * @return the value associated with key, or null if key is not present.
     */
    V get(K key);

    /**
     * Check for the presence of key in the map.
     *
     * @param key the key to search for.
     * @return true if the key is present, false otherwise.
     */
    boolean containsKey(K key);

    /**
     * Empty the map.
     */
    void clear();

    /**
     * Check if the map is empty.
     * @return true if the map is empty.
     */
    boolean isEmpty();

    /**
     * The number of key, value pairs stored in this map.
     * @return
     */
    int size();
}
