package de.uni_koblenz.jgralabtest.dhht;

import de.uni_koblenz.jgralab.impl.disk.GraphDatabaseElementaryMethods;
import de.uni_koblenz.jgralabtest.dhht.schema.DHHTTestGraph;

public class DistributedGraphGenerator extends TreeGraphGenerator {

	DHHTTestGraph[] partialGraphs;
	
	String[] remoteHosts;
	
	public DistributedGraphGenerator(int layers, int roots,
			int[] branchingFactors, int[] firstLayerBranchingFactors,
			int additionalEdgeCount, boolean useHyperedges, String[] remoteHosts ) {
		super(layers, roots, branchingFactors, firstLayerBranchingFactors,
				additionalEdgeCount, useHyperedges, true);
		partialGraphs = new DHHTTestGraph[remoteHosts.length];
		this.remoteHosts = remoteHosts;
	}
	
	protected DHHTTestGraph getGraph(long globalId) {
		int partialGraphId = GraphDatabaseElementaryMethods.getPartialGraphId(globalId);
		System.out.println("Partial graph id : " + partialGraphId);
		return partialGraphs[partialGraphId-1];
	}
	
	protected DHHTTestGraph createPartialGraph(int i) {
		System.out.println("Creating partial graph " + i);
		if (partialGraphs[i] == null) {
			if (i==0)
				partialGraphs[i] = graph;
			else {
				partialGraphs[i] = (DHHTTestGraph) graph.createPartialGraphInGraph(remoteHosts[i]);
				System.out.println("Created remote partial graph");
				partialGraphs[i].getECount();
			}
				
		}		
		return partialGraphs[i];
	}
	
	protected int getPartialGraphCount() {
		return remoteHosts.length;
	}

}
