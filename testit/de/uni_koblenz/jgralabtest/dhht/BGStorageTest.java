package de.uni_koblenz.jgralabtest.dhht;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
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
	

	
	private void testGraph(Graph graph) {
		System.out.println("Graph has: " + graph.getVCount() + " vertices and " + graph.getECount() + " edges");
		System.out.println("Starting search");
		long startTime = System.currentTimeMillis();
		
		CountHypergraphSearchAlgorithm algo = new CountHypergraphSearchAlgorithm();
		DiskStorageManager.reloadedContainers = 0;
		algo.run(graph.getFirstVertex());
		
		System.out.println("Applied BFS in " + (System.currentTimeMillis() - startTime) + " milliseconds");
		System.out.println("Visited " + algo.getVertexCount() + " vertices and " + algo.getEdgeCount() + " edges");
		System.out.println("Reloaded " + DiskStorageManager.reloadedContainers + " containers");
	}
	
	private enum Variant {
		TREELIKE,
		CLIQUE,
		TREELIKEDISK,
		CLIQUEDISK,
	}

	
	private Graph createGraph(Variant variant) {
		switch (variant) {
		case TREELIKE://Tree-like graph, 1 root, 11 levels
			return new TreeGraphGenerator(11, 1, 2, 7, 1500000, false).createGraph();
		case TREELIKEDISK://Tree-like graph, 1 root, 11 levels
			return new TreeGraphGenerator(11, 1, 2, 7, 1500000, true).createGraph();
		case CLIQUE:
			return new TreeGraphGenerator(5, 2850, 2, 7, 1500000, false).createGraph();
		case CLIQUEDISK:
			return new TreeGraphGenerator(5, 2850, 2, 7, 1500000, true).createGraph();
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		BGStorageTest test = new BGStorageTest();
		Graph graph = null;
		Variant variant = Variant.CLIQUE;
		
		
		graph = test.createGraph(variant);
		test.testGraph(graph);


		System.exit(0);
	}
	
}
