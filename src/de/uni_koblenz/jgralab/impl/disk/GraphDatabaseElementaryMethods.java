package de.uni_koblenz.jgralab.impl.disk;

import java.io.FileNotFoundException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphStructureChangedListener;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLabServer;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.JGraLabServerImpl;
import de.uni_koblenz.jgralab.schema.Schema;

public abstract class GraphDatabaseElementaryMethods implements RemoteGraphDatabaseAccessWithInternalMethods {
	
	protected class GraphData {
		long globalSubgraphId;
		long firstVertexId;
		long lastVertexId;
		long firstEdgeId;
		long lastEdgeId;
		long edgeCount;
		long vertexCount;
		long containingElementId;
		int typeId;
	}
	
	/**
	 * Data of local subordinate graphs such as first and last elements, number of elements
	 * and so on
	 */
	protected final ArrayList<GraphData> localSubgraphData;

	/**
	 * Retrieves the partial graph id of the given globalSubgraphOrElementId, this
	 * is the highbyte of the long value.
	 */
	public static final int getPartialGraphId(long globalSubgraphOrElementId) {
		return (int) (globalSubgraphOrElementId >> 32);
	}

	/**
	 * Retrieves the local id of the given globalSubgraphOrElementId, this
	 * is the lowbyte of the long value.
	 */
	public final static int convertToLocalId(long globalSubgraphOrElementId) {
		return (int) globalSubgraphOrElementId;
	}
	
	public final long convertToGlobalId(int localSubgraphOrElementId) {
		long l = localPartialGraphId << 32;
		return l + localSubgraphOrElementId;
	}

	

	/**
	 * The graph schema of the graph whose local subgraphs are managed by this GraphDatabase belongs to
	 */
	protected final Schema schema;

	/**
	 * The unique id of the graph whose subgraphs are stored in this database 
	 */
	protected final String uniqueGraphId;
	
	/**
	 * The id of the local toplevel graph
	 */
	protected final int localPartialGraphId;
	
	/**
	 * The id of the complete or subgraph the local partial graph directly belongs to
	 */
	protected final long parentSubgraphId;
	
	/**
	 * The local JGraLab server instance
	 */
	protected final JGraLabServer localJGraLabServer;
	
	/**
	 * The disk storage storing local graph elements 	
	 */
	protected final DiskStorageManager localDiskStorage;
	
	/**
	 * The GraphFactory to create graphs and graph elements and proxies
	 */
	protected GraphFactory graphFactory;
	
	/**
	 * true iff the local graph is currently loading
	 */
	protected boolean loading = false;
	
	/**
	 * List of vertices to be deleted by a cascading delete caused by deletion
	 * of a composition "parent".
	 */
	protected final List<Long> deleteVertexList;
	
	/**
	 * free index list for vertices
	 */
	protected final FreeIndexList freeVertexList;
	
	/**
	 * free index list for vertices
	 */
	protected final FreeIndexList freeEdgeList;
	
	/**
	 * free index list for vertices
	 */
	protected final FreeIndexList freeIncidenceList;
	
	/**
	 * the local graph version
	 */
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
	 * Remote GraphDatabases of the distributed graph
	 */
	protected Map<Integer, RemoteGraphDatabaseAccessWithInternalMethods> partialGraphDatabases;
	
	protected final Map<Integer, RemoteDiskStorageAccess> remoteDiskStorages;
	
	/**
	 * The map of global subgraph ids to the local representation objects. Those may be
	 * either objects representing a local subgraph or (proxy) objects representing
	 * remote subgraphs
	 */
	protected final Map<Long, Reference<Graph>> subgraphObjects;
	
	/**
	 * Maps the global element id to the local proxy object representing the
	 * remote vertex
	 */
	protected final Map<Long, Reference<Vertex>> remoteVertices;
	protected final Map<Long, Reference<Edge>> remoteEdges;
	protected final Map<Long, Reference<Incidence>> remoteIncidences;
	
	
	
	/* ==================================================================================
	 * Graph listeners
	 * ================================================================================== */
	
	
	/**
	 * Listeners listining on changes of this graph
	 */
	protected final List<GraphStructureChangedListener> graphStructureChangedListeners = new ArrayList<GraphStructureChangedListener>();
	
	protected List<GraphStructureChangedListener> getGraphStructureChangeListeners() {
		return graphStructureChangedListeners;
	}
	
	public void addGraphStructureChangeListener(GraphStructureChangedListener newListener) {
		if (!graphStructureChangedListeners.contains(newListener)) {
			graphStructureChangedListeners.add(newListener);
		}	
	}
	
