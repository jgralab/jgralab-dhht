package de.uni_koblenz.jgralab.algolib.universalsearch;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
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
	protected Graph graph;
	protected CentralAlgorithm stub;

	/* maps partial graph Ids to the remote algorithms stored as local proxies */
	protected Map<Integer, SatelliteAlgorithmRemoteAccess> remoteAlgorithms = new HashMap<Integer, SatelliteAlgorithmRemoteAccess>();

	/* returns the SatelliteAlgorithm for the graph the element belongs to */
	private SatelliteAlgorithmRemoteAccess getAlgorithmForElementId(
			Long elementId) {
		return remoteAlgorithms.get(GraphDatabaseElementaryMethods
				.getPartialGraphId(elementId));
	}

	/** creates a local algorithm object as central instance of a search */
	public CentralAlgorithmImpl(Graph partialGraph, boolean dfs) {
		this.graph = partialGraph;
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
		remoteAlgorithm.enqueueRoot(vertexId);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			boolean stopped = true;
			for (SatelliteAlgorithmRemoteAccess remoteAlgo : remoteAlgorithms.values()) {
				if (remoteAlgo.isWorking())
					stopped = false;
			}
			if (stopped) {
				for (SatelliteAlgorithmRemoteAccess remoteAlgo : remoteAlgorithms.values()) {
					remoteAlgo.stop();
				}
				return;
			}
		}
		
	}

	public boolean testAndProcessEdge(long edgeId, long incId)
			throws RemoteException {
		SatelliteAlgorithmRemoteAccess remoteAlgo = getAlgorithmForElementId(edgeId);
		return remoteAlgo.testAndEnqueueEdge(edgeId, incId);
	}

	public boolean testAndProcessVertex(long vertexId, long incId)
			throws RemoteException {
		SatelliteAlgorithmRemoteAccess remoteAlgo = getAlgorithmForElementId(vertexId);
		return remoteAlgo.testAndEnqueueVertex(vertexId, incId);
	}

	public int getPartialGraphId() {
		return graph.getPartialGraphId();
	}
}

