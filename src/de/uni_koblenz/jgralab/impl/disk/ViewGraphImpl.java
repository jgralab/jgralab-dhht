package de.uni_koblenz.jgralab.impl.disk;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.GraphStructureChangedListener;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLabList;
import de.uni_koblenz.jgralab.JGraLabMap;
import de.uni_koblenz.jgralab.JGraLabSet;
import de.uni_koblenz.jgralab.NoSuchAttributeException;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.EdgeIterable;
import de.uni_koblenz.jgralab.impl.VertexIterable;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * Realizes a view on a complete, subordinate or partial graph defined by an
 * integer value specifying the lowest level of elements visible in this view.
 * 
 * Implemented by delegation, e.g. all methods delegate to the graph this view
 * is an abstraction of
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class ViewGraphImpl implements Graph,
		GraphStructureChangedListener {

	/**
	 * the lowest kappa level of the elements which should be visible in this
	 * graph
	 */
	private final int lowestVisibleKappaLevel;

	/**
	 * The Graph viewed by this view
	 */
	private final Graph viewedGraph;

	private int eCount;
	private int iCount;
	private int vCount;

	/**
	 * Creates a new view graph of the graph <code>viewedGraph</code>, all
	 * elements of that graph (which may be a view, too) whose kappa-values are
	 * equal or above <code>lowestVisibleKappaLevel</code> are visible in this
	 * view, while all others are hidden.
	 * 
	 * @param viewedGraph
	 *            the graph to be viewed
	 * @param lowestVisibleKappaLevel
	 *            the loweset kappa level of visible elements
	 */
	public ViewGraphImpl(Graph viewedGraph, int lowestVisibleKappaLevel)
			 {
		this.lowestVisibleKappaLevel = lowestVisibleKappaLevel;
		this.viewedGraph = viewedGraph;
		setECount(viewedGraph.getECount());
		setICount(viewedGraph.getICount());
		setVCount(viewedGraph.getVCount());
		viewedGraph.addGraphStructureChangedListener(this);
	}

	protected void setECount(int eCount) {
		this.eCount = eCount;
	}

	protected void setICount(int iCount) {
		this.iCount = iCount;
	}

	protected void setVCount(int vCount) {
		this.vCount = vCount;
	}

	/**
	 * Returns a new view on this view, implemented and not delegated to avoid
	 * multi-delegation and unnecessary overhead
	 */
	@Override
	public de.uni_koblenz.jgralab.impl.mem.ViewGraphImpl getView(int level) {
		if (level < lowestVisibleKappaLevel) {
			level = lowestVisibleKappaLevel;
		}
		return ((GraphBaseImpl) viewedGraph).getGraphFactory().createViewGraph(
				this, level);
	}

	@Override
	public void readAttributeValueFromString(String attributeName, String value)
			throws GraphIOException, NoSuchAttributeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String writeAttributeValueToString(String attributeName)
			throws IOException, GraphIOException, NoSuchAttributeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeAttributeValues(GraphIO io) throws IOException,
			GraphIOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void readAttributeValues(GraphIO io) throws GraphIOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getAttribute(String name) throws NoSuchAttributeException, RemoteException {
		return viewedGraph.getAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object data)
			throws NoSuchAttributeException, RemoteException {
		viewedGraph.setAttribute(name, data);
	}

	@Override
	public void initializeAttributesWithDefaultValues() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<? extends Graph> getM1Class() {
		return viewedGraph.getM1Class();
	}

	@Override
	public GraphClass getType() {
		return viewedGraph.getType();
	}

	@Override
	public GraphClass getGraphClass() {
		return viewedGraph.getGraphClass();
	}

	@Override
	public Schema getSchema() {
		return viewedGraph.getSchema();
	}

	@Override
	public int compareTo(Graph arg0) {
			if (viewedGraph == arg0 || getCompleteGraph() == arg0) {
				// each graph is smaller than the complete graph
				return -1;
			} else if (arg0.getViewedGraph() != arg0) {
				// this is a ViewGraphImpl
				// the ViewGraphImpl with smaller lowestVisibleKappaLevel is
				// greater
				return ((ViewGraphImpl) arg0).lowestVisibleKappaLevel
						- lowestVisibleKappaLevel;
			} else {
				// arg0 is a subordinate graph or a partial graph
				return viewedGraph.compareTo(arg0);
			}
	}

	@Override
	public Graph getViewedGraph() {
		return viewedGraph;
	}

	@Override
	public GraphElement<?, ?, ?> getContainingElement() {
		return null;
	}

	@Override
	public Graph getCompleteGraph() {
		return viewedGraph.getCompleteGraph();
	}

	@Override
	public Graph getTraversalContext() {
		return viewedGraph.getTraversalContext();
	}

	@Override
	public void useAsTraversalContext() {
		((GraphBaseImpl) viewedGraph).setTraversalContext(this);
	}

	@Override
	public void releaseTraversalContext() {
		viewedGraph.releaseTraversalContext();
	}

	@Override
	public <T extends Vertex> T createVertex(Class<T> cls)
			 {
		return viewedGraph.createVertex(cls);
	}

	@Override
	public <T extends Edge> T createEdge(Class<T> cls) {
		return viewedGraph.createEdge(cls);
	}

	@Override
	public <T extends Incidence> T connect(Class<T> cls, Vertex vertex,
			Edge edge) {
		return viewedGraph.connect(cls, vertex, edge);
	}

	@Override
	public <T extends BinaryEdge> T createEdge(Class<T> cls, Vertex alpha,
			Vertex omega) {
		return viewedGraph.createEdge(cls, alpha, omega);
	}

	@Override
	public boolean isLoading() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void loadingCompleted() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isGraphModified(long previousVersion) {
		return viewedGraph.isGraphModified(previousVersion);
	}

	@Override
	public long getGraphVersion() {
		return viewedGraph.getGraphVersion();
	}

	@Override
	public boolean isVertexListModified(long previousVersion)
			 {
		return viewedGraph.isVertexListModified(previousVersion);
	}

	@Override
	public long getVertexListVersion() {
		return viewedGraph.getVertexListVersion();
	}

	@Override
	public boolean isEdgeListModified(long edgeListVersion)
			 {
		return viewedGraph.isEdgeListModified(edgeListVersion);
	}

	@Override
	public long getEdgeListVersion() {
		return viewedGraph.getEdgeListVersion();
	}

	@Override
	public boolean containsVertex(Vertex v) {
		return v.isVisible(lowestVisibleKappaLevel)
				&& viewedGraph.containsVertex(v);
	}

	@Override
	public boolean containsEdge(Edge e) {
		return e.isVisible(lowestVisibleKappaLevel)
				&& viewedGraph.containsEdge(e);
	}

	@Override
	public void deleteVertex(Vertex v) {
		if (containsVertex(v)) {
			viewedGraph.deleteVertex(v);
		} else {
			throw new GraphException(
					"The view with lowest visible kappa value "
							+ lowestVisibleKappaLevel
							+ " does not contain vertex " + v.getId() + ".");
		}
	}

	@Override
	public void deleteEdge(Edge e) {
		if (containsEdge(e)) {
			viewedGraph.deleteEdge(e);
		} else {
			throw new GraphException(
					"The view with lowest visible kappa value "
							+ lowestVisibleKappaLevel
							+ " does not contain edge " + e.getId() + ".");
		}
	}

	@Override
	public Vertex getFirstVertex() {
		Vertex v = viewedGraph.getFirstVertex();
		return v == null || containsVertex(v) ? v : v.getNextVertex(this);
	}

	@Override
	public Vertex getLastVertex() {
		Vertex v = viewedGraph.getLastVertex();
		return v == null || containsVertex(v) ? v : v.getPreviousVertex(this);
	}

	@Override
	public Vertex getFirstVertex(VertexClass vertexClass)
			 {
		assert vertexClass != null;
		return getFirstVertex(vertexClass.getM1Class(), false);
	}

	@Override
	public Vertex getFirstVertex(VertexClass vertexClass, boolean noSubclasses)
			 {
		assert vertexClass != null;
		return getFirstVertex(vertexClass.getM1Class(), noSubclasses);
	}

	@Override
	public Vertex getFirstVertex(Class<? extends Vertex> vertexClass)
			 {
		assert vertexClass != null;
		return getFirstVertex(vertexClass, false);
	}

	@Override
	public Vertex getFirstVertex(Class<? extends Vertex> vertexClass,
			boolean noSubclasses) {
		assert vertexClass != null;
		Vertex firstVertex = getFirstVertex();
		if (firstVertex == null) {
			return null;
		}
		if (noSubclasses) {
			if (vertexClass == firstVertex.getM1Class()) {
				return firstVertex;
			}
		} else {
			if (vertexClass.isInstance(firstVertex)) {
				return firstVertex;
			}
		}
		return firstVertex.getNextVertex(this, vertexClass, noSubclasses);
	}

	@Override
	public Edge getFirstEdge() {
		Edge e = viewedGraph.getFirstEdge();
		return e == null || containsEdge(e) ? e : e.getNextEdge(this);
	}

	@Override
	public Edge getLastEdge() {
		Edge e = viewedGraph.getLastEdge();
		return e == null || containsEdge(e) ? e : e.getPreviousEdge(this);
	}

	@Override
	public Edge getFirstEdge(EdgeClass edgeClass) {
		return getFirstEdge(edgeClass.getM1Class(), false);
	}

	@Override
	public Edge getFirstEdge(EdgeClass edgeClass, boolean noSubclasses)
			 {
		return getFirstEdge(edgeClass.getM1Class(), noSubclasses);
	}

	@Override
	public Edge getFirstEdge(Class<? extends Edge> edgeClass)
			 {
		return getFirstEdge(edgeClass, false);
	}

	@Override
	public Edge getFirstEdge(Class<? extends Edge> edgeClass,
			boolean noSubclasses) {
		assert edgeClass != null;
		Edge currentEdge = getFirstEdge();
		while (currentEdge != null) {
			if (noSubclasses) {
				if (edgeClass == currentEdge.getM1Class()) {
					return currentEdge;
				}
			} else {
				if (edgeClass.isInstance(currentEdge)) {
					return currentEdge;
				}
			}
			currentEdge = currentEdge.getNextEdge(this);
		}
		return null;
	}

	@Override
	public Vertex getVertex(long id) {
		Vertex v = viewedGraph.getVertex(id);
		if (v.isVisible(lowestVisibleKappaLevel)) {
			return v;
		} else {
			return null;
		}
	}

	@Override
	public Edge getEdge(long id) {
		Edge e = viewedGraph.getEdge(id);
		if (e.isVisible(lowestVisibleKappaLevel)) {
			return e;
		} else {
			return null;
		}
	}

	@Override
	public int getMaxVCount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMaxECount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getVCount() {
		return vCount;
	}

	@Override
	public int getECount() {
		return eCount;
	}

	@Override
	public int getICount() {
		return iCount;
	}


	@Override
	public Iterable<Edge> getEdges() {
		return new EdgeIterable<Edge>(this);
	}

	@Override
	public Iterable<Edge> getEdges(EdgeClass edgeClass) {
		return new EdgeIterable<Edge>(this, edgeClass.getM1Class());
	}

	@Override
	public Iterable<Edge> getEdges(Class<? extends Edge> edgeClass)
			 {
		return new EdgeIterable<Edge>(this, edgeClass);
	}

	@Override
	public Iterable<Vertex> getVertices() {
		return new VertexIterable<Vertex>(this);
	}

	@Override
	public Iterable<Vertex> getVertices(VertexClass vertexclass)
			 {
		return new VertexIterable<Vertex>(this, vertexclass.getM1Class());
	}

	@Override
	public Iterable<Vertex> getVertices(Class<? extends Vertex> vertexClass)
			 {
		return new VertexIterable<Vertex>(this, vertexClass);
	}

	@Override
	public void defragment() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> JGraLabList<T> createList() {
		return viewedGraph.createList();
	}

	@Override
	public <T> JGraLabList<T> createList(Collection<? extends T> collection)
			 {
		return viewedGraph.createList(collection);
	}

	@Override
	public <T> JGraLabList<T> createList(int initialCapacity)
			 {
		return viewedGraph.createList(initialCapacity);
	}

	@Override
	public <T> JGraLabSet<T> createSet() {
		return viewedGraph.createSet();
	}

	@Override
	public <T> JGraLabSet<T> createSet(Collection<? extends T> collection)
			 {
		return viewedGraph.createSet(collection);
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity)
			 {
		return viewedGraph.createSet(initialCapacity);
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity, float loadFactor)
			 {
		return viewedGraph.createSet(initialCapacity, loadFactor);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap() {
		return viewedGraph.createMap();
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(Map<? extends K, ? extends V> map)
			 {
		return viewedGraph.createMap(map);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity)
			 {
		return viewedGraph.createMap(initialCapacity);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity,
			float loadFactor) {
		return viewedGraph.createMap(initialCapacity, loadFactor);
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io)
			 {
		return viewedGraph.createRecord(recordClass, io);
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Map<String, Object> fields) {
		return viewedGraph.createRecord(recordClass, fields);
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Object... components) {
		return viewedGraph.createRecord(recordClass, components);
	}

	@Override
	public void sortVertices(Comparator<Vertex> comp) {
		viewedGraph.sortVertices(comp);
	}

	@Override
	public void sortEdges(Comparator<Edge> comp) {
		viewedGraph.sortEdges(comp);
	}

	@Override
	public void addGraphStructureChangedListener(
			GraphStructureChangedListener newListener) {
		viewedGraph.addGraphStructureChangedListener(newListener);
	}

	@Override
	public void removeGraphStructureChangedListener(
			GraphStructureChangedListener listener) {
		viewedGraph.removeGraphStructureChangedListener(listener);
	}

	@Override
	public void removeAllGraphStructureChangedListeners()
			 {
		viewedGraph.removeAllGraphStructureChangedListeners();
	}

	@Override
	public int getGraphStructureChangedListenerCount() {
		return viewedGraph.getGraphStructureChangedListenerCount();
	}

	@Override
	public void vertexAdded(Vertex v) {
		if (containsVertex(v)) {
			setVCount(getVCount() + 1);
		}
	}

	@Override
	public void vertexDeleted(Vertex v) {
		if (containsVertex(v)) {
			setVCount(getVCount() + 1);
		}
	}

	@Override
	public void edgeAdded(Edge e) {
		if (containsEdge(e)) {
			setECount(getECount() + 1);
		}
	}

	@Override
	public void edgeDeleted(Edge e) {
		if (containsEdge(e)) {
			setECount(getECount() + 1);
		}
	}

	@Override
	public void maxVertexCountIncreased(int newValue) {
	}

	@Override
	public void maxEdgeCountIncreased(int newValue) {
	}

	@Override
	public void maxIncidenceCountIncreased(int newValue) {
	}

	@Override
	public void incidenceAdded(Incidence i) {
		if (containsEdge(i.getEdge())) {
			setICount(getICount() + 1);
		}
	}

	@Override
	public void incidenceDeleted(Incidence i) {
		if (containsEdge(i.getEdge())) {
			setICount(getICount() + 1);
		}
	}

	@Override
	public Graph getSuperordinateGraph() {
		return viewedGraph.getSuperordinateGraph().getView(lowestVisibleKappaLevel);
	}
	
	@Override
	public Graph getParentDistributedGraph() {
		return viewedGraph.getParentDistributedGraph();
	}



	@Override
	public GraphFactory getGraphFactory() {
		return viewedGraph.getGraphFactory();
	}


	@Override
	public boolean isPartOfGraph(Graph other) {
		return viewedGraph.isPartOfGraph(other);
	}


	@Override
	public int getPartialGraphId() {
		return viewedGraph.getPartialGraphId();
	}


	@Override
	public Graph createPartialGraph(String hostname) {
		return viewedGraph.createPartialGraph(hostname);
	}

	

	@Override
	public void savePartialGraphs(GraphIO graphIO) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<? extends Graph> getPartialGraphs() {
		return viewedGraph.getPartialGraphs();
	}

	@Override
	public Graph getPartialGraph(int partialGraphId) {
		return viewedGraph.getPartialGraph(partialGraphId);
	}

	@Override
	public String getUniqueGraphId() {
		return viewedGraph.getUniqueGraphId();
	}

	@Override
	public int getGlobalSubgraphId() {
		return viewedGraph.getGlobalSubgraphId();
	}

	@Override
	public int getLocalSubgraphId() {
		return viewedGraph.getLocalSubgraphId();
	}

	@Override
	public boolean isLocalElementId(long id) {
		return viewedGraph.isLocalElementId(id);
	}

	@Override
	public GraphDatabase getGraphDatabase() {
		return viewedGraph.getGraphDatabase();
	}



	@Override
	public void setLoading(boolean b) {
		viewedGraph.setLoading(b);
	}
}
