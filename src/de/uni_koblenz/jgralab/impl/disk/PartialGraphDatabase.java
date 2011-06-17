package de.uni_koblenz.jgralab.impl.disk;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.schema.Schema;

public class PartialGraphDatabase extends GraphDatabaseBaseImpl {

	private final CompleteGraphDatabase completeGraphDatabase;

	public PartialGraphDatabase(Schema schema, String uniqueGraphId,
			String hostnameOfCompleteGraph, int localPartialGraphId) {
		super(schema, uniqueGraphId, 1, localPartialGraphId);
		completeGraphDatabase = (CompleteGraphDatabase) localJGraLabServer
				.getRemoteInstance(hostnameOfCompleteGraph).getGraphDatabase(
						uniqueGraphId);
	}

	@Override
	public String getHostname(int id) {
		return completeGraphDatabase.getHostname(id);
	}

	@Override
	public int getFreePartialGraphId() {
		return completeGraphDatabase.getFreePartialGraphId();
	}

	@Override
	public void registerPartialGraph(int id, String hostname) {
		completeGraphDatabase.registerPartialGraph(id, hostname);
	}

	@Override
	public void releasePartialGraphId(int partialGraphId) {
		completeGraphDatabase.releasePartialGraphId(partialGraphId);
	}

	@Override
	public long createPartialGraph(Class<? extends Graph> gc, String hostname) {
		return completeGraphDatabase.createPartialGraph(gc, hostname);
	}

	@Override
	public void deletePartialGraph(int partialGraphId) {
		completeGraphDatabase.deletePartialGraph(partialGraphId);
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
				getPartialGraphId(GraphDatabaseBaseImpl.GLOBAL_GRAPH_ID))
				.incidenceListOfEdgeModified(edgeId);
	}

	// graphmodified, edgelistemodified etc in base class, activating all
	// partial graphs and calling their internalXXModified method

	@Override
	public void graphModified(int graphId) {
		getGraphDatabase(
				getPartialGraphId(GraphDatabaseBaseImpl.GLOBAL_GRAPH_ID))
				.graphModified();
	}

	@Override
	public void setGraphVersion(long graphVersion) {
		getGraphDatabase(
				getPartialGraphId(GraphDatabaseBaseImpl.GLOBAL_GRAPH_ID))
				.setGraphVersion(graphVersion);
	}
	
	public Object getGraphAttribute(String attributeName) {
		return getGraphDatabase(getPartialGraphId(GraphDatabaseBaseImpl.GLOBAL_GRAPH_ID)).getGraphAttribute(attributeName);
	}
	@Override
	public void setGraphAttribute(String attributeName, Object data) {
		getGraphDatabase(getPartialGraphId(GraphDatabaseBaseImpl.GLOBAL_GRAPH_ID)).setGraphAttribute(attributeName, data);
	}
		

}
