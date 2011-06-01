package de.uni_koblenz.jgralab.impl.disk;

import java.rmi.Remote;
import java.util.Collection;
import java.util.Map;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.JGraLabList;
import de.uni_koblenz.jgralab.JGraLabMap;
import de.uni_koblenz.jgralab.JGraLabSet;
import de.uni_koblenz.jgralab.Record;

/**
 * This interface defines all methods needed to access properties of the graph
 * and its elements such as the first or last vertex of a graph, the incidence
 * sequence of vertices and edges and so on. The interface is implemented by
 * GraphDatabase, the dispatcher class managing the connections between the
 * single partial graphs of a distributed one and by DiskStorageManager,
 * controlling the local storage of single elements on the disk and in memory
 * 
 * @author dbildh
 * 
 */

public interface RemoteGraphDatabaseAccess extends Remote {

	/*
	 * ===================================================== Methods to access
	 * graph properties =====================================================
	 */

	public String getUniqueGraphId();

	public long getGraphVersion();

	public void setGraphVersion(long graphVersion);

	public void graphModified();

	/**
	 * Returns the id of the type of the subgraph identified by the given
	 * <code>subgraphId</code>
	 * 
	 * @return
	 */
	public int getGraphTypeId(long subgraphId);

	public long getIdOfParentDistributedGraph();

	public void setLoading(boolean isLoading);

	public boolean isLoading();

	public long createPartialGraph(Class<? extends Graph> m1Class,
			String hostname);

	public void registerPartialGraph(int id, String hostname);

	/**
	 * 
	 * @param globalSubgraphId
	 * @return the id of the element (positive for vertices, negative for edges)
	 *         containing the graph identified by its
	 *         <code>globalSubgraphId</code>
	 */
	public long getContainingElementId(long globalSubgraphId);

	/*
	 * ===================================================== Methods to access
	 * hierarchy =====================================================
	 */

	public long getSigma(long elemId);

	public void setSigma(long elementId, long sigmaId);

	public int getKappa(long elementId);

	public void setKappa(long elementId, int kappa);

	public long getIncidenceListVersion(long elementId);

	/**
	 * Creates a new subordinate graph for the element identified by the given
	 * id
	 * 
	 * @param id
	 * @return
	 */
	public long createSubordinateGraph(long id);

	/*
	 * ===================================================== Methods to access
	 * vertex sequence =====================================================
	 */

	public long getMaxVCount();

	public long getVertexListVersion();

	public void vertexListModified();

	public long getVCount(long subgraphId);

	public void increaseVCount(long subgraphId);

	public void decreaseVCount(long subgraphId);

	/**
	 * Creates an edge of the edge class identified by its id
	 * <code>edgeClassId</code> and adds it to the graph
	 * 
	 * @param edgeClassId
	 * @return the global id of the newly created edge
	 */
	public long createVertex(int edgeClassId);

	/**
	 * Creates a vertex with the id globalVId
	 * 
	 * @param globalVId
	 * @return
	 */
	public long createVertex(int edgeClassId, long globalVId);

	public void deleteVertex(long globalVertexId);

	public boolean containsVertexId(long id);

	/**
	 * Puts the vertex identified by <code>id2</code> directly before the vertex
	 * identified by <code>id1</code>
	 * 
	 * @param id1
	 *            the global id of the anchor vertex
	 * @param id2
	 *            the global id of the vertex to be moved
	 */
	public void putVertexBefore(long id1, long id2);

	/**
	 * Puts the vertex identified by <code>id2</code> directly after the vertex
	 * identified by <code>id1</code>
	 * 
	 * @param id1
	 *            the global id of the anchor vertex
	 * @param id2
	 *            the global id of the vertex to be moved
	 */
	public void putVertexAfter(long id, long id2);

	public long getNextVertexId(long vertexId);

	public long getPreviousVertexId(long vertexId);

	public int getVertexTypeId(long id);

	public long getFirstVertexId(long globalSubgraphId);

	public long getLastVertexId(long globalSubgraphId);

	public void setFirstVertexId(long globalSubgraphId, long id);

	public void setLastVertexId(long globalSubgraphId, long id);

	/*
	 * ===================================================== Methods to access
	 * edge sequence =====================================================
	 */

	public void edgeListModified();

	public long getEdgeListVersion();

	public long getECount(long globalSubgraphId);

	public void setECount(long globalSubgraphId, long count);

	public long getMaxECount();

