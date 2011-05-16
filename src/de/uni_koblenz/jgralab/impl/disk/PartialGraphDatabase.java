package de.uni_koblenz.jgralab.impl.disk;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.schema.GraphClass;

public class PartialGraphDatabase extends GraphDatabase {
	
	private CompleteGraphDatabase completeGraphDatabase;
		
	protected PartialGraphDatabase(PartialGraphImpl localGraph, String hosenameOfCompleteGraph) {
		super(localGraph);
		completeGraphDatabase = (CompleteGraphDatabase) server.getRemoteInstance(hosenameOfCompleteGraph).getGraph(localGraph.getCompleteGraphUid(), 0);
	}

	@Override
	protected String getHostname(int id) {
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
	
	public Graph createPartialGraph(GraphClass gc, String hostname) {
		return completeGraphDatabase.createPartialGraph(gc, hostname);
	}


	@Override
	public void deletePartialGraph(int partialGraphId) {
		completeGraphDatabase.deletePartialGraph(partialGraphId);
	}
	
	public void edgeListModified() {
		edgeListVersion++;
		completeGraphDatabase.edgeListModified();
	}
	
	public void vertexListModified() {
		vertexListVersion++;
		completeGraphDatabase.vertexListModified();
	}
	
	public void graphModified() {
		graphVersion++;
		completeGraphDatabase.graphModified();
	}
	
	/* **************************************************************************
	 * Methods to access traversal context
	 * **************************************************************************/
	
	public Graph getTraversalContext() {
		return completeGraphDatabase.getTraversalContext();
	}
	
	public void releaseTraversalContext() {
		completeGraphDatabase.releaseTraversalContext();
	}
	
	public void setTraversalContext(Graph traversalContext) {
		completeGraphDatabase.setTraversalContext(traversalContext);
	}

	@Override
	public Graph createPartialGraph(String hostname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph loadRemotePartialGraph(String hostname, int id) {
		// TODO Auto-generated method stub
		return null;
	}



	
}
