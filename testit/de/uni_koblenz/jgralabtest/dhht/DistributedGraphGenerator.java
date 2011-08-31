package de.uni_koblenz.jgralabtest.dhht;

import de.uni_koblenz.jgralab.dhhttest.schema.DHHTTestGraph;
import de.uni_koblenz.jgralab.impl.disk.GraphDatabaseElementaryMethods;

public class DistributedGraphGenerator extends TreeGraphGenerator {

	DHHTTestGraph[] partialGraphs;
	
	String[] remoteHosts;
	
	public DistributedGraphGenerator(int layers, int roots,
			int minBranchingFactor, int maxBranchingFactor,
			int additionalEdgeCount, boolean useHyperedges, String[] remoteHosts ) {
		super(layers, roots, minBranchingFactor, maxBranchingFactor,
				additionalEdgeCount, useHyperedges, true);
		partialGraphs = new DHHTTestGraph[remoteHosts.length];
		this.remoteHosts = remoteHosts;
	}
	
	protected DHHTTestGraph getGraph(long globalId) {
		int partialGraphId = GraphDatabaseElementaryMethods.getPartialGraphId(globalId);
		return partialGraphs[partialGraphId];
	}
	
	protected DHHTTestGraph createPartialGraph(int i) {
		if (partialGraphs[i] == null) {
			if (i==0)
				partialGraphs[i] = graph;
			else
				partialGraphs[i] = (DHHTTestGraph) graph.createPartialGraphInGraph(remoteHosts[i]);
		}		
		return partialGraphs[i];
	}
	
	protected int getPartialGraphCount() {
		return remoteHosts.length;
	}
	
	

}
