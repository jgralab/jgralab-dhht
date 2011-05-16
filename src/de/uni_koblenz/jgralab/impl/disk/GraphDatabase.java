package de.uni_koblenz.jgralab.impl.disk;

import java.io.FileNotFoundException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLabServer;
import de.uni_koblenz.jgralab.PartialGraph;
import de.uni_koblenz.jgralab.Record;
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
	
	protected JGraLabServer server;
	
	protected GraphFactory graphFactory;
	
	protected boolean loading = false;

	protected long graphVersion = 0;
	
	/**
	 * Holds the version of the vertex sequence. For every modification (e.g.
	 * adding/deleting a vertex or changing the vertex sequence) this version
	 * number is increased by 1. It is set to 0 when the graph is loaded.
	 */
	protected long vertexListVersion;
	
	/**
	 * Holds the version of the edge sequence. For every modification (e.g.
	 * adding/deleting an edge or changing the edge sequence) this version
	 * number is increased by 1. It is set to 0 when the graph is loaded.
	 */
	protected long edgeListVersion;

	/**
	 * maximum number of vertices
	 */
	protected int vMax;

	/**
	 * current number of vertices
	 */
	private int vCount;
	
	
	
	/**
	 * maximum number of edges
	 */
	protected int eMax;
	
	/**
	 * current number of edges
	 */
	protected int eCount;
	
	/**
	 * free index list for edges
	 */
	protected FreeIndexList freeEdgeList;
	
	
	

	private DiskStorageManager diskStorage;
	
	/*
	 * The list of local (proxy) objects for the remote graphs
	 */
	private Graph[] partialGraphs;
	
	/* stores the graph databases for the partial graphs, these elements
	 * may be local proxies for the remote elements automatically created by RMI
	 */
	protected GraphDatabase[] partialGraphDatabases;
	
