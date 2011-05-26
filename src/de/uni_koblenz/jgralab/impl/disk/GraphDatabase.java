package de.uni_koblenz.jgralab.impl.disk;

import java.io.FileNotFoundException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
import de.uni_koblenz.jgralab.GraphStructureChangedListener;
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
	
	public static final int convertToLocalSubgraphId(int globalSubgraphId) {
		return globalSubgraphId & (MAX_NUMBER_OF_LOCAL_GRAPHS);
	}	
	
	public static final int getLocalElementId(long elementId) {
		return (int) elementId;
	}
	
	public long convertToGlobalId(int localId) {
		long l = localPartialGraphId << (64-BITS_FOR_PARTIAL_GRAPH_MASK);
		return l + localId;
	}	
	
	public int convertToGlobalSubgraphId(int localId) {
		int i = localPartialGraphId << (32-BITS_FOR_PARTIAL_GRAPH_MASK);
		return i + localId;
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
	
	protected final DiskStorageManager localDiskStorage;
	
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
	private List<Long> deleteVertexList;
	
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
	 * Listeners listining on changes of this graph
	 */
	private final List<GraphStructureChangedListener> graphStructureChangedListeners = new ArrayList<GraphStructureChangedListener>();
	
	private List<GraphStructureChangedListener> getGraphStructureChangeListeners() {
		return graphStructureChangedListeners;
	}
	
	
	
	/**
	 * The map of global subgraph ids to the local representation objects. Those may be
	 * either objects representing a local subgraph or (proxy) objects representing
	 * remote subgraphs
	 */
	private final Map<Integer, Reference<Graph>> subgraphObjects;
	
	/**
	 * Data of local subordinate graphs such as first and last elements, number of elements
	 * and so on
	 */
	private final ArrayList<GraphData> localSubgraphData;

	
	private class GraphData {
		long firstVertexId;
		long lastVertexId;
		long firstEdgeId;
		long lastEdgeId;
		long containingElementId;
		int parentDistributedGraphId;
		int subgraphId;
		int vCount;
		int eCount;
		int typeId;
	}
	
	private GraphData getGraphData(int localSubgraphId) {
		GraphData data = localSubgraphData.get(localSubgraphId);
		return data;
	}
		
	private int allocateSubgraphId() {
		return localSubgraphData.size();
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
	protected Map<Integer, RemoteGraphDatabaseAccessWithInternalMethods> partialGraphDatabases;
	
	protected Map<Integer, RemoteDiskStorageAccess> remoteDiskStorages;
	
	/**
	 * Maps the global element id to the local proxy object representing the
	 * remote vertex
	 */
	protected Map<Long, Reference<Vertex>> remoteVertices;
	
	protected Map<Long, Reference<Edge>> remoteEdges;
	
	private final Map<Long, Reference<Incidence>> remoteIncidences;

	
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
			localDiskStorage = new DiskStorageManager(this);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		subgraphObjects = new HashMap<Integer, Reference<Graph>>();
		localSubgraphData = new ArrayList<GraphData>();
		deleteVertexList = new LinkedList<Long>();
		
		partialGraphDatabases = new HashMap<Integer, RemoteGraphDatabaseAccessWithInternalMethods>();
		
		//remoteGraphs = new HashMap<Integer, WeakReference<Graph>>();
		remoteVertices  = new HashMap<Long, Reference<Vertex>>();
		remoteEdges = new HashMap<Long, Reference<Edge>>();
		remoteIncidences = new HashMap<Long, Reference<Incidence>>();
		
		//initialize fields
		graphVersion = -1;
		setGraphVersion(0);

		//register graph database at server
		localJGraLabServer = JGraLabServerImpl.getLocalInstance();
		localJGraLabServer.registerLocalGraphDatabase(this); 
	}
	
	
	
	public String getUniqueGraphId() {
		return uniqueGraphId;
	}
	
	
	/* ===============================================================
	 * Methods to access partial graphs
	   =============================================================== */
	
	
	protected RemoteGraphDatabaseAccessWithInternalMethods getGraphDatabase(int partialGraphId) {
		RemoteGraphDatabaseAccessWithInternalMethods remoteAccess = partialGraphDatabases.get(partialGraphId);
		if (remoteAccess == null) {
			remoteAccess = (RemoteGraphDatabaseAccessWithInternalMethods) localJGraLabServer.getRemoteInstance(getHostname(partialGraphId)).getGraphDatabase(uniqueGraphId);
			partialGraphDatabases.put(partialGraphId, (RemoteGraphDatabaseAccessWithInternalMethods) remoteAccess);
		}
		return remoteAccess;
	}
	
	protected RemoteDiskStorageAccess getRemoteDiskStorage(int partialGraphId) {
		RemoteDiskStorageAccess remoteAccess = remoteDiskStorages.get(partialGraphId);
		if (remoteAccess == null) {
			remoteAccess = getGraphDatabase(partialGraphId).getDiskStorage();
			remoteDiskStorages.put(partialGraphId, remoteAccess);
		}
		return remoteAccess;
	}
	
	/**
	 * Retrieves the hostname that stores all subgraphs with the given partial graph id 
	 * @param id
	 * @return the hostname of the station containing the partial graph with the given id
	 */
	public abstract String getHostname(int partialGraphId);
	
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
	
	public int getGraphTypeId(int subgraphId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			return getGraphDatabase(partialGraphId).getGraphTypeId(subgraphId);
		}	
		return getGraphData(subgraphId).typeId;
	}
	
	public int getVertexTypeId(long vertexId) {
		int partialGraphId = getPartialGraphId(vertexId);
		if (partialGraphId != localPartialGraphId) {
			return getGraphDatabase(partialGraphId).getVertexTypeId(vertexId);
		}	
		return localDiskStorage.getVertexTypeId(getLocalElementId(vertexId));
	}	
	
	public int getEdgeTypeId(long edgeId) {
		int partialGraphId = getPartialGraphId(edgeId);
		if (partialGraphId != localPartialGraphId) {
			return getGraphDatabase(partialGraphId).getEdgeTypeId(edgeId);
		}	
		return localDiskStorage.getEdgeTypeId(getLocalElementId(edgeId));
	}	
	
	
	public int getIncidenceTypeId(long incidenceId) {
		int partialGraphId = getPartialGraphId(incidenceId);
		if (partialGraphId != localPartialGraphId) {
			return getGraphDatabase(partialGraphId).getIncidenceTypeId(incidenceId);
		}	
		return localDiskStorage.getIncidenceTypeId(getLocalElementId(incidenceId));
	}	
	
	
	
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
			return localDiskStorage.getVertexObject(getLocalElementId(id));
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
			return localDiskStorage.getEdgeObject(getLocalElementId(id));
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
			return localDiskStorage.getIncidenceObject(getLocalElementId(id));
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
			proxy = graphFactory.createIncidenceDiskBasedStorage(vc, id, this);
			ref = new WeakReference<Incidence>(proxy);
			remoteIncidences.put(id, ref);
		} 
		return proxy;
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
	 * Returns an object (Vertex or Edge) representing the GraphElement
	 * identified by the given global id
	 * @param elemId
	 * @return
	 */
	public GraphElement<?, ?, ?> getGraphElementObject(long elemId) {
		if (elemId < 0) {
			return getEdgeObject(-elemId);
		} else {
			return getVertexObject(elemId);
		}
	}

	/**
	 * Sets the sigma value of the element identified by <code>elementId</code>
	 * to the value <code>sigmaId</code>. Both values are signed, negative values
	 * identify edges while positve ones identify vertices as in all other methods
	 * of this class 
	 */
	public void setSigma(int elementId, int sigmaId) {
		int partialGraphId = getPartialGraphId(elementId);
		if (partialGraphId == localPartialGraphId) {
			localDiskStorage.setSigmaId(elementId, sigmaId);
		} else {
			getGraphDatabase(partialGraphId).setSigma(elementId, sigmaId);
		}
	}
	
	
	public int getKappa(int elementId) {
		int partialGraphId = getPartialGraphId(elementId);
		if (partialGraphId == localPartialGraphId) {
			return localDiskStorage.getKappa(elementId);
		} else {
			return getGraphDatabase(partialGraphId).getKappa(elementId);
		}
	}
	
	
	public void setKappa(int elementId, int kappa) {
		int partialGraphId = getPartialGraphId(elementId);
		if (partialGraphId == localPartialGraphId) {
			localDiskStorage.setKappa(elementId, kappa);
		} else {
			getGraphDatabase(partialGraphId).setKappa(elementId, kappa);
		}
	}
	
