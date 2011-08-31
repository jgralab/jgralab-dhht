package de.uni_koblenz.jgralabtest.dhht;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.uni_koblenz.jgralab.Edge;
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

	private DHHTTestGraph graph;
	
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
			Vertex v = graph.createSimpleVertex();
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
					if (useHyperedges) {
						Edge e  = graph.createSimpleEdge();
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
							SimpleVertex v = graph.createSimpleVertex();
							vertices.add(v);
							newVertexList[newVertexListSize++] = v.getGlobalId();
							Edge e  = graph.createSimpleEdge();
							e.connect(SimpleEdge_start.class, parent);
							e.connect(SimpleEdge_target.class, v);
							i++;
						} else {
							vCount++;
							SimulatedHyperedge hyperedge = graph.createSimulatedHyperedge();
							Edge e  = graph.createSimulatedIncidence();
							e.connect(SimulatedIncidence_incInc.class, parent);
							e.connect(SimulatedIncidence_outInc.class, hyperedge);
							int j=0;
							for (j=0; j<edgeBranchingFactor; j++) {
						//		System.out.println("Loiop");
								vCount++;
								SimpleVertex v = graph.createSimpleVertex();
								vertices.add(v);
								newVertexList[newVertexListSize++] = v.getGlobalId();
								Edge e1 = graph.createSimulatedIncidence();
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
				while (start instanceof SimulatedHyperedge)
					start = start.getNextVertex();
				if (start == null)
					start = graph.getFirstSimpleVertex();
				Vertex target = getVertex(i * 13);
				while (target instanceof SimulatedHyperedge)
					target = target.getNextVertex();
				if (target == null)
					target = graph.getFirstSimpleVertex().getNextSimpleVertex();
				Edge e = graph.createSimpleEdge();
				e.connect(SimpleEdge_start.class, start);
				e.connect(SimpleEdge_target.class, target);
			} else {
				List<SimpleVertex> startVertices = new LinkedList<SimpleVertex>();
				List<SimpleVertex> targetVertices = new LinkedList<SimpleVertex>();
				Vertex start = getVertex(i * 7);
				while (startVertices.size() < edgeBranchingFactor) {
					
					while (start instanceof SimulatedHyperedge) {
						start = start.getNextVertex();
						if (start == null) {
							start = graph.getFirstSimpleVertex();
							continue;
						}
					}
					startVertices.add((SimpleVertex) start);
					start = start.getNextVertex();
				}
				Vertex target = getVertex(i * 13);
				while (targetVertices.size() < edgeBranchingFactor) {
					while (target instanceof SimulatedHyperedge) {
					//	System.out.println("Target inner loop, " + target.getId());
						target = target.getNextVertex();
						if (target == null) {
							target = graph.getFirstSimpleVertex();
							continue;
						}
					}	
					targetVertices.add((SimpleVertex) target);
					target = target.getNextVertex();
				}
				SimulatedHyperedge simulatedHyperedge = graph.createSimulatedHyperedge();
				for (SimpleVertex v : startVertices) {
					Edge e = graph.createSimulatedIncidence();
					e.connect(SimulatedIncidence_incInc.class, v);
					e.connect(SimulatedIncidence_outInc.class, simulatedHyperedge);
				}
				for (SimpleVertex v : targetVertices) {
					Edge e = graph.createSimulatedIncidence();
					e.connect(SimulatedIncidence_incInc.class, simulatedHyperedge);
					e.connect(SimulatedIncidence_outInc.class, v);
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
	
	private Vertex getVertex(int id) {
		int vId = (int) ( id % vertices.size());
		return vertices.get(vId);
	}
	
	
	
	
}
