package de.uni_koblenz.jgralab.algolib;

import de.uni_koblenz.jgralab.Graph;

public class CountCentralAlgorithm extends CentralAlgorithmImpl {

	public CountCentralAlgorithm(Graph partialGraph, boolean dfs) {
		super(partialGraph, dfs);
	}

	private int eCount = 0;

	private int vCount = 0;

	@Override
	public void handleRoot(long rootId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleVertex(long vertexId) {
		vCount++;
	}

	@Override
	public void handleEdge(long edgeId) {
		eCount++;
	}

	@Override
	public void handleTreeIncidence(long incidenceId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCrossIncidence(long incidenceId) {
		// TODO Auto-generated method stub

	}

	public long getVertexCount() {
		return vCount;
	}

	public long getEdgeCount() {
		return eCount;
	}

}
