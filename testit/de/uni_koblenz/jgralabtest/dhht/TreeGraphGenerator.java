package de.uni_koblenz.jgralabtest.dhht;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralabtest.dhht.schema.DHHTTestGraph;
import de.uni_koblenz.jgralabtest.dhht.schema.DHHTTestSchema;
import de.uni_koblenz.jgralabtest.dhht.schema.SimpleEdge_start;
import de.uni_koblenz.jgralabtest.dhht.schema.SimpleEdge_target;
import de.uni_koblenz.jgralabtest.dhht.schema.SimpleVertex;
import de.uni_koblenz.jgralabtest.dhht.schema.SimulatedHyperedge;
import de.uni_koblenz.jgralabtest.dhht.schema.SimulatedIncidence_incInc;
import de.uni_koblenz.jgralabtest.dhht.schema.SimulatedIncidence_outInc;

public class TreeGraphGenerator {

	protected DHHTTestGraph graph;

	private int layers;
	private int roots;
	private int currentBranchingFactorIndex = 0;
	private boolean diskBased;
	private boolean useHyperedges;

	private int additionalEdgeCount;

	private int[] branchingFactors;
	private int[] firstLayerBranchingFactors;

	private int getNextBranchingFactor() {
		if (firstLayerBranchingFactors != null) {
			if (currentBranchingFactorIndex < firstLayerBranchingFactors.length) {
				// System.out.println("First layer branches " +
				// firstLayerBranchingFactors[currentBranchingFactorIndex] +
				// " times");
				return firstLayerBranchingFactors[currentBranchingFactorIndex++];
			} else {
				firstLayerBranchingFactors = null;
				currentBranchingFactorIndex = 0;
			}
		}
		if (currentBranchingFactorIndex >= branchingFactors.length) {
			currentBranchingFactorIndex = 0;
		}
		return branchingFactors[currentBranchingFactorIndex++];
	}

	private ArrayList<SimpleVertex> vertices;

	int[] edgeBranchingFactors = { 1, 1, 1, 1, 3 };

	int currentEdgeBranchingFactorIndex = 0;

	public TreeGraphGenerator(int layers, int roots, int[] branchingFactors,
			int additionalEdgeCount, boolean useHyperedges, boolean diskBased) {
		this.layers = layers;
		this.roots = roots;
		this.additionalEdgeCount = additionalEdgeCount;
		this.branchingFactors = branchingFactors;
		this.diskBased = diskBased;
		this.useHyperedges = useHyperedges;
		this.vertices = new ArrayList<SimpleVertex>();
	}

	public TreeGraphGenerator(int layers, int roots, int[] branchingFactors,
			int[] firstLayerBranchingFactors, int additionalEdgeCount,
			boolean useHyperedges, boolean diskBased) {
		this.layers = layers;
		this.roots = roots;
		this.additionalEdgeCount = additionalEdgeCount;
		this.branchingFactors = branchingFactors;
		this.firstLayerBranchingFactors = firstLayerBranchingFactors;
		this.diskBased = diskBased;
		this.useHyperedges = useHyperedges;
		this.vertices = new ArrayList<SimpleVertex>();
	}

	private int getNextEdgeBranchingFactor() {
		return edgeBranchingFactors[(currentEdgeBranchingFactorIndex++)
				% edgeBranchingFactors.length];
	}

