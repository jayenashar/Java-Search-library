package au.edu.unsw.cse.jayen.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A Map with an additional getKey() function.
 * 
 * @author jayen
 * @param <K>
 *           the type of keys maintained by this map
 * @param <V>
 *           the type of mapped values
 * 
 */
public class HMap<K, V> {

   private final Map<K, Map<K, V>> map;

   /**
    * Constructs an empty <tt>HMap</tt> with the default initial capacity (16)
    * and the default load factor (0.75).
    */
   public HMap() {
      map = new HashMap<K, Map<K, V>>();
   }

   /**
    * Constructs an empty <tt>HMap</tt> with the specified initial capacity and
    * the default load factor (0.75).
    * 
    * @param initialCapacity
    *           the initial capacity.
    * @throws IllegalArgumentException
    *            if the initial capacity is negative.
    */
   public HMap(final int initialCapacity) {
      map = new HashMap<K, Map<K, V>>(initialCapacity);
   }

   /**
    * Returns <tt>true</tt> if this map contains a mapping for the specified
    * key.
    * 
    * @param key
    *           The key whose presence in this map is to be tested
    * @return <tt>true</tt> if this map contains a mapping for the specified
    *         key.
    */
   public boolean containsKey(final Object key) {
      return map.containsKey(key);
   }

   /**
    * Returns the value to which the specified key is mapped, or {@code null} if
    * this map contains no mapping for the key.
    * 
    * <p>
    * More formally, if this map contains a mapping from a key {@code k} to a
    * value {@code v} such that {@code (key==null ? k==null : key.equals(k))},
    * then this method returns {@code v}; otherwise it returns {@code null}.
    * (There can be at most one such mapping.)
    * 
    * <p>
    * A return value of {@code null} does not <i>necessarily</i> indicate that
    * the map contains no mapping for the key; it's also possible that the map
    * explicitly maps the key to {@code null}. The {@link #containsKey
    * containsKey} operation may be used to distinguish these two cases.
    * 
    * @see #put(Object, Object)
    */
   public V get(final Object key) {
      final Map<K, V> entry = map.get(key);
      if (entry != null)
         return entry.values().iterator().next();
      return null;
   }

   /**
    * Returns a key <code>k</code> such that <code>key.equals(k)</code> if this
    * map contains a mapping for the specified key.
    * 
    * @param key
    *           The key whose presence in this map is to be tested
    * @return a key <code>k</code> such that <code>key.equals(k)</code> if this
    *         map contains a mapping for the specified key.
    */
   public K getKey(final Object key) {
      final Map<K, V> entry = map.get(key);
      if (entry != null)
         return entry.keySet().iterator().next();
      return null;
   }

   /**
    * Associates the specified value with the specified key in this map. If the
    * map previously contained a mapping for the key, the old value is replaced.
    * 
    * @param key
    *           key with which the specified value is to be associated
    * @param value
    *           value to be associated with the specified key
    * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
    *         if there was no mapping for <tt>key</tt>. (A <tt>null</tt> return
    *         can also indicate that the map previously associated <tt>null</tt>
    *         with <tt>key</tt>.)
    */
   public V put(final K key, final V value) {
      final Map<K, V> entry = map
            .put(key, Collections.singletonMap(key, value));
      if (entry != null)
         return entry.values().iterator().next();
      return null;
   }
}
