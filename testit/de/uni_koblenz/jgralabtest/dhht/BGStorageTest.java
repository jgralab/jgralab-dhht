package de.uni_koblenz.jgralabtest.dhht;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.algolib.CountCentralAlgorithm;
import de.uni_koblenz.jgralab.algolib.CountHypergraphSearchAlgorithm;
import de.uni_koblenz.jgralab.algolib.universalsearch.MyCentralAlgorithm;
import de.uni_koblenz.jgralab.impl.disk.DiskStorageManager;
import de.uni_koblenz.jgralabtest.dhht.TreeGraphGenerator.ImplementationVariant;

public class BGStorageTest {

	private long visitedNodes;

	private long visitedEdges;

	private boolean genericSearch = true;
	
	private boolean useDistributedSearch = false;
	
	private void testGraph(Graph graph, Variant variant, boolean dfs) {
		Vertex startVertex = graph.getFirstVertex();
		switch (variant) {
		case TREELIKENESTED:
		case CLIQUENESTED:
		case TREELIKENESTEDDISK:
		case CLIQUENESTEDDISK:
			/* setting traversal context to first subgraph */
			startVertex = startVertex.getNextVertex();
			Graph subgraph = startVertex.getSubordinateGraph();
			subgraph.useAsTraversalContext();
			break;
		case TREELIKEVIEW:
		case CLIQUEVIEW:
		case TREELIKEVIEWDISK:
		case CLIQUEVIEWSDISK:
			/* setting traversal context to view */
			Graph view = graph.getView(2);
			view.useAsTraversalContext();
			break;
		case TREELIKEDISTRIBUTED:
		case CLIQUEDISTRIBUTED:
			Graph partial = graph.getLocalPartialGraph();
			partial.useAsTraversalContext();
		}
		if (useDistributedSearch && ((variant == Variant.TREELIKEDISTRIBUTED) || (variant == Variant.CLIQUEDISTRIBUTED))) {
			System.out.println("Performing distributed search");
			if (genericSearch) {
				MyCentralAlgorithm algo = new MyCentralAlgorithm(graph, dfs);
				try {
					algo.run(startVertex);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				visitedNodes = algo.getVertexCount();
				visitedEdges = algo.getEdgeCount();
			} else {
				CountCentralAlgorithm algo = new CountCentralAlgorithm(graph, dfs);
				try {
					algo.run(startVertex);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				visitedNodes = algo.getVertexCount();
				visitedEdges = algo.getEdgeCount();
			}
		} else {
			CountHypergraphSearchAlgorithm algo = new CountHypergraphSearchAlgorithm(
					dfs);
			DiskStorageManager.reloadedContainers = 0;
			algo.run(startVertex);
			visitedNodes = algo.getVertexCount();
			visitedEdges = algo.getEdgeCount();
		}

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

		for (int i = 0; i < cycles; i++) {
			long creationStartTime = System.currentTimeMillis();
			graph = createGraph(variant, remoteHosts);
			long thisCreationTime = System.currentTimeMillis()
					- creationStartTime;
			totalCreationTime += thisCreationTime;
			System.out.println("Traversing dfs");
			long traversalStartTimeDFS = System.currentTimeMillis();
			testGraph(graph, variant, true);
			dfsNodes = visitedNodes;
			dfsEdges = visitedEdges;
			long thisTraversalTimeDFS = System.currentTimeMillis()
					- traversalStartTimeDFS;
			totalTraversalTimeDFS += thisTraversalTimeDFS;
			System.out.println("Traversing bfs");
			long traversalStartTimeBFS = System.currentTimeMillis();
			testGraph(graph, variant, false);
			bfsNodes = visitedNodes;
			bfsEdges = visitedEdges;
			long thisTraversalTimeBFS = System.currentTimeMillis()
					- traversalStartTimeBFS;
			totalTraversalTimeBFS += thisTraversalTimeBFS;
			eCount = graph.getECount();
			vCount = graph.getVCount();
			graph = null;
			System.gc();
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.gc();
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		long avgTraversalTimeDFS = totalTraversalTimeDFS / cycles;
		long avgTraversalTimeBFS = totalTraversalTimeBFS / cycles;
		long avgCreationTime = totalCreationTime / cycles;

		System.out.println("Variant: " + variant);
		System.out.println("  Avg creation time: " + avgCreationTime);
		System.out.println("  Avg bfs time: " + avgTraversalTimeBFS
				+ " visiting " + bfsNodes + " vertices and " + bfsEdges
				+ " edges");
		System.out.println("  Avg dfs time: " + avgTraversalTimeDFS
				+ " visiting " + dfsNodes + " vertices and " + dfsEdges
				+ " edges");
	}

	private enum Variant {
		TREELIKE, CLIQUE, TREELIKEDIST_LOCAL, CLIQUEDIST_LOCAL, TREELIKEDISK, CLIQUEDISK, TREELIKEHY, CLIQUEHY, TREELIKEDISKHY, CLIQUEDISKHY, TREELIKENESTED, CLIQUENESTED, TREELIKENESTEDDISK, CLIQUENESTEDDISK, TREELIKEVIEW, CLIQUEVIEW, TREELIKEVIEWDISK, CLIQUEVIEWSDISK, TREELIKEDISTRIBUTED, CLIQUEDISTRIBUTED
	}

	private Graph createGraph(Variant variant, String[] hostnames) {
		int[] factors = { 2, 3, 4, 5 };
		int[] firstLayerFactorsTree = { 4, 4, 4, 4 };
		int firstLayerFactorClique = (int) (2289 * 0.75);
		int addEdges = 0; //1500000;
		int[] firstLayerFactorsClique = { firstLayerFactorClique,
				firstLayerFactorClique, firstLayerFactorClique,
				firstLayerFactorClique, firstLayerFactorClique };

		switch (variant) {
		case TREELIKE:// Tree-like graph, 1 root, 11 levels
			return new TreeGraphGenerator(11, 5, factors,
					firstLayerFactorsTree, addEdges, false, ImplementationVariant.MEM)
					.createGraph();
		case TREELIKEDISK:// Tree-like graph, 1 root, 11 levels
			return new TreeGraphGenerator(11, 5, factors,
					firstLayerFactorsTree, addEdges, false, ImplementationVariant.DISK).createGraph();
		case TREELIKEDIST_LOCAL:// Tree-like graph, 1 root, 11 levels
			return new TreeGraphGenerator(11, 5, factors,
					firstLayerFactorsTree, addEdges, false, ImplementationVariant.DISTRIBUTED).createGraph();
		case CLIQUE:
			return new TreeGraphGenerator(6, 5, factors,
					firstLayerFactorsClique, addEdges, false, ImplementationVariant.MEM)
					.createGraph();
		case CLIQUEDIST_LOCAL:
			return new TreeGraphGenerator(6, 5, factors,
					firstLayerFactorsClique, addEdges, false, ImplementationVariant.DISTRIBUTED)
					.createGraph();
		case CLIQUEDISK:
			return new TreeGraphGenerator(6, 5, factors,
					firstLayerFactorsClique, addEdges, false, ImplementationVariant.DISK)
					.createGraph();
		case TREELIKEHY:// Tree-like graph, 1 root, 11 levels
		case TREELIKENESTED:
		case TREELIKEVIEW:
			return new TreeGraphGenerator(11, 5, factors,
					firstLayerFactorsTree, addEdges, true, ImplementationVariant.MEM).createGraph();
		case TREELIKEDISKHY:// Tree-like graph, 1 root, 11 levels
		case TREELIKENESTEDDISK:
		case TREELIKEVIEWDISK:
			return new TreeGraphGenerator(10, 5, factors,
					firstLayerFactorsTree, addEdges, true, ImplementationVariant.DISK).createGraph();
		case CLIQUEHY:
		case CLIQUENESTED:
		case CLIQUEVIEW:
			return new TreeGraphGenerator(6, 5, factors,
					firstLayerFactorsClique, addEdges, true, ImplementationVariant.MEM)
					.createGraph();
		case CLIQUEDISKHY:
		case CLIQUENESTEDDISK:
		case CLIQUEVIEWSDISK:
			return new TreeGraphGenerator(6, 5, factors,
					firstLayerFactorsClique, addEdges, true, ImplementationVariant.DISK)
					.createGraph();
		case TREELIKEDISTRIBUTED:// Tree-like graph, 1 root, 11 levels
			genericSearch = false;
			if (!genericSearch) {
				return new DistributedGraphGenerator(5, 2, factors,
						firstLayerFactorsTree, 7, true, hostnames).createGraph();
			} else {
				return new SimpleDistributedGraphGenerator(10, 2, factors,
						firstLayerFactorsTree, 70000, true, hostnames).createGraph();
			}
			//

		case CLIQUEDISTRIBUTED:
			genericSearch = true;
			if (!genericSearch) {
				return new DistributedGraphGenerator(4, 2, factors,
						firstLayerFactorsClique, 70000, true, hostnames).createGraph();
			} else {
				return new SimpleDistributedGraphGenerator(4, 2, factors,
						firstLayerFactorsClique, 70000, true, hostnames).createGraph();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		BGStorageTest test = new BGStorageTest();

		int cycles = 1;

		boolean distributed = true;
		


		if (distributed) {
			Variant[] distributedVariants = { Variant.TREELIKEDIST_LOCAL };
			String[] hosts = { "141.26.70.230", "helena.uni-koblenz.de" };
			for (Variant variant : distributedVariants) {
				test.iterateTest(1, variant, hosts);
			}
		} else {
			Variant[] variants = { Variant.CLIQUE}; 
			
			for (Variant variant : variants) {
				// System.out.println("Iterating variant: " + variant);
				test.iterateTest(cycles, variant, null);
			}
		}

		System.exit(0);
	}

}
