package de.uni_koblenz.jgralab.impl;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
	 * ========================================================================
	 * Methods to access graph properties
	 * ========================================================================
	 */

	public String getUniqueGraphId() throws RemoteException;

	public long getGraphVersion() throws RemoteException;

	public void setGraphVersion(long graphVersion) throws RemoteException;

	public void graphModified() throws RemoteException;

	/**
	 * Returns the id of the type of the subgraph identified by the given
	 * <code>subgraphId</code>
	 * 
	 * @return
	 */
	public int getGraphTypeId(long subgraphId) throws RemoteException;

	public long getIdOfParentDistributedGraph() throws RemoteException;

	public void setLoading(boolean isLoading) throws RemoteException;

	public boolean isLoading() throws RemoteException;

	public abstract int internalCreatePartialGraphInEntity(
			String remoteHostname, long parentGlobalEntityId,
			ParentEntityKind entityKind) throws RemoteException;

	public long createPartialGraphInGraph(long parentGraphId, String hostname)
			throws RemoteException;

	public long createPartialGraphInVertex(long containingVertexId,
			String hostname) throws RemoteException;

	public long createPartialGraphInEdge(long containingVertexId,
			String hostname) throws RemoteException;

	public void registerPartialGraph(int id, String hostname)
			throws RemoteException;

	public List<Integer> getPartialGraphIds(long globalSubgraphId)
			throws RemoteException;

	public void addPartialGraphId(long globalSubgraphId, int newPartialGraphId)
			throws RemoteException;

	public void decreaseECount(long subgraphId) throws RemoteException;

	public void increaseECount(long subgraphId) throws RemoteException;

	public void increaseICount(long globalSubgraphId) throws RemoteException;

	public void decreaseICount(long globalSubgraphId) throws RemoteException;

	public int getLocalPartialGraphId() throws RemoteException;

	/**
	 * 
	 * @param globalSubgraphId
	 * @return the id of the element (positive for vertices, negative for edges)
	 *         containing the graph identified by its
	 *         <code>globalSubgraphId</code>
	 */
	public long getContainingElementId(long globalSubgraphId)
			throws RemoteException;

	/*
	 * ====================================================================
	 * Methods to access hierarchy
	 * ====================================================================
	 */

	public int getKappaOfVertexId(long elementId) throws RemoteException;

	public void setKappaOfVertexId(long elementId, int kappa)
			throws RemoteException;

	public int getKappaOfEdgeId(long elementId) throws RemoteException;

	public void setKappaOfEdgeId(long elementId, int kappa)
			throws RemoteException;

	public long getSigmaIdOfVertexId(long globalVertexId)
			throws RemoteException;

	public long getSigmaIdOfEdgeId(long globalEdgeId) throws RemoteException;

	public void setSigmaIdOfVertexId(long globalVertexId, long globalSigmaId)
			throws RemoteException;

	public void setSigmaIdOfEdgeId(long globalEdgeId, long globalSigmaId)
			throws RemoteException;

	/**
	 * Creates a new subordinate graph for the element identified by the given
	 * id
	 * 
	 * @param id
	 * @return
	 */
	public long createLocalSubordinateGraphInVertex(long id)
			throws RemoteException;

	/**
	 * Creates a new subordinate graph for the element identified by the given
	 * id
	 * 
	 * @param id
	 * @return
	 */
	public long createLocalSubordinateGraphInEdge(long id)
			throws RemoteException;

	/*
	 * =====================================================================
	 * Methods to access vertex sequence
	 * =====================================================================
	 */

	public long getMaxVCount() throws RemoteException;

	public long getVertexListVersion() throws RemoteException;

	public void vertexListModified() throws RemoteException;

	public long getVCount(long subgraphId) throws RemoteException;

	public void setVCount(long globalSubgraphId, long count)
			throws RemoteException;

	public void increaseVCount(long subgraphId) throws RemoteException;

	public void decreaseVCount(long subgraphId) throws RemoteException;

	/**
	 * Creates an edge of the edge class identified by its id
	 * <code>edgeClassId</code> and adds it to the graph
	 * 
	 * @param edgeClassId
	 * @return the global id of the newly created edge
	 */
	public long createVertex(int edgeClassId) throws RemoteException;

	/**
	 * Creates a vertex with the id globalVId
	 * 
	 * @param globalVId
	 * @return
	 */
	public long createVertex(int edgeClassId, long globalVId)
			throws RemoteException;

	public void deleteVertex(long globalVertexId) throws RemoteException;

	public boolean containsVertexId(long id) throws RemoteException;

	/**
	 * Puts the vertex identified by <code>id2</code> directly before the vertex
	 * identified by <code>id1</code>
	 * 
	 * @param id1
	 *            the global id of the anchor vertex
	 * @param id2
	 *            the global id of the vertex to be moved
	 */
	public void putVertexBefore(long id1, long id2) throws RemoteException;

	/**
	 * Puts the vertex identified by <code>id2</code> directly after the vertex
	 * identified by <code>id1</code>
	 * 
	 * @param id1
	 *            the global id of the anchor vertex
	 * @param id2
	 *            the global id of the vertex to be moved
	 */
	public void putVertexAfter(long id, long id2) throws RemoteException;

	public long getNextVertexId(long vertexId) throws RemoteException;

	public long getPreviousVertexId(long vertexId) throws RemoteException;

	public int getVertexTypeId(long id) throws RemoteException;

	public long getFirstVertexId(long globalSubgraphId) throws RemoteException;

	public long getLastVertexId(long globalSubgraphId) throws RemoteException;

	public void setFirstVertexId(long globalSubgraphId, long id)
			throws RemoteException;

	public void setLastVertexId(long globalSubgraphId, long id)
			throws RemoteException;

	/*
	 * ========================================================================
	 * Methods to access edge sequence
	 * ========================================================================
	 */

	public void edgeListModified() throws RemoteException;

	public long getEdgeListVersion() throws RemoteException;

	public long getECount(long globalSubgraphId) throws RemoteException;

	public void setECount(long globalSubgraphId, long count)
			throws RemoteException;

	public long getMaxECount() throws RemoteException;

	/**
	 * Creates an edge of the edge class identified by its id
	 * <code>edgeClassId</code> and adds it to the graph
	 * 
	 * @param edgeClassId
	 * @return the global id of the newly created edge
	 */
	public long createEdge(int edgeClassId) throws RemoteException;

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
	public long createEdge(int edgeClassId, long edgeId) throws RemoteException;

	public void deleteEdge(long globalEdgeId) throws RemoteException;

	public boolean containsEdgeId(long id) throws RemoteException;

	/**
	 * Puts the edge identified by <code>id2</code> directly after the edge
	 * identified by <code>id1</code>
	 * 
	 * @param id1
	 *            the global id of the anchor edge
	 * @param id2
	 *            the global id of the edge to be moved
	 */
	public void putEdgeAfter(long id1, long id2) throws RemoteException;

	/**
	 * Puts the edge identified by <code>id2</code> directly after the edge
	 * identified by <code>id1</code>
	 * 
	 * @param id1
	 *            the global id of the anchor edge
	 * @param id2
	 *            the global id of the edge to be moved
	 */
	public void putEdgeBefore(long id1, long id2) throws RemoteException;

	public int getEdgeTypeId(long id) throws RemoteException;

	public long getFirstEdgeId(long globalSubgraphId) throws RemoteException;

	public long getLastEdgeId(long globalSubgraphId) throws RemoteException;

	public void setFirstEdgeId(long globalSubgraphId, long id)
			throws RemoteException;

	public void setLastEdgeId(long globalSubgraphId, long id)
			throws RemoteException;

	public long getNextEdgeId(long edgeId) throws RemoteException;

	public long getPreviousEdgeId(long edgeId) throws RemoteException;

	/*
	 * ========================================================================
	 * Methods to access Lambda sequence
	 * ========================================================================
	 */

	public long getIncidenceListVersionOfVertexId(long vertexId)
			throws RemoteException;

	public long getFirstIncidenceIdAtVertexId(long elemId)
			throws RemoteException;

	public long getLastIncidenceIdAtVertexId(long elemId)
			throws RemoteException;

	public long getNextIncidenceIdAtVertexId(long incId) throws RemoteException;

	public long getPreviousIncidenceIdAtVertexId(long globalIncidenceId)
			throws RemoteException;

	public void putIncidenceIdAfterAtVertexId(long id, long id2)
			throws RemoteException;

	public void putIncidenceIdBeforeAtVertexId(long id, long id2)
			throws RemoteException;

	public long getIncidenceListVersionOfEdgeId(long edgeId)
			throws RemoteException;

	public long getFirstIncidenceIdAtEdgeId(long elemId) throws RemoteException;

	public long getLastIncidenceIdAtEdgeId(long elemId) throws RemoteException;

	public long getNextIncidenceIdAtEdgeId(long incId) throws RemoteException;

	public long getPreviousIncidenceIdAtEdgeId(long incId)
			throws RemoteException;

	public void putIncidenceIdBeforeAtEdgeId(long targetId, long movedId)
			throws RemoteException;

	public void putIncidenceIdAfterAtEdgeId(long targetId, long movedId)
			throws RemoteException;

	public long getEdgeIdAtIncidenceId(long id) throws RemoteException;

	public long getVertexIdAtIncidenceId(long id) throws RemoteException;

	public long getICount(long globalSubgraphId) throws RemoteException;

	public int getIncidenceTypeId(long id) throws RemoteException;

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
	public long connect(int incidenceClassId, long vertexId, long edgeId)
			throws RemoteException;

	public long connect(int incidenceClassId, long vertexId, long edgeId,
			long incId) throws RemoteException;

	public void deleteIncidence(long id) throws RemoteException;

	/*
	 * ========================================================================
	 * Attribute Access
	 * ======================================================================
	 */

	public Object getGraphAttribute(String attributeName)
			throws RemoteException;

	public void setGraphAttribute(String attributeName, Object data)
			throws RemoteException;

	public Object getVertexAttribute(long elementId, String attributeName)
			throws RemoteException;

	public void setVertexAttribute(long elementId, String attributeName,
			Object data) throws RemoteException;

	public Object getEdgeAttribute(long elementId, String attributeName)
			throws RemoteException;

	public void setEdgeAttribute(long elementId, String attributeName,
			Object data) throws RemoteException;

	/*
	 * =====================================================================
	 * Methods to access domains
	 * =====================================================================
	 */

	public <T> JGraLabList<T> createList() throws RemoteException;

	public <T> JGraLabList<T> createList(Collection<? extends T> collection)
			throws RemoteException;

	public <T> JGraLabList<T> createList(int initialCapacity)
			throws RemoteException;

	public <T> JGraLabList<T> createList(int initialCapacity, float loadFactor)
			throws RemoteException;

	public <K, V> JGraLabMap<K, V> createMap() throws RemoteException;

	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity)
			throws RemoteException;

	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity,
			float loadFactor) throws RemoteException;

	public <K, V> JGraLabMap<K, V> createMap(Map<? extends K, ? extends V> map)
			throws RemoteException;

	public <T> JGraLabSet<T> createSet() throws RemoteException;

	public <T> JGraLabSet<T> createSet(Collection<? extends T> collection)
			throws RemoteException;

	public <T> JGraLabSet<T> createSet(int initialCapacity)
			throws RemoteException;

	public <T> JGraLabSet<T> createSet(int initialCapacity, float loadFactor)
			throws RemoteException;

	public <T extends Record> T createRecord(Class<T> recordClass,
			Map<String, Object> fields) throws RemoteException;

	public <T extends Record> T createRecord(Class<T> recordClass,
			Object... components) throws RemoteException;

	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io)
			throws RemoteException;

	public String getHostname(int partialGraphId) throws RemoteException;


	public long getTraversalContextSubgraphId() throws RemoteException;

	public void releaseTraversalContext() throws RemoteException;

	public void setTraversalContext(long globalId) throws RemoteException;

}
