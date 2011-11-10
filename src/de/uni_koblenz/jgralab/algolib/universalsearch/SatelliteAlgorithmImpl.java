package de.uni_koblenz.jgralab.algolib.universalsearch;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLabServer;
import de.uni_koblenz.jgralab.RemoteJGraLabServer;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.algolib.Buffer;
import de.uni_koblenz.jgralab.algolib.Queue;
import de.uni_koblenz.jgralab.impl.JGraLabServerImpl;
import de.uni_koblenz.jgralab.impl.disk.GraphDatabaseElementaryMethods;

public class SatelliteAlgorithmImpl implements SatelliteAlgorithm,
		SatelliteAlgorithmRemoteAccess {
	
	protected Buffer<Vertex> buffer;
	protected CentralAlgorithm centralAlgorithm;
	protected Graph graph;
	protected long parentVertexInc[];
	protected long parentEdgeInc[];
	protected boolean started = false;
	protected int localPartialGraphId;
	protected boolean working = false;
	protected boolean isStopped = false;
	
	private long vCount = 0;
	private long eCount = 0;
	
	
	protected SatelliteAlgorithmImpl(Graph partialGraph,
			CentralAlgorithm centralAlgorithm) {
		this.graph = partialGraph;
		this.centralAlgorithm = centralAlgorithm;
		localPartialGraphId = graph.getPartialGraphId();
		buffer = new Queue<Vertex>();
		parentVertexInc = new long[6000000];
		parentEdgeInc = new long[6000000];
		Thread t = new Thread() {
			public void run() {
				try {
					runAlgo();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

	public void enqueueRoot(long vertexId) throws RemoteException {
		Vertex rootVertex = graph.getVertex(vertexId);
		handleRoot(rootVertex);
		handleVertex(rootVertex);
		buffer.add(rootVertex);
	}

	
	public void runAlgo() throws RemoteException {
		while (!isStopped) {
		//	System.out.println("Not stopped ");
			if (!buffer.isEmpty()) {
				synchronized (this) {
					working = true;		
				}
				processBuffer();
			} else {
				//System.out.println("sleeping ");
				synchronized (this) {
					working = false;		
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	
	public boolean isWorking() {
		synchronized (this) {
			return working;	
		}
	}
	
	
	public void stop() {
		isStopped = true;
	}
	
	public void processBuffer() throws RemoteException {
		while (!buffer.isEmpty()) {
			Vertex currentVertex = buffer.get();
			for (Incidence curIncAtVertex : currentVertex
				.getIncidences(Direction.VERTEX_TO_EDGE)) {
				Edge currentEdge = curIncAtVertex.getEdge();
				long curEdgeId = currentEdge.getGlobalId();
				if (GraphDatabaseElementaryMethods.getPartialGraphId(curEdgeId) != localPartialGraphId) {
					//remote edge should be handled on remote machine
					centralAlgorithm.testAndProcessEdge(curEdgeId, curIncAtVertex.getGlobalId());
				} else {
					processEdge(currentEdge, curIncAtVertex);
				}
			}	
		}
	}
	
	public boolean testAndEnqueueEdge(long edgeId, long curIncAtVertexId) throws RemoteException {
		Edge currentEdge = graph.getEdge(edgeId);
		Incidence currentIncidence = graph.getGraphDatabase().getIncidenceObject(curIncAtVertexId);
		return processEdge(currentEdge, currentIncidence);
	}
	
	public boolean testAndEnqueueVertex(long vertexId, long curIncAtEdgeId) {
		Vertex currentVertex = graph.getVertex(vertexId);
		Incidence currentIncidence = graph.getGraphDatabase().getIncidenceObject(curIncAtEdgeId);
		return processVertex(currentVertex, currentIncidence);
	}
	
	
	private boolean processEdge(Edge currentEdge, Incidence curIncAtVertex) throws RemoteException {
		int localEdgeId = currentEdge.getLocalId();
		if (parentEdgeInc[localEdgeId] == 0) {
			parentEdgeInc[localEdgeId] = curIncAtVertex.getGlobalId();
			handleEdge(currentEdge);
			Direction opposite = curIncAtVertex.getDirection().getOppositeDirection();
			for (Incidence curIncAtEdge : currentEdge.getIncidences(opposite)) {
				Vertex omega = curIncAtEdge.getVertex();
				long omegaId = omega.getGlobalId();
				if (GraphDatabaseElementaryMethods.getPartialGraphId(omegaId) != localPartialGraphId) {
					//toggle central algo to process vertex on its local station
					centralAlgorithm.testAndProcessVertex(omegaId, curIncAtEdge.getGlobalId());
				} else {
					//local vertex should be processes without remote communication
					processVertex(omega, curIncAtEdge);
				}					
			}
			return true;
		} else {
			handleCrossIncidence(curIncAtVertex);
			return false;
		}
	}
	
	
	private boolean processVertex(Vertex omega, Incidence curIncAtEdge) {
		int localOmegaId = omega.getLocalId();
		if (parentVertexInc[localOmegaId] == 0) {
			parentVertexInc[localOmegaId] = curIncAtEdge.getGlobalId();
			handleVertex(omega);
			handleTreeIncidence(curIncAtEdge);
			buffer.add(omega);
			return true;
		} else {
			handleCrossIncidence(curIncAtEdge);
			return false;
		}
	}
	
	
	

	public static SatelliteAlgorithmRemoteAccess create(Graph partialGraph,
			CentralAlgorithm parent) {
		return new SatelliteAlgorithmImpl(partialGraph, parent);
	}

	public static SatelliteAlgorithmRemoteAccess createRemote(
			Graph partialGraph, CentralAlgorithm parent) {
		int centralAlgorithmPartialGraphId;
		try {
			centralAlgorithmPartialGraphId = parent.getPartialGraphId();
		} catch (RemoteException e1) {
			throw new RuntimeException(e1);
		}
		int partialGraphId = partialGraph.getPartialGraphId();

		if (partialGraphId == centralAlgorithmPartialGraphId) {
			// create satellite algorithm on station of central algorithm
			SatelliteAlgorithmRemoteAccess algo = new SatelliteAlgorithmImpl(partialGraph, parent);
			return algo;
		} else {
			// create SatelliteAlgorithm object on remote station
			JGraLabServer server = JGraLabServerImpl.getLocalInstance();
			String remoteHostname = partialGraph.getGraphDatabase()
					.getHostname(partialGraphId);
			RemoteJGraLabServer remoteServer = server
					.getRemoteInstance(remoteHostname);
			try {
				SatelliteAlgorithmRemoteAccess remoteAlgo = remoteServer
						.createUniversalSatelliteAlgorithm(
								partialGraph.getUniqueGraphId(),
								partialGraphId, parent);
				return remoteAlgo;
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}
		}
	}


	@Override
	public void handleRoot(Vertex root) {

	}

	@Override
	public void handleVertex(Vertex vertex) {
		vCount++;
	}

	@Override
	public void handleEdge(Edge edge) {
		eCount++;
	}

	@Override
	public void handleTreeIncidence(Incidence incidence) {

	}

	@Override
	public void handleCrossIncidence(Incidence incidence) {
	}

	@Override
	public long getVCount() throws RemoteException {
		return vCount;
	}

	@Override
	public long getECount() throws RemoteException {
		return eCount;
	}


}