	public DHHTTestGraph createGraph() {
		if (diskBased) {
			graph = DHHTTestSchema.instance().createDHHTTestGraphOnDisk();
		} else {
			graph = DHHTTestSchema.instance().createDHHTTestGraphInMem();
		}

		int vCount = 1;
		int sizeOfLastLayer = 6000000;
		Edge[] lastEdgeInSubgraph = new Edge[sizeOfLastLayer];
		long[] vertexList = new long[sizeOfLastLayer];
		int vertexListSize = roots;
		int newVertexListSize = 0;
		int retrievedVertices = 0;
		Vertex root = graph.createSimpleVertex();

		for (int i = 0; i < roots; i++) {
			int partialGraphId = getInitialPartialGraphId(i);
			DHHTTestGraph partialGraph = createPartialGraph(partialGraphId);
			Vertex v = partialGraph.createSimpleVertex();
			vertexList[i] = v.getGlobalId();
			v.setKappa(layers);
			vertices.add((SimpleVertex) v);
			Edge rootEdge = graph.createSimpleEdge();
			rootEdge.connect(SimpleEdge_start.class, root);
			rootEdge.connect(SimpleEdge_target.class, v);
		}

		for (int layer = 1; layer < layers; layer++) {
			long[] newVertexList = new long[sizeOfLastLayer];
			while (retrievedVertices < vertexListSize) {
				Vertex parent = graph
						.getVertex(vertexList[retrievedVertices++]);
				int i = 0;
				int nextBranchingFactor = getNextBranchingFactor();
				while (i < nextBranchingFactor) {
					DHHTTestGraph partialGraph = getGraph(parent.getGlobalId());
					if (useHyperedges) {
						boolean firstEdgeInSubgraph = parent
								.getFirstIncidence() == parent
								.getLastIncidence();
						Edge e = partialGraph.createSimpleEdge();
						e.setKappa(layers - layer);
						if (!firstEdgeInSubgraph) {
							Edge lastEdge = lastEdgeInSubgraph[parent
									.getLocalId()];
							e.putAfter(lastEdge);
						}
						lastEdgeInSubgraph[parent.getLocalId()] = e;
						e.setSigma(parent);
						e.connect(SimpleEdge_start.class, parent);
						int j = 0;
						int nextEdgeBranchingFactor = getNextEdgeBranchingFactor();
						if (i + nextEdgeBranchingFactor > nextBranchingFactor)
							nextEdgeBranchingFactor = nextBranchingFactor - i;
						for (j = 0; j < nextEdgeBranchingFactor; j++) {
							vCount++;
							SimpleVertex v = partialGraph.createSimpleVertex();
							// System.out
							// .println("Created vertex "
							// + v.getLocalId()
							// + " on partial graph "
							// + GraphDatabaseElementaryMethods
							// .getPartialGraphId(v
							// .getGlobalId()));
							v.setKappa(layers - layer);
							v.putAfter(parent);
							v.setSigma(parent);
							vertices.add(v);
							newVertexList[newVertexListSize++] = v
									.getGlobalId();
							e.connect(SimpleEdge_target.class, v);
						}
						i += j;
					} else {
						int edgeBranchingFactor = getNextEdgeBranchingFactor();
						if (edgeBranchingFactor == 1) {
							vCount++;
							SimpleVertex v = partialGraph.createSimpleVertex();
							v.setSigma(parent);
							vertices.add(v);
							newVertexList[newVertexListSize++] = v
									.getGlobalId();
							Edge e = partialGraph.createSimpleEdge();
							e.setSigma(parent.getSigma());
							e.connect(SimpleEdge_start.class, parent);
							e.connect(SimpleEdge_target.class, v);
							i++;
						} else {
							vCount++;
							SimulatedHyperedge hyperedge = partialGraph
									.createSimulatedHyperedge();
							Edge e = partialGraph.createSimulatedIncidence();
							e.setSigma(parent.getSigma());
							e.connect(SimulatedIncidence_incInc.class, parent);
							e.connect(SimulatedIncidence_outInc.class,
									hyperedge);
							int j = 0;
							for (j = 0; j < edgeBranchingFactor; j++) {
								vCount++;
								SimpleVertex v = partialGraph
										.createSimpleVertex();
								v.setSigma(parent);
								vertices.add(v);
								newVertexList[newVertexListSize++] = v
										.getGlobalId();
								Edge e1 = partialGraph
										.createSimulatedIncidence();
								e1.connect(SimulatedIncidence_incInc.class,
										hyperedge);
								e1.connect(SimulatedIncidence_outInc.class, v);
							}
							i += j;
						}
					}

				}
			}
			vertexList = newVertexList;
			vertexListSize = newVertexListSize;
			newVertexListSize = 0;
			retrievedVertices = 0;
		}
		// System.out.println("additonal");
		for (int i = 0; i < additionalEdgeCount; i++) {
			int edgeBranchingFactor = getNextEdgeBranchingFactor();
			if (edgeBranchingFactor == 1) {
				Vertex start = getVertex(i * 7);
				DHHTTestGraph partialGraph = getGraph(start.getGlobalId());
				Vertex target = getVertex(i * 13);
				Edge e = partialGraph.createSimpleEdge();
				GraphElement<?, ?, ?> lca = getLeastCommonAncestor(start,
						target);
				if (lca != null)
					e.setSigma(lca);
				e.connect(SimpleEdge_start.class, start);
				e.connect(SimpleEdge_target.class, target);
			} else {
				List<SimpleVertex> startVertices = new LinkedList<SimpleVertex>();
				List<SimpleVertex> targetVertices = new LinkedList<SimpleVertex>();
				Vertex start = getVertex(i * 7);
				DHHTTestGraph partialGraph = getGraph(start.getGlobalId());
				int j = 1;
				while (startVertices.size() < edgeBranchingFactor) {
					startVertices.add((SimpleVertex) start);
					start = getVertex(i * 7 + j++);
				}
				j = 1;
				Vertex target = getVertex(i * 13);
				while (targetVertices.size() < edgeBranchingFactor) {
					targetVertices.add((SimpleVertex) target);
					target = getVertex(i * 13 + j++);
				}
				if (useHyperedges) {
					Edge e = partialGraph.createSimpleEdge();
					for (Vertex v : startVertices) {
						e.connect(SimpleEdge_start.class, v);
					}
					for (Vertex v : targetVertices) {
						e.connect(SimpleEdge_target.class, v);
					}
					GraphElement<?, ?, ?> lca = getLeastCommonAncestor(
							startVertices, targetVertices);
					if (lca != null) {
						// System.out.println("Setting sigma vertex of edge " +
						// e.getLocalId() + " to " + lca.getLocalId());
						e.setSigma(lca);
					}
				} else {
					SimulatedHyperedge simulatedHyperedge = partialGraph
							.createSimulatedHyperedge();
					for (SimpleVertex v : startVertices) {
						Edge e = partialGraph.createSimulatedIncidence();
						e.connect(SimulatedIncidence_incInc.class, v);
						e.connect(SimulatedIncidence_outInc.class,
								simulatedHyperedge);
					}
					for (SimpleVertex v : targetVertices) {
						Edge e = partialGraph.createSimulatedIncidence();
						e.connect(SimulatedIncidence_incInc.class,
								simulatedHyperedge);
						e.connect(SimulatedIncidence_outInc.class, v);
					}
				}
			}
		}
		return graph;
	}

