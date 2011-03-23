package de.uni_koblenz.jgralab.dhhttest;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

public class HypergraphSearchAlgorithm {

		protected Map<Vertex, Incidence> parentVertexInc;
		protected Map<Edge, Incidence> parentEdgeInc;
		protected int num;

		/* this buffer needs to be instatiated in a subclass
		 * by the appropriate implementation class for e.g. a Queue or Stack*/
		protected Buffer<Vertex> buffer; 
		
		/**
		 * Initializes the algorithm's private fields according to the number of edges
		 * and vertices in the graph
		 * @param graph
		 */
		private void init(Graph graph) {
			if (parentVertexInc != null)
				throw new RuntimeException("Cannot init algorithm, algorithm has already been used");
			int vCount;
			int eCount;
			try {
				vCount = graph.getVCount();
				eCount = graph.getECount();
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}
			parentVertexInc = new HashMap<Vertex, Incidence>(vCount);
			parentEdgeInc = new HashMap<Edge, Incidence>(eCount);
			num = 0;
		}

		/** starts the search beginning from the vertex <code>startVertex</code> 
		 * @throws RemoteException */
		public void run(Vertex startVertex) throws RemoteException {
			init(startVertex.getGraph());
			handleRoot(startVertex);
			handleVertex(startVertex);
			buffer.add(startVertex);
			
			while (!buffer.isEmpty()) {
				Vertex currentVertex = buffer.get();        
				Incidence curIncAtVertex = currentVertex.getFirstIncidence(Direction.BOTH);
				while (curIncAtVertex != null) {
					Edge currentEdge = curIncAtVertex.getEdge();
					if (!parentEdgeInc.containsKey(currentEdge)) {   
						handleEdge(currentEdge);
						parentEdgeInc.put(currentEdge, curIncAtVertex);     
						handleTreeIncidence(curIncAtVertex); 
						Direction opposite = curIncAtVertex.getDirection().getOppositeDirection();
						Incidence curIncAtEdge = currentEdge.getFirstIncidence(opposite);
						while (curIncAtEdge != null) {
							Vertex omega = curIncAtEdge.getVertex();
							if ((!parentVertexInc.containsKey(omega)) && (omega!=startVertex)) {
								parentVertexInc.put(omega, curIncAtEdge);
								handleVertex(omega);
								handleTreeIncidence(curIncAtEdge);
								buffer.add(omega);
							} else {
								handleCrossIncidence(curIncAtEdge);
							}
							curIncAtEdge = curIncAtEdge.getNextIncidenceAtEdge(opposite);
						}	  
					} else {
						handleCrossIncidence(curIncAtVertex);
					}
					curIncAtVertex = curIncAtVertex.getNextIncidenceAtVertex(Direction.BOTH);
				}
			}  
		}	

		public void handleRoot(Vertex v) { }
		  
		public void handleVertex(Vertex v) {}
		  
		public void handleEdge(Edge e) {}
		 
		public void handleTreeIncidence(Incidence i) {}
		  
		public void handleCrossIncidence(Incidence i) {}

	}                
