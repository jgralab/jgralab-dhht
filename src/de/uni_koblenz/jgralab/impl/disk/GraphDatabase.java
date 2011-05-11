package de.uni_koblenz.jgralab.impl.disk;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.Map;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLabServer;
import de.uni_koblenz.jgralab.PartialGraph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.JGraLabServerImpl;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;



/**
 * A GraphDatabase stores one local complete or partial graph and provides an uniform access to 
 * all partial graphs of the distributed graph the store belongs to and all its elements based
 * on the ids
 */

public abstract class GraphDatabase implements Remote {
	
	private Schema schema;
	
	protected CompleteOrPartialGraphImpl localGraph;
	
	protected final int localGraphId;
	
	protected DiskStorageManager diskStorage;
	
	protected JGraLabServer server;
	
	protected GraphFactory factory;
	
	protected long edgeListVersion = 0;
	
	protected long vertexListVersion = 0;
	
	protected long graphVersion = 0;
	

	/*
	 * The list of local (proxy) objects for the remote graphs
	 */
	private Graph[] partialGraphs;
	
	/* stores the graph databases for the partial graphs, these elements
	 * may be local proxies for the remote elements automatically created by RMI
	 */
	protected GraphDatabase[] partialGraphDatabases;
	
//	private Map<Integer, ? extends Reference<GraphProxy>> remoteGraphs;
	
	protected Map<Integer, Reference<VertexProxy>> remoteVertices;
	
	protected Map<Integer, Reference<EdgeProxy>> remoteEdges;
	
	private Map<Integer, Reference<IncidenceProxy>> remoteIncidences;
	
	
	protected GraphDatabase(CompleteOrPartialGraphImpl localGraph) {
		this.localGraph = localGraph;
		localGraphId = localGraph.getPartialGraphId();
		diskStorage = localGraph.getDiskStorage();
		factory = localGraph.graphFactory;
		partialGraphs = new Graph[GraphStorage.MAX_NUMBER_OF_PARTIAL_GRAPHS];
		partialGraphDatabases = new GraphDatabase[GraphStorage.MAX_NUMBER_OF_PARTIAL_GRAPHS];
		
		//remoteGraphs = new HashMap<Integer, WeakReference<Graph>>();
		remoteVertices  = new HashMap<Integer, Reference<VertexProxy>>();
		remoteEdges = new HashMap<Integer, Reference<EdgeProxy>>();
		remoteIncidences = new HashMap<Integer, Reference<IncidenceProxy>>();
		
		//register graph at server
		server = JGraLabServerImpl.getLocalInstance();
		server.registerGraph(localGraph.getCompleteGraphUid(), localGraph.getPartialGraphId(), this);
	}
	
	private GraphDatabase getGraphDatabase(int partialGraphId) {
		if (partialGraphDatabases[partialGraphId] == null) {
			//initialize new remote graph database
			partialGraphDatabases[partialGraphId] = server.getRemoteInstance(getHostname(partialGraphId)).getGraph(localGraph.getCompleteGraphUid(), partialGraphId);
		}
		return partialGraphDatabases[partialGraphId];
	}
	
	protected abstract String getHostname(int id);
	

	protected abstract int getFreePartialGraphId();
	
	protected abstract void releasePartialGraphId(int partialGraphId);
	
	/**
	 * Registers the partial graph with the given id <code>id</code> which is stored on the
	 * host with the name <code>hostname</code>
	 * @param id
	 * @param hostname
	 */
	public abstract void registerPartialGraph(int id, String hostname);
	
	public abstract Graph createPartialGraph(GraphClass gc, String hostname);
	
	public abstract void deletePartialGraph(int partialGraphId);
	
	
	
	
	//Methods to access Graph, vertex, edge and incidence objects
	
	/**
	 * Retrieves a Graph object (local or proxy) that represents the (partial) graph identified by the given id
	 * @param partialGraphId
	 * @return
	 */
	public Graph getGraphObject(int partialGraphId) {
		if (partialGraphId == localGraphId)
			return localGraph;
		if (partialGraphs[partialGraphId] == null) {
			if (partialGraphId == 0) {
				//Proxy for complete graph,
				//TODO change to factory call
				partialGraphs[partialGraphId] = new PartialGraphProxy(partialGraphId, this);				
			} else {
				partialGraphs[partialGraphId] = new PartialGraphProxy(partialGraphId, this);	
			}
		}
		return partialGraphs[partialGraphId];
	}

