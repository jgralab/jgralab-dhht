package de.uni_koblenz.jgralabtest.dhht;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.dhhttest.schema.DHHTTestGraph;
import de.uni_koblenz.jgralab.dhhttest.schema.DHHTTestSchema;
import de.uni_koblenz.jgralab.dhhttest.schema.SimpleEdge_start;
import de.uni_koblenz.jgralab.dhhttest.schema.SimpleEdge_target;
import de.uni_koblenz.jgralab.dhhttest.schema.SimpleVertex;
import de.uni_koblenz.jgralab.dhhttest.schema.SimulatedHyperedge;
import de.uni_koblenz.jgralab.dhhttest.schema.SimulatedIncidence_incInc;
import de.uni_koblenz.jgralab.dhhttest.schema.SimulatedIncidence_outInc;

public class TreeGraphGenerator {

	protected DHHTTestGraph graph;
	
	private int layers;
	private int roots;
	private int minBranchingFactor;
	private int maxBranchingFactor;
	private int currentBranchingFactor;
	private boolean diskBased;
	private boolean useHyperedges;
	
	private int additionalEdgeCount;
	
	
	private int getNextBranchingFactor() {
		int retVal = currentBranchingFactor;
		currentBranchingFactor++;
		if (currentBranchingFactor > maxBranchingFactor) {
			currentBranchingFactor = minBranchingFactor;
		}
		return retVal;
	}
	private ArrayList<SimpleVertex> vertices;
	
	
	int[] edgeBranchingFactors = {1,1,1,1,3};
	
	int currentEdgeBranchingFactorIndex = 0;
	
	public TreeGraphGenerator(int layers, int roots, int minBranchingFactor, int maxBranchingFactor, int additionalEdgeCount, boolean useHyperedges, boolean diskBased) {
		this.layers = layers;
		this.roots = roots;
		this.additionalEdgeCount = additionalEdgeCount;
		this.minBranchingFactor = minBranchingFactor;
		this.maxBranchingFactor = maxBranchingFactor;
		this.currentBranchingFactor = minBranchingFactor;
		this.diskBased = diskBased;
		this.useHyperedges = useHyperedges;
		this.vertices = new ArrayList<SimpleVertex>();
	}
	
	private int getNextEdgeBranchingFactor() {
		return edgeBranchingFactors[(currentEdgeBranchingFactorIndex++)%edgeBranchingFactors.length];
	}
	
