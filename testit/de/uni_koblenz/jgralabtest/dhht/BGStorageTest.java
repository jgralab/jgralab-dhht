package de.uni_koblenz.jgralabtest.dhht;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.dhhttest.schema.Activity;
import de.uni_koblenz.jgralab.dhhttest.schema.DHHTTestGraph;
import de.uni_koblenz.jgralab.dhhttest.schema.DHHTTestSchema;
import de.uni_koblenz.jgralab.dhhttest.schema.SimpleEdge;
import de.uni_koblenz.jgralab.dhhttest.schema.SimpleEdge_start;
import de.uni_koblenz.jgralab.dhhttest.schema.SimpleEdge_target;
import de.uni_koblenz.jgralab.dhhttest.schema.SimpleVertex;

public class BGStorageTest {
	
	private static int vertexCount = 1000000;

	private static int linkCount = 1000000;
	
	private DHHTTestGraph  graph;
	
	private Vertex getVertex(int id) {
		int vId = id % vertexCount;
		return graph.getVertex(vId+1);
	}
	
	
	private Graph createGraph()  throws RemoteException {
		System.out.println("Creating graph...");
		graph = DHHTTestSchema.instance().createDHHTTestGraphOnDisk();
		
		long startTime = System.currentTimeMillis();
		for (int i=0; i<vertexCount;i++) {
			graph.createSimpleVertex();
		}
		for (int i=0; i<linkCount; i++) {
			SimpleEdge e = graph.createSimpleEdge();
			e.connect(SimpleEdge_start.class, getVertex(i));
			e.connect(SimpleEdge_start.class, getVertex(i*97));
			e.connect(SimpleEdge_target.class, getVertex(i+1));
			e.connect(SimpleEdge_target.class, getVertex(i*137));
			if (i%2==0)
				e.connect(SimpleEdge_target.class, getVertex(i*97));
			else
				e.connect(SimpleEdge_start.class, getVertex(i*137));
		}
		
		
		long time = System.currentTimeMillis() - startTime;
		System.out.println("Sucessfully created graph in " + time + " milliseconds");
		System.out.println("Graph has: " + graph.getVCount() + " vertices and " + graph.getECount() + " edges");

		return graph;
	}
	
	private Graph createTreeLikeGraph()  throws RemoteException {
		System.out.println("Creating graph...");
		graph = DHHTTestSchema.instance().createDHHTTestGraphOnDisk();
		
		long startTime = System.currentTimeMillis();
		for (int i=0; i<vertexCount;i++) {
		//	System.out.println("Creating vertex " + i);
			graph.createSimpleVertex();
		}
		for (int i=0; i<linkCount; i++) {
		//	System.out.println("Creating edge " + i);
			SimpleEdge e = graph.createSimpleEdge();
			e.connect(SimpleEdge_start.class, getVertex(i));
			e.connect(SimpleEdge_start.class, getVertex(i+3));
			e.connect(SimpleEdge_target.class, getVertex(i+1));
			e.connect(SimpleEdge_target.class, getVertex(i+2));
			if (i%2==0)
				e.connect(SimpleEdge_target.class, getVertex((i*97)%100));
			else
				e.connect(SimpleEdge_start.class, getVertex((i*137)%200));
		}
		
		
		long time = System.currentTimeMillis() - startTime;
		System.out.println("Sucessfully created graph in " + time + " milliseconds");
		System.out.println("Graph has: " + graph.getVCount() + " vertices and " + graph.getECount() + " edges");

		return graph;
	}
	
	
	public Graph createSimpleTestGraph() throws RemoteException {
		graph = DHHTTestSchema.instance().createDHHTTestGraphOnDisk();
		SimpleVertex v1 = graph.createSimpleVertex();
		SimpleVertex v2 = graph.createSimpleVertex();
		SimpleEdge e1 = graph.createSimpleEdge();
		Incidence i1 = v1.connect(SimpleEdge_start.class, e1);
		Incidence i2 = v1.connect(SimpleEdge_target.class, e1);
		int count=0;
		Incidence current = v1.getFirstIncidence();
		if (current != i1)
			throw new RuntimeException();
		current = current.getNextIncidenceAtVertex();
		if (current != i2)
			throw new RuntimeException();
		current = current.getNextIncidenceAtVertex();
		if (current != null)
			throw new RuntimeException();
		current = e1.getFirstIncidence();
		if (current != i1)
			throw new RuntimeException();
		current = current.getNextIncidenceAtEdge();
		if (current != i2)
			throw new RuntimeException();
		current = current.getNextIncidenceAtEdge();
		if (current != null)
			throw new RuntimeException();
		return graph;
	}
	
	
	
	public static void main(String[] args) {
		try {
			BGStorageTest test = new BGStorageTest();
			Graph graph = test.createTreeLikeGraph();
			
			System.out.println("Starting search");
			long startTime = System.currentTimeMillis();
			
			CountHypergraphSearchAlgorithm algo = new CountHypergraphSearchAlgorithm();
			algo.run(graph.getFirstVertex());
			
			System.out.println("Applied BFS in " + (System.currentTimeMillis() - startTime) + " milliseconds");
			System.out.println("Visited " + algo.getVertexCount() + " vertices and " + algo.getEdgeCount() + " edges");
			
			
			for (Vertex ac : graph.getVertices(Activity.class)) {
				System.out.println("Activity id: " + ac.getGlobalId());
			}
			System.exit(1);
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}
	
}