	/**
	 * Creates an edge of the edge class identified by its id
	 * <code>edgeClassId</code> and adds it to the graph
	 * 
	 * @param edgeClassId
	 * @return the global id of the newly created edge
	 */
	public long createEdge(int edgeClassId);

	/**
	 * Creates an edge of the edge class identified by its id
	 * <code>edgeClassId</code> and adds it to the graph, the edge id is set to
	 * the given id. This method may be called only during loading a (partial)
	 * graph
	 * 
	 * @param edgeClassId
	 *            the id identifying the edge class
	 * @param edgeId
	 *            the global edge id
	 * @return the global id of the newly created edge
	 */
	public long createEdge(int edgeClassId, long edgeId);

	public void deleteEdge(long globalEdgeId);

	public boolean containsEdgeId(long id);

	/**
	 * Puts the edge identified by <code>id2</code> directly after the edge
	 * identified by <code>id1</code>
	 * 
	 * @param id1
	 *            the global id of the anchor edge
	 * @param id2
	 *            the global id of the edge to be moved
	 */
	public void putEdgeAfter(long id1, long id2);

	/**
	 * Puts the edge identified by <code>id2</code> directly after the edge
	 * identified by <code>id1</code>
	 * 
	 * @param id1
	 *            the global id of the anchor edge
	 * @param id2
	 *            the global id of the edge to be moved
	 */
	public void putEdgeBefore(long id1, long id2);

	public int getEdgeTypeId(long id);

	public long getFirstEdgeId(long globalSubgraphId);

	public long getLastEdgeId(long globalSubgraphId);

	public void setFirstEdgeId(long globalSubgraphId, long id);

	public void setLastEdgeId(long globalSubgraphId, long id);

	public long getNextEdgeId(long edgeId);

	public long getPreviousEdgeId(long edgeId);

	/*
	 * ===================================================== Methods to access
	 * Lambda sequence =====================================================
	 */

	public long getICount(long globalSubgraphId);

	public long getFirstIncidenceIdAtVertexId(long elemId);

	public long getLastIncidenceIdAtVertexId(long elemId);

	public long getNextIncidenceIdAtVertexId(long incId);

	public long getPreviousIncidenceIdAtVertexId(long globalIncidenceId);

	public void putIncidenceIdAfterAtVertexId(long id, long id2);

	public void putIncidenceIdBeforeAtVertexId(long id, long id2);

	public long getFirstIncidenceIdAtEdgeId(long elemId);

	public long getLastIncidenceIdAtEdgeId(long elemId);

	public long getNextIncidenceIdAtEdgeId(long incId);

	public long getPreviousIncidenceIdAtEdgeId(long incId);

	public void putIncidenceIdBeforeAtEdgeId(long targetId, long movedId);

	public void putIncidenceIdAfterAtEdgeId(long targetId, long movedId);

	public int getIncidenceTypeId(long id);

	/**
	 * Creates a new incidence of the IncidenceClass identified by the id
	 * <code>incidenceClassId</code> between the vertex identified by
	 * <code>vertexId</code> and the edge identified by <code>edgeId</code>. The
	 * method creates the local incidence object on the graph database storing
	 * the edge and updates the lambda sequences of edge and vertex
	 * 
	 * @param incidenceClassId
	 * @param vertexId
	 * @param edgeId
	 * @return
	 */
	public long connect(int incidenceClassId, long vertexId, long edgeId);

	public long connect(int incidenceClassId, long vertexId, long edgeId,
			long incId);

	public void deleteIncidence(long id);

	/*
	 * ===================================================== Methods to access
	 * domains =====================================================
	 */

	public <T> JGraLabList<T> createList();

	public <T> JGraLabList<T> createList(Collection<? extends T> collection);

	public <T> JGraLabList<T> createList(int initialCapacity);

	public <T> JGraLabList<T> createList(int initialCapacity, float loadFactor);

	public <K, V> JGraLabMap<K, V> createMap();

	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity);

	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity,
			float loadFactor);

	public <K, V> JGraLabMap<K, V> createMap(Map<? extends K, ? extends V> map);

	public <T> JGraLabSet<T> createSet();

	public <T> JGraLabSet<T> createSet(Collection<? extends T> collection);

	public <T> JGraLabSet<T> createSet(int initialCapacity);

	public <T> JGraLabSet<T> createSet(int initialCapacity, float loadFactor);

	public <T extends Record> T createRecord(Class<T> recordClass,
			Map<String, Object> fields);

	public <T extends Record> T createRecord(Class<T> recordClass,
			Object[] components);

	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io);

}
