package de.uni_koblenz.jgralab.impl.disk;

import java.io.FileNotFoundException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLabServer;
import de.uni_koblenz.jgralab.JGraLabSet;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.JGraLabServerImpl;
import de.uni_koblenz.jgralab.impl.JGraLabSetImpl;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;
import de.uni_koblenz.jgralab.schema.Schema;



/**
 * A GraphDatabase stores one local complete or partial graph and provides an uniform access to 
 * all partial graphs of the distributed graph the store belongs to and all its elements based
 * on the ids
 * 
 * TODO: Fields such as firstVertexId in (Subordinate)Graph may be represented directly in the
 * respective objects, since the objects are created and removed on demand and stored only locally,
 * but the distributed access and update must be considered
 * -> If the same graph classes should be used to represent the original elements and its proxies,
 *    all fields need to be saved only inside graph database
 */

public abstract class GraphDatabase implements RemoteGraphDatabaseAccess {
	
	//Static parts 
	/* Switches that toggle number of elements in a local partial graph
	 * and number of partial graphs
	 */
	private final static int BITS_FOR_PARTIAL_GRAPH_MASK = 12;
	
	/* Values that are calculated on the basis of BITS_FOR_PARTIAL_GRAPH_MASK */
	
	public final static int MAX_NUMBER_OF_LOCAL_GRAPHS = Integer.MAX_VALUE >> BITS_FOR_PARTIAL_GRAPH_MASK;

	public static final int getPartialGraphId(long globalElementId) {
		return (int) globalElementId >> (64-BITS_FOR_PARTIAL_GRAPH_MASK);
	}
	
	public static final int getPartialGraphId(int globalSubgraphId) {
		return (int) globalSubgraphId >> (32-BITS_FOR_PARTIAL_GRAPH_MASK);
	}
	
	public static final int getSubgraphIdInPartialGraph(int globalSubgraphId) {
		return globalSubgraphId & (MAX_NUMBER_OF_LOCAL_GRAPHS);
	}	
	
	public static final int getLocalElementId(long elementId) {
		return (int) elementId;
	}
	
	/**
	 * The graph schema of the graph whose local subgraphs are managed by this GraphDatabase belongs to
	 */
	private final Schema schema;
	
	/**
	 * The unique id of the graph whose subgraphs are stored in this database 
	 */
	protected final String uniqueGraphId; 
	
	/**
	 * The id of the local toplevel graph
	 */
	protected final int localPartialGraphId;
	
	/**
	 * The id of the distributed graph the local partial graph directly belongs to
	 */
	protected final int parentDistributedGraphId;
	
	/**
	 * The local JGraLab server instance
	 */
	protected final JGraLabServer localJGraLabServer;

	/**
	 * The GraphFactory to create graphs and graph elements
	 */
	protected GraphFactory graphFactory;
	
	/**
	 * true iff the local graph is currently loading
	 */
	protected boolean loading = false;

	
	/**
	 * the local graph version
	 */
	protected long graphVersion = 0;
	
	
	/**
	 * List of vertices to be deleted by a cascading delete caused by deletion
	 * of a composition "parent".
	 */
	private List<VertexImpl> deleteVertexList;
	
	/**
	 * free index list for vertices
	 */
	protected FreeIndexList freeVertexList;
	

	/**
	 * free index list for vertices
	 */
	protected FreeIndexList freeEdgeList;


	/**
	 * free index list for vertices
	 */
	protected FreeIndexList freeIncidenceList;
	
	
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
	 * current number of incidences
	 */
	protected int iCount;
	

	/**
	 * The disk storage used to store local element on disk
	 */
	private DiskStorageManager diskStorage;
	
	/*
	 * The map of global subgraph ids to the local representation objects. Those may be
	 * either objects representing a local subgraph or (proxy) objects representing
	 * remote subgraphs
	 */
	private Map<Integer, Reference<Graph>> subgraphObjects;
	
	private ArrayList<GraphData> localSubgraphData;

	
	private class GraphData {
		long firstVertexId;
		long lastVertexId;
		long firstEdgeId;
		long lastEdgeId;
		long containingElementId;
		int parentDistributedGraphId;
		int subgraphId;
		public int vCount;
	}
	
	private GraphData getGraphData(int localSubgraphId) {
		GraphData data = localSubgraphData.get(localSubgraphId);
		return data;
	}
	
	
	public GraphData getAndInitializeSubordinateGraph(long containingGraphElementId) {
		GraphData data = new GraphData();
		data.subgraphId = this.localPartialGraphId << (32-BITS_FOR_PARTIAL_GRAPH_MASK) + localSubgraphData.size(); 
		localSubgraphData.add(data);
		data.containingElementId = containingGraphElementId;
		data.parentDistributedGraphId = getPartialGraphId(containingGraphElementId);
		return data;
	}
	
	
	/* stores the graph databases for the remote graphs, these elements
	 * may be local proxies for the remote elements automatically created by RMI
	 * Map key is the 12-bit partial graph id.
	 */
	protected Map<Integer, RemoteGraphDatabaseAccess> partialGraphDatabases;
	
	/**
	 * Maps the global element id to the local proxy object representing the
	 * remote vertex
	 */
	protected Map<Long, Reference<Vertex>> remoteVertices;
	
	protected Map<Long, Reference<Edge>> remoteEdges;
	
