package de.uni_koblenz.jgralabtest.dhht;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.algolib.CountHypergraphSearchAlgorithm;
import de.uni_koblenz.jgralab.impl.disk.DiskStorageManager;

public class BGStorageTest {
	
	private long visitedNodes;
	
	private long visitedEdges;
	
	private void testGraph(Graph graph, Variant variant, boolean dfs) {
		switch (variant) {
			case TREELIKENESTED:
			case CLIQUENESTED:
			case TREELIKENESTEDDISK:
			case CLIQUENESTEDDISK:
				/* setting traversal context to first subgraph*/
				Vertex v = graph.getFirstVertex();
				Graph subgraph = v.getSubordinateGraph();
				System.out.println("Vertices in subgraph " + subgraph.getVCount());
				subgraph.useAsTraversalContext();
				break;
			case TREELIKEVIEW:
			case CLIQUEVIEW:
			case TREELIKEVIEWDISK:
			case CLIQUEVIEWSDISK:
				/* setting traversal context to view */
				Graph view = graph.getView(4);
				view.useAsTraversalContext();
		}
		CountHypergraphSearchAlgorithm algo = new CountHypergraphSearchAlgorithm(dfs);
		DiskStorageManager.reloadedContainers = 0;
		algo.run(graph.getFirstVertex());
		visitedNodes = algo.getVertexCount();
		visitedEdges = algo.getEdgeCount();
		graph.releaseTraversalContext();
	}
	
	
	public void iterateTest(int cycles, Variant variant, String[] remoteHosts) {
		long totalTraversalTimeDFS = 0;
		long totalTraversalTimeBFS = 0;
		long totalCreationTime = 0;
		long eCount = 0;
		long vCount = 0;
		long dfsNodes = 0;
		long dfsEdges = 0;
		long bfsNodes = 0;
		long bfsEdges = 0;
		Graph graph = null;
		
		for (int i=0; i<cycles; i++) {
			long creationStartTime = System.currentTimeMillis();
			graph = createGraph(variant, remoteHosts);
			long thisCreationTime = System.currentTimeMillis() - creationStartTime;
			totalCreationTime += thisCreationTime;
			
			long traversalStartTimeDFS = System.currentTimeMillis();
			testGraph(graph, variant, true);
			dfsNodes = visitedNodes;
			dfsEdges = visitedEdges;
			long thisTraversalTimeDFS = System.currentTimeMillis() - traversalStartTimeDFS;
			totalTraversalTimeDFS += thisTraversalTimeDFS;
			
			long traversalStartTimeBFS = System.currentTimeMillis();
			testGraph(graph, variant, false);
			bfsNodes = visitedNodes;
			bfsEdges = visitedEdges;
			long thisTraversalTimeBFS = System.currentTimeMillis() - traversalStartTimeBFS;
			totalTraversalTimeBFS += thisTraversalTimeBFS;
			eCount = graph.getECount();
			vCount = graph.getVCount();
			graph = null;
			System.gc();
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.gc();
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		long avgTraversalTimeDFS = totalTraversalTimeDFS / cycles;
		long avgTraversalTimeBFS = totalTraversalTimeBFS / cycles;
		long avgCreationTime = totalCreationTime / cycles;
		
		
		System.out.println("Variant: " + variant);
		System.out.println("  Vertices: " + vCount + " Edges: " + eCount);
		System.out.println("  Avg creation time: " + avgCreationTime);
		System.out.println("  Avg dfs time: " + avgTraversalTimeDFS + " visiting " + dfsNodes + " vertices and " + dfsEdges + " edges");
		System.out.println("  Avg bfs time: " + avgTraversalTimeBFS + " visiting " + bfsNodes + " vertices and " + bfsEdges + " edges");

	}
	
	
	
	private enum Variant {
		TREELIKE,
		CLIQUE,
		TREELIKEDISK,
		CLIQUEDISK,
		TREELIKEHY,
		CLIQUEHY,
		TREELIKEDISKHY,
		CLIQUEDISKHY,
		TREELIKENESTED,
		CLIQUENESTED,
		TREELIKENESTEDDISK,
		CLIQUENESTEDDISK,
		TREELIKEVIEW,
		CLIQUEVIEW,
		TREELIKEVIEWDISK,
		CLIQUEVIEWSDISK,
		TREELIKEDISTRIBUTED,
		CLIQUEDISTRIBUTED
	}

	
	private Graph createGraph(Variant variant, String[] hostnames) {
		int[] factors = {2,3,4,5,6,7};
		int[] firstLayerFactorsTree = {5,5,5,5,5};
		int[] firstLayerFactorsClique = {1400,1400,1400,1400,1400};
		
		switch (variant) {
		case TREELIKE://Tree-like graph, 1 root, 11 levels
			return new TreeGraphGenerator(10, 5, factors, firstLayerFactorsTree, 1500000, false, false).createGraph();
		case TREELIKEDISK://Tree-like graph, 1 root, 11 levels
			return new TreeGraphGenerator(5, 1, factors, firstLayerFactorsTree, 15000, false, true).createGraph();
		case CLIQUE:
			return new TreeGraphGenerator(6, 5, factors, firstLayerFactorsClique, 1500000, false, false).createGraph();
		case CLIQUEDISK:
			return new TreeGraphGenerator(6, 5, factors, firstLayerFactorsClique, 1500000, false, true).createGraph();
		case TREELIKEHY://Tree-like graph, 1 root, 11 levels
		case TREELIKENESTED:
		case TREELIKEVIEW:	
			return new TreeGraphGenerator(10, 5, factors, firstLayerFactorsTree, 1500000, true, false).createGraph();
		case TREELIKEDISKHY://Tree-like graph, 1 root, 11 levels
		case TREELIKENESTEDDISK:
		case TREELIKEVIEWDISK:
			return new TreeGraphGenerator(10, 5, factors, firstLayerFactorsTree, 1500000, true, true).createGraph();
		case CLIQUEHY:
		case CLIQUENESTED:
		case CLIQUEVIEW:	
			return new TreeGraphGenerator(6, 5, factors, firstLayerFactorsClique, 1500000, true, false).createGraph();
		case CLIQUEDISKHY:
		case CLIQUENESTEDDISK:
		case CLIQUEVIEWSDISK:
			return new TreeGraphGenerator(6, 5, factors, firstLayerFactorsClique, 1500000, true, true).createGraph();
		case TREELIKEDISTRIBUTED://Tree-like graph, 1 root, 11 levels
			return new DistributedGraphGenerator(10, 5, factors, firstLayerFactorsTree, 1500000, true, hostnames).createGraph();
		case CLIQUEDISTRIBUTED:
			return new DistributedGraphGenerator(6, 5, factors, firstLayerFactorsClique, 1500000, true, hostnames).createGraph();
		}
		return null;
	}
	


	
	public static void main(String[] args) {
		BGStorageTest test = new BGStorageTest();
	//	Variant[] variants = {Variant.TREELIKE, Variant.CLIQUE, Variant.TREELIKEHY, Variant.CLIQUEHY, Variant.TREELIKEDISK, Variant.CLIQUEDISK, Variant.TREELIKEDISKHY, Variant.CLIQUEDISKHY};
	//	Variant[] variants = {Variant.TREELIKEDISK, Variant.CLIQUEDISK, Variant.TREELIKEDISKHY, Variant.CLIQUEDISKHY};
	

		
	//	System.out.println("Running BGStorageTests");
		
		int cycles = 10;
		
		boolean distributed = false;
		
		if (distributed) {
			Variant[] distributedVariants = {Variant.TREELIKEDISTRIBUTED};
			String[] hosts = {"localhost", "helena.uni-koblenz.de"};
			for (Variant variant : distributedVariants) {
				test.iterateTest(1, variant, hosts);
			}
		} else { 	
	//		Variant[] variants = {Variant.TREELIKEDISK /*, Variant.CLIQUEDISK, Variant.TREELIKEDISKHY, Variant.CLIQUEDISKHY */};
		//	Variant[] variants = {Variant.TREELIKE, Variant.CLIQUE, Variant.TREELIKEHY, Variant.CLIQUEHY};
		//	Variant[] variants = {Variant.TREELIKENESTED, Variant.TREELIKEVIEW, Variant.CLIQUENESTED, Variant.CLIQUEVIEW /*, Variant.CLIQUEDISK, Variant.TREELIKEDISKHY, Variant.CLIQUEDISKHY */};
			//Variant[] variants = {Variant.TREELIKENESTED, Variant.TREELIKEVIEW, Variant.CLIQUENESTED, Variant.CLIQUEVIEW /*, Variant.CLIQUEDISK, Variant.TREELIKEDISKHY, Variant.CLIQUEDISKHY */};
			Variant[] variants = {Variant.TREELIKE, Variant.CLIQUE, Variant.TREELIKEHY, Variant.CLIQUEHY, Variant.TREELIKENESTED, Variant.CLIQUENESTED};  //, Variant.TREELIKEVIEW, Variant.CLIQUENESTED, Variant.CLIQUEVIEW /*, Variant.CLIQUEDISK, Variant.TREELIKEDISKHY, Variant.CLIQUEDISKHY */};
			
			for (Variant variant : variants)  {
				//System.out.println("Iterating variant: " + variant);
				test.iterateTest(cycles, variant, null);
			}
		}
		
		

		System.exit(0);
	}
	
}
