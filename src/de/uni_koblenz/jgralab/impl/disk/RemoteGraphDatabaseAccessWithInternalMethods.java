package de.uni_koblenz.jgralab.impl.disk;


/**
 * As an extension of RemoteGraphDatabaseAccess, this interface declares all remote accessible methods
 * which should only be called by a database in the distributed environment and which should not be
 * visible to the objects representing vertices, edges and graphs.
 *
 */
public interface RemoteGraphDatabaseAccessWithInternalMethods extends RemoteGraphDatabaseAccess {

	
	/* =====================================================
	 * Methods to access graph database properties
	 * ===================================================== */
	
	public RemoteDiskStorageAccess getLocalDiskStorage();
	
	
	
	
	/* =====================================================
	 * Methods to notify about changes in vertex, edge and incidence sequences
	 * ===================================================== */
	
	/**
	 * Notifies this graph database that the vertex identified by its global vertex id
	 * <code>vertexId</code> has been added to the distributed graph
	 */
	public void internalNotifyVertexAdded(long vertexId);
	
	/**
	 * Notifies this graph database that the vertex identified by its global vertex id
	 * <code>vertexId</code> will be deleted from the distributed graph. When this
	 * method is called, the vertex still exists by will be deleted after this method
	 * returns.
	 */
	public void internalNotifyVertexDeleted(long vertexId);

	
	/**
	 * Notifies this graph database that the edge identified by its global edge id
	 * <code>edgeId</code> has been added to the distributed graph
	 */
	public void internalNotifyEdgeAdded(long edgeId);
	
	/**
	 * Notifies this graph database that the edge identified by its global edge id
	 * <code>edgeId</code> will be deleted from the distributed graph. When this
	 * method is called, the edge still exists by will be deleted after this method
	 * returns.
	 */
	public void internalNotifyEdgeDeleted(long edgeId);
	
	/**
	 * Notifies this graph database that the incidence identified by its global incidence id
	 * <code>incidenceId</code> has been added to the distributed graph
	 */
	public void internalNotifyIncidenceAdded(long incidenceId);
	
	/**
	 * Notifies this graph database that the incidence identified by its global incidence id
	 * <code>incidenceId</code> will be deleted from the distributed graph. When this
	 * method is called, the incidence still exists by will be deleted after this method
	 * returns.
	 */
	public void internalNotifyIncidenceDeleted(long incidenceId);

	
	
	/* =====================================================
	 * Methods providing access to vertex, edge and incidence sequences
	 * ===================================================== */

	/**
	 * Removes the vertex identified by its global id <code>vertexId</code> from the 
	 * vertex sequence of the global graph
	 * @param vertexId
	 */
	public void removeVertexFromVSeq(long vertexId);

	/**
	 * Removes the edge identified by its global id <code>edgeId</code> from the 
	 * edge sequence of the global graph
	 * @param edgeId
	 */
	public void removeEdgeFromESeq(long edgeId);
	

	
	
	public void appendIncidenceToLambdaSeqOfVertex(long edgeId, long incidenceId);

	public void removeIncidenceFromLambdaSeqOfVertex(long incidenceId);

	public void setFirstIncidenceIdAtVertexId(long vertexId, long incidenceId);
		
	public void setLastIncidenceIdAtVertexId(long vertexId, long incidenceId);
		
	public void setNextIncidenceIdAtVertexId(long incId, long prevId);
		
	public void setPreviousIncidenceIdAtVertexId(long globalIncidenceId, long nextIncidenceId);

	public void incidenceListOfVertexModified(long edgeId);
	
	
	public void setFirstIncidenceIdAtEdgeId(long edgeId, long incidenceId);
	
	public void setLastIncidenceIdAtEdgeId(long edgeId, long incidenceId);
	
	public void setNextIncidenceIdAtEdgeId(long incId, long nextId);
		
	public void setPreviousIncidenceIdAtEdgeId(long incId, long prevId);
	
	public void appendIncidenceToLambdaSeqOfEdge(long edgeId, long incidenceId);

	public void removeIncidenceFromLambdaSeqOfEdge(long incidenceId);
		
	public void incidenceListOfEdgeModified(long edgeId);




	public void setVCount(long subgraphId, long count);




	public void graphModified(int graphId);





	public int loadPartialGraph(String hostname);



	/**
	 * @param globalSubgraphId
	 * @return the kind of the parent entity (graph, edge or vertex) containing
	 * the graph identified by its globalSubgraphId
	 */
	public ParentEntityKind getParentEntityKind(long globalSubgraphId);


}
