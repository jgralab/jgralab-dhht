package de.uni_koblenz.jgralabtest.dhht;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLabServer;
import de.uni_koblenz.jgralab.RemoteJGraLabServer;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.JGraLabServerImpl;

/** implementation of distributed part of the search algorithm */ 	 
public class SatelliteAlgorithmImpl 
					  implements SatelliteAlgorithm {     
	
	protected CentralAlgorithm centralAlgorithm;   
	
	protected Graph graph;
	
	protected SatelliteAlgorithmImpl(Graph partialGraph, CentralAlgorithm centralAlgorithm) {     
		this.graph = partialGraph;
		this.centralAlgorithm = centralAlgorithm;
	}
	  
	public void processVertex(Long vertexId) {  
		Vertex currentVertex = graph.getVertex(vertexId);
		for (Incidence curIncAtVertex : 
				         currentVertex.getIncidences(Direction.VERTEX_TO_EDGE)) {
			Edge currentEdge = curIncAtVertex.getEdge();   
			Long curIncAtVertexId = curIncAtVertex.getGlobalId();
		    Long curEdgeId = currentEdge.getGlobalId();  
			if (centralAlgorithm.testAndProcessEdge(curEdgeId, curIncAtVertexId)) {
				handleEdge(currentEdge);                             
				Direction opposite = curIncAtVertex.getDirection().getOppositeDirection();
				for (Incidence curIncAtEdge : 
						         currentEdge.getIncidences(opposite)) {
					Vertex omega = curIncAtEdge.getVertex(); 
					Long omegaId = omega.getGlobalId();     
					Long curIncAtEdgeId = curIncAtEdge.getGlobalId();
					if (centralAlgorithm.testAndProcessVertex(omegaId, curIncAtEdgeId)) {
						handleVertex(omega);
						handleTreeIncidence(curIncAtEdge);
					} else {
						handleCrossIncidence(curIncAtEdge);
					} 
				}	  
			}
		}
	}   
	
	public static SatelliteAlgorithm create(Graph partialGraph, 
					       			            CentralAlgorithm parent) {
		int centralAlgorithmPartialGraphId = parent.getPartialGraphID();
		int partialGraphId = partialGraph.getPartialGraphId();
		
		if (partialGraphId == centralAlgorithmPartialGraphId) {
			//create satellite algorithm on station of central algorithm
			return new SatelliteAlgorithmImpl(partialGraph, parent); 
		} else {
			//create SatelliteAlgorithm object on remote station 
			JGraLabServer server = JGraLabServerImpl.getLocalInstance();
			String remoteHostname = partialGraph.getGraphDatabase().getHostname(partialGraphId);
			RemoteJGraLabServer remoteServer = server.getRemoteInstance(remoteHostname);
			return remoteServer.createSatelliteAlgorithm(partialGraph.getUniqueGraphId(), partialGraphId, parent);
		}
	}
	
//	protected StatelliteAlgorithm createLocalInstance(Graph partialGraph, CentralAlgorithm parent) {
//		
//	}

	@Override
	public void handleRoot(Vertex root) {
		
	}

	@Override
	public void handleVertex(Vertex vertex) {
		
	}

	@Override
	public void handleEdge(Edge edge) {
		
	}

	@Override
	public void handleTreeIncidence(Incidence incidence) {
		
	}

	@Override
	public void handleCrossIncidence(Incidence incidence) {
		
	}
	
}   