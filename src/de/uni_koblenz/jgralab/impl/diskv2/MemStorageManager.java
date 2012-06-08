package de.uni_koblenz.jgralab.impl.diskv2;


import java.rmi.RemoteException;
import java.util.Stack;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.RemoteStorageAccess;
import de.uni_koblenz.jgralab.impl.diskv2.EdgeImpl;
import de.uni_koblenz.jgralab.impl.diskv2.IncidenceImpl;
import de.uni_koblenz.jgralab.impl.diskv2.VertexImpl;

/**
 * This class realizes the caching of vertices, edges and incidences 
 * in memory in a distributed environment. All methods may be used
 * only with local objects and local ids.
 * 
 * @author dbildh, aheld
 * 
 */
public final class MemStorageManager implements RemoteStorageAccess {

	//constant used by the hash function
	private static final int S = (int) Math.floor((0.5*(Math.sqrt(5)) - 1) * Math.pow(2, 32));
	
	//maximum load factor of the caches. If load factor is exceeded, do a rehash
	private static final double MAX_LOAD_FACTOR = 0.7;
	
	//maximum number of entries in the vertex, edge and incidence caches
	//if these are exceeded by the amount of entries of the respective cache,
	//the load factor of the respective cache has been exceeded
	private long vertexCacheMaxEntries;
	private long edgeCacheMaxEntries;
	private long incidenceCacheMaxEntries;
	
	//log(2) of the cache sizes
	private int vertexCacheExp;
	private int edgeCacheExp;
	private int incidenceCacheExp;
	
	//number of objects in the caches
	private long vertexCacheEntries;
	private long edgeCacheEntries;
	private long incidenceCacheEntries;

	/**
	 * in-memory-cache for vertices
	 */
	private CacheEntry<VertexImpl>[] vertexCache;

	/**
	 * in-memory-cache for edges
	 */
	private CacheEntry<EdgeImpl>[] edgeCache;

	/**
	 * in-memory-cache for incidences
	 */
	private CacheEntry<IncidenceImpl>[] incidenceCache;
	
	public MemStorageManager(GraphDatabaseBaseImpl database) {
		//long memory = Runtime.getRuntime().freeMemory();
		
		//set log(2) of the cache sizes
		vertexCacheExp = 21;
		edgeCacheExp = 21;
		incidenceCacheExp = 23;
		
		//calculate default cache sizes and max load factors
		int vertexCacheSize = (int) Math.pow(2, vertexCacheExp);
		vertexCacheMaxEntries = (int) (vertexCacheSize * MAX_LOAD_FACTOR);
		int edgeCacheSize = (int) Math.pow(2, edgeCacheExp);
		edgeCacheMaxEntries = (int) (edgeCacheSize * MAX_LOAD_FACTOR);
		int incidenceCacheSize = (int) Math.pow(2, incidenceCacheExp);
		incidenceCacheMaxEntries = (int) (incidenceCacheSize * MAX_LOAD_FACTOR);
		
		//instantiate caches with calculated sizes
		vertexCache = new CacheEntry[vertexCacheSize];
		edgeCache = new CacheEntry[edgeCacheSize];
		incidenceCache = new CacheEntry[incidenceCacheSize];
		
		//initialize element counts
		vertexCacheEntries = 0;
		edgeCacheEntries = 0;
		incidenceCacheEntries = 0;
	}

	//---- Methods to put, get and remove Graph elements and incidences from the cache ----

	/**
	 * Retrieves a Vertex from the cache
	 * 
	 * @param id the id of the Vertex to be retrieved
	 * @return the Vertex with the given id
	 */
	public final Vertex getVertexObject(int id) {
		int hash = vertexHashValue(id);
		
		//retrieve first vertex in the bucket
		CacheEntry<VertexImpl> current = vertexCache[hash];
		
		//search bucket for the requested vertex
		while (current != null && !current.hasKey(id)){
			current = current.getNext();
		}
		
		if (current == null) return null;
		return current.get();
	}

	/**
	 * Retrieves an Edge from the cache
	 * 
	 * @param id the id of the Edge to be retrieved
	 * @return the Edge with the given id
	 */
	public final Edge getEdgeObject(int id) {
		int hash = edgeHashValue(id);
		
		//retrieve first edge in the bucket
		CacheEntry<EdgeImpl> current = edgeCache[hash];
		
		//search bucket for the requested edge
		while (current != null && !current.hasKey(id)){
			current = current.getNext();
		}
		
		if (current == null) return null;
		return current.get();
	}
	
