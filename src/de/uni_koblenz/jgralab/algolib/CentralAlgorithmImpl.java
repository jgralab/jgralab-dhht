package de.uni_koblenz.jgralab.algolib;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.disk.GraphDatabaseElementaryMethods;

/**
 * Implementation of central part of the search algorithm with all
 * datastructures needed to control the algorithm such as the buffer and the
 * marking of vertices and edges
 */
public abstract class CentralAlgorithmImpl implements CentralAlgorithm {

	// each element is identified by a combination of the partial graph Id it
	// belongs to and its element Id local to the partial graph
	protected Map<Long, Long> number = new HashMap<Long, Long>();
	protected long[][] numberA = new long[3][1000];
	protected List<Long> order = new ArrayList<Long>();
	protected Map<Long, Long> parentVertexInc = new HashMap<Long, Long>();
	protected Map<Long, Long> parentEdgeInc = new HashMap<Long, Long>();
	protected long num = 0;
	protected Buffer<Long> buffer;
	protected Graph graph;
	protected CentralAlgorithm stub;

	/* maps partial graph Ids to the remote algorithms stored as local proxies */
	private Map<Integer, SatelliteAlgorithmRemoteAccess> remoteAlgorithms = new HashMap<Integer, SatelliteAlgorithmRemoteAccess>();

	/* returns the SatelliteAlgorithm for the graph the element belongs to */
	private SatelliteAlgorithmRemoteAccess getAlgorithmForElementId(
			Long elementId) {
		return remoteAlgorithms.get(GraphDatabaseElementaryMethods
				.getPartialGraphId(elementId));
	}

	/** creates a local algorithm object as central instance of a search */
	public CentralAlgorithmImpl(Graph partialGraph, boolean dfs) {
		this.graph = partialGraph;
		if (dfs) {
			this.buffer = new Stack<Long>();
		} else {
			this.buffer = new Queue<Long>();
		}
		// create satellite algorithms for all partial graphs incl. the local
		// one
		try {
			this.stub = (CentralAlgorithm) UnicastRemoteObject.exportObject(
					this, 0);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		remoteAlgorithms.put(partialGraph.getPartialGraphId(),
				SatelliteAlgorithmImpl.createRemote(partialGraph, stub));
		for (Graph pg : partialGraph.getCompleteGraph().getPartialGraphs()) {
			remoteAlgorithms.put(pg.getPartialGraphId(),
					SatelliteAlgorithmImpl.createRemote(pg, stub));
		}
	}

	public void run(Vertex startVertex) throws RemoteException {
		long vertexId = startVertex.getGlobalId();
		SatelliteAlgorithmRemoteAccess remoteAlgorithm = getAlgorithmForElementId(vertexId);
		number.put(vertexId, ++num); // number first vertex with 1
		order.add(vertexId);
		handleRoot(vertexId);
		handleVertex(vertexId);
		buffer.add(vertexId);
		remoteAlgorithm.processRoot(vertexId);
		while (!buffer.isEmpty()) {
			getAlgorithmForElementId(vertexId).processVertex(buffer.get());
		}
	}

	public boolean testAndProcessEdge(long edgeId, long incId)
			throws RemoteException {
		if (!parentEdgeInc.containsKey(edgeId)) {
			parentEdgeInc.put(edgeId, incId);
			handleEdge(edgeId);
			handleTreeIncidence(incId);
			return true;
		}
		handleCrossIncidence(incId);
		return false;
	}

	public boolean testAndProcessVertex(long vertexId, long incId)
			throws RemoteException {
		Long key = Long.valueOf(vertexId);
		if (!number.containsKey(key)) {
			number.put(key, ++num);
			order.add(key);
			parentVertexInc.put(key, incId);
			buffer.add(key);
			handleVertex(vertexId);
			handleTreeIncidence(incId);
			return true;
		}
		handleCrossIncidence(incId);
		return false;
	}

	public int getPartialGraphId() {
		return graph.getPartialGraphId();
	}
}
