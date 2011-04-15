package de.uni_koblenz.jgralab.dhhttest;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

public class HypergraphSearchAlgorithmOptimized {

	//TODO: Record TreeIncidences separately to avoid handling them as tree and as cross incidence
		//protected Set<Vertex> marking;
		//protected Map<Vertex, Integer> number;
	//	protected List<Vertex> order;
		protected Incidence[] parentVertexInc;
		protected Incidence[] parentEdgeInc;
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
		//	marking = new HashSet<Vertex>(vCount);
			//number = new HashMap<Vertex, Integer>(vCount);
			//order = new ArrayList<Vertex>(vCount);
			parentVertexInc = new Incidence[vCount+1];
			parentEdgeInc = new Incidence[eCount+1];
			num = 0;
		}

		/** starts the search beginning from the vertex <code>startVertex</code> 
		 * @throws RemoteException */
		public void run(Vertex startVertex) throws RemoteException {
			init(startVertex.getGraph());
			//number.put(startVertex, ++num); //number first vertex with 1
		//	order.add(startVertex);
			handleRoot(startVertex);
			handleVertex(startVertex);
			buffer.add(startVertex);
			
			while (!buffer.isEmpty()) {
				Vertex currentVertex = buffer.get();        
				Incidence curIncAtVertex = currentVertex.getFirstIncidence(Direction.BOTH);
				while (curIncAtVertex != null) {
				//	System.out.println("Iterating incidence at vertex " + currentVertex.getId());
					Edge currentEdge = curIncAtVertex.getEdge();
					if (parentEdgeInc[currentEdge.getId()]==null) {   
					//	handleEdge(currentEdge);
						parentEdgeInc[currentEdge.getId()] = curIncAtVertex;     
					//	handleTreeIncidence(curIncAtVertex); 
						Direction opposite = curIncAtVertex.getDirection().getOppositeDirection();
						Incidence curIncAtEdge = currentEdge.getFirstIncidence(opposite);
						while (curIncAtEdge != null) {
							Vertex omega = curIncAtEdge.getVertex();
							//if (!number.containsKey(omega)) {
							//	number.put(omega, ++num);
							if ((parentVertexInc[omega.getId()]==null) && (omega!=startVertex)) {
						//		order.add(omega);
								parentVertexInc[omega.getId()]= curIncAtEdge;
								//handleVertex(omega);
								//handleTreeIncidence(curIncAtEdge);
								buffer.add(omega);
							} else {
							//	handleCrossIncidence(curIncAtEdge);
							}
							curIncAtEdge = curIncAtEdge.getNextIncidenceAtEdge(opposite);
						}	  
					} else {
						//handleCrossIncidence(curIncAtVertex);
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