	/**
	 * Retrieves an Incidence from the cache
	 * 
	 * @param id the id of the Incidence to be retrieved
	 * @return the Incidence with the given id
	 */
	public final Incidence getIncidenceObject(int id) {
		int hash = incidenceHashValue(id);
		
		//retrieve first incidence in the bucket
		CacheEntry<IncidenceImpl> current = incidenceCache[hash];
		
		//search bucket for the requested incidence
		while (current != null && !current.hasKey(id)){
			current = current.getNext();
		}

		if (current == null) return null;
		return current.get();
	}

	/**
	 * Puts a Vertex in the cache
	 * 
	 * @param v the Vertex to be cached
	 */
	public void storeVertex(VertexImpl v) {
		CacheEntry<VertexImpl> vEntry = new CacheEntry<VertexImpl>(v);
		
		putVertex(vEntry, vertexCache);
		
		vertexCacheEntries++;
		testVertexLoadFactor();
	}
	
	//helper method to avoid duplicate code
	private void putVertex(CacheEntry<VertexImpl> vEntry, CacheEntry<VertexImpl>[] cache){
		int hashValue = vertexHashValue(vEntry.getKey());
		
		//case 1: no collision
		if (cache[hashValue] == null) {
			cache[hashValue] = vEntry;
			return;
		}
					
		//collision detected
		CacheEntry<VertexImpl> current = cache[hashValue];
				
		//find end of list of vertices in buckets
		while(current.getNext() != null){
			current = current.getNext();
		}
				
		//append new element
		current.setNext(vEntry);
	}
	
	/**
	 * Stores an Edge in the cache
	 * 
	 * @param e the Edge to be cached
	 */
	public void storeEdge(EdgeImpl e) {
		CacheEntry<EdgeImpl> eEntry = new CacheEntry<EdgeImpl>(e);
		
		putEdge(eEntry, edgeCache);
		
		edgeCacheEntries++;
		testEdgeLoadFactor();
	}
	
	private void putEdge(CacheEntry<EdgeImpl> eEntry, CacheEntry<EdgeImpl>[] cache){
		int hashValue = edgeHashValue(eEntry.getKey());
		
		//case 1: no collision
		if (cache[hashValue] == null) {
			cache[hashValue] = eEntry;
			return;
		}
					
		//collision detected
		CacheEntry<EdgeImpl> current = cache[hashValue];
				
		//find end of list of vertices in buckets
		while(current.getNext() != null){
			current = current.getNext();
		}
				
		//append new element
		current.setNext(eEntry);
	}
	
	/**
	 * Stores an Incidence in the cache
	 * 
	 * @param i the Incidence to be cached
	 */
	public void storeIncidence(IncidenceImpl i) {
		CacheEntry<IncidenceImpl> iEntry = new CacheEntry<IncidenceImpl>(i);
		
		putIncidence(iEntry, incidenceCache);
		
		incidenceCacheEntries++;
		testIncidenceLoadFactor();
	}
	
	private void putIncidence(CacheEntry<IncidenceImpl> iEntry, CacheEntry<IncidenceImpl>[] cache){
		int hashValue = incidenceHashValue(iEntry.getKey());
		
		//case 1: no collision
		if (cache[hashValue] == null) {
			cache[hashValue] = iEntry;
			return;
		}
					
		//collision detected
		CacheEntry<IncidenceImpl> current = cache[hashValue];
				
		//find end of list of vertices in buckets
		while(current.getNext() != null){
			current = current.getNext();
		}
				
		//append new element
		current.setNext(iEntry);
	}

	/**
	 * Removes a vertex from the cache
	 * 
	 * @param vertexId the id of the vertex to be deleted
	 */
	public void removeVertexFromStorage(int vertexId) {
		int hashValue = vertexHashValue(vertexId);
		
		//retrieve first element in bucket
		CacheEntry<VertexImpl> current = vertexCache[hashValue];
		
		//look for element to be deleted
		while(!current.hasKey(vertexId)){
			current = current.getNext();
		}
		
		//delete element from cache
		if (current.getPrev() == null){
			//case 1: element is first element in the list
			if (current.getNext() == null){
				//case 1a: no collisions, simply delete the element
				vertexCache[hashValue] = null;
			} else {
				//case 1b: element has successor, put its successor as first element
				vertexCache[hashValue] = current.getNext();
			}
		} else {
			//case 2: element is not first element in the list
			current.getPrev().setNext(current.getNext());
			if (!(current.getNext() == null)){
				//if current has successor, adjust its pointer to its predecessor
				current.getNext().setPrev(current.getPrev());
			}
		}
		
		vertexCacheEntries--;
	}
	