	protected GraphElement<?, ?, ?> getLeastCommonAncestor(
			List<? extends Vertex> vertices,
			List<? extends Vertex> otherVertices) {
		LinkedList<SimpleVertex> newList = new LinkedList<SimpleVertex>();
		for (Vertex v : vertices) {
			newList.add((SimpleVertex) v);
		}
		for (Vertex v : otherVertices) {
			newList.add((SimpleVertex) v);
		}
		return getLeastCommonAncestor(newList);
	}

	protected GraphElement<?, ?, ?> getLeastCommonAncestor(
			List<? extends Vertex> vertices) {
		Vertex leastCommonAncestor = vertices.get(0);
		vertices.remove(0);
		for (Vertex v : vertices) {
			leastCommonAncestor = (Vertex) getLeastCommonAncestor(
					leastCommonAncestor, v);
		}
		return leastCommonAncestor;
	}

	protected GraphElement<?, ?, ?> getLeastCommonAncestor(Vertex v1, Vertex v2) {
		Set<GraphElement<?, ?, ?>> v1Ancs = new HashSet<GraphElement<?, ?, ?>>();
		// Set<GraphElement<?,?,?>> v2Ancs = new HashSet<GraphElement<?, ?,
		// ?>>();
		GraphElement<?, ?, ?> v1Anc = v1;
		GraphElement<?, ?, ?> v2Anc = v2;
		while (v1Anc != null) {
			v1Ancs.add(v1Anc);
			v1Anc = v1Anc.getSigma();
		}
		while (v2Anc != null) {
			if (v1Ancs.contains(v2Anc)) {
				return v2Anc;
			}
			v2Anc = v2Anc.getSigma();
		}
		return null;
	}

	// private Vertex getVertex(int id) {
	// int vId = (int) ( id % graph.getVCount());
	// if (diskBased)
	// return
	// graph.getVertex(graph.getGraphDatabase().convertToGlobalId(vId+1));
	// else
	// return graph.getVertex(vId+1);
	// }

	protected DHHTTestGraph createPartialGraph(int i) {
		return graph;
	}

	protected DHHTTestGraph getGraph(long globalId) {
		return graph;
	}

	private Vertex getVertex(int id) {
		int vId = (int) (id % vertices.size());
		return vertices.get(vId);
	}

	protected int getPartialGraphCount() {
		return 1;
	}

	protected int getInitialPartialGraphId(int i) {
		double range = (roots + 1) / getPartialGraphCount();
		double val = i / range;
		return (int) val + 1;
	}

}
