package de.uni_koblenz.jgralabtest.dhht;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.algolib.CountHypergraphSearchAlgorithm;
import de.uni_koblenz.jgralab.impl.disk.DiskStorageManager;

public class BGStorageTest {
	
	
	private void testGraph(Graph graph, boolean dfs) {
	//	System.out.println("Graph has: " + graph.getVCount() + " vertices and " + graph.getECount() + " edges");
//		System.out.println("Starting search");
		
		CountHypergraphSearchAlgorithm algo = new CountHypergraphSearchAlgorithm(dfs);
		DiskStorageManager.reloadedContainers = 0;
		algo.run(graph.getFirstVertex());
		
				
		
		

		//System.out.println("Visited " + algo.getVertexCount() + " vertices and " + algo.getEdgeCount() + " edges");
		//System.out.println("Reloaded " + DiskStorageManager.reloadedContainers + " containers");
	}
	
	
	public void iterateTest(int cycles, Variant variant, String[] remoteHosts) {
		long totalTraversalTimeDFS = 0;
		long totalTraversalTimeBFS = 0;
		long totalCreationTime = 0;
		long eCount = 0;
		long vCount = 0;
		Graph graph = null;
		
		for (int i=0; i<cycles; i++) {
			long creationStartTime = System.currentTimeMillis();
			graph = createGraph(variant, remoteHosts);
			long thisCreationTime = System.currentTimeMillis() - creationStartTime;
			totalCreationTime += thisCreationTime;
			
			long traversalStartTimeDFS = System.currentTimeMillis();
			testGraph(graph, true);
			long thisTraversalTimeDFS = System.currentTimeMillis() - traversalStartTimeDFS;
			totalTraversalTimeDFS += thisTraversalTimeDFS;
			
			long traversalStartTimeBFS = System.currentTimeMillis();
			testGraph(graph, false);
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
		System.out.println("  Avg dfs time: " + avgTraversalTimeDFS);
		System.out.println("  Avg bfs time: " + avgTraversalTimeBFS);

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
		TREELIKEDISTRIBUTED,
		CLIQUEDISTRIBUTED
	}

	
	private Graph createGraph(Variant variant, String[] hostnames) {
		switch (variant) {
		case TREELIKE://Tree-like graph, 1 root, 11 levels
			return new TreeGraphGenerator(11, 1, 2, 7, 1500000, false, false).createGraph();
		case TREELIKEDISK://Tree-like graph, 1 root, 11 levels
			return new TreeGraphGenerator(11, 1, 2, 7, 1500000, false, true).createGraph();
		case CLIQUE:
			return new TreeGraphGenerator(5, 4500, 2, 7, 1500000, false, false).createGraph();
		case CLIQUEDISK:
			return new TreeGraphGenerator(5, 4500, 2, 7, 1500000, false, true).createGraph();
		case TREELIKEHY://Tree-like graph, 1 root, 11 levels
			return new TreeGraphGenerator(11, 1, 2, 7, 1500000, true, false).createGraph();
		case TREELIKEDISKHY://Tree-like graph, 1 root, 11 levels
			return new TreeGraphGenerator(11, 1, 2, 7, 1500000, true, true).createGraph();
		case CLIQUEHY:
			return new TreeGraphGenerator(5, 4500, 2, 7, 1500000, true, false).createGraph();
		case CLIQUEDISKHY:
			return new TreeGraphGenerator(5, 4500, 2, 7, 1500000, true, true).createGraph();
		case TREELIKEDISTRIBUTED://Tree-like graph, 1 root, 11 levels
			return new DistributedGraphGenerator(3, 2, 2, 7, 15, true, hostnames).createGraph();
		case CLIQUEDISTRIBUTED:
			return new DistributedGraphGenerator(5, 4500, 2, 7, 1500000, true, hostnames).createGraph();
		}
		return null;
	}
	

	
		
	
	public static void main(String[] args) {
		BGStorageTest test = new BGStorageTest();
	//	Variant[] variants = {Variant.TREELIKE, Variant.CLIQUE, Variant.TREELIKEHY, Variant.CLIQUEHY, Variant.TREELIKEDISK, Variant.CLIQUEDISK, Variant.TREELIKEDISKHY, Variant.CLIQUEDISKHY};
	//	Variant[] variants = {Variant.TREELIKEDISK, Variant.CLIQUEDISK, Variant.TREELIKEDISKHY, Variant.CLIQUEDISKHY};
	
		

	//	System.out.println("Running BGStorageTests");
		
		int cycles = 30;
		
		boolean distributed = true;
		
		if (distributed) {
			Variant[] distributedVariants = {Variant.TREELIKEDISTRIBUTED};
			String[] hosts = {"localhost", "helena.uni-koblenz.de"};
			for (Variant variant : distributedVariants) {
				test.iterateTest(1, variant, hosts);
			}
		} else { 	
			Variant[] variants = {Variant.TREELIKE, Variant.CLIQUE, Variant.TREELIKEHY, Variant.CLIQUEHY};
			for (Variant variant : variants)  {
				//System.out.println("Iterating variant: " + variant);
				test.iterateTest(cycles, variant, null);
			}
		}
		
		

		System.exit(0);
	}
	
}
