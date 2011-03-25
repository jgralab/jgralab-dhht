package de.uni_koblenz.jgralab.impl;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.GraphStructureChangedListener;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLabList;
import de.uni_koblenz.jgralab.JGraLabMap;
import de.uni_koblenz.jgralab.JGraLabSet;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;

public abstract class CompleteOrPartialGraphImpl extends GraphBaseImpl {

	protected CompleteOrPartialGraphImpl(GraphClass cls) {
		super(cls);
		schema = cls.getSchema();
		graphVersion = -1;
		setGraphVersion(0);
	}


	// ------------- GRAPH VARIABLES -------------
	
	/**
	 * The id of this complete or partial graph identifying it in the complete graph  
	 */
	protected int id;
	
	
	/**
	 * The GraphFactory that was used to create this graph. This factory will be
	 * used to create vertices and edges in this graph.
	 */
	protected GraphFactory graphFactory;
	
	@Override
	public GraphFactory getGraphFactory() {
		return graphFactory;
	}

	/**
	 * Holds the version of the graph, for every modification (e.g. adding a
	 * vertex or edge or changing the vertex or edge sequence or changing of an
	 * attribute value), this version number is increased by 1, It is saved in
	 * the tg-file.
	 */
	protected long graphVersion;
	
	@Override
	public long getGraphVersion() {
		return graphVersion;
	}
	
	/**
	 * Sets the version counter of this graph. Should only be called immediately
	 * after loading and in graphModified.
	 * 
	 * @param graphVersion
	 *            new version value
	 */
	public void setGraphVersion(long graphVersion) {
		this.graphVersion = graphVersion;
	}

	
	/**
	 * The schema this graph belongs to
	 */
	protected final Schema schema;
	
	@Override
	public Schema getSchema() {
		return schema;
	}

	/**
	 * Indicates if this graph is currently loading.
	 */
	private boolean loading;
	

	
	// ------------- VERTEX LIST VARIABLES -------------
	/**
	 * maximum number of vertices
	 */
	protected int vMax;

	/**
	 * current number of vertices
	 */
	private int vCount;
	
	/**
	 * free index list for vertices
	 */
	protected FreeIndexList freeVertexList;
	
	/**
	 * array of vertices
	 */
	private VertexImpl[] vertexArray;

	private VertexImpl firstVertex;

	private VertexImpl lastVertex;
	
	/**
	 * Holds the version of the vertex sequence. For every modification (e.g.
	 * adding/deleting a vertex or changing the vertex sequence) this version
	 * number is increased by 1. It is set to 0 when the graph is loaded.
	 */
	protected long vertexListVersion;
	
	
	// ------------- EDGE LIST VARIABLES -------------

	
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

	/**
	 * array of edges
	 */
	private EdgeImpl[] edgeArray;
	
	private EdgeImpl firstEdge;

	private EdgeImpl lastEdge;
	
	/**
	 * Holds the version of the edge sequence. For every modification (e.g.
	 * adding/deleting an edge or changing the edge sequence) this version
	 * number is increased by 1. It is set to 0 when the graph is loaded.
	 */
	protected long edgeListVersion;

	
	// ------------- INCIDENCE LIST VARIABLES -------------


	/**
	 * maximal number of incidences
	 */
	private int iMax;

	/**
	 * current number of incidences
	 */
	private int iCount;

	/**
	 * free index list for vertices
	 */
	protected FreeIndexList freeIncidenceList;
	
	/**
	 * array of incidences
	 */
	private IncidenceImpl[] incidenceArray;



	
	/**
	 * Use to allocate a <code>Edge</code>-index.
	 * 
	 * @param currentId
	 *            needed for transaction support
	 */
	protected int allocateEdgeIndex(int currentId) {
		int eId = freeEdgeList.allocateIndex();
		if (eId == 0) {
			expandEdgeArray(getExpandedEdgeCount());
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
		if (iId == 0) {
			expandIncidenceArray(getExpandedIncidenceCount());
			iId = freeIncidenceList.allocateIndex();
		}
		return iId;
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
			expandVertexArray(getExpandedVertexCount());
			vId = freeVertexList.allocateIndex();
		}
		return vId;
	}
	
	public boolean containsVertexLocally(Vertex v) {
		return (v != null) && (v.getGraph() == this) && (getVertexArray()[((VertexImpl) v).id] == v);
	}

