package de.uni_koblenz.jgralab.impl.disk;

import java.rmi.RemoteException;

public class PartialGraphDatabase extends GraphDatabase {
	
	private CompleteGraphDatabase completeGraphDatabase;
	
	protected PartialGraphDatabase(PartialGraphImpl localGraph, String hosenameOfCompleteGraph) {
		super(localGraph);
		try {
			completeGraphDatabase = (CompleteGraphDatabase) server.getRemoteInstance(hosenameOfCompleteGraph).getGraph(localGraph.getCompleteGraphUid(), localGraph.getPartialGraphId());
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
	
}