	/**
	 * @return an object realizing the vertex with the given id. The object
	 *         may be either a local one or a proxy for a remote one
	 */
	public Vertex getVertexObject(int id) {
		int partialGraphId = GraphStorage.getPartialGraphId(id);
		if (partialGraphId == localGraphId) {
			return diskStorage.getVertexObject(id);
		}
		Reference<VertexProxy> ref = remoteVertices.get(id);
		VertexProxy proxy = null;
		if ((ref == null) || (ref.get() == null)) {
			//create new vertex proxy
			GraphDatabase remoteDatabase = getGraphDatabase(partialGraphId);
			int vertexClassId = remoteDatabase.getVertexTypeId(id);
			Class<? extends Vertex> vc = (Class<? extends Vertex>) schema.getM1ClassForId(vertexClassId);
			proxy = (VertexProxy) factory.createVertexProxy(vc, id, getGraphObject(partialGraphId));
			ref = new WeakReference<VertexProxy>(proxy);
			remoteVertices.put(id, ref);
		} else {
			proxy = ref.get();
		}
		return proxy;
	}
	

	private int getVertexTypeId(int id) {
		return getVertexObject(id).getType().getId();
	}

	
	/**
	 * @return an object realizing the edge with the given id. The object
	 *         may be either a local one or a proxy for a remote one
	 */
	public Edge getEdgeObject(int id) {
		int partialGraphId = GraphStorage.getPartialGraphId(id);
		if (partialGraphId == localGraphId) {
			return diskStorage.getEdgeObject(id);
		}
		Reference<EdgeProxy> ref = remoteEdges.get(id);
		EdgeProxy proxy = null;
		if ((ref == null) || (ref.get() == null)) {
			//create new vertex proxy
			GraphDatabase remoteDatabase = getGraphDatabase(partialGraphId);
			int edgeClassId = remoteDatabase.getEdgeTypeId(id);
			Class<? extends Edge> ec = (Class<? extends Edge>) schema.getM1ClassForId(edgeClassId);
			proxy = (EdgeProxy) factory.createEdgeProxy(ec, id, getGraphObject(partialGraphId));
			ref = new WeakReference<EdgeProxy>(proxy);
			remoteEdges.put(id, ref);
		} else {
			proxy = ref.get();
		}
		return proxy;
	}

	private int getEdgeTypeId(int id) {
		return getEdgeObject(id).getType().getId();
	}	

	
	
	/**
	 * @return an object realizing the incidence with the given id. The object
	 *         may be either a local one or a proxy for a remote one
	 */
	public Incidence getIncidenceObject(int id) {
		int partialGraphId = GraphStorage.getPartialGraphId(id);
		if (partialGraphId == localGraphId) {
			return diskStorage.getIncidenceObject(id);
		}
		Reference<IncidenceProxy> ref = remoteIncidences.get(id);
		IncidenceProxy proxy = null;
		if ((ref == null) || (ref.get() == null)) {
			//create new vertex proxy
			GraphDatabase remoteDatabase = getGraphDatabase(partialGraphId);
			int incidenceClassId = remoteDatabase.getIncidenceTypeId(id);
			Class<? extends Incidence> ec = (Class<? extends Incidence>) schema.getM1ClassForId(incidenceClassId);
			proxy = (IncidenceProxy) factory.createIncidenceProxy(ec, id, getGraphObject(partialGraphId));
			ref = new WeakReference<IncidenceProxy>(proxy);
			remoteIncidences.put(id, ref);
		} else {
			proxy = ref.get();
		}
		return proxy;
	}

	private int getIncidenceTypeId(int id) {
		return getIncidenceObject(id).getType().getId();
	}	
	
	
	
	
	// Methods to access vsed
	int getFirstVertex(int partialGraphId) {
		return 0;
	}
	
	int getLastVertex(int partialGraphId) {
		return 0;
	}

	public int getLocalGraphId() {
		return localGraphId;
	}
	
	public abstract void edgeListModified();
	
	public abstract void vertexListModified();
	
	public abstract void graphModified();

}
