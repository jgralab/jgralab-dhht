package de.uni_koblenz.jgralab.impl.disk;

import java.util.LinkedList;
import java.util.List;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.JGraLabServer;
import de.uni_koblenz.jgralab.schema.GraphClass;

public class CompleteGraphDatabase extends GraphDatabase {

	/*
	 * Stores the hostnames of the partial graphs
	 */
	private String[] hostnames;
	
	private List<Integer> freePartialGraphIds;
	
	
	protected CompleteGraphDatabase(CompleteOrPartialGraphImpl localGraph, String hostname) {
		super(localGraph);
		hostnames = new String[GraphStorage.MAX_NUMBER_OF_PARTIAL_GRAPHS];
		hostnames[0] = hostname;
		freePartialGraphIds = new LinkedList<Integer>();
		for (int i=0; i<GraphStorage.MAX_NUMBER_OF_PARTIAL_GRAPHS; i++) {
			freePartialGraphIds.add(i);
		}
	}
	
	protected String getHostname(int id) {
		return hostnames[id];
	}


	@Override
	public int getFreePartialGraphId() {
		if (freePartialGraphIds.size() > 0)
			return freePartialGraphIds.remove(0);
		else
			throw new RuntimeException("There is no free partial graph id");
	}
	
	@Override
	public void releasePartialGraphId(int partialGraphId) {
		freePartialGraphIds.add(partialGraphId);
	}

	/**
	 * Registers the partial graph with the given id <code>id</code> which is stored on the
	 * host with the name <code>hostname</code>
	 * @param id
	 * @param hostname
	 */
	public void registerPartialGraph(int id, String hostname) {
		if (hostnames[id] == null) {
			hostnames[id] = hostname;
		} else {
			throw new RuntimeException("There is already a graph with the id " + id + " registered");
		}
	}
	
	

	public Graph createPartialGraph(GraphClass gc, String hostname) {
		int partialGraphId = getFreePartialGraphId();
		JGraLabServer remoteServer = server.getRemoteInstance(hostname);
		GraphDatabase p = remoteServer.createGraph(gc.getId(), localGraph.getCompleteGraphUid(), partialGraphId, this.hostnames[0]);
		partialGraphDatabases[partialGraphId] = p;
		return p.getGraphObject(partialGraphId); 
	}

	@Override
	public void deletePartialGraph(int partialGraphId) {
		throw new RuntimeException("Not yet implemented");
	}
	

	public void edgeListModified() {
		edgeListVersion++;
	}
	
	public void vertexListModified() {
		vertexListVersion++;
	}
	
	public void graphModified() {
		graphVersion++;
	}
	
	
	
}
