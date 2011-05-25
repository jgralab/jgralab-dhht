package de.uni_koblenz.jgralab.impl.disk;

/**
 * As an extension of RemoteGraphDatabaseAccess, this interface declares all remote accessible methods
 * which should only be called by a database in the distributed environment and which should not be
 * visible to the objects representing vertices, edges and graphs.
 *
 */
public interface RemoteGraphDatabaseAccessWithInternalMethods extends RemoteGraphDatabaseAccess {

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
	
}
