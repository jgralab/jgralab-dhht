package de.uni_koblenz.jgralabtest.dhht;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.algolib.CountHypergraphSearchAlgorithm;
import de.uni_koblenz.jgralab.dhhttest.schema.Activity;
import de.uni_koblenz.jgralab.dhhttest.schema.DHHTTestGraph;
import de.uni_koblenz.jgralab.dhhttest.schema.DHHTTestSchema;
import de.uni_koblenz.jgralab.dhhttest.schema.SimpleEdge;
import de.uni_koblenz.jgralab.dhhttest.schema.SimpleEdge_start;
import de.uni_koblenz.jgralab.dhhttest.schema.SimpleEdge_target;
import de.uni_koblenz.jgralab.dhhttest.schema.SimpleVertex;
import de.uni_koblenz.jgralab.impl.disk.DiskStorageManager;
import de.uni_koblenz.jgralab.impl.disk.GraphDatabaseElementaryMethods;

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
			graph = createGraph(variant);
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
				Thread.sleep(10000);
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
	}

	
	private Graph createGraph(Variant variant) {
		switch (variant) {
		case TREELIKE://Tree-like graph, 1 root, 11 levels
			return new TreeGraphGenerator(11, 1, 2, 7, 1500000, false, false).createGraph();
		case TREELIKEDISK://Tree-like graph, 1 root, 11 levels
			return new TreeGraphGenerator(11, 1, 2, 7, 1500000, false, true).createGraph();
		case CLIQUE:
			return new TreeGraphGenerator(5, 4220, 2, 7, 1500000, false, false).createGraph();
		case CLIQUEDISK:
			return new TreeGraphGenerator(5, 4220, 2, 7, 1500000, false, true).createGraph();
		case TREELIKEHY://Tree-like graph, 1 root, 11 levels
			return new TreeGraphGenerator(11, 1, 2, 7, 1500000, true, false).createGraph();
		case TREELIKEDISKHY://Tree-like graph, 1 root, 11 levels
			return new TreeGraphGenerator(11, 1, 2, 7, 1500000, true, true).createGraph();
		case CLIQUEHY:
			return new TreeGraphGenerator(5, 4220, 2, 7, 1500000, true, false).createGraph();
		case CLIQUEDISKHY:
			return new TreeGraphGenerator(5, 4220, 2, 7, 1500000, true, true).createGraph();
		}
		return null;
	}
	

	
		
	
	public static void main(String[] args) {
		BGStorageTest test = new BGStorageTest();
		Variant[] variants = {Variant.TREELIKE, Variant.CLIQUE, Variant.TREELIKEHY, Variant.CLIQUEHY, Variant.TREELIKEDISK, Variant.CLIQUEDISK, Variant.TREELIKEDISKHY, Variant.CLIQUEDISKHY};
	//	Variant[] variants = {Variant.TREELIKEDISK, Variant.CLIQUEDISK, Variant.TREELIKEDISKHY, Variant.CLIQUEDISKHY};
		
		int cycles = 50;
		
		for (Variant variant : variants) 
			test.iterateTest(cycles, variant, null);

		
		Variant[] distributedVariants = {Variant.TREELIKEDISKHY, Variant.CLIQUEDISKHY};
		String[] hosts = {"localhost", "helena.uni-koblenz.de"};
		for (Variant variant : distributedVariants) {
			test.iterateTest(1, variant, hosts);
		}
		

		System.exit(0);
	}
	
}
