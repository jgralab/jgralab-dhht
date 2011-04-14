package de.uni_koblenz.jgralab.dhhttest;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

public class CountHypergraphSearchAlgorithm extends HypergraphSearchAlgorithm {

	public CountHypergraphSearchAlgorithm() {
		super();
		buffer = new Queue<Vertex>();
	}
	
	private int vertices = 0;
	
	private int edges = 0;
	
	private int treeIncs = 0;
	
	private int crossIncs = 0;
	
	public void handleVertex(Vertex v) {
		vertices++;
	}
	  
	public void handleEdge(Edge e) {
		edges++;
	}
	 
	public void handleTreeIncidence(Incidence i) {
		treeIncs++;
	}
	  
	public void handleCrossIncidence(Incidence i) {
		crossIncs++;
	}

	public int getVertexCount() {
		return vertices;
	}
	
	public int getEdgeCount() {
		return edges;
	}
	
	public int getTreeIncCount() {
		return treeIncs;
	}
	
	public int getCrossIncCount() {
		return crossIncs;
	}
}
