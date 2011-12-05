package de.uni_koblenz.jgralab.algolib;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.disk.GraphDatabaseElementaryMethods;

public class HypergraphSearchAlgorithmOptimized {

	protected int[][] parentVertexInc;
	protected int[][] parentEdgeInc;
	protected long num;

	/*
	 * this buffer needs to be instatiated in a subclass by the appropriate
	 * implementation class for e.g. a Queue or Stack
	 */
	protected Buffer<Vertex> buffer;

	/**
	 * Initializes the algorithm's private fields according to the number of
	 * edges and vertices in the graph
	 * 
	 * @param graph
	 */
	private void init(Graph graph) {
		if (parentVertexInc != null)
			throw new RuntimeException(
					"Cannot init algorithm, algorithm has already been used");
		int vCount;
		int eCount;
		vCount = (int) graph.getVCount();
		eCount = (int) graph.getECount();
		parentVertexInc = new int[3][vCount*2+1];
		parentEdgeInc = new int[3][eCount*2 + 1];
		num = 0;
	}

	/**
	 * starts the search beginning from the vertex <code>startVertex</code>
	 * 
	 * @throws RemoteException
	 */
	public void run(Vertex startVertex) {
		init(startVertex.getGraph());
		handleRoot(startVertex);
		handleVertex(startVertex);
		buffer.add(startVertex);

		while (!buffer.isEmpty()) {
			Vertex currentVertex = buffer.get();
			Incidence curIncAtVertex = currentVertex.getFirstIncidence();
			while (curIncAtVertex != null) {
				Edge currentEdge = curIncAtVertex.getEdge();
				int ePgId = GraphDatabaseElementaryMethods
						.getPartialGraphId(currentEdge.getGlobalId());
				if (parentEdgeInc[ePgId][currentEdge.getLocalId()] == 0) {
					handleEdge(currentEdge);
					parentEdgeInc[ePgId][currentEdge.getLocalId()] = curIncAtVertex
							.getLocalId();
					handleTreeIncidence(curIncAtVertex);
					Direction opposite = curIncAtVertex.getDirection().getOppositeDirection();
					Incidence curIncAtEdge = currentEdge.getFirstIncidence();
					while (curIncAtEdge != null) {
						Vertex omega = curIncAtEdge.getVertex();
						int omegaPgId = GraphDatabaseElementaryMethods
								.getPartialGraphId(omega.getGlobalId());
						if ((parentVertexInc[omegaPgId][omega.getLocalId()] == 0)
								&& (omega != startVertex)) {
							// System.out.println("Omega vertex is handled");
							parentVertexInc[omegaPgId][omega.getLocalId()] = curIncAtEdge
									.getLocalId();
							handleVertex(omega);
							handleTreeIncidence(curIncAtEdge);
							// System.out.println("Omega vertex is enqueed");
							buffer.add(omega);
						} else {
							handleCrossIncidence(curIncAtEdge);
						}
						curIncAtEdge = curIncAtEdge.getNextIncidenceAtEdge();
					}
				} else {
					handleCrossIncidence(curIncAtVertex);
				}
				curIncAtVertex = curIncAtVertex.getNextIncidenceAtVertex();
			}
		}
	}

	public void handleRoot(Vertex v) {
	}

	public void handleVertex(Vertex v) {
	}

	public void handleEdge(Edge e) {
	}

	public void handleTreeIncidence(Incidence i) {
	}

	public void handleCrossIncidence(Incidence i) {
	}

}