//	/**
//	 * To be implemented by RemoteDatabaseAccess as well as the DiskBasedStorages
//	 * @author dbildh
//	 *
//	 */
//	private class GraphAccessById {
//		
//	}
//	
//	private GraphAccessById[] graphAccesses;
//	
//	private final GraphAccessById getGraphAccessForElement(long elementId) {
//		int pgId = getPartialGraphId(elementId);
//		if (graphAccesses[pgId] == null) {
//			//load direct graph access
//		}
//		return graphAccesses[pgId];
//	}
//	
	
	public DiskStorageManager getDiskStorage() {
		return localDiskStorage;
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

	
	/* **************************************************************************
	 * Methods to access graph properties
	 * **************************************************************************/
	
	public GraphFactory getGraphFactory() {
		return graphFactory;
	}

	public long getGraphVersion() {
		return graphVersion;
	}

	public void setGraphVersion(long graphVersion2) {
		this.graphVersion = graphVersion2;		
	}
	
	public boolean isLoading() {
		return loading;
		
	}

	public void setLoading(boolean isLoading) {
		this.loading = isLoading;
	}
	
	private boolean canAddGraphElement(long eId) {
		return ! (eId < 0 ? containsEdgeId(eId) : containsVertexId(eId));
	}
	
	public int getIdOfParentDistributedGraph() {
		return parentDistributedGraphId;
	}
	
	
	/* **************************************************************************
	 * Methods to access Vseq 
	 * **************************************************************************/
	
	public long getVertexListVersion() {
		return vertexListVersion;
	}
		
	public void setVCount(int localSubgraphId, int count) {
		getGraphData(localSubgraphId).vCount = count; 
	}
	
	public int getMaxVCount() {
		return Integer.MAX_VALUE;
	}
	
	/**
	 * Use to free a <code>Vertex</code>-index.
	 * 
	 * @param index
	 */
	protected void freeVertexIndex(int index) {
		freeVertexList.freeIndex(index);
	}
	
	protected FreeIndexList getFreeVertexList() {
		return freeVertexList;
	}
	
	
	/**
	 * Use to allocate a <code>Vertex</code>-index.
	 */
	protected int allocateVertexIndex() {
		int vId = freeVertexList.allocateIndex();
		if (vId == 0) {
			vId = freeVertexList.allocateIndex();
		}
		return vId;
	}
	
	
	
	private boolean containsVertexId(long vId) {
		return getVertexObject(vId) != null;
	}
	
	boolean containsVertex(Vertex vertex) {
		return getVertexObject(vertex.getId()) == vertex;
	}
	
	public long createVertex(int vertexClassId) {
		return createVertex(vertexClassId, 0);
	}	
	


	public long createVertex(int vertexClassId, long vertexId) {
		//set id
		if (isLoading()) {
			if (vertexId == 0) {
				throw new GraphException("Cannot add a vertex without a predefined id during graph loading");
			} 
		} else {
			if (vertexId == 0) {
				vertexId =  convertToGlobalId(allocateVertexIndex());
			} else {
				throw new GraphException("Cannot add a vertex with a predefined id outside the graph loading");
			}
		}

		//instantiate object
		VertexImpl v = (VertexImpl) graphFactory.createVertexDiskBasedStorage((Class<? extends Vertex>) schema.getM1ClassForId(vertexClassId), vertexId, this);
		localDiskStorage.storeVertex(v);

		int toplevelSubgraphId = convertToGlobalSubgraphId(1);
		
		getGraphData(0).vCount++;
			if (getFirstVertexId(toplevelSubgraphId) == 0) {
				setFirstVertexId(toplevelSubgraphId, vertexId);
			}
			if (getLastVertexId(toplevelSubgraphId) != 0) {
				setNextVertexId(getLastVertexId(toplevelSubgraphId), vertexId);
				setPreviousVertexId(vertexId, getLastVertexId(toplevelSubgraphId));
			}
			setLastVertexId(toplevelSubgraphId, vertexId);
		
		
		
		if (!isLoading()) {
			vertexListModified();
			notifyVertexAdded(vertexId);
		}
		return vertexId;
	}

	
	public void deleteVertex(long vertexId) {
		deleteVertexList.add(vertexId);
		deleteVerticesInDeleteList();
	}	
	
	
	/**
	 * Deletes all vertices in deleteVertexList from the internal structures of
	 * this graph. Possibly, cascading deletes of child vertices occur when
	 * parent vertices of Composition classes are deleted.
	 */
	private void deleteVerticesInDeleteList()  {
		boolean edgeHasBeenDeleted = false;
		while (deleteVertexList.isEmpty()) {
			long vertexId = deleteVertexList.remove(0);
			assert (vertexId != 0) && containsVertexId(vertexId);
			notifyVertexDeleted(vertexId);
			// delete all incident edges including incidence objects
			Vertex v = getVertexObject(vertexId);
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
									&& !deleteVertexList.contains(omega.getId())) {
								deleteVertexList.add(omega.getId());
								notifyEdgeDeleted(bedge.getId());
								removeEdgeFromESeq(bedge.getId());
								deleteEdge = true;
							}
						}
					} else if (bedge.getOmega() == v) {
						if (bedge.getAlphaSemantics() == IncidenceType.COMPOSITION) {
							VertexImpl alpha = (VertexImpl) bedge.getAlpha();
							if ((alpha != v) && containsVertex(alpha)
									&& !deleteVertexList.contains(alpha.getId())) {
								deleteVertexList.add(alpha.getId());
								notifyEdgeDeleted(bedge.getId());
								removeEdgeFromESeq(bedge.getId());
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
			removeVertexFromVSeq(vertexId);
		}
		vertexListModified();
		if (edgeHasBeenDeleted) {
			edgeListModified();
		}
	}
	
	
	private long getNextVertexId(long vertexId) {
		int partialGraphId = getPartialGraphId(vertexId);
		RemoteDiskStorageAccess diskStore = getRemoteDiskStorage(partialGraphId);
		return diskStore.getNextVertexId(getLocalElementId(vertexId));
	}
	
	private long getPreviousVertexId(long vertexId) {
		int partialGraphId = getPartialGraphId(vertexId);
		RemoteDiskStorageAccess diskStore = getRemoteDiskStorage(partialGraphId);
		return diskStore.getPreviousVertexId(getLocalElementId(vertexId));	
	}
	
	
	/**
	 * Removes the vertex v from the global vertex sequence of this graph.
	 * @param vertexId
	 *            a vertex
	 */
	protected void removeVertexFromVSeq(long vertexId) {
		assert vertexId != 0;
		int partialGraphId = getPartialGraphId(vertexId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId).removeVertexFromVSeq(vertexId);
			return;
		}	
		
		//TODO: instead of the toplevel graph, the lowest subgraph the vertex is 
		//      contained in needs to be determined. Because of the restrictions to 
		//      the ordering v may be only the first vertex of the lowest graph 
		//      it is contained in
		int toplevelGraphId = convertToGlobalSubgraphId(1);
		
		//if current vertex is the first or last one in the local graph,
		//the respecitive values need to be set to its next or previous vertex 
		long firstV = getFirstVertexId(toplevelGraphId);
		long lastV = getLastVertexId(toplevelGraphId);
		long nextV = getNextVertexId(vertexId);
		long prevV = getPreviousVertexId(vertexId);
		
		if (firstV == vertexId) {
			setFirstVertexId(toplevelGraphId, nextV);
		}	
		if (lastV == vertexId) {
			setLastVertexId(toplevelGraphId, prevV);
		}	

		
		//next and previous pointer of previous and next vertex need to be set
		//in any case (only exception: its the globally first or last vertex)
		if (prevV != 0)
			setNextVertexId(prevV, nextV);
		if (nextV != 0)
			setPreviousVertexId(prevV, nextV);
	
		
		//remove vertex from storage
		freeVertexIndex(getLocalElementId(vertexId));
		setPreviousVertexId(vertexId, 0);
		setNextVertexId(vertexId, 0);
		setVCount(toplevelGraphId, getVCount(toplevelGraphId) - 1);
		getDiskStorage().removeVertexFromDiskStorage(getLocalElementId(vertexId));
		notifyVertexDeleted(vertexId);
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
		putVertexBefore(targetVertex.getId(), movedVertex.getId());
		
	}

	/**
	 * Global methods, changes Vseq so that the vertex identified by movedVertexId is 
	 * directly before the vertex identified by targetVertexId
	 * @param targetVertexId global id of the target vertex
	 * @param movedVertexId global id of the vertex to be moved
	 */
	public void putVertexBefore(long targetVertexId, long movedVertexId) {
		long prevVertexId = getPreviousVertexId(targetVertexId);

		if ((targetVertexId == movedVertexId) || (prevVertexId == movedVertexId)) {
			return;
		}

		int toplevelGraphId = convertToGlobalSubgraphId(1);
		
		assert getFirstVertexId(toplevelGraphId) != getLastVertexId(toplevelGraphId);

		long firstV = getFirstVertexId(toplevelGraphId);
		long lastV = getLastVertexId(toplevelGraphId);
		long mvdNextV = getNextVertexId(movedVertexId);
		long mvdPrevV = getPreviousVertexId(movedVertexId);
		
		// remove moved vertex from vSeq
		if (movedVertexId == firstV) {
			setFirstVertexId(toplevelGraphId, mvdNextV);
		} 
		if (movedVertexId == lastV) {
			setLastVertexId(toplevelGraphId, mvdPrevV);
		}
		if (mvdPrevV != 0) {
			setNextVertexId(mvdPrevV, mvdNextV);
		}	
		if (mvdNextV != 0) {
			setPreviousVertexId(mvdNextV, mvdPrevV);
		}
		
		// insert moved vertex in vSeq immediately before target
		else if (targetVertexId == firstV) {
			setFirstVertexId(toplevelGraphId, movedVertexId);
		}	
		

		long tgtPrevV = getPreviousVertexId(targetVertexId);
		
		setPreviousVertexId(movedVertexId, tgtPrevV);
		setNextVertexId(movedVertexId, targetVertexId);
		setPreviousVertexId(targetVertexId, movedVertexId);
		if (tgtPrevV != 0) {
			setNextVertexId(tgtPrevV, movedVertexId);
		}

		vertexListModified();
		
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

	}	
	
	/**
	 * Global methods, changes Vseq so that the vertex identified by movedVertexId is 
	 * directly after the vertex identified by targetVertexId
	 * @param targetVertexId global id of the target vertex
	 * @param movedVertexId global id of the vertex to be moved
	 */
	@Override
	public void putVertexAfter(long targetVertexId, long movedVertexId)  {
		assert (targetVertexId != 0) && (containsVertexId(targetVertexId));
		assert (targetVertexId != 0) && (containsVertexId(targetVertexId));
		
	
		long prevVertexId = getPreviousVertexId(targetVertexId);

		if ((targetVertexId == movedVertexId) || (prevVertexId == movedVertexId)) {
			return;
		}

		int toplevelGraphId = convertToGlobalSubgraphId(1);
		
		assert getFirstVertexId(toplevelGraphId) != getLastVertexId(toplevelGraphId);

		long firstV = getFirstVertexId(toplevelGraphId);
		long lastV = getLastVertexId(toplevelGraphId);
		long mvdNextV = getNextVertexId(movedVertexId);
		long mvdPrevV = getPreviousVertexId(movedVertexId);
		
		// remove moved vertex from vSeq
		if (movedVertexId == firstV) {
			setFirstVertexId(toplevelGraphId, mvdNextV);
		} 
		if (movedVertexId == lastV) {
			setLastVertexId(toplevelGraphId, mvdPrevV);
		}
		if (mvdPrevV != 0) {
			setNextVertexId(mvdPrevV, mvdNextV);
		}	
		if (mvdNextV != 0) {
			setPreviousVertexId(mvdNextV, mvdPrevV);
		}
		
		// insert moved vertex in vSeq immediately after target
		else if (targetVertexId == lastV) {
			setLastVertexId(toplevelGraphId, movedVertexId);
		}	
		

		long tgtNextV = getNextVertexId(targetVertexId);
		
		setNextVertexId(movedVertexId, tgtNextV);
		setPreviousVertexId(movedVertexId, targetVertexId);
		setNextVertexId(targetVertexId, movedVertexId);
		if (tgtNextV != 0) {
			setPreviousVertexId(tgtNextV, movedVertexId);
		}

		vertexListModified();
		
	}
	
	private void setNextVertexId(long modifiedVertexId, long nextVertexId) {
		int partialGraphId = getPartialGraphId(modifiedVertexId);
		RemoteDiskStorageAccess diskStore = getRemoteDiskStorage(partialGraphId);
		diskStore.setNextVertexId(getLocalElementId(modifiedVertexId), nextVertexId);
	}

	private void setPreviousVertexId(long modifiedVertexId, long nextVertexId) {
		int partialGraphId = getPartialGraphId(modifiedVertexId);
		RemoteDiskStorageAccess diskStore = getRemoteDiskStorage(partialGraphId);
		diskStore.setPreviousVertexId(getLocalElementId(modifiedVertexId), nextVertexId);
	}

	public long getFirstVertexId(int subgraphId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			RemoteGraphDatabaseAccess remoteDb = getGraphDatabase(partialGraphId);
			return remoteDb.getFirstVertexId(subgraphId);
		} else {
			return getGraphData(convertToLocalSubgraphId(subgraphId)).firstVertexId;
		}
	}
	
	public void setFirstVertexId(int subgraphId, long edgeId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			RemoteGraphDatabaseAccess remoteDb = getGraphDatabase(partialGraphId);
			remoteDb.setFirstVertexId(subgraphId, edgeId);
		} else {
			getGraphData(convertToLocalSubgraphId(subgraphId)).firstVertexId = edgeId;
		}
	}
	
	
	public long getLastVertexId(int subgraphId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			RemoteGraphDatabaseAccess remoteDb = getGraphDatabase(partialGraphId);
			return remoteDb.getLastVertexId(subgraphId);
		} else {
			return getGraphData(convertToLocalSubgraphId(subgraphId)).lastVertexId;
		}
	}
	
	public void setLastVertexId(int subgraphId, long edgeId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			RemoteGraphDatabaseAccess remoteDb = getGraphDatabase(partialGraphId);
			remoteDb.setLastVertexId(subgraphId, edgeId);
		} else {
			getGraphData(convertToLocalSubgraphId(subgraphId)).lastVertexId = edgeId;
		}
	}

	
	
	
	/* **************************************************************************
	 * Methods to access Eseq
	 * **************************************************************************/
			
	public int getMaxECount() {
		return Integer.MAX_VALUE;
	}

	public long getEdgeListVersion() {
		return edgeListVersion;
	}

	public boolean containsEdgeId(long eId) {
		return getEdgeObject(eId) != null;
	}
	
	boolean containsEdge(Edge edge) {
		return getEdgeObject(edge.getId()) == edge;
	}

	/**
	 * Use to free an <code>Edge</code>-index
	 * 
	 * @param index
	 */
	protected void freeEdgeIndex(int index) {
		freeEdgeList.freeIndex(index);
	}
	
	protected FreeIndexList getFreeEdgeList() {
		return freeEdgeList;
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

	private long getNextEdgeId(long vertexId) {
		int partialGraphId = getPartialGraphId(vertexId);
		RemoteDiskStorageAccess diskStore = getRemoteDiskStorage(partialGraphId);
		return diskStore.getNextEdgeId(getLocalElementId(vertexId));
	}
	
	private long getPreviousEdgeId(long vertexId) {
		int partialGraphId = getPartialGraphId(vertexId);
		RemoteDiskStorageAccess diskStore = getRemoteDiskStorage(partialGraphId);
		return diskStore.getPreviousEdgeId(getLocalElementId(vertexId));
	}
	
	
	@Override
	public long createEdge(int edgeClassId) {
		return createEdge(edgeClassId, 0);
	}
	
	public long createEdge(int edgeClassId, long edgeId) {
		//set id
		if (isLoading()) {
			if (edgeId == 0) {
				throw new GraphException("Cannot add a edge without a predefined id during graph loading");
			} 
		} else {
			if (edgeId == 0) {
				edgeId =  convertToGlobalId(allocateEdgeIndex());
			} else {
				throw new GraphException("Cannot add a edge with a predefined id outside the graph loading");
			}
		}

		//instantiate object
		EdgeImpl v = (EdgeImpl) graphFactory.createEdgeDiskBasedStorage((Class<? extends Edge>) schema.getM1ClassForId(edgeClassId), edgeId, this);
		localDiskStorage.storeEdge(v);

		int toplevelSubgraphId = convertToGlobalSubgraphId(1);
		
		getGraphData(0).vCount++;
			if (getFirstEdgeId(toplevelSubgraphId) == 0) {
				setFirstEdgeId(toplevelSubgraphId, edgeId);
			}
			if (getLastEdgeId(toplevelSubgraphId) != 0) {
				setNextEdgeId(getLastEdgeId(toplevelSubgraphId), edgeId);
				setPreviousEdgeId(edgeId, getLastEdgeId(toplevelSubgraphId));
			}
			setLastEdgeId(toplevelSubgraphId, edgeId);
		
		
		
		if (!isLoading()) {
			edgeListModified();
			notifyEdgeAdded(edgeId);
		}
		return edgeId;
	}
	

	/**
	 * Deletes the edge from the internal structures of this graph.
	 * 
	 * @param edge
	 *            an edge
	 */
	public void deleteEdge(long edgeId) {
		assert (edgeId != 0) && containsEdgeId(edgeId);
		
		Edge e = getEdgeObject(edgeId);
		
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

		removeEdgeFromESeq(edgeId);

	}
	


	
	/**
	 * Removes the edge v from the global edge sequence of this graph.
	 * @param edgeId
	 *            a edge
	 */
	protected void removeEdgeFromESeq(long edgeId) {
		assert edgeId != 0;
		int partialGraphId = getPartialGraphId(edgeId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId).removeEdgeFromESeq(edgeId);
			return;
		}	
		
		//TODO: instead of the toplevel graph, the lowest subgraph the edge is 
		//      contained in needs to be determined. Because of the restrictions to 
		//      the ordering v may be only the first edge of the lowest graph 
		//      it is contained in
		int toplevelGraphId = convertToGlobalSubgraphId(1);
		
		//if current edge is the first or last one in the local graph,
		//the respecitive values need to be set to its next or previous edge 
		long firstV = getFirstEdgeId(toplevelGraphId);
		long lastV = getLastEdgeId(toplevelGraphId);
		long nextV = getNextEdgeId(edgeId);
		long prevV = getPreviousEdgeId(edgeId);
		
		if (firstV == edgeId) {
			setFirstEdgeId(toplevelGraphId, nextV);
		}	
		if (lastV == edgeId) {
			setLastEdgeId(toplevelGraphId, prevV);
		}	

		
		//next and previous pointer of previous and next edge need to be set
		//in any case (only exception: its the globally first or last edge)
		if (prevV != 0)
			setNextEdgeId(prevV, nextV);
		if (nextV != 0)
			setPreviousEdgeId(prevV, nextV);
	
		
		//remove edge from storage
		freeEdgeIndex(getLocalElementId(edgeId));
		setPreviousEdgeId(edgeId, 0);
		setNextEdgeId(edgeId, 0);
		setVCount(toplevelGraphId, getVCount(toplevelGraphId) - 1);
		getDiskStorage().removeEdgeFromDiskStorage(getLocalElementId(edgeId));
		notifyEdgeDeleted(edgeId);
	}

	
	

	/**
	 * Modifies vSeq such that the movedEdge is immediately before the
	 * targetEdge.
	 * 
	 * GlobalOperation
	 * 
	 * @param targetEdge
	 *            a edge
	 * @param movedEdge
	 *            the edge to be moved
	 */
	protected void putEdgeBefore(EdgeImpl targetEdge,	EdgeImpl movedEdge) {
		assert (targetEdge != null) && targetEdge.isValid()
				&& containsEdge(targetEdge);
		assert (movedEdge != null) && movedEdge.isValid()
				&& containsEdge(movedEdge);
		assert targetEdge != movedEdge;
		putEdgeBefore(targetEdge.getId(), movedEdge.getId());
		
	}

	/**
	 * Global methods, changes Vseq so that the edge identified by movedEdgeId is 
	 * directly before the edge identified by targetEdgeId
	 * @param targetEdgeId global id of the target edge
	 * @param movedEdgeId global id of the edge to be moved
	 */
	public void putEdgeBefore(long targetEdgeId, long movedEdgeId) {
		long prevEdgeId = getPreviousEdgeId(targetEdgeId);

		if ((targetEdgeId == movedEdgeId) || (prevEdgeId == movedEdgeId)) {
			return;
		}

		int toplevelGraphId = convertToGlobalSubgraphId(1);
		
		assert getFirstEdgeId(toplevelGraphId) != getLastEdgeId(toplevelGraphId);

		long firstV = getFirstEdgeId(toplevelGraphId);
		long lastV = getLastEdgeId(toplevelGraphId);
		long mvdNextV = getNextEdgeId(movedEdgeId);
		long mvdPrevV = getPreviousEdgeId(movedEdgeId);
		
		// remove moved edge from vSeq
		if (movedEdgeId == firstV) {
			setFirstEdgeId(toplevelGraphId, mvdNextV);
		} 
		if (movedEdgeId == lastV) {
			setLastEdgeId(toplevelGraphId, mvdPrevV);
		}
		if (mvdPrevV != 0) {
			setNextEdgeId(mvdPrevV, mvdNextV);
		}	
		if (mvdNextV != 0) {
			setPreviousEdgeId(mvdNextV, mvdPrevV);
		}
		
		// insert moved edge in vSeq immediately before target
		else if (targetEdgeId == firstV) {
			setFirstEdgeId(toplevelGraphId, movedEdgeId);
		}	
		

		long tgtPrevV = getPreviousEdgeId(targetEdgeId);
		
		setPreviousEdgeId(movedEdgeId, tgtPrevV);
		setNextEdgeId(movedEdgeId, targetEdgeId);
		setPreviousEdgeId(targetEdgeId, movedEdgeId);
		if (tgtPrevV != 0) {
			setNextEdgeId(tgtPrevV, movedEdgeId);
		}

		edgeListModified();
		
	}

	
	/**
	 * Modifies vSeq such that the movedEdge is immediately after the
	 * targetEdge.
	 * 
	 * @param targetEdge
	 *            a edge
	 * @param movedEdge
	 *            the edge to be moved
	 */
	protected void putEdgeAfter(EdgeImpl targetEdge, EdgeImpl movedEdge) {
		assert (targetEdge != null) && targetEdge.isValid()	&& containsEdge(targetEdge);
		assert (movedEdge != null) && movedEdge.isValid() && containsEdge(movedEdge);
		assert targetEdge != movedEdge;

	}	
	
	/**
	 * Global methods, changes Vseq so that the edge identified by movedEdgeId is 
	 * directly after the edge identified by targetEdgeId
	 * @param targetEdgeId global id of the target edge
	 * @param movedEdgeId global id of the edge to be moved
	 */
	public void putEdgeAfter(long targetEdgeId, long movedEdgeId)  {
		assert (targetEdgeId != 0) && (containsEdgeId(targetEdgeId));
		assert (targetEdgeId != 0) && (containsEdgeId(targetEdgeId));
		
	
		long prevEdgeId = getPreviousEdgeId(targetEdgeId);

		if ((targetEdgeId == movedEdgeId) || (prevEdgeId == movedEdgeId)) {
			return;
		}

		int toplevelGraphId = convertToGlobalSubgraphId(1);
		
		assert getFirstEdgeId(toplevelGraphId) != getLastEdgeId(toplevelGraphId);

		long firstV = getFirstEdgeId(toplevelGraphId);
		long lastV = getLastEdgeId(toplevelGraphId);
		long mvdNextV = getNextEdgeId(movedEdgeId);
		long mvdPrevV = getPreviousEdgeId(movedEdgeId);
		
		// remove moved edge from vSeq
		if (movedEdgeId == firstV) {
			setFirstEdgeId(toplevelGraphId, mvdNextV);
		} 
		if (movedEdgeId == lastV) {
			setLastEdgeId(toplevelGraphId, mvdPrevV);
		}
		if (mvdPrevV != 0) {
			setNextEdgeId(mvdPrevV, mvdNextV);
		}	
		if (mvdNextV != 0) {
			setPreviousEdgeId(mvdNextV, mvdPrevV);
		}
		
		// insert moved edge in vSeq immediately after target
		else if (targetEdgeId == lastV) {
			setLastEdgeId(toplevelGraphId, movedEdgeId);
		}	
		

		long tgtNextE = getNextEdgeId(targetEdgeId);
		
		setNextEdgeId(movedEdgeId, tgtNextE);
		setPreviousEdgeId(movedEdgeId, targetEdgeId);
		setNextEdgeId(targetEdgeId, movedEdgeId);
		if (tgtNextE != 0) {
			setPreviousEdgeId(tgtNextE, movedEdgeId);
		}

		edgeListModified();
		
	}
		
	
	private void setNextEdgeId(long modifiedEdgeId, long nextEdgeId) {
		int partialGraphId = getPartialGraphId(modifiedEdgeId);
		RemoteDiskStorageAccess diskStore = getRemoteDiskStorage(partialGraphId);
		diskStore.setNextEdgeId(getLocalElementId(modifiedEdgeId), nextEdgeId);
	}

	private void setPreviousEdgeId(long modifiedEdgeId, long nextEdgeId) {
		int partialGraphId = getPartialGraphId(modifiedEdgeId);
		RemoteDiskStorageAccess diskStore = getRemoteDiskStorage(partialGraphId);
		diskStore.setPreviousEdgeId(getLocalElementId(modifiedEdgeId), nextEdgeId);
	}

	public void setFirstEdgeId(int subgraphId, long edgeId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			RemoteGraphDatabaseAccess remoteDb = getGraphDatabase(partialGraphId);
			remoteDb.setFirstEdgeId(subgraphId, edgeId);
		} else {
			getGraphData(convertToLocalSubgraphId(subgraphId)).firstEdgeId = edgeId;
		}
	}
	
	public void setLastEdgeId(int subgraphId, long edgeId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			RemoteGraphDatabaseAccess remoteDb = getGraphDatabase(partialGraphId);
			remoteDb.setLastEdgeId(subgraphId, edgeId);
		} else {
			getGraphData(convertToLocalSubgraphId(subgraphId)).lastEdgeId = edgeId;
		}
	}


	
	/* **************************************************************************
	 * Methods to access Lambda sequences
	 * **************************************************************************/

	private boolean containsIncidenceId(long iId) {
		return getIncidenceObject(iId) != null;
	}

	
	protected FreeIndexList getFreeIncidenceList() {
		return freeIncidenceList;
	}
	
	/**
	 * Use to allocate a <code>Incidence</code>-index.
	 */
	protected int allocateIncidenceIndex() {
		int iId = freeIncidenceList.allocateIndex();
		return iId;
	}

	
	/**
	 * Sets the first {@link Incidence} of this {@link GraphElement} to
	 * <code>firstIncidence</code>.
	 * 
	 * @param firstIncidence
	 *            {@link IncidenceImpl}
	 */
	@Override
	public void setFirstIncidenceId(long elementId, long incidenceId) {
		int partialGraphId = getPartialGraphId(elementId);
		if (partialGraphId == localPartialGraphId) {
			localDiskStorage.setFirstIncidenceId(getLocalElementId(elementId), incidenceId);
		} else {
			getGraphDatabase(partialGraphId).setLastIncidenceId(elementId, incidenceId);
		}
	}
	
	@Override
	public void setLastIncidenceId(long elemId, long incidenceId) {
		int partialGraphId = getPartialGraphId(elemId);
		if (partialGraphId == localPartialGraphId) {
			localDiskStorage.setLastIncidenceId(getLocalElementId(elemId), incidenceId);
		} else {
			getGraphDatabase(partialGraphId).setLastIncidenceId(elemId, incidenceId);
		}
	}

	@Override
	public void setNextIncidenceIdAtVertexId(long globalIncidenceId, long nextIncidenceId) {
		int partialGraphId = getPartialGraphId(globalIncidenceId);
		if (partialGraphId == localPartialGraphId) {
			localDiskStorage.setNextIncidenceAtVertexId(getLocalElementId(globalIncidenceId), nextIncidenceId);
		} else {
			getGraphDatabase(partialGraphId).setLastIncidenceId(globalIncidenceId, nextIncidenceId);
		}
	}
	
	@Override
	public void setPreviousIncidenceIdAtVertexId(long globalIncidenceId, long nextIncidenceId) {
		int partialGraphId = getPartialGraphId(globalIncidenceId);
		if (partialGraphId == localPartialGraphId) {
			localDiskStorage.setPreviousIncidenceAtVertexId(getLocalElementId(globalIncidenceId), nextIncidenceId);
		} else {
			getGraphDatabase(partialGraphId).setLastIncidenceId(globalIncidenceId, nextIncidenceId);
		}
	}
	
	public void setNextIncidenceAtEdge(long globalIncidenceId, long nextIncidenceId) {
		int partialGraphId = getPartialGraphId(globalIncidenceId);
		if (partialGraphId == localPartialGraphId) {
			localDiskStorage.setNextIncidenceAtEdgeId(getLocalElementId(globalIncidenceId), nextIncidenceId);
		} else {
			getGraphDatabase(partialGraphId).setLastIncidenceId(globalIncidenceId, nextIncidenceId);
		}
	}
	
	public void setPreviousIncidenceAtEdge(long globalIncidenceId, long nextIncidenceId) {
		int partialGraphId = getPartialGraphId(globalIncidenceId);
		if (partialGraphId == localPartialGraphId) {
			localDiskStorage.setPreviousIncidenceAtEdgeId(getLocalElementId(globalIncidenceId), nextIncidenceId);
		} else {
			getGraphDatabase(partialGraphId).setLastIncidenceId(globalIncidenceId, nextIncidenceId);
		}
	}
	



	public void incidenceListModified(int elemId) {
		int partialGraphId = getPartialGraphId(elemId);
		if (partialGraphId == localPartialGraphId) {
			localDiskStorage.incidenceListModified(elemId);
		} else {
			getGraphDatabase(partialGraphId).incidenceListModified(elemId);
		}
	}
	
	/**
	 * Connects the specified vertex <code>v</code> to the speficied edge <code>e</code> by an
	 * incidence of class <code>cls</code> and sets the incidence's id to the next locally 
	 * available incidence id
	 * @param cls
	 * @param vertex
	 * @param edge
	 */
	public long connect(Class<? extends Incidence> cls, long vertexId, long edgeId) {
		return connect(cls, vertexId, edgeId, 0);
	}
	
	/**
	 * Connects the specified vertex <code>v</code> to the speficied edge <code>e</code> by an
	 * incidence of class <code>cls</code> and sets the incidence's id to <code>id</code>
	 * @param cls
	 * @param vertex
	 * @param edge
	 * @param id
	 */
	@Override
	public long connect(Class<? extends Incidence> cls, long vertexId, long edgeId, long incId) {	
		IncidenceClass incClass = (IncidenceClass) schema.getTypeForId(schema.getClassId(cls));
		Direction dir = incClass.getDirection();
		
		//check id 
		if (incId != 0) {
			if (!isLoading()) {
				throw new GraphException("Incidences with a defined id may only be created during graph loading");
			}
		} else {
			incId = convertToGlobalId(allocateIncidenceIndex());
		}	
	    //call graph factory to create object
		IncidenceImpl newInc = (IncidenceImpl) graphFactory.createIncidenceDiskBasedStorage(cls, incId, this);
		
		//set incident edge and vertex ids of incidence 
		setIncidentEdgeId(incId, edgeId);
		setIncidentVertexId(incId, vertexId);
		setDirection(incId, dir);
		
		//append created incidence to lambda sequences of vertex and edge
		
		// add this incidence to the sequence of incidences of v
		if (getFirstIncidenceId(vertexId) == 0) {
			// v has no incidences
			setFirstIncidenceId(vertexId, incId);
			setLastIncidenceId(vertexId, incId);
		} else {
			long lastIncId = getLastIncidenceId(vertexId);
			setNextIncidenceIdAtVertexId(lastIncId, incId);
			setPreviousIncidenceIdAtVertexId(incId, lastIncId);
			setLastIncidenceId(vertexId, incId);
		}

		incidenceListModified(vertexId);

		if (getFirstIncidenceId(-edgeId) == 0) {
			// v has no incidences
			setFirstIncidenceId(-edgeId, incId);
			setLastIncidenceId(-edgeId, incId);
		} else {
			long lastIncId = getLastIncidenceId(-edgeId);
			setNextIncidenceIdAtEdgeId(lastIncId, incId);
			setPreviousIncidenceIdAtEdgeId(incId, lastIncId);
			setLastIncidenceId(-edgeId, incId);
		}

		incidenceListModified(vertexId);
		
		
		localDiskStorage.storeIncidence(newInc);
		if (!isLoading()) {
			notifyIncidenceAdded(incId);
		}
		return incId;
	}



	








	






	

	


	
	
	

	protected void notifyEdgeAdded(long edgeId) {
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases.values()) {
			gdb.internalNotifyEdgeAdded(edgeId);
		}	
	}
	
	protected void notifyEdgeDeleted(long edgeId) {
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases.values()) {
			gdb.internalNotifyEdgeDeleted(edgeId);
		}	
	}
	
	public void internalNotifyEdgeAdded(long edgeId) {
		Edge o = getEdgeObject(edgeId);
		for (GraphStructureChangedListener l : graphStructureChangedListeners) {
			l.edgeAdded(o);  
		}	
	}
	
	public void internalNotifyEdgeDeleted(long edgeId) {
		Edge o = getEdgeObject(edgeId);
		for (GraphStructureChangedListener l : graphStructureChangedListeners) {
			l.edgeDeleted(o);  
		}	
	}
	
	protected void notifyVertexAdded(long vertexId) {
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases.values()) {
			gdb.internalNotifyVertexAdded(vertexId);
		}	
	}
	
	protected void notifyVertexDeleted(long vertexId) {
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases.values()) {
			gdb.internalNotifyVertexDeleted(vertexId);
		}	
	}
	
	public void internalNotifyVertexAdded(long vertexId) {
		Vertex o = getVertexObject(vertexId);
		for (GraphStructureChangedListener l : graphStructureChangedListeners) {
			l.vertexAdded(o);  
		}	
	}
	
	public void internalNotifyVertexDeleted(long vertexId) {
		Vertex o = getVertexObject(vertexId);
		for (GraphStructureChangedListener l : graphStructureChangedListeners) {
			l.vertexDeleted(o);  
		}	
	}
	
	protected void notifyIncidenceAdded(long incidenceId) {
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases.values()) {
			gdb.internalNotifyIncidenceAdded(incidenceId);
		}	
	}
	
	protected void notifyIncidenceDeleted(long incidenceId) {
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases.values()) {
			gdb.internalNotifyIncidenceDeleted(incidenceId);
		}	
	}
	
	public void internalNotifyIncidenceAdded(long incidenceId) {
		Incidence o = getIncidenceObject(incidenceId);
		for (GraphStructureChangedListener l : graphStructureChangedListeners) {
			l.incidenceAdded(o);  
		}	
	}
	
	public void internalNotifyIncidenceDeleted(long incidenceId) {
		Incidence o = getIncidenceObject(incidenceId);
		for (GraphStructureChangedListener l : graphStructureChangedListeners) {
			l.incidenceDeleted(o);  
		}	
	}

	
	
	
	
	
	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io) {
		T record = graphFactory.createRecord(recordClass, getGraphObject(convertToGlobalSubgraphId(1)));
		try {
			record.readComponentValues(io);
		} catch (GraphIOException e) {
			e.printStackTrace();
		}
		return record;
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,Map<String, Object> fields) {
		T record = graphFactory.createRecord(recordClass, getGraphObject(convertToGlobalSubgraphId(1)));
		record.setComponentValues(fields);
		return record;
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, Object... components) {
		T record = graphFactory.createRecord(recordClass, getGraphObject(convertToGlobalSubgraphId(1)));
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

	


	public Graph createViewGraph(Graph g, int kappa) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * TODO GraphClass == containingElement.getType()?
	 * 
	 * @param containingVertex
	 *            {@link Vertex} which contains this subordinate graph
	 */

	public int createSubordinateGraph(long elementId) {
		//get m1 class and free id
		Class<? extends Graph> m1Class = schema.getGraphClass().getM1Class();
		int localGraphId = allocateSubgraphId();
		int globalGraphId = convertToGlobalSubgraphId(localGraphId);
		Graph subordinateGraph = graphFactory.createSubordinateGraphDiskBasedStorage(m1Class, globalGraphId);
		
		GraphData data = getGraphData(localGraphId);
		data.containingElementId = elementId;
		data.parentDistributedGraphId = localPartialGraphId;
		data.subgraphId = globalGraphId;
		data.typeId = schema.getClassId(m1Class);
		data.vCount = 0;
		data.eCount = 0;
		localSubgraphData.add(data);
		return globalGraphId;
	}





}