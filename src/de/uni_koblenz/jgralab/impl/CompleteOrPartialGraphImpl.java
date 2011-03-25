package de.uni_koblenz.jgralab.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.JGraLabList;
import de.uni_koblenz.jgralab.JGraLabMap;
import de.uni_koblenz.jgralab.JGraLabSet;
import de.uni_koblenz.jgralab.NoSuchAttributeException;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;

public abstract class CompleteOrPartialGraphImpl extends GraphBaseImpl {

	protected CompleteOrPartialGraphImpl(String id, GraphClass cls) {
		super(id, cls);
		schema = cls.getSchema();
	}

	// ------------- GRAPH VARIABLES -------------
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
	
	/**
	 * The schema this graph belongs to
	 */
	private final Schema schema;
	
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
	private long vertexListVersion;
	
	
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
	private long edgeListVersion;

	
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

	@Override
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

	@Override
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


	@Override
	public void readAttributeValueFromString(String attributeName, String value)
			throws GraphIOException, NoSuchAttributeException {
		// TODO Auto-generated method stub

	}

	@Override
	public String writeAttributeValueToString(String attributeName)
			throws IOException, GraphIOException, NoSuchAttributeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeAttributeValues(GraphIO io) throws IOException,
			GraphIOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void readAttributeValues(GraphIO io) throws GraphIOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getAttribute(String name) throws NoSuchAttributeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(String name, Object data)
			throws NoSuchAttributeException {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<? extends Graph> getM1Class() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphClass getType() {
		// TODO Auto-generated method stub
		return null;
	}



}