	public void removeGraphStructureChangeListener(GraphStructureChangedListener newListener) {	
		graphStructureChangedListeners.remove(newListener);
	}
	
	
	
	/* ==================================================================================
	 * Constructur and basic methods to access subgraphs
	 * ================================================================================== */
	
	
	public GraphDatabaseElementaryMethods( Schema schema, String uniqueGraphId, long parentSubgraphId, int localPartialGraphId) {
		this.uniqueGraphId = uniqueGraphId;
		this.schema = schema;
		this.parentSubgraphId = parentSubgraphId;
		this.localPartialGraphId = localPartialGraphId;
		try {
			this.localDiskStorage = new DiskStorageManager((GraphDatabaseBaseImpl) this);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		remoteDiskStorages = new HashMap<Integer, RemoteDiskStorageAccess>();
		remoteDiskStorages.put(localPartialGraphId, localDiskStorage);
		this.freeVertexList = new FreeIndexList(Integer.MAX_VALUE);
		this.freeEdgeList = new FreeIndexList(Integer.MAX_VALUE);
		this.freeIncidenceList = new FreeIndexList(Integer.MAX_VALUE);
		this.deleteVertexList = new LinkedList<Long>();

		localSubgraphData = new ArrayList<GraphData>();
		subgraphObjects = new HashMap<Long, Reference<Graph>>();
		remoteVertices = new HashMap<Long, Reference<Vertex>>();
		remoteEdges = new HashMap<Long, Reference<Edge>>();
		remoteIncidences = new HashMap<Long, Reference<Incidence>>();
		//initialize fields
		graphVersion = -1;
		setGraphVersion(0);

		//register graph database at server
		localJGraLabServer = JGraLabServerImpl.getLocalInstance();
		localJGraLabServer.registerLocalGraphDatabase((GraphDatabaseBaseImpl) this); 
	}
	
	
	/**
	 * @param localSubgraphId
	 * @return the graph data object storing the data of the local
	 * subgraph identified by <code>localSubgraphId</code>
	 */
	protected GraphData getGraphData(int localSubgraphId) {
		GraphData data = localSubgraphData.get(localSubgraphId);
		return data;
	}

//	/**
//	 * Allocates and returns a free local subgraph id
//	 */
//	protected int allocateLocalSubgraphId() {
//		return localSubgraphData.size();
//	}

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
	

	@Override
	public String getUniqueGraphId() {
		return uniqueGraphId;
	}
	
	public int getLocalPartialGraphId() {
		return localPartialGraphId;
	}
	
	@Override
	public long getIdOfParentDistributedGraph() {
		return parentSubgraphId;
	}
	
	public GraphFactory getGraphFactory() {
		return graphFactory;
	}

	public long getGraphVersion() {
		return graphVersion;
	}

	@Override
	public boolean isLoading() {
		return loading;
		
	}

	@Override
	public void setLoading(boolean isLoading) {
		this.loading = isLoading;
	}

	protected boolean canAddGraphElement(long eId) {
		return ! (eId < 0 ? containsEdgeId(eId) : containsVertexId(eId));
	}



	protected RemoteGraphDatabaseAccessWithInternalMethods getGraphDatabase(int partialGraphId) {
		RemoteGraphDatabaseAccessWithInternalMethods remoteAccess = partialGraphDatabases.get(partialGraphId);
		if (remoteAccess == null) {
			remoteAccess = (RemoteGraphDatabaseAccessWithInternalMethods) localJGraLabServer.getRemoteInstance(getHostname(partialGraphId)).getGraphDatabase(uniqueGraphId);
			partialGraphDatabases.put(partialGraphId, (RemoteGraphDatabaseAccessWithInternalMethods) remoteAccess);
		}
		return remoteAccess;
	}

	protected RemoteDiskStorageAccess getDiskStorageForPartialGraph(int partialGraphId) {
		RemoteDiskStorageAccess remoteAccess = remoteDiskStorages.get(partialGraphId);
		if (remoteAccess == null) {
			remoteAccess = getGraphDatabase(partialGraphId).getLocalDiskStorage();
			remoteDiskStorages.put(partialGraphId, remoteAccess);
		}
		return remoteAccess;
	}
	
	public DiskStorageManager getLocalDiskStorage() {
		return localDiskStorage;
	}


	public Schema getSchema() {
		return schema;
	}

	
	/* ==================================================================================
	 * Methods to access types and type ids
	 * ================================================================================== */


	/**
	 * Returns the type of the graph identified by the global subgraph id
	 * @param globalSubgraphId
	 * @return the type of the 
	 */
	@SuppressWarnings("unchecked")
	protected Class<? extends Graph> getGraphType(long globalSubgraphId) {
		return (Class<? extends Graph>) schema.getM1ClassForId(getGraphTypeId(globalSubgraphId));
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

	public int getGraphTypeId(long globalSubgraphId) {
		int partialGraphId = getPartialGraphId(globalSubgraphId);
		int localSubgraphId = convertToLocalId(globalSubgraphId);
		if (partialGraphId != localPartialGraphId) {
			return getGraphDatabase(partialGraphId).getGraphTypeId(localSubgraphId);
		}	
		return getGraphData(localSubgraphId).typeId;
	}

	public int getVertexTypeId(long vertexId) {
		int partialGraphId = getPartialGraphId(vertexId);
		return getDiskStorageForPartialGraph(partialGraphId).getVertexTypeId(convertToLocalId(vertexId));
	}

	public int getEdgeTypeId(long edgeId) {
		int partialGraphId = getPartialGraphId(edgeId);
		return getDiskStorageForPartialGraph(partialGraphId).getEdgeTypeId(convertToLocalId(edgeId));
	}

	public int getIncidenceTypeId(long incidenceId) {
		int partialGraphId = getPartialGraphId(incidenceId);
		return getDiskStorageForPartialGraph(partialGraphId).getIncidenceTypeId(convertToLocalId(incidenceId));
	}
	
	
	/* ==================================================================================
	 * Methods to access local and proxy objects for graphs and their elements
	 * ================================================================================== */

	/**
	 * Retrieves a Graph object (local or proxy) that represents the (partial) (sub) graph identified by the given id
	 * @param partialGraphId
	 * @return
	 */
	public Graph getGraphObject(long globalSubgraphId) {
		Reference<Graph> ref = subgraphObjects.get(globalSubgraphId);
		Graph g = null;
		if (ref != null) {
			g = ref.get();
		}
		if (g == null) {
			g = graphFactory.createGraphDiskBasedStorage(getGraphType(globalSubgraphId), (GraphDatabaseBaseImpl) this);
			subgraphObjects.put(globalSubgraphId, new WeakReference<Graph>(g));
		}
		return g;
	}
//
//	/**
//	 * 
//	 * @return
//	 */
//	@Override
//	public Graph getCompleteGraphObject() {
//		return getGraphObject(0);
//	}

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
	 * @return an object realizing the vertex with the given global id. The object
	 *         may be either a local one or a proxy for a remote one
	 */
	public Vertex getVertexObject(long id) {
		int partialGraphId = getPartialGraphId(id);
		if (partialGraphId == localPartialGraphId) {
			return localDiskStorage.getVertexObject(convertToLocalId(id));
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
			proxy = graphFactory.createVertexProxy(vc, id, (GraphDatabaseBaseImpl) this, remoteDatabase);
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
			return localDiskStorage.getEdgeObject(convertToLocalId(id));
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
			proxy = graphFactory.createEdgeProxy(ec, id, (GraphDatabaseBaseImpl) this, remoteDatabase);
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
			return localDiskStorage.getIncidenceObject(convertToLocalId(id));
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
			proxy = graphFactory.createIncidenceProxy(vc, id, (GraphDatabaseBaseImpl) this, remoteDatabase);
			ref = new WeakReference<Incidence>(proxy);
			remoteIncidences.put(id, ref);
		} 
		return proxy;
	}


	/* =====================================================
	 * Methods to access properties common for edges and vertices
	 * ===================================================== */

	
	@Override
	public long getSigma(long globalElementId) {
		int partialGraphId = getPartialGraphId(globalElementId);
		int localElementId = convertToLocalId(globalElementId);
		return getDiskStorageForPartialGraph(partialGraphId).getSigmaId(localElementId);
	}
	
	@Override
	public void setSigma(long globalElementId, long globalSigmaId) {
		int partialGraphId = getPartialGraphId(globalElementId);
		int localElementId = convertToLocalId(globalElementId);
		getDiskStorageForPartialGraph(partialGraphId).setSigmaId(localElementId, globalSigmaId);
	}

	@Override
	public int getKappa(long globalElementId) {
		int partialGraphId = getPartialGraphId(globalElementId);
		int localElementId = convertToLocalId(globalElementId);
		return getGraphDatabase(partialGraphId).getKappa(localElementId);
	}

	@Override
	public void setKappa(long globalElementId, int kappa) {
		int partialGraphId = getPartialGraphId(globalElementId);
		int localElementId = convertToLocalId(globalElementId);
		getGraphDatabase(partialGraphId).setKappa(localElementId, kappa);
	}

	

	
	
	
	/* =====================================================
	 * Methods to access vertex sequence
	 * ===================================================== */
	
	public long getVCount(long subgraphId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			RemoteGraphDatabaseAccess remoteDb = getGraphDatabase(partialGraphId);
			return remoteDb.getVCount(subgraphId);
		} else {
			return getGraphData(convertToLocalId(subgraphId)).vertexCount;
		}
	}
	
	
	public int getMaxVCount() {
		return Integer.MAX_VALUE;
	}
	
	@Override
	public long getFirstVertexId(long globalSubgraphId) {
		int partialGraphId = getPartialGraphId(globalSubgraphId);
		if (partialGraphId != localPartialGraphId) {
			RemoteGraphDatabaseAccess remoteDb = getGraphDatabase(partialGraphId);
			return remoteDb.getFirstVertexId(globalSubgraphId);
		} else {
			return getGraphData(convertToLocalId(globalSubgraphId)).firstVertexId;
		}
	}

	@Override
	public void setFirstVertexId(long globalSubgraphId, long edgeId) {
		int partialGraphId = getPartialGraphId(globalSubgraphId);
		if (partialGraphId != localPartialGraphId) {
			RemoteGraphDatabaseAccess remoteDb = getGraphDatabase(partialGraphId);
			remoteDb.setFirstVertexId(globalSubgraphId, edgeId);
		} else {
			getGraphData(convertToLocalId(globalSubgraphId)).firstVertexId = edgeId;
		}
	}

	@Override
	public long getLastVertexId(long globalSubgraphId) {
		int partialGraphId = getPartialGraphId(globalSubgraphId);
		if (partialGraphId != localPartialGraphId) {
			RemoteGraphDatabaseAccess remoteDb = getGraphDatabase(partialGraphId);
			return remoteDb.getLastVertexId(globalSubgraphId);
		} else {
			return getGraphData(convertToLocalId(globalSubgraphId)).lastVertexId;
		}
	}


	public void setLastVertexId(long globalSubgraphId, long edgeId) {
		int partialGraphId = getPartialGraphId(globalSubgraphId);
		if (partialGraphId != localPartialGraphId) {
			RemoteGraphDatabaseAccess remoteDb = getGraphDatabase(partialGraphId);
			remoteDb.setLastVertexId(globalSubgraphId, edgeId);
		} else {
			getGraphData(convertToLocalId(globalSubgraphId)).lastVertexId = edgeId;
		}
	}

	protected long getNextVertexId(long vertexId) {
		int partialGraphId = getPartialGraphId(vertexId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getNextVertexId(convertToLocalId(vertexId));
	}

	protected long getPreviousVertexId(long vertexId) {
		int partialGraphId = getPartialGraphId(vertexId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getPreviousVertexId(convertToLocalId(vertexId));	
	}

	protected void setNextVertexId(long modifiedVertexId, long nextVertexId) {
		int partialGraphId = getPartialGraphId(modifiedVertexId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		diskStore.setNextVertexId(convertToLocalId(modifiedVertexId), nextVertexId);
	}

	protected void setPreviousVertexId(long modifiedVertexId, long nextVertexId) {
		int partialGraphId = getPartialGraphId(modifiedVertexId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		diskStore.setPreviousVertexId(convertToLocalId(modifiedVertexId), nextVertexId);
	}

	
	
	
	/* =====================================================
	 * Methods to access edge sequence
	 * ===================================================== */
	
	
	
	@Override
	public long getFirstEdgeId(long globalSubgraphId) {
		int partialGraphId = getPartialGraphId(globalSubgraphId);
		if (partialGraphId != localPartialGraphId) {
			RemoteGraphDatabaseAccess remoteDb = getGraphDatabase(partialGraphId);
			return remoteDb.getFirstEdgeId(globalSubgraphId);
		} else {
			return getGraphData(convertToLocalId(globalSubgraphId)).firstEdgeId;
		}
	}

	@Override
	public void setFirstEdgeId(long globalSubgraphId, long edgeId) {
		assert getPartialGraphId(globalSubgraphId) == getPartialGraphId(edgeId);
		int partialGraphId = getPartialGraphId(globalSubgraphId);
		if (partialGraphId != localPartialGraphId) {
			RemoteGraphDatabaseAccess remoteDb = getGraphDatabase(partialGraphId);
			remoteDb.setFirstEdgeId(globalSubgraphId, edgeId);
		} else {
			getGraphData(convertToLocalId(globalSubgraphId)).firstEdgeId = edgeId;
		}
	}

	@Override
	public long getLastEdgeId(long globalSubgraphId) {
		int partialGraphId = getPartialGraphId(globalSubgraphId);
		if (partialGraphId != localPartialGraphId) {
			RemoteGraphDatabaseAccess remoteDb = getGraphDatabase(partialGraphId);
			return remoteDb.getLastEdgeId(globalSubgraphId);
		} else {
			return getGraphData(convertToLocalId(globalSubgraphId)).lastEdgeId;
		}
	}

	@Override
	public void setLastEdgeId(long globalSubgraphId, long edgeId) {
		assert getPartialGraphId(globalSubgraphId) == getPartialGraphId(edgeId);
		int partialGraphId = getPartialGraphId(globalSubgraphId);
		if (partialGraphId != localPartialGraphId) {
			RemoteGraphDatabaseAccess remoteDb = getGraphDatabase(partialGraphId);
			remoteDb.setLastEdgeId(globalSubgraphId, edgeId);
		} else {
			getGraphData(convertToLocalId(globalSubgraphId)).lastEdgeId = edgeId;
		}
	}


	@Override
	public long getNextEdgeId(long edgeId) {
		int partialGraphId = getPartialGraphId(edgeId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getNextEdgeId(convertToLocalId(edgeId));
	}
	
	protected void setNextEdgeId(long modifiedEdgeId, long nextEdgeId) {
		int partialGraphId = getPartialGraphId(modifiedEdgeId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		diskStore.setNextEdgeId(convertToLocalId(modifiedEdgeId), nextEdgeId);
	}

	@Override
	public long getPreviousEdgeId(long edgeId) {
		int partialGraphId = getPartialGraphId(edgeId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getPreviousEdgeId(convertToLocalId(edgeId));
	}

	protected void setPreviousEdgeId(long modifiedEdgeId, long nextEdgeId) {
		int partialGraphId = getPartialGraphId(modifiedEdgeId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		diskStore.setPreviousEdgeId(convertToLocalId(modifiedEdgeId), nextEdgeId);
	}
	
	
	


	
	
	
	
	

	protected boolean containsIncidenceId(long iId) {
		return getIncidenceObject(iId) != null;
	}


	@Override
	public void setFirstIncidenceId(long elementId, long incidenceId) {
		int partialGraphId = getPartialGraphId(elementId);
		getDiskStorageForPartialGraph(partialGraphId).setFirstIncidenceId(convertToLocalId(elementId), incidenceId);
	}


	@Override
	public void setLastIncidenceId(long elementId, long incidenceId) {
		int partialGraphId = getPartialGraphId(elementId);
		getDiskStorageForPartialGraph(partialGraphId).setLastIncidenceId(convertToLocalId(elementId), incidenceId);
	}


	@Override
	public void setNextIncidenceIdAtVertexId(long globalIncidenceId, long nextIncidenceId) {
		int partialGraphId = getPartialGraphId(globalIncidenceId);
		getDiskStorageForPartialGraph(partialGraphId).setNextIncidenceAtVertexId(convertToLocalId(globalIncidenceId), nextIncidenceId);
	}


	@Override
	public void setPreviousIncidenceIdAtVertexId(long globalIncidenceId, long nextIncidenceId) {
		int partialGraphId = getPartialGraphId(globalIncidenceId);
		getDiskStorageForPartialGraph(partialGraphId).setPreviousIncidenceAtVertexId(convertToLocalId(globalIncidenceId), nextIncidenceId);
		
	}

	@Override
	public void setNextIncidenceIdAtEdgeId(long globalIncidenceId, long nextIncidenceId) {
		int partialGraphId = getPartialGraphId(globalIncidenceId);
		getDiskStorageForPartialGraph(partialGraphId).setNextIncidenceAtEdgeId(convertToLocalId(globalIncidenceId), nextIncidenceId);
	}

	@Override
	public void setPreviousIncidenceIdAtEdgeId(long globalIncidenceId, long nextIncidenceId) {
		int partialGraphId = getPartialGraphId(globalIncidenceId);
		getDiskStorageForPartialGraph(partialGraphId).setPreviousIncidenceAtEdgeId(convertToLocalId(globalIncidenceId), nextIncidenceId);
	}
	
//	@Override
//	public void setDirection(long globalIncidenceId, Direction dir) {
//		int partialGraphId = getPartialGraphId(globalIncidenceId);
//		getDiskStorageForPartialGraph(partialGraphId).setDirection(convertToLocalId(globalIncidenceId), dir);
//	}

}