	/**
	 * Removes an edge from the cache
	 * 
	 * @param edgeId the id of the edge to be deleted
	 */
	public void removeEdgeFromStorage(int edgeId) {
		int hashValue = edgeHashValue(edgeId);
		
		//retrieve first element in bucket
		CacheEntry<EdgeImpl> current = edgeCache[hashValue];
		
		//look for element to be deleted
		while(!current.hasKey(edgeId)){
			current = current.getNext();
		}
		
		//delete element from cache
		if (current.getPrev() == null){
			//case 1: element is first element in the list
			if (current.getNext() == null){
				//case 1a: no collisions, simply delete the element
				edgeCache[hashValue] = null;
			} else {
				//case 1b: element has successor, put its successor as first element
				edgeCache[hashValue] = current.getNext();
			}
		} else {
			//case 2: element is not first element in the list
			current.getPrev().setNext(current.getNext());
			if (!(current.getNext() == null)){
				//if current has successor, adjust its pointer to its predecessor
				current.getNext().setPrev(current.getPrev());
			}
		}
		
		edgeCacheEntries--;
	}

	/**
	 * Removes an incidence from the cache
	 * 
	 * @param incidenceId the id of the incidence to be deleted
	 */
	public void removeIncidenceFromStorage(int incidenceId) {
		int hashValue = incidenceHashValue(incidenceId);
		
		//retrieve first element in bucket
		CacheEntry<IncidenceImpl> current = incidenceCache[hashValue];
		
		//look for element to be deleted
		while(!current.hasKey(incidenceId)){
			current = current.getNext();
		}
		
		//delete element from cache
		if (current.getPrev() == null){
			//case 1: element is first element in the list
			if (current.getNext() == null){
				//case 1a: no collisions, simply delete the element
				incidenceCache[hashValue] = null;
			} else {
				//case 1b: element has successor, put its successor as first element
				incidenceCache[hashValue] = current.getNext();
			}
		} else {
			//case 2: element is not first element in the list
			current.getPrev().setNext(current.getNext());
			if (!(current.getNext() == null)){
				//if current has successor, adjust its pointer to its predecessor
				current.getNext().setPrev(current.getPrev());
			}
		}
		
		incidenceCacheEntries--;
	}
	
	//---- Methods to access other attributes of cached graph elements and incidences ----
	
	//These methods are taken over from distributed.MemStoreManager and remain unchanged

	@Override
	public long getSigmaIdOfVertexId(int localElementId) throws RemoteException {
		return ((VertexImpl) getVertexObject(localElementId)).getSigmaId();
	}



	@Override
	public void setSigmaIdOfVertexId(int localElementId, long globalSigmaId)
			throws RemoteException {
		((VertexImpl) getVertexObject(localElementId)).setSigmaId(globalSigmaId);
	}



	@Override
	public int getKappaOfVertexId(int localElementId) throws RemoteException {
		return ((VertexImpl) getVertexObject(localElementId)).getKappa();
	}



	@Override
	public void setKappaOfVertexId(int localElementId, int kappa)
			throws RemoteException {
		 ((VertexImpl) getVertexObject(localElementId)).setKappa(kappa);
	}



	@Override
	public long getSigmaIdOfEdgeId(int localElementId) throws RemoteException {
		return ((EdgeImpl) getEdgeObject(localElementId)).getSigmaId();
	}



	@Override
	public void setSigmaIdOfEdgeId(int localElementId, long globalSigmaId)
			throws RemoteException {
		((EdgeImpl) getEdgeObject(localElementId)).setSigmaId(globalSigmaId);
	}



	@Override
	public int getKappaOfEdgeId(int localElementId) throws RemoteException {
		return ((EdgeImpl) getEdgeObject(localElementId)).getKappa();
	}



	@Override
	public void setKappaOfEdgeId(int localElementId, int kappa)
			throws RemoteException {
		((EdgeImpl) getEdgeObject(localElementId)).setKappa(kappa);
	}



	@Override
	public int getVertexTypeId(int localVertexId) throws RemoteException {
		return ((VertexImpl) getVertexObject(localVertexId)).getType().getId();
	}



