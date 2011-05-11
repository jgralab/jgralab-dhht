package de.uni_koblenz.jgralab.impl.disk;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.schema.GraphClass;

public class PartialGraphDatabase extends GraphDatabase {
	
	private CompleteGraphDatabase completeGraphDatabase;
		
	protected PartialGraphDatabase(PartialGraphImpl localGraph, String hosenameOfCompleteGraph) {
		super(localGraph);
		try {
			completeGraphDatabase = (CompleteGraphDatabase) server.getRemoteInstance(hosenameOfCompleteGraph).getGraph(localGraph.getCompleteGraphUid(), 0);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
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
	
}
