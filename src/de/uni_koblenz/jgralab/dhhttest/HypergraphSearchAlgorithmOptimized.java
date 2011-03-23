package de.uni_koblenz.jgralab.dhhttest;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

public class HypergraphSearchAlgorithmOptimized {
	
		protected int[] parentVertexInc;
		protected int[] parentEdgeInc;
		protected int num;

		/* this buffer needs to be instatiated in a subclass
		 * by the appropriate implementation class for e.g. a Queue or Stack*/
		protected Queue<Vertex> buffer; 
		
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
			parentVertexInc = new int[vCount+1];
			parentEdgeInc = new int[eCount+1];
			num = 0;
		}

		/** starts the search beginning from the vertex <code>startVertex</code> 
		 * @throws RemoteException */
		public void run(Vertex startVertex) throws RemoteException {
			Graph graph = startVertex.getGraph();
			int handlingsOf77 = 0;
			init(startVertex.getGraph());
			handleRoot(startVertex);
			handleVertex(startVertex);
			buffer.add(startVertex);
			
			while (!buffer.isEmpty()) {
				Vertex currentVertex = buffer.get();    
				if (currentVertex.getId() == 77)
					handlingsOf77++;
				if (handlingsOf77 > 1) {
					System.out.println("Handling vertex " + currentVertex + " twice");	
					System.exit(0);
				}	
				//System.out.println("Handling vertex " + currentVertex);	
				Incidence curIncAtVertex = currentVertex.getFirstIncidence(Direction.BOTH);
				while (curIncAtVertex != null) {
					Edge currentEdge = curIncAtVertex.getEdge();
					if (parentEdgeInc[currentEdge.getId()]==0) { 
						handleEdge(currentEdge);
						parentEdgeInc[currentEdge.getId()] = curIncAtVertex.getId();     
						handleTreeIncidence(curIncAtVertex); 
						Direction opposite = curIncAtVertex.getDirection().getOppositeDirection();
						Incidence curIncAtEdge = currentEdge.getFirstIncidence(opposite);
						while (curIncAtEdge != null) {
						//	System.out.println("Handling incidence at edge " + curIncAtEdge);
							Vertex omega = curIncAtEdge.getVertex();
							if ((parentVertexInc[omega.getId()]==0) && (omega!=startVertex)) {
								parentVertexInc[omega.getId()]= curIncAtEdge.getId();
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
