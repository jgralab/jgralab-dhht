package de.uni_koblenz.jgralab.impl.disk;

import java.rmi.Remote;
import java.util.Collection;
import java.util.Map;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLabList;
import de.uni_koblenz.jgralab.JGraLabMap;
import de.uni_koblenz.jgralab.JGraLabSet;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;

/**
 * This interface defines all methods needed to access properties of the graph and its elements
 * such as the first or last vertex of a graph, the incidence sequence of vertices and edges
 * and so on. The interface is implemented by GraphDatabase, the dispatcher class managing 
 * the connections between the single partial graphs of a distributed one and by DiskStorageManager,
 * controlling the local storage of single elements on the disk and in memory
 * @author dbildh
 *
 */

public interface RemoteGraphDatabaseAccess extends Remote {

	/**
	 * Creates a vertex with the id globalVId
	 * @param globalVId
	 * @return
	 */
	public void createVertex(long globalVId);
	

	
	/**
	 * Sets the first {@link Incidence} of this {@link GraphElement} to
	 * <code>firstIncidence</code>.
	 * 
	 * @param firstIncidence
	 *            {@link IncidenceImpl}
	 */
	public void setFirstIncidence(long elemId, long incidenceId);
	
	
	public void setLastIncidence(long elemId, long incidenceId);


	public void incidenceListModified(long elemId);


	public int getSigma(long elemId);

	
	public void setSigma(long elementId, long sigmaId);
	
	
	public int getKappa(long elementId); 
	
	public void setKappa(long elementId, int kappa);
	
	public void setIncidenceListVersion(long elementId, long incidenceListVersion);

	public long getIncidenceListVersion(long elementId);
	
	public abstract void removeEdgeFromDatabase(long edgeId);

	public abstract void removeVertexFromDatabase(long vertexId);

	public int getIdOfParentDistributedGraph();

	public void edgeListModified();


	public void graphModified();


	public int getECount();


	public int getMaxECount();


	public int getMaxVCount();


	public long getVertexListVersion();


	public int getVCount();


	public void setVCount(long count);


	public void setLoading(boolean isLoading);


	public long getEdgeListVersion();


	public void deleteEdge(Edge e);


	public void deleteVertex(Vertex v);
	

	public <T> JGraLabList<T> createList();
	

	public <T> JGraLabList<T> createList(Collection<? extends T> collection);
	
	
	public <T> JGraLabList<T> createList(int initialCapacity);
	
	
	public <T> JGraLabList<T> createList(int initialCapacity, float loadFactor);
	
	
	public <K, V> JGraLabMap<K, V> createMap();

	
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity);

	
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity, float loadFactor);

	
	public <K, V> JGraLabMap<K, V> createMap(Map<? extends K, ? extends V> map);
	
	
	public <T> JGraLabSet<T> createSet();
	
	
	public <T> JGraLabSet<T> createSet(Collection<? extends T> collection);


	public <T> JGraLabSet<T> createSet(int initialCapacity);


	public <T> JGraLabSet<T> createSet(int initialCapacity, float loadFactor);
	

	public <T extends Record> T createRecord(Class<T> recordClass, Map<String, Object> fields);


	public <T extends Record> T createRecord(Class<T> recordClass, Object[] components);


	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io);


	/**
	 * Puts the edge identified by <code>id2</code> directly after the edge 
	 * identified by <code>id1</code>
	 * @param id1 the global id of the anchor edge
	 * @param id2 the global id of the edge to be moved
	 */
	public void putEdgeAfterInGraph(long id1, long id2);

	/**
	 * Puts the edge identified by <code>id2</code> directly after the edge 
	 * identified by <code>id1</code>
	 * @param id1 the global id of the anchor edge
	 * @param id2 the global id of the edge to be moved
	 */
	public void putEdgeBeforeInGraph(long id1, long id2);

	/**
	 * Puts the vertex identified by <code>id2</code> directly before the vertex 
	 * identified by <code>id1</code>
	 * @param id1 the global id of the anchor vertex
	 * @param id2 the global id of the vertex to be moved
	 */
	public void putVertexBeforeInGraph(long id1, long id2);

	/**
	 * Puts the vertex identified by <code>id2</code> directly after the vertex 
	 * identified by <code>id1</code>
	 * @param id1 the global id of the anchor vertex
	 * @param id2 the global id of the vertex to be moved
	 */
	public void putVertexAfterInGraph(long id, long id2);

	/**
	 * 
	 */
	public void vertexListModified();


	public void setGraphVersion(long graphVersion);
	
}