	@Override
	public long getNextVertexId(int localVertexId) throws RemoteException {
		return ((VertexImpl) getVertexObject(localVertexId)).getNextElementId();
	}



	@Override
	public void setNextVertexId(int localVertexId, long nextVertexId)
			throws RemoteException {
		((VertexImpl) getVertexObject(localVertexId)).setNextElementId(nextVertexId);
	}



	@Override
	public long getPreviousVertexId(int localVertexId) throws RemoteException {
		return ((VertexImpl) getVertexObject(localVertexId)).getPreviousElementId();
	}



	@Override
	public void setPreviousVertexId(int localVertexId, long previousVertexId)
			throws RemoteException {
		((VertexImpl) getVertexObject(localVertexId)).setNextElementId(previousVertexId);
	}



	@Override
	public int getEdgeTypeId(int localEdgeId) throws RemoteException {
		return ((EdgeImpl) getEdgeObject(localEdgeId)).getType().getId();
	}



	@Override
	public long getNextEdgeId(int localEdgeId) throws RemoteException {
		return ((EdgeImpl) getEdgeObject(localEdgeId)).getNextElementId();
	}



	@Override
	public void setNextEdgeId(int localEdgeId, long nextEdgeId)
			throws RemoteException {
		((EdgeImpl) getEdgeObject(localEdgeId)).setNextElementId(nextEdgeId);
	}



	@Override
	public long getPreviousEdgeId(int localEdgeId) throws RemoteException {
		return ((EdgeImpl) getEdgeObject(localEdgeId)).getPreviousElementId();
	}



	@Override
	public void setPreviousEdgeId(int localEdgeId, long previousEdgeId)
			throws RemoteException {
		((EdgeImpl) getEdgeObject(localEdgeId)).setNextElementId(previousEdgeId);
	}




	@Override
	public long getFirstIncidenceIdAtVertexId(int localVertexId)
			throws RemoteException {
		return ((VertexImpl) getVertexObject(localVertexId)).getFirstIncidenceId();
	}



	@Override
	public void setFirstIncidenceIdAtVertexId(int localVertexId, long incidenceId)
			throws RemoteException {
		((VertexImpl) getVertexObject(localVertexId)).setFirstIncidenceId(incidenceId);
	}



	@Override
	public long getLastIncidenceIdAtVertexId(int localVertexId)
			throws RemoteException {
		return ((VertexImpl) getVertexObject(localVertexId)).getLastIncidenceId();
	}



	@Override
	public void setLastIncidenceIdAtVertexId(int localVertexId, long incidenceId)
			throws RemoteException {
		((VertexImpl) getVertexObject(localVertexId)).setLastIncidenceId(incidenceId);
	}



	@Override
	public long getNextIncidenceIdAtVertexId(int incidenceId)
			throws RemoteException {
		return ((IncidenceImpl) getIncidenceObject(incidenceId)).getNextIncidenceIdAtVertex();
	}



	@Override
	public long getPreviousIncidenceIdAtVertexId(int incidenceId)
			throws RemoteException {
		return ((IncidenceImpl) getIncidenceObject(incidenceId)).getPreviousIncidenceIdAtVertex();
	}



	@Override
	public void setNextIncidenceAtVertexId(int incidenceId, long nextIncId)
			throws RemoteException {
		((IncidenceImpl) getIncidenceObject(incidenceId)).setNextIncidenceIdAtVertex(nextIncId);
	}



	@Override
	public void setPreviousIncidenceAtVertexId(int incidenceId,	long previousIncId) throws RemoteException {
		((IncidenceImpl) getIncidenceObject(incidenceId)).setPreviousIncidenceIdAtVertex(previousIncId);
	}



	@Override
	public long getConnectedVertexId(int incidenceId) throws RemoteException {
		return ((IncidenceImpl) getIncidenceObject(incidenceId)).getIncidentVertexId();
	}



	@Override
	public long getIncidenceListVersionOfVertexId(int vertexId)
			throws RemoteException {
		return ((VertexImpl) getVertexObject(vertexId)).getIncidenceListVersion();
	}



	@Override
	public void increaseIncidenceListVersionOfVertexId(int vertexId)
			throws RemoteException {
		((VertexImpl) getVertexObject(vertexId)).increaseIncidenceListVersion();
		
	}



	@Override
	public long getFirstIncidenceIdAtEdgeId(int localEdgeId)
			throws RemoteException {
		return ((EdgeImpl) getEdgeObject(localEdgeId)).getFirstIncidenceId();
	}



