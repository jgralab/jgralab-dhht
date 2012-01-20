package de.uni_koblenz.jgralabtest.dhht;

import java.util.ArrayList;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.JGraLabServerImpl;
import de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl;
import de.uni_koblenz.jgralabtest.dhht.schema.DHHTTestGraph;
import de.uni_koblenz.jgralabtest.dhht.schema.SimpleEdge_start;
import de.uni_koblenz.jgralabtest.dhht.schema.SimpleEdge_target;
import de.uni_koblenz.jgralabtest.dhht.schema.SimpleVertex;


public class SubgraphGenerator implements RemoteGraphGenerator {
	
	
	protected DHHTTestGraph graph;
	
	
	private int layers;
	
	private int[] branchingFactors;
	
	private int currentBranchingFactorIndex = 0;
	
	private ArrayList<SimpleVertex> vertices;
	
	private int getNextBranchingFactor() {
		if (currentBranchingFactorIndex >= branchingFactors.length) {
			currentBranchingFactorIndex = 0;
		}
		return branchingFactors[currentBranchingFactorIndex++];
	}
	
	int[] edgeBranchingFactors = { 1, 1, 1, 1, 3 };

	int currentEdgeBranchingFactorIndex = 0;
	
	private int getNextEdgeBranchingFactor() {
		return edgeBranchingFactors[(currentEdgeBranchingFactorIndex++)
				% edgeBranchingFactors.length];
	}

	
	public SubgraphGenerator(String uid, int levels, int[] branchingFactors) {
		this.branchingFactors = branchingFactors;
		this.layers = levels;
		GraphDatabaseBaseImpl db = (GraphDatabaseBaseImpl) JGraLabServerImpl.getLocalInstance().getLocalGraphDatabase(uid, ImplementationType.DISTRIBUTED);
		graph = (DHHTTestGraph) db.getGraphObject(db.getToplevelGraphForPartialGraphId(db.getLocalPartialGraphId()));
	}

	
	
	/* creates a subgraph with the given number of levels and returns the id of the root vertex */
	public long createSubgraph() {
		vertices = new ArrayList<SimpleVertex>();
		int branchingFactorIndex = 0;
		int vCount = 1;
		int sizeOfLastLayer = 6000000;
		long createdVertices = 0;
		long[] vertexList = new long[sizeOfLastLayer];
		int vertexListSize = 1;
		int newVertexListSize = 0;
		int retrievedVertices = 0;
		Vertex root = graph.createSimpleVertex();
		createdVertices++;
		vertexList[0]=root.getGlobalId();
		vertices.add((SimpleVertex) root);
		
		
		for (int layer = 1; layer < layers; layer++) {
			long[] newVertexList = new long[sizeOfLastLayer];
			while (retrievedVertices < vertexListSize) {
				Vertex parent = graph.getVertex(vertexList[retrievedVertices++]);
				int i = 0;
				int nextBranchingFactor = getNextBranchingFactor();
				while (i < nextBranchingFactor) {
						Edge e = graph.createSimpleEdge();
							e.connect(SimpleEdge_start.class, parent);
						int j = 0;
						int nextEdgeBranchingFactor = getNextEdgeBranchingFactor();
						if (i + nextEdgeBranchingFactor > nextBranchingFactor)
							nextEdgeBranchingFactor = nextBranchingFactor - i;
						for (j = 0; j < nextEdgeBranchingFactor; j++) {
							vCount++;
							SimpleVertex v = graph.createSimpleVertex();
							createdVertices++;
							v.setKappa(layers - layer);
							v.putAfter(parent);
							v.setSigma(parent);
							vertices.add(v);
							newVertexList[newVertexListSize++] = v
									.getGlobalId();
							e.connect(SimpleEdge_target.class, v);
						}
						i += j;
				}
			}
			vertexList = newVertexList;
			vertexListSize = newVertexListSize;
			newVertexListSize = 0;
			retrievedVertices = 0;
		}
		return root.getGlobalId();
	}

}