//	private Map<Integer, ? extends Reference<GraphProxy>> remoteGraphs;
	
	protected Map<Integer, Reference<Vertex>> remoteVertices;
	
	protected Map<Integer, Reference<Edge>> remoteEdges;
	
	private Map<Integer, Reference<Incidence>> remoteIncidences;
	
	
	protected GraphDatabase(CompleteOrPartialGraphImpl localGraph, Schema schema) {
		// initialize graph, factory and schema
		this.localGraph = localGraph;
		localGraphId = localGraph.getPartialGraphId();
		graphFactory = schema.getGraphFactory();
		
		try {
			diskStorage = new DiskStorageManager(this);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}


		partialGraphs = new Graph[MAX_NUMBER_OF_PARTIAL_GRAPHS];
		partialGraphDatabases = new GraphDatabase[MAX_NUMBER_OF_PARTIAL_GRAPHS];
		
		//remoteGraphs = new HashMap<Integer, WeakReference<Graph>>();
		remoteVertices  = new HashMap<Integer, Reference<Vertex>>();
		remoteEdges = new HashMap<Integer, Reference<Edge>>();
		remoteIncidences = new HashMap<Integer, Reference<Incidence>>();
		
		//initialize fields
		graphVersion = -1;
		vCount =0;
		
		
		//register graph at server
		server = JGraLabServerImpl.getLocalInstance();
		server.registerGraph(localGraph.getCompleteGraphUid(), localGraph.getPartialGraphId(), this);
	}
	
	protected GraphDatabase getGraphDatabase(int partialGraphId) {
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
				partialGraphs[partialGraphId] = new PartialGraphImpl(partialGraphId, this);				
			} else {
				partialGraphs[partialGraphId] = new PartialGraphImpl(partialGraphId, this);	
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
		Reference<Vertex> ref = remoteVertices.get(id);
		Vertex proxy = null;
		if ((ref == null) || (ref.get() == null)) {
			//create new vertex proxy
			GraphDatabase remoteDatabase = getGraphDatabase(partialGraphId);
			int vertexClassId = remoteDatabase.getVertexTypeId(id);
			Class<? extends Vertex> vc = (Class<? extends Vertex>) schema.getM1ClassForId(vertexClassId);
			proxy = (Vertex) graphFactory.createVertexProxy(vc, id, getGraphObject(partialGraphId));
			ref = new WeakReference<Vertex>(proxy);
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
		Reference<Edge> ref = remoteEdges.get(id);
		Edge proxy = null;
		if ((ref == null) || (ref.get() == null)) {
			//create new vertex proxy
			GraphDatabase remoteDatabase = getGraphDatabase(partialGraphId);
			int edgeClassId = remoteDatabase.getEdgeTypeId(id);
			Class<? extends Edge> ec = (Class<? extends Edge>) schema.getM1ClassForId(edgeClassId);
			proxy = (Edge) graphFactory.createEdgeProxy(ec, id, getGraphObject(partialGraphId));
			ref = new WeakReference<Edge>(proxy);
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
		Reference<Incidence> ref = remoteIncidences.get(id);
		Incidence proxy = null;
		if ((ref == null) || (ref.get() == null)) {
			//create new vertex proxy
			GraphDatabase remoteDatabase = getGraphDatabase(partialGraphId);
			int incidenceClassId = remoteDatabase.getIncidenceTypeId(id);
			Class<? extends Incidence> ec = (Class<? extends Incidence>) schema.getM1ClassForId(incidenceClassId);
			proxy = (Incidence) graphFactory.createIncidenceProxy(ec, id, getGraphObject(partialGraphId));
			ref = new WeakReference<Incidence>(proxy);
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
			return diskStorage.getSigma(elemId);
		else
			return getGraphDatabase(partialGraphId).getSigma(elemId);
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

	/**
	 * Sets the sigma value of the element identified by <code>elementId</code>
	 * to the value <code>sigmaId</code>. Both values are signed, negative values
	 * identify edges while positve ones identify vertices as in all other methods
	 * of this class 
	 */
	public void setSigma(int elementId, int sigmaId) {
		int partialGraphId = getPartialGraphId(elementId);
		if (partialGraphId == localGraphId)
			diskStorage.setSigma(elementId, sigmaId);
		else
			getGraphDatabase(partialGraphId).setSigma(elementId, sigmaId);
	}

	
	
	public int getKappa(int elementId) {
		int partialGraphId = getPartialGraphId(elementId);
		if (partialGraphId == localGraphId)
			return diskStorage.getKappa(elementId);
		else
			return getGraphDatabase(partialGraphId).getKappa(elementId);
	}
	
	
	public void setKappa(int elementId, int kappa) {
		int partialGraphId = getPartialGraphId(elementId);
		if (partialGraphId == localGraphId)
			diskStorage.setKappa(elementId, kappa);
		else
			getGraphDatabase(partialGraphId).setKappa(elementId, kappa);
	}
	
	

	public void setIncidenceListVersion(int elementId, long incidenceListVersion) {
		int partialGraphId = getPartialGraphId(elementId);
		if (partialGraphId == localGraphId)
			diskStorage.setIncidenceListVersion(elementId, incidenceListVersion);
		else
			getGraphDatabase(partialGraphId).getKappa(elementId);
	}
	

	public long getIncidenceListVersion(int elementId) {
		int partialGraphId = getPartialGraphId(elementId);
		if (partialGraphId == localGraphId)
			return diskStorage.getIncidenceListVersion(elementId);
		else
			return getGraphDatabase(partialGraphId).getIncidenceListVersion(elementId);
	}
	
	public abstract void edgeListModified();
	
	public abstract void vertexListModified();
	
	public abstract void graphModified();
	

	

	/* **************************************************************************
	 * Methods to access traversal context
	 * **************************************************************************/
	

	
	public abstract Graph getTraversalContext();
	
	public abstract void releaseTraversalContext();
	
	public abstract void setTraversalContext(Graph traversalContext);

	public GraphFactory getGraphFactory() {
		return graphFactory;
	}

	public long getGraphVersion() {
		return graphVersion;
	}

	public void setGraphVersion(long graphVersion2) {
		this.graphVersion = graphVersion2;		
	}

	/**
	 * Adds an edge to this graph. If the edges id is 0, a valid id is set,
	 * otherwise the edges current id is used if possible. Should only be used
	 * by m1-Graphs derived from Graph. To create a new Edge as user, use the
	 * appropriate methods from the derived Graphs like
	 * <code>createStreet(...)</code>
	 * 
	 * @param newEdge
	 *            Edge to add
	 * @throws RemoteException 
	 * @throws GraphException
	 *             an edge with same id already exists in graph, id of edge
	 *             greater than possible count of edges in graph
	 */
	protected void addEdge(Edge newEdge) {
		assert newEdge != null;
		assert (newEdge.getSchema() == getSchema()) : "The schemas of newEdge and this graph don't match!";
		assert (newEdge.getGraph() == this) : "The graph of  newEdge and this graph don't match!";

		EdgeImpl e = (EdgeImpl) newEdge;

		int eId = e.getId();
		if (isLoading()) {
			if (eId > 0) {
				// the given edge already has an id, try to use it
				if (containsEdgeId(eId)) {
					throw new GraphException("edge with id " + e.getId()
							+ " already exists");
				}
			} else {
				throw new GraphException("can not load an edge with id <= 0");
			}
		} else {
			if (!canAddGraphElement(eId)) {
				throw new GraphException("can not add an edge with id " + eId);
			}
			eId = allocateEdgeIndex(eId);
			assert eId != 0;
			e.setId(eId);
		}
		diskStorage.storeEdge(e);
		appendEdgeToESeq(e);

		if (!isLoading()) {
			edgeListModified();
			internalEdgeAdded(e);
		}
	}
	
	/*
	 * Adds a incidence to this graph. If the incidence's id is 0, a valid id is
	 * set, otherwise the incidence's current id is used if possible. Should
	 * only be used by m1-Graphs derived from Graph. To create a new Incidence
	 * as user, use the appropriate <code>connect(...)</code>-methods from the
	 * GraphElements
	 * 
	 * @param newIncidence the Incidence to add
	 * 
	 * @throws GraphException if a incidence with the same id already exists
	 */
	protected void addIncidence(Incidence newIncidence) {
		IncidenceImpl i = (IncidenceImpl) newIncidence;
		int iId = i.getId();
		if (isLoading()) {
			if (iId > 0) {
				// the given vertex already has an id, try to use it
				if (containsIncidenceId(iId)) {
					throw new GraphException("incidence with id " + iId
							+ " already exists");
				}
				if (iId > iMax) {
					throw new GraphException("vertex id " + iId
							+ " is bigger than vSize");
				}
			} else {
				throw new GraphException(
						"can not load an incidence with id <= 0");
			}
		} else {
			if (!canAddGraphElement(iId)) {
				throw new GraphException("can not add an incidence with iId "
						+ iId);
			}
			iId = allocateIncidenceIndex(iId);
			assert iId != 0;
			i.setId(iId);
			
		}
		diskStorage.storeIncidence(i);
		if (!isLoading()) {
			internalIncidenceAdded(i);
		}
	}

	/**
	 * Adds a vertex to this graph. If the vertex' id is 0, a valid id is set,
	 * otherwise the vertex' current id is used if possible. Should only be used
	 * by m1-Graphs derived from Graph. To create a new Vertex as user, use the
	 * appropriate methods from the derived Graphs like
	 * <code>createStreet(...)</code>
	 * 
	 * @param newVertex
	 *            the Vertex to add
	 * 
	 * @throws GraphException
	 *             if a vertex with the same id already exists
	 */
	protected void addVertex(Vertex newVertex) {
		VertexImpl v = (VertexImpl) newVertex;

		int vId = v.getId();
		if (isLoading()) {
			if (vId > 0) {
				// the given vertex already has an id, try to use it
				if (containsVertexId(vId)) {
					throw new GraphException("vertex with id " + vId
							+ " already exists");
				}
				if (vId > vMax) {
					throw new GraphException("vertex id " + vId
							+ " is bigger than vSize");
				}
			} else {
				throw new GraphException("can not load a vertex with id <= 0");
			}
		} else {
			if (!canAddGraphElement(vId)) {
				throw new GraphException("can not add a vertex with vId " + vId);
			}
			vId = allocateVertexIndex(vId);
			assert vId != 0;
			v.setId(vId);
		}
		diskStorage.storeVertex(v);
		appendVertexToVSeq(v);

		if (!isLoading()) {
			vertexListModified();
			internalVertexAdded(v);
		}
	}
	

	private void internalEdgeAdded(EdgeImpl e) {
		// TODO Auto-generated method stub
		
	}

	private void appendEdgeToESeq(EdgeImpl e) {
		// TODO Auto-generated method stub
		
	}

	private int allocateEdgeIndex(int eId) {
		// TODO Auto-generated method stub
		return 0;
	}

	private boolean canAddGraphElement(int eId) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean containsEdgeId(int eId) {
		// TODO Auto-generated method stub
		return false;
	}

	boolean isLoading() {
		return loading;
	}

	public void setLoading(boolean isLoading) {
		this.loading = isLoading;
	}

	public void setVCount(int count) {
		vCount = count;
	}

	public void removeEdgeFromDatabase(EdgeImpl e) {
		int partialGraphId = getPartialGraphId(e.getId());

	}

	
	public void removeVertexFromDatabase(VertexImpl v) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io) {
		T record = graphFactory.createRecord(recordClass, this);
		try {
			record.readComponentValues(io);
		} catch (GraphIOException e) {
			e.printStackTrace();
		}
		return record;
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,Map<String, Object> fields) {
		T record = graphFactory.createRecord(recordClass, this);
		record.setComponentValues(fields);
		return record;
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, Object... components) {
		T record = graphFactory.createRecord(recordClass, this);
		record.setComponentValues(components);
		return record;
	}
	

}
