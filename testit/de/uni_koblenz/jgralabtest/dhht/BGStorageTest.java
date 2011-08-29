package de.uni_koblenz.jgralabtest.dhht;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Edge;
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
import de.uni_koblenz.jgralab.impl.disk.DiskStorageManager;
import de.uni_koblenz.jgralab.impl.disk.GraphDatabaseElementaryMethods;

public class BGStorageTest {
	
	private static int vertexCount = 1000000;

	private static int linkCount = 2000000;
	
	private DHHTTestGraph  graph;
	
	private Vertex getVertex(int id) {
		int vId = id % vertexCount;
	//	System.out.println("Local vertex id: " + vId);
	//	System.out.println("Global vertex id: " + graph.getGraphDatabase().convertToGlobalId(vId + 1));
		return graph.getVertex(graph.getGraphDatabase().convertToGlobalId(vId+1));
	}
	
	private Edge getEdge(int id) {
		int vId = id % linkCount;
	//	System.out.println("Local vertex id: " + vId);
	//	System.out.println("Global vertex id: " + graph.getGraphDatabase().convertToGlobalId(vId + 1));
		return graph.getEdge(graph.getGraphDatabase().convertToGlobalId(vId+1));
	}
	
	
	private Graph createGraph()  throws RemoteException {
		System.out.println("Creating graph...");
		graph = DHHTTestSchema.instance().createDHHTTestGraphOnDisk();
		
		long startTime = System.currentTimeMillis();
		System.out.println("Creating vertices...");
		for (int i=0; i<vertexCount;i++) {
			Vertex v = graph.createSimpleVertex();
		}
		System.out.println("Creating edges");
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
		//	System.out.println("Edge from vertex " + getVertex(i).getLocalId() + " to vertex " + getVertex(i+1).getLocalId());
		}
		
		
		long time = System.currentTimeMillis() - startTime;
		System.out.println("Sucessfully created graph in " + time + " milliseconds");
		System.out.println("Graph has: " + graph.getVCount() + " vertices and " + graph.getECount() + " edges");

		return graph;
	}
	
	private Graph createTreeLikeGraph()  throws RemoteException {
		System.out.println("Creating tree-like graph...");
		graph = DHHTTestSchema.instance().createDHHTTestGraphOnDisk();
		
		long startTime = System.currentTimeMillis();
		for (int i=0; i<vertexCount;i++) {
		//	System.out.println("Creating vertex " + i);
			Vertex v = graph.createSimpleVertex();
		//	System.out.println("Vertexid " + v.getGlobalId());
		}
		for (int i=0; i<linkCount; i++) {
		//	System.out.println("Creating edge " + (i+1));
			SimpleEdge e = graph.createSimpleEdge();
	//		System.out.println("FirstInc of vertex " + (i+1) + " : " + getVertex(i).getFirstIncidence().getLocalId());
			e.connect(SimpleEdge_start.class, getVertex(i));
			
//			for (int j=0; j<i; j++) {
//				System.out.println("FirstInc of vertex " + j + " : " + getVertex(j).getFirstIncidence().getLocalId());
//				System.out.println("FirstInc of edge " + j + " : " + getEdge(j).getFirstIncidence().getLocalId());
//			}
		//	e.connect(SimpleEdge_start.class, getVertex(i+3));
			//if (i == 3)
			//System.out.println("FirstInc of vertex " + (i+1) + " : " + getVertex(i).getFirstIncidence().getLocalId());
			e.connect(SimpleEdge_target.class, getVertex(i+1));
//			if (i==3)
//			for (int j=0; j<=i; j++) {
//				System.out.println("Vertex: " + getVertex(j));
//				System.out.println("FirstInc: " + getVertex(j).getFirstIncidence());
//				System.out.println("FirstInc of vertex " + (j+1) + " : " + getVertex(j).getFirstIncidence().getLocalId());
//				System.out.println("FirstInc of edge " + (j+1) + " : " + getEdge(j).getFirstIncidence().getLocalId());
//			}
//			e.connect(SimpleEdge_target.class, getVertex(i+2));
//			if (i%2==0)
//				e.connect(SimpleEdge_target.class, getVertex((i*97)%100));
//			else
//				e.connect(SimpleEdge_start.class, getVertex((i*137)%200));
		//	System.out.println("Edge from vertex " + getVertex(i).getLocalId() + " to vertex " + getVertex(i+1).getLocalId());
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
	
	
	int localPartialGraphId = 1;
	public final long convertToGlobalId(int localSubgraphOrElementId) {
		long l = ((long)localPartialGraphId) << 32;
		return l + localSubgraphOrElementId;
	}
	
	public void shiftRightTest() {
		for (int i=0; i<Integer.MAX_VALUE; i++) {
			long globalId = convertToGlobalId(i);
			int pgId = GraphDatabaseElementaryMethods.getPartialGraphId(globalId);
			if (pgId != localPartialGraphId) {
				throw new RuntimeException("Conversion doesn't work for value " + i + " partialGraphId " + pgId);
			}
			int localId = GraphDatabaseElementaryMethods.convertToLocalId(globalId);
			if (localId != i) {
				throw new RuntimeException("Conversion doesn't work for value " + i + " convertedLocalId " + localId); 
			}
		}
		System.out.println("Converted all values");
	}
	
	
	public void elementMaskTest() {
		System.out.println("Container size: " + DiskStorageManager.CONTAINER_SIZE);
		System.out.println("Container count: " + DiskStorageManager.ELEMENT_CONTAINER_COUNT);
		for (int i=0; i<40; i++) {
			System.out.println("Number: " + i + " ContainerId: " + DiskStorageManager.getContainerId(i) + " Id in Container: " + DiskStorageManager.getElementIdInContainer(i));
		}
	}
	
	
	public static void main(String[] args) {
		try {
			BGStorageTest test = new BGStorageTest();
			//test.elementMaskTest();
		//	System.exit(0);
			Graph graph = test.createTreeLikeGraph();
			
			System.out.println("Starting search");
			long startTime = System.currentTimeMillis();
			
			CountHypergraphSearchAlgorithm algo = new CountHypergraphSearchAlgorithm();
			System.out.println("First vertex is: " + graph.getFirstVertex().getLocalId());
			
			algo.run(graph.getFirstVertex());
			
			System.out.println("Applied BFS in " + (System.currentTimeMillis() - startTime) + " milliseconds");
			System.out.println("Visited " + algo.getVertexCount() + " vertices and " + algo.getEdgeCount() + " edges");
			
			
			for (Vertex ac : graph.getVertices(Activity.class)) {
				System.out.println("Activity id: " + ac.getGlobalId());
			}
			
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
		System.exit(0);
	}
	
}
