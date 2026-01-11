public class LRUCache<K, V> implements Serializable {
	private static final long serialVersionUID = 4L;
	private int sizeLimit; // Size limit of the LRUCache(Maximum size)
	private List<K> list;
	private Map<K, V> cache; // Maps key vs node location in Doubly linked list
	
	public LRUCache(int sizeLimit) {
		this.sizeLimit = sizeLimit;
		cache = new HashMap<>();
		list = new ArrayList<>();
	}
	
	/* Returns the value associated with the key in the LRU cache */
	public synchronized V get(K key) {
		if(cache.containsKey(key)) { // Key present in cache
			V retVal = cache.get(key);
			list.remove(key); // Delete from list
//			cache.remove(key); // Delete from cache
			list.add(key); // Insert into list as the Most recently used
//			cache.put(key, retVal); // Update the cache
			return retVal;
		}
		return null; // Key not present in the cache
	}
	
	/* Inserts the key, value as most recently used element in the LRU cache */
	public synchronized void put(K key, V val) {
		if(cache.containsKey(key)) { // Key already present in cache
			list.remove(key); // Delete old node from list
			cache.remove(key); // Update the cache
			list.add(key); // Insert new node into list
			cache.put(key, val);
		}
		else { // New key to be inserted
			if(cache.size() == sizeLimit) { // Cache is full
				K leastRecentKey = list.get(0); // Get least recently used node
				cache.remove(leastRecentKey); // Delete least recently used from cache
				list.remove(leastRecentKey); // Update list
				cache.put(key, val); // Insert new node into cache
				list.add(key);
			}
			else { // Cache has space
				cache.put(key, val); // Insert into cache
				list.add(key); // Insert into list
			}
		}
	}
}