package de.uni_koblenz.jgralab.impl.disk;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.Map;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
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

public abstract class GraphDatabase implements Remote, GraphPropertyAccess {
	
	//Static parts 
	/* Switches that toggle number of elements in a local partial graph
	 * and number of partial graphs
	 */
	private final static int BITS_FOR_PARTIAL_GRAPH_MASK = 5;
	
	/* Values that are calculated on the basis of BITS_FOR_PARTIAL_GRAPH_MASK */
	
	public final static int MAX_NUMBER_OF_LOCAL_ELEMENTS = Integer.MAX_VALUE >> BITS_FOR_PARTIAL_GRAPH_MASK;
	
	public final static int MAX_NUMBER_OF_LOCAL_INCIDENCES = Integer.MAX_VALUE >> BITS_FOR_PARTIAL_GRAPH_MASK;
	
	public static final int MAX_NUMBER_OF_PARTIAL_GRAPHS = Integer.MAX_VALUE >> (32-BITS_FOR_PARTIAL_GRAPH_MASK);
	

	public static final int getPartialGraphId(int elementId) {
		return elementId >> (32-BITS_FOR_PARTIAL_GRAPH_MASK);
	}
	
	public static final int getElementIdInPartialGraph(int elementId) {
		return elementId & (MAX_NUMBER_OF_LOCAL_ELEMENTS);
	}	
	
	private Schema schema;
	
	protected CompleteOrPartialGraphImpl localGraph;
	
	protected final int localGraphId;
	
	/**
	 * The disk storage to store all local elements
	 */
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
		partialGraphs = new Graph[MAX_NUMBER_OF_PARTIAL_GRAPHS];
		partialGraphDatabases = new GraphDatabase[MAX_NUMBER_OF_PARTIAL_GRAPHS];
		
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
	
	public abstract Graph createPartialGraph(String hostname);
	
	/**
	 * Loads the partial graph 
	 * @param hostname
	 * @param id
	 * @return
	 */
	public abstract Graph loadRemotePartialGraph(String hostname, int id);
	
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
		int partialGraphId = getPartialGraphId(id);
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
		int partialGraphId = getPartialGraphId(id);
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
		int partialGraphId = getPartialGraphId(id);
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
	
	public abstract void graphModified(int i);

	public Schema getSchema() {
		return schema;
	}

	public Graph getCompleteGraphObject() {
		return getGraphObject(0);
	}
	
	
	
	/**
	 * Sets the first {@link Incidence} of this {@link GraphElement} to
	 * <code>firstIncidence</code>.
	 * 
	 * @param firstIncidence
	 *            {@link IncidenceImpl}
	 */
	public void setFirstIncidence(int elemId, int incidenceId) {
		int partialGraphId = getPartialGraphId(elemId);
		if (partialGraphId == localGraphId)
			diskStorage.setFirstIncidence(elemId, incidenceId);
		else
			getGraphDatabase(partialGraphId).setFirstIncidence(elemId, incidenceId);
	}
	
	public void setLastIncidence(int elemId, int incidenceId) {
		int partialGraphId = getPartialGraphId(elemId);
		if (partialGraphId == localGraphId)
			diskStorage.setLastIncidence(elemId, incidenceId);
		else
			getGraphDatabase(partialGraphId).setLastIncidence(elemId, incidenceId);
	}


	public void incidenceListModified(int elemId) {
		int partialGraphId = getPartialGraphId(elemId);
		if (partialGraphId == localGraphId)
			diskStorage.incidenceListModified(elemId);
		else
			getGraphDatabase(partialGraphId).incidenceListModified(elemId);
	}



	public int getSigma(int elemId) {
		int partialGraphId = getPartialGraphId(elemId);
		if (partialGraphId == localGraphId)
			diskStorage.incidenceListModified(elemId);
		else
			getGraphDatabase(partialGraphId).incidenceListModified(elemId);
		
		
		int id = container.sigmaId[DiskStorageManager.getElementIdInContainer(this.id)];
		if (id < 0) {
			return container.backgroundStorage.getEdgeObject(-id);
		} else {
			return container.backgroundStorage.getVertexObject(id);
		}
	}

	/**
	 * Returns an object (Vertex or Edge) representing the GraphElement
	 * identified by the given global id
	 * @param elemId
	 * @return
	 */
	public GraphElement<?, ?, ?> getGraphElementObject(int elemId) {
		if (elemId < 0)
			return getEdgeObject(-elemId);
		else
			return getVertexObject(elemId);
	}

	public void setSigma(int elementId, int elementId2) {
		// TODO Auto-generated method stub
		GraphElement s = (GraphElement) newSigma;
		if (s instanceof Edge) {
			container.sigmaId[DiskStorageManager.getElementIdInContainer(this.id)] = - newSigma.getId();
		} else {
			container.sigmaId[DiskStorageManager.getElementIdInContainer(this.id)] = newSigma.getId();
		}
	}

	
	
	public int getKappa(int elementId) {
		int partialGraphId = getPartialGraphId(elementId);
		if (partialGraphId == localGraphId)
			return diskStorage.getKappa(elementId);
		else
			getGraphDatabase(partialGraphId).getKappa(elementId);
	}
	
	
	public Graph getTraversalContext() {
		// TODO Auto-generated method stub
		return null;
	}


	public void setIncidenceListVersion(int elementId, long incidenceListVersion) {
		// TODO Auto-generated method stub
		
	}

	public long getIncidenceListVersion(int elementId) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	



}
