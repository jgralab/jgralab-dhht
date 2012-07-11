package de.uni_koblenz.jgralab.impl.diskv2;

import java.util.Queue;

import de.uni_koblenz.jgralab.GraphElement;

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
	
	//Tracks the changed attributes of the referenced object
	private Tracker tracker;
	
	public CacheEntry(V value){
		this.value = value;
		key = value.hashCode();
	}
	
	public IncidenceTracker getOrCreateIncidenceTracker(IncidenceImpl inc){
		if (tracker == null){
			IncidenceTracker incTracker = new IncidenceTracker();
			incTracker.fill(inc);
			this.tracker = incTracker;
		}
		
		return (IncidenceTracker) tracker;
	}
	
	public GraphElementTracker getOrCreateGETracker(GraphElementImpl<?,?,?,?> ge){
		if (tracker == null){
			GraphElementTracker geTracker = new GraphElementTracker();
			geTracker.fill(ge);
			this.tracker = geTracker;
		}
		
		return (GraphElementTracker) tracker;
	}
	
	public Tracker getTracker(){
		return tracker;
	}
	
	public V get(){
		return value;
	}
	
	//TODO: Temporary method for testing, delete this eventually
	public void delete(Queue<CacheEntry<V>> queue){
		queue.add(this);
		value = null;
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