	public boolean containsEdgeLocally(Edge e) {
		return (e != null) && (e.getGraph() == this) && (getEdgeArray()[((EdgeImpl) e).id] == e);
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
	 * @throws GraphException
	 *             an edge with same id already exists in graph, id of edge
	 *             greater than possible count of edges in graph
	 */
	protected void addEdge(Edge newEdge) {
		assert newEdge != null;
		assert (newEdge.getSchema() == schema) : "The schemas of newEdge and this graph don't match!";
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
				if (eId > eMax) {
					throw new GraphException("edge id " + e.getId()
							+ " is bigger than eSize");
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

		appendVertexToVSeq(v);

		if (!isLoading()) {
			vertexListModified();
			internalVertexAdded(v);
		}
	}
	
	/**
	 * Checks if the edge id eId is valid and if there is an such an edge locally in
	 * this graph.
	 * 
	 * @param eId
	 *            an edge id
	 * @return true if this graph contains an edge with id eId
	 */
	protected final boolean containsEdgeId(int eId) {
		return (eId > 0) && (eId <= eMax) && (getEdgeArray()[eId] != null);
	}

	/**
	 * Checks if the incidence id iId is valid and if there is an such an
	 * incidence locally in this graph.
	 * 
	 * @param iId
	 *            a incidence id
	 * @return true if this graph contains an incidence with id iId
	 */
	private final boolean containsIncidenceId(int iId) {
		return (iId > 0) && (iId <= vMax) && (getIncidenceArray()[iId] != null);
	}


	
	/**
	 * Checks if the vertex id vId is valid and if there is an such a vertex locally in
	 * this graph.
	 * 
	 * @param vId
	 *            a vertex id
	 * @return true if this graph contains a vertex with id vId
	 */
	protected final boolean containsVertexId(int vId) {
		return (vId > 0) && (vId <= vMax) && (getVertexArray()[vId] != null);
	}
	
	
	/**
	 * Appends the edge e to the global edge sequence of this graph.
	 * 
	 * @param e
	 *            an edge
	 */
	protected void appendEdgeToESeq(EdgeImpl e) {
		getEdgeArray()[e.id] = e;
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

	/**
	 * Appends the vertex v to the global vertex sequence of this graph.
	 * 
	 * @param v
	 *            a vertex
	 */
	protected void appendVertexToVSeq(VertexImpl v) {
		getVertexArray()[v.id] = v;
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

	protected boolean canAddGraphElement(int graphElementId) {
		return graphElementId == 0;
	}
	
	
	@Override
	public Graph getView(int kappa) {
		return graphFactory.createViewGraph(this, kappa);
	}

	@Override
	public boolean isLoading() {
		return loading;
	}

	@Override
	public void loadingCompleted() {

	}


	/**
	 * Sets the loading flag.
	 * 
	 * @param isLoading
	 */
	public void setLoading(boolean isLoading) {
		loading = isLoading;
	}
	
	@Override
	protected void setVCount(int count) {
		vCount = count;
	}

	protected void setVertexArray(VertexImpl[] vertex) {
		this.vertexArray = vertex;
	}


	protected void setVertexListVersion(long vertexListVersion) {
		this.vertexListVersion = vertexListVersion;
	}
	
	protected void setIncidenceArray(IncidenceImpl[] incidenceArray) {
		this.incidenceArray = incidenceArray;
	}

	@Override
	protected void setLastEdge(EdgeImpl lastEdge) {
		this.lastEdge = lastEdge;
	}

	@Override
	protected void setLastVertex(VertexImpl lastVertex) {
		this.lastVertex = lastVertex;
	}
	
	@Override
	protected void setECount(int count) {
		eCount = count;
	}


	protected void setEdgeArray(EdgeImpl[] edge) {
		this.edgeArray = edge;
	}


	protected void setEdgeListVersion(long edgeListVersion) {
		this.edgeListVersion = edgeListVersion;
	}

	@Override
	protected void setFirstEdge(EdgeImpl firstEdge) {
		this.firstEdge = firstEdge;
	}

	@Override
	protected void setFirstVertex(VertexImpl firstVertex) {
		this.firstVertex = firstVertex;
	}


	protected void setFreeEdgeList(FreeIndexList freeEdgeList) {
		this.freeEdgeList = freeEdgeList;
	}

	protected void setFreeIncidenceList(FreeIndexList freeIndexList) {
		this.freeIncidenceList = freeIndexList;
	}

	protected void setFreeVertexList(FreeIndexList freeVertexList) {
		this.freeVertexList = freeVertexList;
	}


	protected void setICount(int count) {
		iCount = count;
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
	
	
	protected VertexImpl[] getVertexArray() {
		return vertexArray;
	}


	@Override
	public long getVertexListVersion() {
		return vertexListVersion;
	}
	
	@Override
	public int getVCount() {
		return vCount;
	}
	
	protected IncidenceImpl[] getIncidenceArray() {
		return incidenceArray;
	}
	
	@Override
	public Edge getLastEdge() {
		return lastEdge;
	}
	
	@Override
	public Vertex getLastVertex() {
		return lastVertex;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Graph#getExpandedVertexCount()
	 */
	public int getExpandedVertexCount() {
		return computeNewSize(vMax);
	}

	@Override
	public Edge getFirstEdge() {
		return firstEdge;
	}

	@Override
	public Vertex getFirstVertex() {
		return firstVertex;
	}

	protected FreeIndexList getFreeEdgeList() {
		return freeEdgeList;
	}

	abstract protected FreeIndexList getFreeIncidenceList();

	protected FreeIndexList getFreeVertexList() {
		return freeVertexList;
	}

	@Override
	public int getICount() {
		return iCount;
	}
	
	protected EdgeImpl[] getEdgeArray() {
		return edgeArray;
	}
	
	@Override
	public long getEdgeListVersion() {
		return edgeListVersion;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Graph#getExpandedEdgeCount()
	 */
	public int getExpandedEdgeCount() {
		return computeNewSize(eMax);
	}
	
	protected int getExpandedIncidenceCount() {
		return computeNewSize(iMax);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Graph#getMaxECount()
	 */
	@Override
	public int getMaxECount() {
		return eMax;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Graph#getMaxVCount()
	 */
	@Override
	public int getMaxVCount() {
		return vMax;
	}

	@Override
	public int getECount() {
		return eCount;
	}

	
	/**
	 * Use to free an <code>Edge</code>-index
	 * 
	 * @param index
	 */
	protected void freeEdgeIndex(int index) {
		freeEdgeList.freeIndex(index);
	}

	/**
	 * Use to free a <code>Vertex</code>-index.
	 * 
	 * @param index
	 */
	protected void freeVertexIndex(int index) {
		freeVertexList.freeIndex(index);
	}
	
	/**
	 * Changes the size of the edge array of this graph to newSize.
	 * 
	 * @param newSize
	 *            the new size of the edge array
	 */
	protected void expandEdgeArray(int newSize) {
		if (newSize <= eMax) {
			throw new GraphException("newSize must be > eSize: eSize=" + eMax
					+ ", newSize=" + newSize);
		}

		EdgeImpl[] e = new EdgeImpl[newSize + 1];
		if (getEdgeArray() != null) {
			System.arraycopy(getEdgeArray(), 0, e, 0, getEdgeArray().length);
		}
		setEdgeArray(e);

		if (getFreeEdgeList() == null) {
			setFreeEdgeList(new FreeIndexList(newSize));
		} else {
			getFreeEdgeList().expandBy(newSize - eMax);
		}

		eMax = newSize;
		notifyMaxEdgeCountIncreased(newSize);
	}

	// handle GraphStructureChangedListener

	/**
	 * Changes the size of the incidence array of this graph to newSize.
	 * 
	 * @param newSize
	 *            the new size of the incidence array
	 */
	protected void expandIncidenceArray(int newSize) {
		if (newSize <= iMax) {
			throw new GraphException("newSize must > iSize: iSize=" + iMax
					+ ", newSize=" + newSize);
		}
		IncidenceImpl[] expandedArray = new IncidenceImpl[newSize + 1];
		if (getIncidenceArray() != null) {
			System.arraycopy(getIncidenceArray(), 0, expandedArray, 0,
					getIncidenceArray().length);
		}
		if (getFreeIncidenceList() == null) {
			setFreeIncidenceList(new FreeIndexList(newSize));
		} else {
			getFreeIncidenceList().expandBy(newSize - vMax);
		}
		setIncidenceArray(expandedArray);
		iMax = newSize;
		notifyMaxIncidenceCountIncreased(newSize);
	}

	/**
	 * Changes the size of the vertex array of this graph to newSize.
	 * 
	 * @param newSize
	 *            the new size of the vertex array
	 */
	protected void expandVertexArray(int newSize) {
		if (newSize <= vMax) {
			throw new GraphException("newSize must > vSize: vSize=" + vMax
					+ ", newSize=" + newSize);
		}
		VertexImpl[] expandedArray = new VertexImpl[newSize + 1];
		if (getVertexArray() != null) {
			System.arraycopy(getVertexArray(), 0, expandedArray, 0,
					getVertexArray().length);
		}
		if (getFreeVertexList() == null) {
			setFreeVertexList(new FreeIndexList(newSize));
		} else {
			getFreeVertexList().expandBy(newSize - vMax);
		}
		setVertexArray(expandedArray);
		vMax = newSize;
		notifyMaxVertexCountIncreased(newSize);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Graph#defragment()
	 */
	@Override
	public void defragment() {
		// defragment vertex array
		if (getVCount() < vMax) {
			if (getVCount() > 0) {
				int vId = vMax;
				while (getFreeVertexList().isFragmented()) {
					while ((vId >= 1) && (getVertexArray()[vId] == null)) {
						--vId;
					}
					assert vId >= 1;
					VertexImpl v = getVertexArray()[vId];
					getVertexArray()[vId] = null;
					getFreeVertexList().freeIndex(vId);
					int newId = allocateVertexIndex(vId);
					assert newId < vId;
					v.setId(newId);
					getVertexArray()[newId] = v;
					--vId;
				}
			}
			int newVMax = getVCount() == 0 ? 1 : getVCount();
			if (newVMax != vMax) {
				vMax = newVMax;
				VertexImpl[] newVertex = new VertexImpl[vMax + 1];
				System.arraycopy(getVertexArray(), 0, newVertex, 0,
						newVertex.length);
				setVertexArray(newVertex);
			}
			graphModified();
			System.gc();
		}
		// defragment edge array
		if (getECount() < eMax) {
			if (getECount() > 0) {
				int eId = eMax;
				while (getFreeEdgeList().isFragmented()) {
					while ((eId >= 1) && (getEdgeArray()[eId] == null)) {
						--eId;
					}
					assert eId >= 1;
					EdgeImpl e = getEdgeArray()[eId];
					getEdgeArray()[eId] = null;
					getFreeEdgeList().freeIndex(eId);
					int newId = allocateEdgeIndex(eId);
					assert newId < eId;
					e.setId(newId);
					getEdgeArray()[newId] = e;
					--eId;
				}
			}
			int newEMax = getECount() == 0 ? 1 : getECount();
			if (newEMax != eMax) {
				eMax = newEMax;
				EdgeImpl[] newEdge = new EdgeImpl[eMax + 1];
				System.arraycopy(getEdgeArray(), 0, newEdge, 0, newEdge.length);
				setEdgeArray(newEdge);
				System.gc();
			}
			graphModified();
			System.gc();
		}

		if (getICount() < iMax) {
			if (getICount() > 0) {
				int iId = iMax;
				while (getFreeEdgeList().isFragmented()) {
					while ((iId >= 1) && (getIncidenceArray()[iId] == null)) {
						--iId;
					}
					assert iId >= 1;
					IncidenceImpl i = getIncidenceArray()[iId];
					getIncidenceArray()[iId] = null;
					getFreeIncidenceList().freeIndex(iId);
					int newId = allocateIncidenceIndex(iId);
					assert newId < iId;
					i.setId(newId);
					getIncidenceArray()[newId] = i;
					// getRevEdge()[newId] = r;
					--iId;
				}
			}
			int newIMax = getICount() == 0 ? 1 : getICount();
			if (newIMax != iMax) {
				iMax = newIMax;
				IncidenceImpl[] newIncidence = new IncidenceImpl[iMax + 1];
				System.arraycopy(getIncidenceArray(), 0, newIncidence, 0,
						newIncidence.length);
				setIncidenceArray(newIncidence);
				System.gc();
			}
			graphModified();
			System.gc();
		}
	}
	
	@Override
	public <T> JGraLabList<T> createList() {
		return new JGraLabListImpl<T>();
	}

	@Override
	public <T> JGraLabList<T> createList(Collection<? extends T> collection) {
		return new JGraLabListImpl<T>(collection);
	}

	@Override
	public <T> JGraLabList<T> createList(int initialCapacity) {
		return new JGraLabListImpl<T>(initialCapacity);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap() {
		return new JGraLabMapImpl<K, V>();
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity) {
		return new JGraLabMapImpl<K, V>(initialCapacity);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity,
			float loadFactor) {
		return new JGraLabMapImpl<K, V>(initialCapacity, loadFactor);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(Map<? extends K, ? extends V> map) {
		return new JGraLabMapImpl<K, V>(map);
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
	public <T extends Record> T createRecord(Class<T> recordClass,
			Map<String, Object> fields) {
		T record = graphFactory.createRecord(recordClass, this);
		record.setComponentValues(fields);
		return record;
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Object... components) {
		T record = graphFactory.createRecord(recordClass, this);
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

	@Override
	public boolean containsVertex(Vertex v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsEdge(Edge e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deleteVertex(Vertex v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteEdge(Edge e) {
		// TODO Auto-generated method stub

	}

	@Override
	public Vertex getVertex(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Edge getEdge(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * Computes new size of vertex and edge array depending on the current size.
	 * Up to 256k elements, the size is doubled. Between 256k and 1M elements,
	 * 256k elements are added. Beyond 1M, increase is 128k elements.
	 * 
	 * @param n
	 *            current size
	 * @return new size
	 */
	private int computeNewSize(int n) {
		return n >= 1048576 ? n + 131072 : n >= 262144 ? n + 262144 : n + n;
	}





	/**
	 * Notifies all registered <code>GraphStructureChangedListener</code> that
	 * the maximum edge count has been increased to the given
	 * <code>newValue</code>. All invalid <code>WeakReference</code>s are
	 * deleted automatically from the internal listener list.
	 * 
	 * @param newValue
	 *            the new maximum edge count.
	 */
	protected void notifyMaxEdgeCountIncreased(int newValue) {
		if (graphStructureChangedListenersWithAutoRemoval != null) {
			Iterator<WeakReference<GraphStructureChangedListener>> iterator = getListenerListIteratorForAutoRemove();
			while (iterator.hasNext()) {
				GraphStructureChangedListener currentListener = iterator.next()
						.get();
				if (currentListener == null) {
					iterator.remove();
				} else {
					currentListener.maxEdgeCountIncreased(newValue);
				}
			}
			setAutoListenerListToNullIfEmpty();
		}
		int n = graphStructureChangedListeners.size();
		for (int i = 0; i < n; i++) {
			graphStructureChangedListeners.get(i).maxEdgeCountIncreased(
					newValue);
		}
	}



	/**
	 * Notifies all registered <code>GraphStructureChangedListener</code> that
	 * the maximum incidence count has been increased to the given
	 * <code>newValue</code>. All invalid <code>WeakReference</code>s are
	 * deleted automatically from the internal listener list.
	 * 
	 * @param newValue
	 *            the new maximum incidence count.
	 */
	protected void notifyMaxIncidenceCountIncreased(int newValue) {
		if (graphStructureChangedListenersWithAutoRemoval != null) {
			Iterator<WeakReference<GraphStructureChangedListener>> iterator = getListenerListIteratorForAutoRemove();
			while (iterator.hasNext()) {
				GraphStructureChangedListener currentListener = iterator.next()
						.get();
				if (currentListener == null) {
					iterator.remove();
				} else {
					currentListener.maxIncidenceCountIncreased(newValue);
				}
			}
			setAutoListenerListToNullIfEmpty();
		}
		int n = graphStructureChangedListeners.size();
		for (int i = 0; i < n; i++) {
			graphStructureChangedListeners.get(i).maxIncidenceCountIncreased(
					newValue);
		}
	}

	/**
	 * Notifies all registered <code>GraphStructureChangedListener</code> that
	 * the maximum vertex count has been increased to the given
	 * <code>newValue</code>. All invalid <code>WeakReference</code>s are
	 * deleted automatically from the internal listener list.
	 * 
	 * @param newValue
	 *            the new maximum vertex count.
	 */
	protected void notifyMaxVertexCountIncreased(int newValue) {
		if (graphStructureChangedListenersWithAutoRemoval != null) {
			Iterator<WeakReference<GraphStructureChangedListener>> iterator = getListenerListIteratorForAutoRemove();
			while (iterator.hasNext()) {
				GraphStructureChangedListener currentListener = iterator.next()
						.get();
				if (currentListener == null) {
					iterator.remove();
				} else {
					currentListener.maxVertexCountIncreased(newValue);
				}
			}
			setAutoListenerListToNullIfEmpty();
		}
		int n = graphStructureChangedListeners.size();
		for (int i = 0; i < n; i++) {
			graphStructureChangedListeners.get(i).maxVertexCountIncreased(
					newValue);
		}
	}
	
	public Graph getViewedGraph() {
		return this;
	}
}