	private Map<Long, Reference<Incidence>> remoteIncidences;

	
	/**
	 * Creates a new graph database to store all local subgraphs of the 
	 * complete graph identified by the given <code>uniqueGraphId</code>.
	 * All those local subgraphs have the same <code>partialGraphId</code>
	 * and belong to the specified <code>schema</code>.
	 * @param schema the schema of the graph whose subgraphs are represented 
	 *        by this graph database 
	 * @param uniqueGraphId the unique id of the graph
	 * @param partialGraphId the common partial graph id of all local subgraphs
	 */
	protected GraphDatabase(Schema schema, String uniqueGraphId, int partialGraphId, int parentDistributedGraphId) {
		freeVertexList = new FreeIndexList(10);
		freeIncidenceList = new FreeIndexList(10);
		freeEdgeList = new FreeIndexList(10);

		this.uniqueGraphId = uniqueGraphId;
		this.schema = schema;
		localPartialGraphId = partialGraphId;
		graphFactory = schema.getGraphFactory();
		this.parentDistributedGraphId = parentDistributedGraphId;
		
		try {
			diskStorage = new DiskStorageManager(this);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		subgraphObjects = new HashMap<Integer, Reference<Graph>>();
		localSubgraphData = new ArrayList<GraphData>();
		
		partialGraphDatabases = new HashMap<Integer, RemoteGraphDatabaseAccess>();
		
		//remoteGraphs = new HashMap<Integer, WeakReference<Graph>>();
		remoteVertices  = new HashMap<Long, Reference<Vertex>>();
		remoteEdges = new HashMap<Long, Reference<Edge>>();
		remoteIncidences = new HashMap<Long, Reference<Incidence>>();
		
		//initialize fields
		graphVersion = -1;
		vCount =0;
		setGraphVersion(0);

		//register graph database at server
		localJGraLabServer = JGraLabServerImpl.getLocalInstance();
		localJGraLabServer.registerLocalGraphDatabase(this); 
	}
	
	
	
	public String getUniqueGraphId() {
		return uniqueGraphId;
	}
	
	
	
	
	protected RemoteGraphDatabaseAccess getGraphDatabase(int partialGraphId) {
		RemoteGraphDatabaseAccess remoteAccess = partialGraphDatabases.get(partialGraphId);
		if (remoteAccess == null) {
			remoteAccess = localJGraLabServer.getRemoteInstance(getHostname(partialGraphId)).getGraphDatabase(uniqueGraphId);
			partialGraphDatabases.put(partialGraphId, remoteAccess);
		}
		return remoteAccess;
	}
	
	/**
	 * Retrieves the hostname that stores all subgraphs with the given partial graph id 
	 * @param id
	 * @return the hostname of the station containing the partial graph with the given id
	 */
	protected abstract String getHostname(int partialGraphId);
	
	/**
	 * Retrieves a free partial graph id
	 * @return a free and currently unused partial graph id
	 */
	protected abstract int getFreePartialGraphId();
	
	/**
	 * Deletes  the partial graph identified by its id
	 * @param partialGraphId
	 */	
	public abstract void deletePartialGraph(int partialGraphId);
	
	
	/**
	 * Registers the partial graph with the given id <code>id</code> which is stored on the
	 * host with the name <code>hostname</code>
	 * @param id
	 * @param hostname
	 */
	public abstract void registerRemotePartialGraph(int id, String hostname);
	
	/**
	 * Creates a new partial graph on the given hostname and returns the globalSubgraphId of that
	 * partial graph
	 */
	public abstract int createPartialGraph(Class<? extends Graph> graphClass, String hostname);
	
	
	/**
	 * Returns the type of the graph identified by the global subgraph id
	 * @param subgraphId
	 * @return the type of the 
	 */
	@SuppressWarnings("unchecked")
	protected Class<? extends Graph> getGraphType(int subgraphId) {
		return (Class<? extends Graph>) schema.getM1ClassForId(getGraphTypeId(subgraphId));
	}
	
	/**
	 * Returns the type of the edge identified by its global id. 
	 * @param elementId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Class<? extends Edge> getEdgeType(long elementId) {
		return (Class<? extends Edge>) schema.getM1ClassForId(getEdgeTypeId(elementId));
	}
	
	
	/**
	 * Returns the type of the edge identified by its global id. 
	 * @param elementId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Class<? extends Vertex> getVertexType(long elementId) {
		return (Class<? extends Vertex>) schema.getM1ClassForId(getVertexTypeId(elementId));
	}
	
	/**
	 * Returns the type of the edge identified by its global id. 
	 * @param elementId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Class<? extends Incidence> getIncidenceType(long elementId) {
		return (Class<? extends Incidence>) schema.getM1ClassForId(getIncidenceTypeId(elementId));
	}
	
	protected abstract int getGraphTypeId(int subgraphId);
	
	public abstract int getVertexTypeId(long vertexId);
	
	public abstract int getEdgeTypeId(long edgeId);
	
	public abstract int getIncidenceTypeId(long incidenceId);
	
	
	//Methods to access Graph, vertex, edge and incidence objects
	
	/**
	 * Retrieves a Graph object (local or proxy) that represents the (partial) (sub) graph identified by the given id
	 * @param partialGraphId
	 * @return
	 */
	public Graph getGraphObject(int globalSubgraphId) {
		Reference<Graph> ref = subgraphObjects.get(globalSubgraphId);
		Graph g = null;
		if (ref != null) {
			g = ref.get();
		}
		if (g == null) {
			g = graphFactory.createGraphDiskBasedStorage(getGraphType(globalSubgraphId), this);
			subgraphObjects.put(globalSubgraphId, new WeakReference<Graph>(g));
		}
		return g;
	}

	/**
	 * @return an object realizing the vertex with the given global id. The object
	 *         may be either a local one or a proxy for a remote one
	 */
	public Vertex getVertexObject(long id) {
		int partialGraphId = getPartialGraphId(id);
		if (partialGraphId == localPartialGraphId) {
			return diskStorage.getVertexObject(getLocalElementId(id));
		}
		Reference<Vertex> ref = remoteVertices.get(id);
		Vertex proxy = null;
		if (ref != null) {
			proxy = ref.get();
		}
		if (proxy == null) {
			//create new vertex proxy
			RemoteGraphDatabaseAccess remoteDatabase = getGraphDatabase(partialGraphId);
			Class<? extends Vertex> vc = getVertexType(id);
			proxy = graphFactory.createVertexProxy(vc, id, this, remoteDatabase);
			ref = new WeakReference<Vertex>(proxy);
			remoteVertices.put(id, ref);
		} 
		return proxy;
	}


	/**
	 * @return an object realizing the vertex with the given global id. The object
	 *         may be either a local one or a proxy for a remote one
	 */
	public Edge getEdgeObject(long id) {
		int partialGraphId = getPartialGraphId(id);
		if (partialGraphId == localPartialGraphId) {
			return diskStorage.getEdgeObject(getLocalElementId(id));
		}
		Reference<Edge> ref = remoteEdges.get(id);
		Edge proxy = null;
		if (ref != null) {
			proxy = ref.get();
		}
		if (proxy == null) {
			//create new vertex proxy
			RemoteGraphDatabaseAccess remoteDatabase = getGraphDatabase(partialGraphId);
			Class<? extends Edge> ec = getEdgeType(id);
			proxy = graphFactory.createEdgeProxy(ec, id, this, remoteDatabase);
			ref = new WeakReference<Edge>(proxy);
			remoteEdges.put(id, ref);
		} 
		return proxy;
	}
	
	
	/**
	 * @return an object realizing the vertex with the given global id. The object
	 *         may be either a local one or a proxy for a remote one
	 */
	public Incidence getIncidenceObject(long id) {
		int partialGraphId = getPartialGraphId(id);
		if (partialGraphId == localPartialGraphId) {
			return diskStorage.getIncidenceObject(getLocalElementId(id));
		}
		Reference<Incidence> ref = remoteIncidences.get(id);
		Incidence proxy = null;
		if (ref != null) {
			proxy = ref.get();
		}
		if (proxy == null) {
			//create new vertex proxy
			RemoteGraphDatabaseAccess remoteDatabase = getGraphDatabase(partialGraphId);
			Class<? extends Incidence> vc = getIncidenceType(id);
			proxy = graphFactory.createIncidenceDiskBasedStorage(vc, id, this, remoteDatabase);
			ref = new WeakReference<Incidence>(proxy);
			remoteIncidences.put(id, ref);
		} 
		return proxy;
	}
	
	
	
	// Methods to access vsed
	int getFirstVertex(int partialGraphId) {
		return 0;
	}
	
	int getLastVertex(int partialGraphId) {
		return 0;
	}

	public int getLocalGraphId() {
		return localPartialGraphId;
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
	public void setFirstIncidence(long elementId, long incidenceId) {
		
	}
	
	public void setLastIncidence(int elemId, int incidenceId) {
		int partialGraphId = getPartialGraphId(elemId);
		if (partialGraphId == localPartialGraphId)
			diskStorage.setLastIncidenceId(elemId, incidenceId);
		else
			getGraphDatabase(partialGraphId).setLastIncidence(elemId, incidenceId);
	}


	public void incidenceListModified(int elemId) {
		int partialGraphId = getPartialGraphId(elemId);
		if (partialGraphId == localPartialGraphId)
			diskStorage.incidenceListModified(elemId);
		else
			getGraphDatabase(partialGraphId).incidenceListModified(elemId);
	}


	/**
	 * Returns an object (Vertex or Edge) representing the GraphElement
	 * identified by the given global id
	 * @param elemId
	 * @return
	 */
	public GraphElement<?, ?, ?> getGraphElementObject(long elemId) {
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
		if (partialGraphId == localPartialGraphId)
			diskStorage.setSigmaId(elementId, sigmaId);
		else
			getGraphDatabase(partialGraphId).setSigma(elementId, sigmaId);
	}

	
	
	public int getKappa(int elementId) {
		int partialGraphId = getPartialGraphId(elementId);
		if (partialGraphId == localPartialGraphId)
			return diskStorage.getKappa(elementId);
		else
			return getGraphDatabase(partialGraphId).getKappa(elementId);
	}
	
	
	public void setKappa(int elementId, int kappa) {
		int partialGraphId = getPartialGraphId(elementId);
		if (partialGraphId == localPartialGraphId)
			diskStorage.setKappa(elementId, kappa);
		else
			getGraphDatabase(partialGraphId).setKappa(elementId, kappa);
	}
	
	/**
	 * To be implemented by RemoteDatabaseAccess as well as the DiskBasedStorages
	 * @author dbildh
	 *
	 */
	private class GraphAccessById {
		
	}
	
	private GraphAccessById[] graphAccesses;
	
	private final GraphAccessById getGraphAccessForElement(long elementId) {
		int pgId = getPartialGraphId(elementId);
		if (graphAccesses[pgId] == null) {
			//load direct graph access
		}
		return graphAccesses[pgId];
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
	
	@Override
	public long createEdge(int edgeClassId) {
		return createEdge(edgeClassId, 0);
	}
	
	
	public long convertToGlobalId(int localId) {
		long l = localPartialGraphId << (32-BITS_FOR_PARTIAL_GRAPH_MASK);
		return l + localId;
	}
	
	@Override
	public long createEdge(int edgeClassId, long edgeId) {
		//set id
		if (edgeId != 0) {
			if (!isLoading()) {
				throw new GraphException("Cannot add an edge with a predefined id outside the graph loading");
			} 
		} else {
			edgeId = convertToGlobalId(allocateEdgeIndex());
		}
		//instantiate object
		EdgeImpl e = (EdgeImpl) graphFactory.createEdgeDiskBasedStorage((Class<? extends Edge>) schema.getM1ClassForId(edgeClassId), edgeId, this);
		diskStorage.storeEdge(e);
		appendEdgeToESeq(e);

		if (!isLoading()) {
			edgeListModified();
			internalEdgeAdded(e);
		}
		return e.getId();
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
		long iId = i.getId();
		if (isLoading()) {
			if (iId > 0) {
				// the given vertex already has an id, try to use it
				if (containsIncidenceId(iId)) {
					throw new GraphException("incidence with id " + iId
							+ " already exists");
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
			int intId = allocateIncidenceIndex();
			assert iId != 0;
			i.setId(convertToGlobalId(intId));
			
		}
		diskStorage.storeIncidence(i);
		if (!isLoading()) {
			internalIncidenceAdded(i);
		}
	}

	private boolean containsIncidenceId(long iId) {
		// TODO Auto-generated method stub
		return false;
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
	

	private boolean containsVertexId(long eId) {
		return getVertexObject(eId) != null;
	}


	private boolean canAddGraphElement(long eId) {
		return ! (eId < 0 ? containsEdgeId(eId) : containsVertexId(eId));
	}

	private boolean containsEdgeId(long eId) {
		return getEdgeObject(eId) != null;
	}

	public boolean isLoading() {
		return loading;
	}

	public void setLoading(boolean isLoading) {
		this.loading = isLoading;
	}

	public void setVCount(int localSubgraphId, int count) {
		getGraphData(localSubgraphId).vCount = count; 
	}

	
	/**
	 * Deletes the edge from the internal structures of this graph.
	 * 
	 * @param edge
	 *            an edge
	 */
	private void internalDeleteEdge(Edge edge) {
		assert (edge != null) && edge.isValid() && containsEdge(edge);

		EdgeImpl e = (EdgeImpl) edge;
		internalEdgeDeleted(e);

		Incidence inc = e.getFirstIncidence();
		Set<Vertex> vertices = new HashSet<Vertex>();
		while (inc != null) {
			vertices.add(inc.getVertex());
			((VertexImpl) inc.getVertex())
					.removeIncidenceFromLambdaSeq((IncidenceImpl) inc);
			inc = e.getFirstIncidence();
		}
		for (Vertex vertex : vertices) {
			((VertexImpl) vertex).incidenceListModified();
		}

		removeEdgeFromESeq(e);

	}


	boolean containsEdge(Edge edge) {
		return getEdgeObject(edge.getId()) == edge;
	}

	/**
	 * Deletes all vertices in deleteVertexList from the internal structures of
	 * this graph. Possibly, cascading deletes of child vertices occur when
	 * parent vertices of Composition classes are deleted.
	 */
	private void internalDeleteVertex()  {
		boolean edgeHasBeenDeleted = false;
		while (!getDeleteVertexList().isEmpty()) {
			VertexImpl v = getDeleteVertexList().remove(0);
			assert (v != null) && v.isValid() && containsVertex(v);
			internalVertexDeleted(v);
			// delete all incident edges including incidence objects
			Incidence inc = v.getFirstIncidence();

			Set<EdgeImpl> edges = new HashSet<EdgeImpl>();
			while (inc != null) {
				EdgeImpl edge = (EdgeImpl) inc.getEdge();
				boolean deleteEdge = false;
				if (edge.isBinary()) {
					BinaryEdge bedge = (BinaryEdge) edge;
					if (bedge.getAlpha() == v) {
						if (bedge.getOmegaSemantics() == IncidenceType.COMPOSITION) {
							VertexImpl omega = (VertexImpl) bedge.getOmega();
							if ((omega != v) && containsVertex(omega)
									&& !getDeleteVertexList().contains(omega)) {
								getDeleteVertexList().add(omega);
								removeEdgeFromESeq((EdgeImpl) bedge);
								edgeAfterDeleted(bedge);
								deleteEdge = true;
							}
						}
					} else if (bedge.getOmega() == v) {
						if (bedge.getAlphaSemantics() == IncidenceType.COMPOSITION) {
							VertexImpl alpha = (VertexImpl) bedge.getAlpha();
							if ((alpha != v) && containsVertex(alpha)
									&& !getDeleteVertexList().contains(alpha)) {
								getDeleteVertexList().add(alpha);
								removeEdgeFromESeq((EdgeImpl) bedge);
								edgeAfterDeleted(bedge);
								deleteEdge = true;
							}
						}
					}
				}
				edgeHasBeenDeleted |= deleteEdge;
				if (!deleteEdge) {
					edges.add(edge);
					edge.removeIncidenceFromLambdaSeq((IncidenceImpl) inc);
				}
				inc = v.getFirstIncidence();
			}
			for (EdgeImpl edge : edges) {
				edge.incidenceListModified();
			}
			removeVertexFromVSeq(v);
			vertexAfterDeleted(v);
		}
		vertexListModified();
		if (edgeHasBeenDeleted) {
			edgeListModified();
		}
	}
	
	private void vertexAfterDeleted(VertexImpl v) {
		// TODO Notify all graph objects 
		
	}

	boolean containsVertex(Vertex vertex) {
		return getVertexObject(vertex.getId()) == vertex;
	}

	protected void putEdgeAfterInGraph(EdgeImpl targetEdge, EdgeImpl movedEdge) {
		assert (targetEdge != null) && targetEdge.isValid()
				&& containsEdge(targetEdge);
		assert (movedEdge != null) && movedEdge.isValid()
				&& containsEdge(movedEdge);
		assert targetEdge != movedEdge;

		if ((targetEdge == movedEdge)
				|| (targetEdge.getNextEdge() == movedEdge)) {
			return;
		}

		assert getFirstEdge() != getLastEdge();

		// remove moved edge from eSeq
		if (movedEdge == getFirstEdge()) {
			setFirstEdge((EdgeImpl) movedEdge.getNextEdge());
			((EdgeImpl) movedEdge.getNextEdge()).setPreviousEdge(null);
		} else if (movedEdge == getLastEdge()) {
			setLastEdge((EdgeImpl) movedEdge.getPreviousEdge());
			((EdgeImpl) movedEdge.getPreviousEdge()).setNextEdge(null);
		} else {
			((EdgeImpl) movedEdge.getPreviousEdge()).setNextEdge(movedEdge
					.getNextEdge());
			((EdgeImpl) movedEdge.getNextEdge()).setPreviousEdge(movedEdge
					.getPreviousEdge());
		}

		// insert moved edge in eSeq immediately after target
		if (targetEdge == getLastEdge()) {
			setLastEdge(movedEdge);
			movedEdge.setNextEdge(null);
		} else {
			((EdgeImpl) targetEdge.getNextEdge()).setPreviousEdge(movedEdge);
			movedEdge.setNextEdge(targetEdge.getNextEdge());
		}
		movedEdge.setPreviousEdge(targetEdge);
		targetEdge.setNextEdge(movedEdge);
		edgeListModified();
	}
	
	
	
	/**
	 * Modifies eSeq such that the movedEdge is immediately before the
	 * targetEdge. Both edges need to be member of the same partial graph
	 * 
	 * @param targetEdge
	 *            an edge
	 * @param movedEdge
	 *            the edge to be moved
	 */
	public void putEdgeBeforeInGraph(EdgeImpl e, EdgeImpl edgeImpl) {
		Edge targetEdge = getEdgeObject(e);
		Edge movedEdge = getEdgeObject(edgeImpl);
		assert (targetEdge != null) && targetEdge.isValid()
				&& containsEdge(targetEdge);
		assert (movedEdge != null) && movedEdge.isValid()
				&& containsEdge(movedEdge);
		assert targetEdge != movedEdge;

		if ((targetEdge == movedEdge)
				|| (targetEdge.getPreviousEdge() == movedEdge)) {
			return;
		}

		assert getFirstEdge() != getLastEdge();

		removeEdgeFromESeqWithoutDeletingIt((EdgeImpl) movedEdge);

		// insert moved edge in eSeq immediately before target
		if (targetEdge == getFirstEdge()) {
			setFirstEdge(movedEdge);
			setPreviousEdge(movedEdge, null);
		} else {
			EdgeImpl previousEdge = ((EdgeImpl) targetEdge.getPreviousEdge());
			previousEdge.setNextEdge(movedEdge);
			setPreviousEdge(movedEdge, previousEdge);
		}
		setNextEdge(movedEdge, targetEdge);
		setPreviousEdge(targetEdge, movedEdge);
		edgeListModified();
	}

	
	
	
	
	
	private void setNextEdge(Edge movedEdge, Edge targetEdge) {
		assert getPartialGraphId(movedEdge.getId()) == getPartialGraphId(targetEdge.getId());
		diskStorage.setNextEdge(movedEdge, targetEdge);
	}

	private void setPreviousEdge(Edge movedEdge, Edge targetEdge) {
		assert getPartialGraphId(movedEdge.getId()) == getPartialGraphId(targetEdge.getId());
	}

	private void setFirstEdge(Edge movedEdge) {
		assert getPartialGraphId(movedEdge.getId()) == localPartialGraphId;
		
	}
	
	private void setLastEdge(Object object) {
		// TODO Auto-generated method stub
		
	}

	

	public Incidence connect(Class<? extends Incidence> incClass, Vertex vertex, Edge edge) {
		return null;
	}
	
	
	
	/**
	 * Modifies vSeq such that the movedVertex is immediately after the
	 * targetVertex.
	 * 
	 * @param targetVertex
	 *            a vertex
	 * @param movedVertex
	 *            the vertex to be moved
	 */
	protected void putVertexAfter(VertexImpl targetVertex, VertexImpl movedVertex) {
		assert (targetVertex != null) && targetVertex.isValid()	&& containsVertex(targetVertex);
		assert (movedVertex != null) && movedVertex.isValid() && containsVertex(movedVertex);
		assert targetVertex != movedVertex;

		Vertex nextVertex = targetVertex.getNextVertex();
		if ((targetVertex == movedVertex) || (nextVertex == movedVertex)) {
			return;
		}

		assert getFirstVertex() != getLastVertex();

		// remove moved vertex from vSeq
		if (movedVertex == getFirstVertex()) {
			VertexImpl newFirstVertex = (VertexImpl) movedVertex.getNextVertex();
			setFirstVertex(newFirstVertex);
			newFirstVertex.setPreviousVertex(null);
		} else if (movedVertex == getLastVertex()) {
			setLastVertex((VertexImpl) movedVertex.getPreviousVertex());
			((VertexImpl) movedVertex.getPreviousVertex()).setNextVertex(null);
		} else {
			((VertexImpl) movedVertex.getPreviousVertex())
					.setNextVertex(movedVertex.getNextVertex());
			((VertexImpl) movedVertex.getNextVertex())
					.setPreviousVertex(movedVertex.getPreviousVertex());
		}

		// insert moved vertex in vSeq immediately after target
		if (targetVertex == getLastVertex()) {
			setLastVertex(movedVertex);
			movedVertex.setNextVertex(null);
		} else {
			((VertexImpl) targetVertex.getNextVertex())
					.setPreviousVertex(movedVertex);
			movedVertex.setNextVertex(targetVertex.getNextVertex());
		}
		movedVertex.setPreviousVertex(targetVertex);
		targetVertex.setNextVertex(movedVertex);
		vertexListModified();
	}

	private long getLastVertexId(int localSubgraphId) {
		GraphData data = getGraphData(localSubgraphId);
		return data.firstVertexId;
	}



	private long getFirstVertexId(int localSubgraphId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Modifies vSeq such that the movedVertex is immediately before the
	 * targetVertex.
	 * 
	 * GlobalOperation
	 * 
	 * @param targetVertex
	 *            a vertex
	 * @param movedVertex
	 *            the vertex to be moved
	 */
	protected void putVertexBefore(VertexImpl targetVertex,	VertexImpl movedVertex) {
		assert (targetVertex != null) && targetVertex.isValid()
				&& containsVertex(targetVertex);
		assert (movedVertex != null) && movedVertex.isValid()
				&& containsVertex(movedVertex);
		assert targetVertex != movedVertex;

		Vertex prevVertex = targetVertex.getPreviousVertex();
		if ((targetVertex == movedVertex) || (prevVertex == movedVertex)) {
			return;
		}

		assert getFirstVertex() != getLastVertex();

		// remove moved vertex from vSeq
		if (movedVertex == getFirstVertex()) {
			setFirstVertex((VertexImpl) movedVertex.getNextVertex());
			((VertexImpl) movedVertex.getNextVertex()).setPreviousVertex(null);
		} else if (movedVertex == getLastVertex()) {
			setLastVertex((VertexImpl) movedVertex.getPreviousVertex());
			((VertexImpl) movedVertex.getPreviousVertex()).setNextVertex(null);
		} else {
			((VertexImpl) movedVertex.getPreviousVertex())
					.setNextVertex(movedVertex.getNextVertex());
			((VertexImpl) movedVertex.getNextVertex())
					.setPreviousVertex(movedVertex.getPreviousVertex());
		}

		// insert moved vertex in vSeq immediately before target
		if (targetVertex == getFirstVertex()) {
			setFirstVertex(movedVertex);
			movedVertex.setPreviousVertex(null);
		} else {
			VertexImpl previousVertex = (VertexImpl) targetVertex
					.getPreviousVertex();
			previousVertex.setNextVertex(movedVertex);
			movedVertex.setPreviousVertex(previousVertex);
		}
		movedVertex.setNextVertex(targetVertex);
		targetVertex.setPreviousVertex(movedVertex);
		vertexListModified();
	}
	
	
	/**
	 * Removes the edge e from the global edge sequence of this graph.
	 * 
	 * @param e
	 *            an edge
	 */
	protected void removeEdgeFromESeq(EdgeImpl e) {
		assert e != null;
		removeEdgeFromESeqWithoutDeletingIt(e);

		// freeIndex(getFreeEdgeList(), e.getId());
		freeEdgeIndex(e.getId());
		e.setPreviousEdge(null);
		e.setNextEdge(null);
		removeEdgeFromDatabase(e);
		e.setId(0);
		setECount(getECount() - 1);
	}

	private void setECount(int i) {
		eCount = i;
	}	

	protected void removeEdgeFromESeqWithoutDeletingIt(EdgeImpl e) {
		if (e == getFirstEdge()) {
			// delete at head of edge list
			setFirstEdge((EdgeImpl) e.getNextEdge());
			if (getFirstEdge() != null) {
				((EdgeImpl) getFirstEdge()).setPreviousEdge(null);
			}
			if (e == getLastEdge()) {
				// this edge was the only one...
				setLastEdge(null);
			}
		} else if (e == getLastEdge()) {
			// delete at tail of edge list
			setLastEdge((EdgeImpl) e.getPreviousEdge());
			if (getLastEdge() != null) {
				((EdgeImpl) getLastEdge()).setNextEdge(null);
			}
		} else {
			// delete somewhere in the middle
			((EdgeImpl) e.getPreviousEdge()).setNextEdge(e.getNextEdge());
			((EdgeImpl) e.getNextEdge()).setPreviousEdge(e.getPreviousEdge());
		}
	}



	/**
	 * Removes the vertex v from the global vertex sequence of this graph.
	 * 
	 * @param v
	 *            a vertex
	 */
	protected void removeVertexFromVSeq(VertexImpl v) {
		assert v != null;
		if (v == getFirstVertex()) {
			// delete at head of vertex list
			setFirstVertex((VertexImpl) v.getNextVertex());
			if (getFirstVertex() != null) {
				((VertexImpl) getFirstVertex()).setPreviousVertex(null);
			}
			if (v == getLastVertex()) {
				// this vertex was the only one...
				setLastVertex(null);
			}
		} else if (v == getLastVertex()) {
			// delete at tail of vertex list
			setLastVertex((VertexImpl) v.getPreviousVertex());
			if (getLastVertex() != null) {
				((VertexImpl) getLastVertex()).setNextVertex(null);
			}
		} else {
			// delete somewhere in the middle
			((VertexImpl) v.getPreviousVertex()).setNextVertex(v
					.getNextVertex());
			((VertexImpl) v.getNextVertex()).setPreviousVertex(v
					.getPreviousVertex());
		}
		// freeIndex(getFreeVertexList(), v.getId());
		freeVertexIndex(v.getId());
		v.setPreviousVertex(null);
		v.setNextVertex(null);
		localGraphDatabase.removeVertexFromDatabase(v);
		v.setId(0);
		setVCount(getVCount() - 1);
	}
	
	
	protected void internalEdgeAdded(EdgeImpl e) {
		notifyEdgeAdded(e);
	}
	
	
	protected void internalEdgeDeleted(EdgeImpl e) {
		assert e != null;
		notifyEdgeDeleted(e);
	}
	
	protected void internalIncidenceAdded(IncidenceImpl i) {
		notifyIncidenceAdded(i);
	}
	
	protected void internalVertexAdded(VertexImpl v) {
		notifyVertexAdded(v);
	}

	protected void internalVertexDeleted(VertexImpl v) {
		assert v != null;
		notifyVertexDeleted(v);
	}
	
	
	
	
	
	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io) {
		T record = graphFactory.createRecord(recordClass, localGraph);
		try {
			record.readComponentValues(io);
		} catch (GraphIOException e) {
			e.printStackTrace();
		}
		return record;
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,Map<String, Object> fields) {
		T record = graphFactory.createRecord(recordClass, localGraph);
		record.setComponentValues(fields);
		return record;
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, Object... components) {
		T record = graphFactory.createRecord(recordClass, localGraph);
		record.setComponentValues(components);
		return record;
	}
	
	
	@Override
	public <T> JGraLabSet<T> createSet() {
		return new JGraLabSetImpl<T>();
	}

	@Override
	public <T> JGraLabSet<T> createSet(Collection<? extends T> collection) {
		return new JGraLabSetImpl<T>(collection);
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity) {
		return new JGraLabSetImpl<T>(initialCapacity);
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity, float loadFactor) {
		return new JGraLabSetImpl<T>(initialCapacity, loadFactor);
	}

	
	/**
	 * Connects the specified vertex <code>v</code> to the speficied edge <code>e</code> by an
	 * incidence of class <code>cls</code> and sets the incidence's id to the next locally 
	 * available incidence id
	 * @param cls
	 * @param vertex
	 * @param edge
	 */
	public long connect(Class<? extends Incidence> cls, Vertex v, Edge e) {
		return connect(cls, v, e, 0);
	}
	
	/**
	 * Connects the specified vertex <code>v</code> to the speficied edge <code>e</code> by an
	 * incidence of class <code>cls</code> and sets the incidence's id to <code>id</code>
	 * @param cls
	 * @param vertex
	 * @param edge
	 * @param id
	 */
	public long connect(Class<? extends Incidence> cls, Vertex v, Edge e, long id) {	
		IncidenceClass incClass = (IncidenceClass) schema.getTypeForId(schema.getClassId(cls));
		Direction dir = incClass.getDirection();
		
		//check id 
		
		
	    //call graph factory to create object
		Incidence newInc = graphFactory.createIncidenceDiskBasedStorage(cls, id, graphDatabase, remoteDatabase)
		
		//append created incidence to lambda sequences of vertex and edge
		
		
		//
			this.storage = v.storage.backgroundStorage.getIncidenceContainer(id);
			setIncidentEdge(e);
			setIncidentVertex(v);
			setDirection(dir);

			// add this incidence to the sequence of incidences of v
			if (v.getFirstIncidence() == null) {
				// v has no incidences
				v.setFirstIncidence(this);
				v.setLastIncidence(this);
			} else {
				((IncidenceImpl) v.getLastIncidence()).setNextIncidenceAtVertex(this);
				setPreviousIncidenceAtVertex((IncidenceImpl) v.getLastIncidence());
				v.setLastIncidence(this);
			}

			v.incidenceListModified();

			// add this incidence to the sequence of incidences of e
			if (e.getFirstIncidence() == null) {
				// v has no incidences
				e.setFirstIncidence(this);
				e.setLastIncidence(this);
			} else {
				((IncidenceImpl) e.getLastIncidence()).setNextIncidenceAtEdge(this);
				setPreviousIncidenceAtEdge((IncidenceImpl) e.getLastIncidence());
				e.setLastIncidence(this);
			}
//			if (getNextIncidenceAtEdge() != null)
//				throw new RemoteException();
			if (getNextIncidenceAtVertex() != null)
				throw new RuntimeException("id: " + id + " next id:" + getNextIncidenceAtVertex().getId() );
			e.incidenceListModified();
		}
	}
	
	
	
	
	
	
	

	public int getECount() {
		return eCount;
	}

	public long getEdgeListVersion() {
		return edgeListVersion;
	}

	public int getICount(int globalSubgraphId) {
		return iCount;
	}

	public int getMaxECount() {
		return eMax;
	}

	public int getMaxVCount() {
		return vMax;
	}
	
	/**
	 * Use to allocate a <code>Vertex</code>-index.
	 * 
	 * @param currentId
	 *            needed for transaction support
	 */
	protected int allocateVertexIndex(int currentId) {
		int vId = freeVertexList.allocateIndex();
		if (vId == 0) {
			vId = freeVertexList.allocateIndex();
		}
		return vId;
	}

	/**
	 * Use to allocate a <code>Edge</code>-index.
	 */
	protected int allocateEdgeIndex() {
		int eId = freeEdgeList.allocateIndex();
		if (eId == 0) {
			eId = freeEdgeList.allocateIndex();
		}
		return eId;
	}

	/**
	 * Use to allocate a <code>Incidence</code>-index.
	 * 
	 * @param currentId
	 *            needed for transaction support
	 */
	protected int allocateIncidenceIndex(int currentId) {
		int iId = freeIncidenceList.allocateIndex();
		return iId;
	}
	
	
	/**
	 * Use to free an <code>Edge</code>-index
	 * 
	 * @param index
	 */
	protected void freeEdgeIndex(int index) {
		freeEdgeList.freeIndex(index);
	}


	// ------------- GRAPH VARIABLES -------------
	
	/**
	 * Use to free a <code>Vertex</code>-index.
	 * 
	 * @param index
	 */
	protected void freeVertexIndex(int index) {
		freeVertexList.freeIndex(index);
	}
	
	protected FreeIndexList getFreeEdgeList() {
		return freeEdgeList;
	}


	protected FreeIndexList getFreeIncidenceList() {
		return freeIncidenceList;
	}

	
	protected FreeIndexList getFreeVertexList() {
		return freeVertexList;
	}
	
	public int getIdOfParentDistributedGraph() {
		return parentDistributedGraphId;
	}
	
	/**
	 * Appends the vertex v to the global vertex sequence of this graph.
	 * 
	 * @param v
	 *            a vertex
	 * @throws RemoteException 
	 */
	protected void appendVertexToVSeq(VertexImpl v) {
		setVCount(getVCount() + 1);
		if (getFirstVertex() == null) {
			setFirstVertex(v);
		}
		if (getLastVertex() != null) {
			((VertexImpl) getLastVertex()).setNextVertex(v);
			v.setPreviousVertex(getLastVertex());
		}
		setLastVertex(v);
	}
	
	
	public int getVCount() {
		return vCount;
	}

	/**
	 * Appends the edge e to the global edge sequence of this graph.
	 * 
	 * @param e
	 *            an edge
	 * @throws RemoteException 
	 */
	protected void appendEdgeToESeq(EdgeImpl e) {
		setECount(getECount() + 1);
		if (getFirstEdge() == null) {
			setFirstEdge(e);
		}
		if (getLastEdge() != null) {
			((EdgeImpl) getLastEdge()).setNextEdge(e);
			e.setPreviousEdge(getLastEdge());
		}
		setLastEdge(e);
	}

	public long getVertexListVersion() {
		return vertexListVersion;
	}

	public DiskStorageManager getDiskStorage() {
		return diskStorage;
	}

	/**
	 * Global methods, changes Vseq so that the vertex identified by movedVertexId is 
	 * directly before the vertex identified by targetVertexId
	 * @param targetVertexId global id of the target vertex
	 * @param movedVertexId global id of the vertex to be moved
	 */
	public void putVertexBefore(long targetVertexId, long movedVertexId) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Global methods, changes Vseq so that the vertex identified by movedVertexId is 
	 * directly after the vertex identified by targetVertexId
	 * @param targetVertexId global id of the target vertex
	 * @param movedVertexId global id of the vertex to be moved
	 */
	public void putVertexAfter(long targetVertexId, long movedVertexId)  {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Global methods, changes Eseq so that the edge identified by movedEdgeId is 
	 * directly after the edge identified by targetEdgeId
	 * @param targetEdgeId global id of the target edge
	 * @param movedEdgeId global id of the edge to be moved
	 */
	public void putEdgeAfter(long targetEdgeId, long movedEdgeId) {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Global methods, changes Eseq so that the edge identified by movedEdgeId is 
	 * directly before the edge identified by targetEdgeId
	 * @param targetEdgeId global id of the target edge
	 * @param movedEdgeId global id of the edge to be moved
	 */
	public void putEdgeBefore(long targetEdgeId, long movedEdgeId) {
		// TODO Auto-generated method stub
		
	}

	public Graph createViewGraph(SubordinateGraphImpl subordinateGraphImpl,
			int kappa) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * TODO GraphClass == containingElement.getType()?
	 * 
	 * @param containingVertex
	 *            {@link Vertex} which contains this subordinate graph
	 */
	//TODO: Move logics to GraphDb
	protected createSubordinateGraphImpl(Vertex containingVertex) {
		super(containingVertex.getGraph().getType());
		initializeCommonFields(containingVertex);
//System.out.println("Initialozing subordinate graph " + this);
		for (Vertex current = containingVertex.getNextVertex((Graph) null); 
		     current != null && ((GraphElementImpl<?, ?, ?>) current).isChildOf(getContainingElement()); 
		     current = current.getNextVertex((Graph)null)) {
			if (getFirstVertex() == null) {
				setFirstVertex((VertexImpl) current);
			}
			//System.out.println("  Iterating vertex " + current);
			setLastVertex((VertexImpl) current);
			setVCount(getVCount()+1);
		}
//System.out.println("Iterating edges");
		// initialize edges
		Edge current = containingVertex.getGraph().getFirstEdge();
		while (current != null
				&& !((GraphElementImpl<?, ?, ?>) current).isChildOf(getContainingElement())) {
			current = current.getNextEdge();
		}
		if (current != null) {
			setFirstEdge((EdgeImpl) current);
			do {
				setLastEdge((EdgeImpl) current);
				setECount(getECount() + 1);
				setICount(getICount() + current.getDegree());
				current = current.getNextEdge();
			} while (current != null
					&& ((GraphElementImpl<?, ?, ?>) current)
							.isChildOf(containingElement));
		}
		getCompleteGraph().addGraphStructureChangedListener(this);
	}

	/**
	 * TODO GraphClass == containingElement.getType()?
	 * 
	 * @param containingEdge
	 *            {@link Edge} which contains this subordinate graph
	 */
	//TODO: Move logics to GraphDb
	protected createSubordinateGraphImpl(Edge containingEdge) {
		super(containingEdge.getGraph().getType());
		initializeCommonFields(containingEdge);

		// initialize edges
		for (Edge current = containingEdge.getNextEdge(); current != null
				&& ((GraphElementImpl<?, ?, ?>) current)
						.isChildOf(containingElement); current.getNextEdge()) {
			if (getFirstEdge() == null) {
				setFirstEdge((de.uni_koblenz.jgralab.impl.mem.EdgeImpl) current);
			}
			setLastEdge((de.uni_koblenz.jgralab.impl.mem.EdgeImpl) current);
			eCount++;
			iCount += current.getDegree();
		}

		// initialize vertices
		Vertex current = containingEdge.getGraph().getFirstVertex();
		while (current != null
				&& !((GraphElementImpl<?, ?, ?>) current)
						.isChildOf(containingElement)) {
			current = current.getNextVertex();
		}
		if (current != null) {
			setFirstVertex((de.uni_koblenz.jgralab.impl.mem.VertexImpl) current);
			do {
				setLastVertex((de.uni_koblenz.jgralab.impl.mem.VertexImpl) current);
				setVCount(getVCount() + 1);
				current = current.getNextVertex();
			} while (current != null
					&& ((GraphElementImpl<?, ?, ?>) current)
							.isChildOf(containingElement));
		}
		getCompleteGraph().addGraphStructureChangedListener(this);
	}

	private void initializeCommonFields(GraphElement<?, ?, ?> containingElement) {
		this.containingElement = containingElement;
	}
	

}