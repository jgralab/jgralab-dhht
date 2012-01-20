package de.uni_koblenz.jgralabtest.dhht;


import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.JGraLabServerImpl;
import de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseElementaryMethods;
import de.uni_koblenz.jgralabtest.dhht.schema.DHHTTestGraph;
import de.uni_koblenz.jgralabtest.dhht.schema.DHHTTestSchema;
import de.uni_koblenz.jgralabtest.dhht.schema.SimpleEdge;
import de.uni_koblenz.jgralabtest.dhht.schema.SimpleEdge_start;
import de.uni_koblenz.jgralabtest.dhht.schema.SimpleEdge_target;

public class SimpleDistributedGraphGenerator {

	
	DHHTTestGraph[] partialGraphs;
	
	DHHTTestGraph topGraph;

	String[] remoteHosts;
	
	int layers;
	
	int roots;
	
	int[] branchingFactors;
	
	int additionalEdgeCount;

	public SimpleDistributedGraphGenerator(int layers, int roots,
			int[] branchingFactors, int[] firstLayerBranchingFactors,
			int additionalEdgeCount, boolean useHyperedges, String[] remoteHosts) {
		this.layers = layers;
		this.roots = roots;
		this.branchingFactors = branchingFactors;
		this.additionalEdgeCount = additionalEdgeCount;
		partialGraphs = new DHHTTestGraph[remoteHosts.length + 1];
		this.remoteHosts = remoteHosts;
	}

	
	public DHHTTestGraph createGraph() {
		topGraph = DHHTTestSchema.instance().createDHHTTestGraphOnDisk();
		for (int i=0; i<remoteHosts.length;i++) {
			createPartialGraph(i+1);
		}
		Vertex root = topGraph.createSimpleVertex();
		SubgraphGenerator localGenerator = new SubgraphGenerator(topGraph.getUniqueGraphId(), layers, branchingFactors);
		long firstPartRootId = localGenerator.createSubgraph();
		Vertex firstPartRootV = topGraph.getVertex(firstPartRootId);
		SimpleEdge e = topGraph.createSimpleEdge();
		e.connect(SimpleEdge_start.class, root);
		e.connect(SimpleEdge_target.class, firstPartRootV);
		System.out.println("Creating remot graph");
		RemoteGraphGenerator remoteGenerator = null;
		try {
			remoteGenerator = (RemoteGraphGenerator) JGraLabServerImpl.getLocalInstance().getRemoteInstance(remoteHosts[1]).createSubgraphGenerator(topGraph.getUniqueGraphId(), layers, branchingFactors);
			System.out.println("Got server, start creation");
			long secondPartRootId = remoteGenerator.createSubgraph();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Created remote graph");
		Vertex secondPartRootV = topGraph.getVertex(firstPartRootId);
		SimpleEdge e2 = topGraph.createSimpleEdge();
		e.connect(SimpleEdge_start.class, root);
		e.connect(SimpleEdge_target.class, firstPartRootV);
		
		
		//add some edges 
		
		for (int i=0; i<20; i++) {
			SimpleEdge crosslinks = topGraph.createSimpleEdge();
			long localVId = ((GraphDatabaseElementaryMethods) topGraph.getGraphDatabase()).convertToGlobalId((int) (1 + i%(topGraph.getVCount()-2)));
			Vertex local = topGraph.getVertex(localVId);
			long remoteVId =  ((GraphDatabaseElementaryMethods)partialGraphs[2].getGraphDatabase()).convertToGlobalId((int) (1 + i%(topGraph.getVCount()-2)));
			Vertex remote = partialGraphs[2].getVertex(localVId);
			if (i%2 == 0) {
				e.connect(SimpleEdge_start.class, local);
				e.connect(SimpleEdge_target.class, remote);
			} else {
				e.connect(SimpleEdge_start.class, remote);
				e.connect(SimpleEdge_target.class, local);
			}
		}
		
		for (int i=0; i<additionalEdgeCount-2000; i++) {
			int pg = i%2 + 1;
			SimpleEdge crosslinks = topGraph.createSimpleEdge();
			long localVId =  ((GraphDatabaseElementaryMethods)partialGraphs[pg].getGraphDatabase()).convertToGlobalId((int) (1 + i%(topGraph.getVCount()-2)));
			Vertex local = partialGraphs[pg].getVertex(localVId);
			long remoteVId =  ((GraphDatabaseElementaryMethods)partialGraphs[pg].getGraphDatabase()).convertToGlobalId((int) (1 + i%(topGraph.getVCount()-2)));
			Vertex remote = partialGraphs[pg].getVertex(localVId);
			if (i%2 == 0) {
				e.connect(SimpleEdge_start.class, local);
				e.connect(SimpleEdge_target.class, remote);
			} else {
				e.connect(SimpleEdge_start.class, remote);
				e.connect(SimpleEdge_target.class, local);
			}
		}
		
		return topGraph;
	}
	

	protected DHHTTestGraph createPartialGraph(int i) {
		// System.out.println("Creating partial graph " + i);
		if (partialGraphs[i] == null) {
			if (i == 1)
				partialGraphs[i] = topGraph;
			else {
				// System.out.println("Host is: " + remoteHosts[i - 1]);
				partialGraphs[i] = (DHHTTestGraph) topGraph
						.createPartialGraphInGraph(remoteHosts[i - 1]);
				// System.out.println("Created remote partial graph");
				partialGraphs[i].getECount();
			}

		}
		return partialGraphs[i];
	}

	protected int getPartialGraphCount() {
		return remoteHosts.length;
	}

	
}
