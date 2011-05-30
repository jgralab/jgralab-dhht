package de.uni_koblenz.jgralab.impl.disk;

import java.util.Collection;
import java.util.Map;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.JGraLabList;
import de.uni_koblenz.jgralab.JGraLabMap;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;

public class PartialGraphDatabase extends GraphDatabaseBaseImpl {

	private final CompleteGraphDatabase completeGraphDatabase;

	public PartialGraphDatabase(Schema schema, String uniqueGraphId, String hostnameOfCompleteGraph, int localPartialGraphId) {
		super(schema, uniqueGraphId, 1, localPartialGraphId); 
		completeGraphDatabase = (CompleteGraphDatabase) localJGraLabServer.getRemoteInstance(hostnameOfCompleteGraph).getGraphDatabase(uniqueGraphId);
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

	public void releasePartialGraphId(int partialGraphId) {
		completeGraphDatabase.releasePartialGraphId(partialGraphId);
	}

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


	
	
}