	@Override
	public void setFirstIncidenceIdAtEdgeId(int localEdgeId, long incidenceId)
			throws RemoteException {
		((EdgeImpl) getEdgeObject(localEdgeId)).setFirstIncidenceId(incidenceId);
	}



	@Override
	public long getLastIncidenceIdAtEdgeId(int localEdgeId)
			throws RemoteException {
		return ((EdgeImpl) getEdgeObject(localEdgeId)).getLastIncidenceId();
	}



	@Override
	public void setLastIncidenceIdAtEdgeId(int localEdgeId, long incidenceId)
			throws RemoteException {
		((EdgeImpl) getEdgeObject(localEdgeId)).setLastIncidenceId(incidenceId);
	}



	@Override
	public long getNextIncidenceIdAtEdgeId(int incidenceId)
			throws RemoteException {
		return ((IncidenceImpl) getIncidenceObject(incidenceId)).getNextIncidenceIdAtEdge();
	}



	@Override
	public long getPreviousIncidenceIdAtEdgeId(int incidenceId)
			throws RemoteException {
		return ((IncidenceImpl) getIncidenceObject(incidenceId)).getPreviousIncidenceIdAtEdge();
	}



	@Override
	public void setNextIncidenceAtEdgeId(int incidenceId, long nextIncId)
			throws RemoteException {
		((IncidenceImpl) getIncidenceObject(incidenceId)).setNextIncidenceIdAtEdge(nextIncId);
	}



	@Override
	public void setPreviousIncidenceAtEdgeId(int incidenceId,	long previousIncId) throws RemoteException {
		((IncidenceImpl) getIncidenceObject(incidenceId)).setPreviousIncidenceIdAtEdge(previousIncId);
	}



	@Override
	public long getConnectedEdgeId(int incidenceId) throws RemoteException {
		return ((IncidenceImpl) getIncidenceObject(incidenceId)).getIncidentEdgeId();
	}



	@Override
	public long getIncidenceListVersionOfEdgeId(int edgeId)
			throws RemoteException {
		return ((EdgeImpl) getEdgeObject(edgeId)).getIncidenceListVersion();
	}



	@Override
	public void increaseIncidenceListVersionOfEdgeId(int edgeId)
			throws RemoteException {
		((EdgeImpl) getEdgeObject(edgeId)).increaseIncidenceListVersion();

	}



	@Override
	public int getIncidenceTypeId(int localIncidenceId) throws RemoteException {
		return ((IncidenceImpl)getIncidenceObject(localIncidenceId)).getType().getId();
	}


	// ---- Methods to manage the cache ----
	
	/**
	 * Tests the load factor of the vertex cache an does a rehashing if it exceeds 
	 * MAX_LOAD_FACTOR.
	 */
	protected void testVertexLoadFactor(){		
		if (vertexCacheEntries > vertexCacheMaxEntries){
			rehashVertexCache();
		}
	}
	
	/**
	 * Doubles the size of the vertex cache and rehashes all vertices
	 */
	protected void rehashVertexCache(){
		//max allowed cache size is 2^31 because Multiplication Method requires that
		//log(2) of cache size is smaller then the number of bits in an Integer
		if (vertexCacheExp >= 31) return;
		
		vertexCacheExp++;
		int vertexCacheSize = (int) Math.pow(2, vertexCacheExp);
		vertexCacheMaxEntries *= 2;
		
	    CacheEntry<VertexImpl>[] newCache = new CacheEntry[vertexCacheSize];
	    
	    for (int i = 0; i < vertexCache.length; i++){
	    	if (vertexCache[i] != null){
	    		
	    		//Put vertices in current bucket on a stack
	    		CacheEntry<VertexImpl> current = vertexCache[i];
	    		Stack<CacheEntry<VertexImpl>> vertexEntries = new Stack<CacheEntry<VertexImpl>>();
	    		vertexEntries.push(current);
	    		
	    		while (current.getNext() != null){
	    			current = current.getNext();
	    			vertexEntries.push(current);
	    		}
	    		
	    		//rehash all the vertices on the stack
	    		while(!vertexEntries.empty()){
	    			current = vertexEntries.pop();
	    			current.setNext(null);
	    			current.setPrev(null);
	    			putVertex(current, newCache);
	    		}
	    	}
	    }
	    
	    vertexCache = newCache;
	}
	
