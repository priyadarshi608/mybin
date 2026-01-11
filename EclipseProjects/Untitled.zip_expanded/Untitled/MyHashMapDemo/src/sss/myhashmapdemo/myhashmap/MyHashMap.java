package sss.myhashmapdemo.myhashmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * This class is the hash based map, which have specified number of buckets 
 * which are actually linked lists. The entries going to be stored this map are
 * key value pairs which are stored in the particular bucket according to the 
 * hashCode() of the key. More than one entries can be stored in one bucket as 
 * the nodes of the linked list. While searching the element in the map, first 
 * the hashCode() of the key is calculated to find the bucket and then equals()
 * method of the key is used to find the entry in that particular bucket.
 * 
 * @author shubham
 *
 * @param <K> the key used to store the value in the map
 * @param <V> the value of the corresponding key
 */
public class MyHashMap<K, V> {
	
	private int NUMBER_OF_BUCKETS = 100;
	private int size;

	private Object[] myHashedBuckets = new Object[NUMBER_OF_BUCKETS];

	@SuppressWarnings("unchecked")
	public V get(K key) {
		if (key == null)
			throw new NullPointerException();
		int hashValue = key.hashCode() % 100;

		LinkedList<MyEntry<K, V>> thisList = null;

		try {
			thisList = ((LinkedList<MyEntry<K, V>>) myHashedBuckets[hashValue]);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}

		if (thisList != null) {
			for (MyEntry<K, V> myEntry : thisList) {
				if (myEntry.getKey().equals(key))
					return myEntry.getValue();
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public V put(K key, V value) {
		if (key == null)
			throw new NullPointerException();
		int hashValue = key.hashCode() % 100;

		LinkedList<MyEntry<K, V>> thisList = null;

		try {
			thisList = ((LinkedList<MyEntry<K, V>>) myHashedBuckets[hashValue]);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}

		if (thisList == null) {
			thisList = new LinkedList<MyEntry<K, V>>();
			myHashedBuckets[hashValue] = thisList;
			MyEntry<K, V> myEntry = new MyEntry<K, V>(key, value);
			thisList.addFirst(myEntry);
			size++;
			return null;
		} else {
			for (MyEntry<K, V> myEntry : thisList) {
				if (myEntry.getKey().equals(key)) {
					V temp = myEntry.getValue();
					myEntry.setValue(value);
					return temp;
				}
			}

			// If not present in the specified list.
			MyEntry<K, V> myEntry = new MyEntry<K, V>(key, value);
			thisList.addFirst(myEntry);
			size++;
			return null;
		}
	}

	public int size() {
		return size;
	}

	@SuppressWarnings("unchecked")
	public V remove(K key) {
		if (key == null)
			throw new NullPointerException();
		int hashValue = key.hashCode() % 100;

		LinkedList<MyEntry<K, V>> thisList = null;

		try {
			thisList = ((LinkedList<MyEntry<K, V>>) myHashedBuckets[hashValue]);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}

		if (thisList == null) {
			return null;
		} else {
			Iterator<MyEntry<K, V>> iterator = thisList.iterator();
			if (iterator.hasNext()) {
				MyEntry<K, V> myEntry = iterator.next();
				if (myEntry.getKey().equals(key)) {
					V temp = myEntry.getValue();
					iterator.remove();
					size--;
					return temp;
				}
			}

			// If not present in the specified list.
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Set<K> keySet() {
		Set<K> myKeySet = new HashSet<K>();
		for (int i = 0; i < NUMBER_OF_BUCKETS; i++) {
			LinkedList<MyEntry<K, V>> thisList = null;
			try {
				thisList = ((LinkedList<MyEntry<K, V>>) myHashedBuckets[i]);
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
			if (thisList != null) {
				for (MyEntry<K, V> myEntry : thisList) {
					myKeySet.add(myEntry.getKey());
				}
			}
		}
		return myKeySet;
	}

	@SuppressWarnings("unchecked")
	public Set<MyEntry<K, V>> entrySet() {
		Set<MyEntry<K, V>> myEntrySet = new HashSet<MyEntry<K, V>>();
		for (int i = 0; i < NUMBER_OF_BUCKETS; i++) {
			LinkedList<MyEntry<K, V>> thisList = null;
			try {
				thisList = ((LinkedList<MyEntry<K, V>>) myHashedBuckets[i]);
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
			if (thisList != null) {
				for (MyEntry<K, V> myEntry : thisList) {
					myEntrySet.add(myEntry);
				}
			}
		}
		return myEntrySet;
	}

	private static class MyEntry<K, V> implements Map.Entry<K, V>{
		private final K key;
		private V value;

		public MyEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public V setValue(V value) {
			V temp = this.value;
			this.value = value;
			return temp;
		}
	}
}
