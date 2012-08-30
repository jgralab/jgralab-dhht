package de.uni_koblenz.jgralab.impl.diskv2;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Queue;

import de.uni_koblenz.jgralab.GraphElement;

/**
 * Entry that can be stored in the MemStorageManager's cache.
 * 
 * @author aheld
 *
 */

public class CacheEntry<V> extends SoftReference<V>{
	
	/**
	 * the key of this entry
	 */
	private int key;
	
	/**
	 * in case of a collision, this points to the next entry in the same bucket
	 */
	private CacheEntry<V> next;
	
	/**
	 * Tracks the changed attributes of the referenced object
	 */
	private Tracker tracker;
	
	public CacheEntry(V value, ReferenceQueue<V> refQueue){
		super(value, refQueue);
		key = value.hashCode();
	}
	
	/**
	 * Returns the tracker. If the tracker hasn't been created yet, 
	 * A new one is created and returned. 
	 * Only call this method if this CacheEntry references an Incidence.
	 * 
	 * @param inc - The Incidence for which this tracker is created
	 * @return @link{tracker}
	 */
	public IncidenceTracker getOrCreateIncidenceTracker(IncidenceImpl inc){
		if (tracker == null){
			IncidenceTracker incTracker = new IncidenceTracker();
			incTracker.fill(inc);
			this.tracker = incTracker;
		}
		
		return (IncidenceTracker) tracker;
	}
	
	/**
	 * Returns the tracker. If the tracker hasn't been created yet, 
	 * A new one is created and returned. 
	 * Only call this method if this CacheEntry references a GraphElement.
	 * 
	 * @param inc - The Incidence for which this tracker is created
	 * @return @link{tracker}
	 */
	public GraphElementTracker getOrCreateGETracker(GraphElementImpl<?,?,?,?> ge){
		if (tracker == null){
			GraphElementTracker geTracker = new GraphElementTracker();
			geTracker.storeAttributes(ge);
			this.tracker = geTracker;
		}
		
		return (GraphElementTracker) tracker;
	}
	
	/**
	 * Returns the tracker.
	 * 
	 * @return @link{tracker}, or null if no tracker is present.
	 */
	public Tracker getTracker(){
		return tracker;
	}
	
	//TODO: Temporary method for testing, delete this eventually
	public void delete(Queue<CacheEntry<V>> queue){
		queue.add(this);
	}
	
	/**
	 * Get the key.
	 * 
	 * @return @link{key}
	 */
	public int getKey(){
		return key;
	}
	
	/**
	 * Checks if this tracker has a given key.
	 * 
	 * @param key - The key to compare to
	 * @return true if this tracker has the given key; false otherwise.
	 */
	public boolean hasKey(int key){
		return this.key == key;
	}
	
	/**
	 * Set @link{next} to point at the entry 
	 * @param entry - the Entry to point towards
	 */
	public void setNext(CacheEntry<V> entry){
		this.next = entry;
	}
	
	/**
	 * Get the Entry to which @link{next} points.
	 * @return
	 */
	public CacheEntry<V> getNext(){
		return next;
	}
	
	/**
	 * Checks if this and the given CacheEntry have the same key.
	 * 
	 * @param e The CacheEntry to compare to
	 * @return true if the keys are the same, false otherwise.
	 */
	public boolean equals(CacheEntry<V> e){
		return this.key == e.getKey();
	}
}
