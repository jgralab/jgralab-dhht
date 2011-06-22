package de.uni_koblenz.jgralab.impl.disk;


import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.RemoteJGraLabServer;
import de.uni_koblenz.jgralab.schema.Schema;

public class PartialGraphDatabase extends GraphDatabaseBaseImpl {

	private final CompleteGraphDatabaseImpl completeGraphDatabase;
	
	@SuppressWarnings("unused")
	private ParentEntityKind kindOfParentElement;

	public PartialGraphDatabase(Schema schema, String uniqueGraphId,
			String hostnameOfCompleteGraph, long parentSubgraphId, ParentEntityKind kindOfParentElement, int localPartialGraphId) {
		super(schema, uniqueGraphId, parentSubgraphId, localPartialGraphId);
		this.kindOfParentElement = kindOfParentElement;
		completeGraphDatabase = (CompleteGraphDatabaseImpl) localJGraLabServer
				.getRemoteInstance(hostnameOfCompleteGraph).getGraphDatabase(
						uniqueGraphId);
	}

	@Override
	public String getHostname(int id) {
		return completeGraphDatabase.getHostname(id);
	}

	
	
	//for partial graph database
	public int internalCreatePartialGraphInEntity(String remoteHostname, long parentEntityGlobalId, ParentEntityKind parentEntityKind) {
		RemoteGraphDatabaseAccessWithInternalMethods compDatabase = getGraphDatabase(DiskImplementationBasicMethods.TOPLEVEL_PARTIAL_GRAPH_ID);
		int partialGraphId = compDatabase.internalCreatePartialGraphInEntity(remoteHostname, parentEntityGlobalId, parentEntityKind);
		RemoteJGraLabServer remoteServer = localJGraLabServer.getRemoteInstance(remoteHostname);
		RemoteGraphDatabaseAccess p = remoteServer.getGraphDatabase(uniqueGraphId);
		partialGraphDatabases.put(partialGraphId, (RemoteGraphDatabaseAccessWithInternalMethods) p);
		return partialGraphId;
	}
	
	
	
	@Override
	public void registerPartialGraph(int id, String hostname) {
		completeGraphDatabase.registerPartialGraph(id, hostname);
	}


	
	

	@Override
	public void deletePartialGraph(int partialGraphId) {
		completeGraphDatabase.deletePartialGraph(partialGraphId);
		//remove from list of partial graph ids
	}

	@Override
	public void edgeListModified() {
		edgeListVersion++;
		completeGraphDatabase.edgeListModified();
	}

	@Override
	public void vertexListModified() {
		vertexListVersion++;
		completeGraphDatabase.vertexListModified();
	}

	@Override
	public void graphModified() {
		graphVersion++;
		completeGraphDatabase.graphModified();
	}

	/* **************************************************************************
	 * Methods to access traversal context
	 * *************************************************************************
	 */

	@Override
	public Graph getTraversalContext() {
		return completeGraphDatabase.getTraversalContext();
	}

	@Override
	public void releaseTraversalContext() {
		completeGraphDatabase.releaseTraversalContext();
	}

	@Override
	public void setTraversalContext(Graph traversalContext) {
		completeGraphDatabase.setTraversalContext(traversalContext);
	}

	@Override
	public void incidenceListOfEdgeModified(long edgeId) {
		getGraphDatabase(
				getPartialGraphId(DiskImplementationBasicMethods.GLOBAL_GRAPH_ID))
				.incidenceListOfEdgeModified(edgeId);
	}

	// graphmodified, edgelistemodified etc in base class, activating all
	// partial graphs and calling their internalXXModified method

	@Override
	public void graphModified(int graphId) {
		getGraphDatabase(
				getPartialGraphId(DiskImplementationBasicMethods.GLOBAL_GRAPH_ID))
				.graphModified();
	}

	@Override
	public void setGraphVersion(long graphVersion) {
		getGraphDatabase(
				getPartialGraphId(DiskImplementationBasicMethods.GLOBAL_GRAPH_ID))
				.setGraphVersion(graphVersion);
	}
	
	public Object getGraphAttribute(String attributeName) {
		return getGraphDatabase(getPartialGraphId(DiskImplementationBasicMethods.GLOBAL_GRAPH_ID)).getGraphAttribute(attributeName);
	}
	@Override
	public void setGraphAttribute(String attributeName, Object data) {
		getGraphDatabase(getPartialGraphId(DiskImplementationBasicMethods.GLOBAL_GRAPH_ID)).setGraphAttribute(attributeName, data);
	}




}
