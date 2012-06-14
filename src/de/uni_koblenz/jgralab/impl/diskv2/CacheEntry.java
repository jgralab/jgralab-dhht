package de.uni_koblenz.jgralab.impl.diskv2;

/**
 * Entry that can be stored in the MemStorageManager's cache.
 * 
 * @author aheld
 *
 */

public class CacheEntry<V>{
	
	//the value of this entry
	private V value;
	
	//the key of this entry
	private int key;
	
	//in case of a collision, this points to the next entry in the same bucket
	private CacheEntry<V> next;
	
	public CacheEntry(V value){
		this.value = value;
		key = value.hashCode();
	}
	
	public V get(){
		return value;
	}
	
	public int getKey(){
		return key;
	}
	
	public boolean hasKey(int key){
		return this.key == key;
	}
	
	public void setNext(CacheEntry<V> entry){
		this.next = entry;
	}
	
	public CacheEntry<V> getNext(){
		return next;
	}
	
	public boolean equals(CacheEntry<V> e){
		return this.key == e.getKey();
	}
}