	/**
	 * Tests the load factor of the edge cache an does a rehashing if it exceeds 
	 * MAX_LOAD_FACTOR.
	 */
	protected void testEdgeLoadFactor(){		
		if (edgeCacheEntries > edgeCacheMaxEntries){
			rehashEdgeCache();
		}
	}
	
	/**
	 * Doubles the size of the vertex cache and rehashes all vertices
	 */
	protected void rehashEdgeCache(){
		//max allowed cache size is 2^31 because Multiplication Method requires that
		//log(2) of cache size is smaller then the number of bits in an Integer
		if (edgeCacheExp >= 31) return;
		
		edgeCacheExp++;
		int edgeCacheSize = (int) Math.pow(2, edgeCacheExp);
		edgeCacheMaxEntries *= 2;
		
	    CacheEntry<EdgeImpl>[] newCache = new CacheEntry[edgeCacheSize];
	    
	    for (int i = 0; i < edgeCache.length; i++){
	    	if (edgeCache[i] != null){
	    		
	    		//Put edges in current bucket on a stack
	    		CacheEntry<EdgeImpl> current = edgeCache[i];
	    		Stack<CacheEntry<EdgeImpl>> edgeEntries = new Stack<CacheEntry<EdgeImpl>>();
	    		edgeEntries.push(current);
	    		
	    		while (current.getNext() != null){
	    			current = current.getNext();
	    			edgeEntries.push(current);
	    		}
	    		
	    		//rehash all the vertices on the stack
	    		while(!edgeEntries.empty()){
	    			current = edgeEntries.pop();
	    			current.setNext(null);
	    			current.setPrev(null);
	    			putEdge(current, newCache);
	    		}
	    	}
	    }
	    
	    edgeCache = newCache;
	}
	
	/**
	 * Tests the load factor of the incidence cache an does a rehashing if it exceeds 
	 * MAX_LOAD_FACTOR.
	 */
	protected void testIncidenceLoadFactor(){		
		if (incidenceCacheEntries > incidenceCacheMaxEntries){
			rehashIncidenceCache();
		}
	}
	
	/**
	 * Doubles the size of the incidence cache and rehashes all vertices
	 */
	protected void rehashIncidenceCache(){
		//max allowed cache size is 2^31 because Multiplication Method requires that
		//log(2) of cache size is smaller then the number of bits in an Integer
		if (incidenceCacheExp >= 31) return;
		
		incidenceCacheExp++;
		int incidenceCacheSize = (int) Math.pow(2, incidenceCacheExp);
		incidenceCacheMaxEntries *= 2;
		
		CacheEntry<IncidenceImpl>[] newCache = new CacheEntry[incidenceCacheSize];
	    
	    for (int i = 0; i < incidenceCache.length; i++){
	    	if (incidenceCache[i] != null){
	    		
	    		//Put edges in current bucket on a stack
	    		CacheEntry<IncidenceImpl> current = incidenceCache[i];
	    		Stack<CacheEntry<IncidenceImpl>> incidenceEntries = new Stack<CacheEntry<IncidenceImpl>>();
	    		incidenceEntries.push(current);
	    		
	    		while (current.getNext() != null){
	    			current = current.getNext();
	    			incidenceEntries.push(current);
	    		}
	    		
	    		//rehash all the vertices on the stack
	    		while(!incidenceEntries.empty()){
	    			current = incidenceEntries.pop();
	    			current.setNext(null);
	    			current.setPrev(null);
	    			putIncidence(current, newCache);
	    		}
	    	}
	    }
	    
	    incidenceCache = newCache;
	}
	
	/**
	 * Calculates the hash value for a vertex using Cormen's Multiplication Method
	 * 
	 * @param hashCode
	 *            the hashCode of the vertex to be cached
	 */
	protected int vertexHashValue(int hashCode){
		int x = hashCode * S;
		return x >>> (32 - vertexCacheExp);
	}
	
	/**
	 * Calculates the hash value for an edge using Cormen's Multiplication Method
	 * 
	 * @param hashCode
	 *            the hashCode of the edge to be cached
	 */
	protected int edgeHashValue(int hashCode){
		int x = hashCode * S;
		return x >>> (32 - edgeCacheExp);
	}
	
	/**
	 * Calculates the hash value for an incidence using Cormen's Multiplication Method
	 * 
	 * @param hashCode
	 *            the hashCode of the incidence to be cached
	 */
	protected int incidenceHashValue(int hashCode){
		int x = hashCode * S;
		return x >>> (32 - incidenceCacheExp);
	}
}
