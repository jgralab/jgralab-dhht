package de.uni_koblenz.jgralab.impl;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Graph;


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
	
	public RemoteStorageAccess getLocalDiskStorage() throws RemoteException;
	
	
	
	
	/* =====================================================
	 * Methods to notify about changes in vertex, edge and incidence sequences
	 * ===================================================== */
	
	/**
	 * Notifies this graph database that the vertex identified by its global vertex id
	 * <code>vertexId</code> has been added to the distributed graph
	 */
	public void internalNotifyVertexAdded(long vertexId) throws RemoteException;
	
	/**
	 * Notifies this graph database that the vertex identified by its global vertex id
	 * <code>vertexId</code> will be deleted from the distributed graph. When this
	 * method is called, the vertex still exists by will be deleted after this method
	 * returns.
	 */
	public void internalNotifyVertexDeleted(long vertexId) throws RemoteException;

	
	/**
	 * Notifies this graph database that the edge identified by its global edge id
	 * <code>edgeId</code> has been added to the distributed graph
	 */
	public void internalNotifyEdgeAdded(long edgeId) throws RemoteException;
	
	/**
	 * Notifies this graph database that the edge identified by its global edge id
	 * <code>edgeId</code> will be deleted from the distributed graph. When this
	 * method is called, the edge still exists by will be deleted after this method
	 * returns.
	 */
	public void internalNotifyEdgeDeleted(long edgeId) throws RemoteException;
	
	/**
	 * Notifies this graph database that the incidence identified by its global incidence id
	 * <code>incidenceId</code> has been added to the distributed graph
	 */
	public void internalNotifyIncidenceAdded(long incidenceId) throws RemoteException;
	
	/**
	 * Notifies this graph database that the incidence identified by its global incidence id
	 * <code>incidenceId</code> will be deleted from the distributed graph. When this
	 * method is called, the incidence still exists by will be deleted after this method
	 * returns.
	 */
	public void internalNotifyIncidenceDeleted(long incidenceId) throws RemoteException;

	
	
	/* =====================================================
	 * Methods providing access to vertex, edge and incidence sequences
	 * ===================================================== */

	/**
	 * Removes the vertex identified by its global id <code>vertexId</code> from the 
	 * vertex sequence of the global graph
	 * @param vertexId
	 */
	public void removeVertexFromVSeq(long vertexId) throws RemoteException;

	/**
	 * Removes the edge identified by its global id <code>edgeId</code> from the 
	 * edge sequence of the global graph
	 * @param edgeId
	 */
	public void removeEdgeFromESeq(long edgeId) throws RemoteException;
	

	
	
	public void appendIncidenceToLambdaSeqOfVertex(long edgeId, long incidenceId) throws RemoteException;

	public void removeIncidenceFromLambdaSeqOfVertex(long incidenceId) throws RemoteException;

	public void setFirstIncidenceIdAtVertexId(long vertexId, long incidenceId) throws RemoteException;
		
	public void setLastIncidenceIdAtVertexId(long vertexId, long incidenceId) throws RemoteException;
		
	public void setNextIncidenceIdAtVertexId(long incId, long prevId) throws RemoteException;
		
	public void setPreviousIncidenceIdAtVertexId(long globalIncidenceId, long nextIncidenceId) throws RemoteException;

	public void incidenceListOfVertexModified(long edgeId) throws RemoteException;
	
	
	public void setFirstIncidenceIdAtEdgeId(long edgeId, long incidenceId) throws RemoteException;
	
	public void setLastIncidenceIdAtEdgeId(long edgeId, long incidenceId) throws RemoteException;
	
	public void setNextIncidenceIdAtEdgeId(long incId, long nextId) throws RemoteException;
		
	public void setPreviousIncidenceIdAtEdgeId(long incId, long prevId) throws RemoteException;
	
	public void appendIncidenceToLambdaSeqOfEdge(long edgeId, long incidenceId) throws RemoteException;

	public void removeIncidenceFromLambdaSeqOfEdge(long incidenceId) throws RemoteException;
		
	public void incidenceListOfEdgeModified(long edgeId) throws RemoteException;




	public void setVCount(long subgraphId, long count) throws RemoteException;




	public void graphModified(int graphId) throws RemoteException;





	public int loadPartialGraph(String hostname) throws RemoteException;



	/**
	 * @param globalSubgraphId
	 * @return the kind of the parent entity (graph, edge or vertex) containing
	 * the graph identified by its globalSubgraphId
	 */
	public ParentEntityKind getParentEntityKind(long globalSubgraphId) throws RemoteException;




	public String getHostname(int id) throws RemoteException;




	public void deletePartialGraph(int partialGraphId) throws RemoteException;




	public long getTraversalContextSubgraphId() throws RemoteException;




	public void releaseTraversalContext() throws RemoteException;




	public void setTraversalContext(long globalId) throws RemoteException;


}
