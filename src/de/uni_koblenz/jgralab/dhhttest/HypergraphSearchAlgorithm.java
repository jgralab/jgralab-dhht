package de.uni_koblenz.jgralab.dhhttest;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

public class HypergraphSearchAlgorithm {

		protected Map<Vertex, Integer> number = new HashMap<Vertex, Integer>();
		protected List<Vertex> order = new ArrayList<Vertex>();
		protected Map<Vertex, Incidence> parentVertexInc = new HashMap<Vertex, Incidence>();
		protected Map<Edge, Incidence> parentEdgeInc = new HashMap<Edge, Incidence>();
		protected int num = 0;

		/* this buffer needs to be instatiated in a subclass
		 * by the appropriate implementation class for e.g. a Queue or Stack*/
		protected Buffer<Vertex> buffer; 

		/** starts the search beginning from the vertex <code>startVertex</code> 
		 * @throws RemoteException */
		public void run(Vertex startVertex) throws RemoteException {
			number.put(startVertex, ++num); //number first vertex with 1
			order.add(startVertex);
			handleRoot(startVertex);
			handleVertex(startVertex);
			buffer.add(startVertex);
			
			while (!buffer.isEmpty()) {
				Vertex currentVertex = buffer.get();        
				Incidence curIncAtVertex = currentVertex.getFirstIncidence(Direction.BOTH);
				while (curIncAtVertex != null) {
				//	System.out.println("Iterating incidence at vertex " + currentVertex.getId());
					Edge currentEdge = curIncAtVertex.getEdge();
					if (!parentEdgeInc.containsKey(currentEdge)) {   
						handleEdge(currentEdge);
						parentEdgeInc.put(currentEdge, curIncAtVertex);     
						handleTreeIncidence(curIncAtVertex); 
						Direction opposite = curIncAtVertex.getDirection().getOppositeDirection();
						Incidence curIncAtEdge = currentEdge.getFirstIncidence(opposite);
						while (curIncAtEdge != null) {
							Vertex omega = curIncAtEdge.getVertex();
							if (!number.containsKey(omega)) {
								number.put(omega, ++num);
								order.add(omega);
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
