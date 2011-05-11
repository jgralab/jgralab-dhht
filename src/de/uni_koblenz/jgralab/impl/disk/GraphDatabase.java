package de.uni_koblenz.jgralab.impl.disk;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.Schema;



/**
 * A GraphDatabase stores one local complete or partial graph and provides an uniform access to 
 * all partial graphs of the distributed graph the store belongs to and all its elements based
 * on the ids
 */

public class GraphDatabase implements Remote {
	
	private Schema schema;
	
	private CompleteOrPartialGraphImpl localGraph;
	
	private final int localGraphId;
	
	
	/*
	 * Stores the hostnames of the partial graphs
	 */
	private String[] hostnames;
	
	/*
	 * The list of local (proxy) objects for the remote graphs
	 */
	private Graph[] partialGraphs;
	
	/* stores the graph databases for the partial graphs, these elements
	 * may be local proxies for the remote elements automatically created by RMI
	 */
	private GraphDatabase[] partialGraphDatabases;
	
//	private Map<Integer, ? extends Reference<GraphProxy>> remoteGraphs;
	
	private Map<Integer, Reference<VertexProxy>> remoteVertices;
	
	private Map<Integer, Reference<EdgeProxy>> remoteEdges;
	
	private Map<Integer, Reference<IncidenceProxy>> remoteIncidences;
	
	
	protected GraphDatabase(CompleteOrPartialGraphImpl localGraph) throws RemoteException {
		this.localGraph = localGraph;
		localGraphId = localGraph.getId();
		hostnames = new String[GraphStorage.MAX_NUMBER_OF_PARTIAL_GRAPHS];
		partialGraphs = new Graph[GraphStorage.MAX_NUMBER_OF_PARTIAL_GRAPHS];
		partialGraphDatabases = new GraphDatabase[GraphStorage.MAX_NUMBER_OF_PARTIAL_GRAPHS];
		
		//remoteGraphs = new HashMap<Integer, WeakReference<Graph>>();
		remoteVertices  = new HashMap<Integer, Reference<VertexProxy>>();
		remoteEdges = new HashMap<Integer, Reference<EdgeProxy>>();
		remoteIncidences = new HashMap<Integer, Reference<IncidenceProxy>>();
	}
	
	/*
	 * Methods to access Vseq
	 */
	
	private GraphDatabase getGraphDatabase(int partialGraphId) {
		if (partialGraphDatabases[partialGraphId] == null) {
			//initialize new remote graph database
		}
		return partialGraphDatabases[partialGraphId];
	}
	
	public Graph getPartialGraph(int partialGraphId) {
		if (partialGraphs[partialGraphId] == null) {
			partialGraphs[partialGraphId] = new PartialGraphProxy(int partialGraphId, this);
		}
	}
	
	/**
	 * Registers the partial graph with the given id <code>id</code> which is stored on the
	 * host with the name <code>hostname</code>
	 * @param id
	 * @param hostname
	 */
	public void registerPartialGraph(int id, String hostname) {
		if (hostnames[id] == null) {
			hostnames[id] = hostname;
		} else {
			throw new RuntimeException("There is already a graph with the id " + id + " registered");
		}
	}

	

	/**
	 * @return an object realizing the vertex with the given id. The object
	 *         may be either a local one or a proxy for a remote one
	 */
	public Vertex getVertexObject(int id) {
		int partialGraphId = GraphStorage.getPartialGraphId(id);
		if (partialGraphId == localGraphId) {
			return localGraph.getVertex(id);
		}
		Reference<VertexProxy> ref = remoteVertices.get(id);
		VertexProxy proxy = null;
		if ((ref == null) || (ref.get() == null)) {
			//create new vertex proxy
			GraphDatabase remoteDatabase = getGraphDatabase(partialGraphId);
			int vertexClassId = 
			VertexClass vc = schema.getM1ClassForId(vertexClassId);
			
			
			ref = new WeakReference<VertexProxy>(proxy);
			remoteVertices.put(id, ref);
		} else {
			proxy = ref.get();
		}
		return proxy;
	}
	
	
	

	int getFirstVertex(int partialGraphId) {
		
	}
	
	int getLastVertex(int partialGraphId) {
		
	}
	
	
	
	public Edge getEdgeObject(int id) {
	}
	
	

}