	public DHHTTestGraph createGraph() {
		if (diskBased)  {
			graph = DHHTTestSchema.instance().createDHHTTestGraphOnDisk();
			System.out.println("Creating disk graph");
		} else {
			System.out.println("Creating mem graph");
			graph = DHHTTestSchema.instance().createDHHTTestGraphInMem();
		}

		int vCount = 1;       
		int sizeOfLastLayer = 20000000;
		long[] vertexList = new long[sizeOfLastLayer];
		int vertexListSize = roots;
		int newVertexListSize = 0;
		int retrievedVertices = 0;
		for (int i=0; i<roots; i++) {
			DHHTTestGraph partialGraph = createPartialGraph(i/getPartialGraphCount());
			Vertex v = partialGraph.createSimpleVertex();
			vertexList[i] = v.getGlobalId();
			vertices.add((SimpleVertex) v);
		}	
		for (int layer=1; layer<layers; layer++) { 
			long[] newVertexList = new long[sizeOfLastLayer];
			while (retrievedVertices < vertexListSize) {
				Vertex parent = graph.getVertex(vertexList[retrievedVertices++]);
				int i = 0;
				int nextBranchingFactor = getNextBranchingFactor();
				//System.out.println("out loop");
				while (i<nextBranchingFactor) {
				//	System.out.println("loop");
					DHHTTestGraph partialGraph = getGraph(parent.getGlobalId());
					if (useHyperedges) {
						Edge e  = partialGraph.createSimpleEdge();
						e.connect(SimpleEdge_start.class, parent);
						int j=0;
						for (j=0; j<getNextEdgeBranchingFactor(); j++) {
							vCount++;
							SimpleVertex v = graph.createSimpleVertex();
							vertices.add(v);
							newVertexList[newVertexListSize++] = v.getGlobalId();
							e.connect(SimpleEdge_target.class, v);
						}	
						i +=j;
					} else {
						int edgeBranchingFactor = getNextEdgeBranchingFactor();
						if (edgeBranchingFactor == 1) {
							vCount++;
							SimpleVertex v = partialGraph.createSimpleVertex();
							vertices.add(v);
							newVertexList[newVertexListSize++] = v.getGlobalId();
							Edge e  = partialGraph.createSimpleEdge();
							e.connect(SimpleEdge_start.class, parent);
							e.connect(SimpleEdge_target.class, v);
							i++;
						} else {
							vCount++;
							SimulatedHyperedge hyperedge = partialGraph.createSimulatedHyperedge();
							Edge e  = partialGraph.createSimulatedIncidence();
							e.connect(SimulatedIncidence_incInc.class, parent);
							e.connect(SimulatedIncidence_outInc.class, hyperedge);
							int j=0;
							for (j=0; j<edgeBranchingFactor; j++) {
						//		System.out.println("Loiop");
								vCount++;
								SimpleVertex v = partialGraph.createSimpleVertex();
								vertices.add(v);
								newVertexList[newVertexListSize++] = v.getGlobalId();
								Edge e1 = partialGraph.createSimulatedIncidence();
								e1.connect(SimulatedIncidence_incInc.class, hyperedge);
								e1.connect(SimulatedIncidence_outInc.class, v);
								i++;
							}
						//	System.out.println("Out");
							i +=j;
						}
					}
					


				}
			}
			vertexList = newVertexList;
			vertexListSize = newVertexListSize;
			newVertexListSize = 0;
			retrievedVertices = 0;
		}
		//System.out.println("additonal");
		for (int i=0; i< additionalEdgeCount; i++) {
			int edgeBranchingFactor = getNextEdgeBranchingFactor();
			if (edgeBranchingFactor == 1) {
				Vertex start = getVertex(i * 7);
				DHHTTestGraph partialGraph = getGraph(start.getGlobalId());
				Vertex target = getVertex(i * 13);
				Edge e = partialGraph.createSimpleEdge();
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
					start = getVertex(i*7+j++);
				}
				j=1;
				Vertex target = getVertex(i * 13);
				while (targetVertices.size() < edgeBranchingFactor) {
					targetVertices.add((SimpleVertex) target);
					target = getVertex(i*13+j++);
				}
				if (useHyperedges) {
					Edge e = partialGraph.createSimpleEdge();
					for (Vertex v : startVertices) {
						e.connect(SimpleEdge_start.class, v);	
					}
					for (Vertex v : targetVertices) {
						e.connect(SimpleEdge_target.class, v);	
					}					
				} else {
					SimulatedHyperedge simulatedHyperedge = partialGraph.createSimulatedHyperedge();
					for (SimpleVertex v : startVertices) {
						Edge e = partialGraph.createSimulatedIncidence();
						e.connect(SimulatedIncidence_incInc.class, v);
						e.connect(SimulatedIncidence_outInc.class, simulatedHyperedge);
					}
					for (SimpleVertex v : targetVertices) {
						Edge e = partialGraph.createSimulatedIncidence();
						e.connect(SimulatedIncidence_incInc.class, simulatedHyperedge);
						e.connect(SimulatedIncidence_outInc.class, v);
					}	
				}
			} 
		}
		return graph;
	}
	
//	private Vertex getVertex(int id) {
//		int vId = (int) ( id % graph.getVCount());
//		if (diskBased)
//			return graph.getVertex(graph.getGraphDatabase().convertToGlobalId(vId+1));
//		else
//			return graph.getVertex(vId+1);
//	}
	
	protected DHHTTestGraph createPartialGraph(int i) {
		return graph;
	}

	protected DHHTTestGraph getGraph(long globalId) {
		return graph;
	}

	private Vertex getVertex(int id) {
		int vId = (int) ( id % vertices.size());
		return vertices.get(vId);
	}
	
	protected int getPartialGraphCount() {
		return 1;
	}
	
	
}
