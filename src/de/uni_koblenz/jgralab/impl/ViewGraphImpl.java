package de.uni_koblenz.jgralab.impl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphException;
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
	public ViewGraphImpl(Graph viewedGraph, int lowestVisibleKappaLevel) throws RemoteException {
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
	public ViewGraphImpl getView(int level) throws RemoteException {
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
	public Object getAttribute(String name) throws NoSuchAttributeException {
		return viewedGraph.getAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object data)
			throws NoSuchAttributeException {
		viewedGraph.setAttribute(name, data);
	}

	@Override
	public void initializeAttributesWithDefaultValues() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<? extends Graph> getM1Class() throws RemoteException {
		return viewedGraph.getM1Class();
	}

	@Override
	public GraphClass getType() throws RemoteException {
		return viewedGraph.getType();
	}

	@Override
	public GraphClass getGraphClass() throws RemoteException {
		return viewedGraph.getGraphClass();
	}

	@Override
	public Schema getSchema() throws RemoteException {
		return viewedGraph.getSchema();
	}

	@Override
	public int compareTo(Graph arg0) {
		try {
			if (viewedGraph == arg0) {
				// each graph is smaller than the complete graph
				return -1;
			} else
			if (arg0.getContainingElement() == null) {
					// this is a ViewGraphImpl
					// the ViewGraphImpl with smaller lowestVisibleKappaLevel is greater
					return ((ViewGraphImpl) arg0).lowestVisibleKappaLevel
							- lowestVisibleKappaLevel;
			} else {
				// arg0 is a subordinate or partial graph
				return viewedGraph.compareTo(arg0);
			}
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public GraphElement<?, ?, ?> getContainingElement() {
		return null;
	}

	@Override
	public Graph getCompleteGraph() {
		return viewedGraph;
	}

	@Override
	public Graph getTraversalContext() throws RemoteException {
		return viewedGraph.getTraversalContext();
	}

	@Override
	public void useAsTraversalContext() throws RemoteException {
		((GraphBaseImpl) viewedGraph).setTraversalContext(this);
	}

	@Override
	public void releaseTraversalContext() throws RemoteException {
		viewedGraph.releaseTraversalContext();
	}

	@Override
	public <T extends Vertex> T createVertex(Class<T> cls) throws RemoteException {
		return viewedGraph.createVertex(cls);
	}

	@Override
	public <T extends Edge> T createEdge(Class<T> cls) throws RemoteException {
		return viewedGraph.createEdge(cls);
	}

	@Override
	public <T extends Incidence> T connect(Class<T> cls, Vertex vertex,
			Edge edge) throws RemoteException {
		return viewedGraph.connect(cls, vertex, edge);
	}

	@Override
	public <T extends BinaryEdge> T createEdge(Class<T> cls, Vertex alpha,
			Vertex omega) throws RemoteException {
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
	public boolean isGraphModified(long previousVersion) throws RemoteException {
		return viewedGraph.isGraphModified(previousVersion);
	}

	@Override
	public long getGraphVersion() throws RemoteException {
		return viewedGraph.getGraphVersion();
	}

	@Override
	public boolean isVertexListModified(long previousVersion) throws RemoteException {
		return viewedGraph.isVertexListModified(previousVersion);
	}

	@Override
	public long getVertexListVersion() throws RemoteException {
		return viewedGraph.getVertexListVersion();
	}

	@Override
	public boolean isEdgeListModified(long edgeListVersion) throws RemoteException {
		return viewedGraph.isEdgeListModified(edgeListVersion);
	}

	@Override
	public long getEdgeListVersion() throws RemoteException {
		return viewedGraph.getEdgeListVersion();
	}

	@Override
	public boolean containsVertex(Vertex v) throws RemoteException {
		return v.isVisible(lowestVisibleKappaLevel)
				&& viewedGraph.containsVertex(v);
	}

	@Override
	public boolean containsEdge(Edge e) throws RemoteException {
		return e.isVisible(lowestVisibleKappaLevel)
				&& viewedGraph.containsEdge(e);
	}

	@Override
	public void deleteVertex(Vertex v) throws RemoteException {
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
	public void deleteEdge(Edge e) throws RemoteException {
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
	public Vertex getFirstVertex() throws RemoteException {
		Vertex v = viewedGraph.getFirstVertex();
		return v == null || containsVertex(v) ? v : v.getNextVertex(this);
	}

	@Override
	public Vertex getLastVertex() throws RemoteException {
		Vertex v = viewedGraph.getLastVertex();
		return v == null || containsVertex(v) ? v : v.getPreviousVertex(this);
	}

	@Override
	public Vertex getFirstVertex(VertexClass vertexClass) throws RemoteException {
		assert vertexClass != null;
		return getFirstVertex(vertexClass.getM1Class(), false);
	}

	@Override
	public Vertex getFirstVertex(VertexClass vertexClass, boolean noSubclasses) throws RemoteException {
		assert vertexClass != null;
		return getFirstVertex(vertexClass.getM1Class(), noSubclasses);
	}

	@Override
	public Vertex getFirstVertex(Class<? extends Vertex> vertexClass) throws RemoteException {
		assert vertexClass != null;
		return getFirstVertex(vertexClass, false);
	}

	@Override
	public Vertex getFirstVertex(Class<? extends Vertex> vertexClass,
			boolean noSubclasses) throws RemoteException {
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
	public Edge getFirstEdge() throws RemoteException {
		Edge e = viewedGraph.getFirstEdge();
		return e == null || containsEdge(e) ? e : e.getNextEdge(this);
	}

	@Override
	public Edge getLastEdge() throws RemoteException {
		Edge e = viewedGraph.getLastEdge();
		return e == null || containsEdge(e) ? e : e.getPreviousEdge(this);
	}

	@Override
	public Edge getFirstEdge(EdgeClass edgeClass) throws RemoteException {
		return getFirstEdge(edgeClass.getM1Class(), false);
	}

	@Override
	public Edge getFirstEdge(EdgeClass edgeClass, boolean noSubclasses) throws RemoteException {
		return getFirstEdge(edgeClass.getM1Class(), noSubclasses);
	}

	@Override
	public Edge getFirstEdge(Class<? extends Edge> edgeClass) throws RemoteException {
		return getFirstEdge(edgeClass, false);
	}

	@Override
	public Edge getFirstEdge(Class<? extends Edge> edgeClass,
			boolean noSubclasses) throws RemoteException {
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
	public Vertex getVertex(int id) throws RemoteException {
		Vertex v = viewedGraph.getVertex(id);
		if (v.isVisible(lowestVisibleKappaLevel)) {
			return v;
		} else {
			return null;
		}
	}

	@Override
	public Edge getEdge(int id) throws RemoteException {
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
	public String getUid() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<Edge> getEdges() throws RemoteException {
		return new EdgeIterable<Edge>(this);
	}

	@Override
	public Iterable<Edge> getEdges(EdgeClass edgeClass) throws RemoteException {
		return new EdgeIterable<Edge>(this, edgeClass.getM1Class());
	}

	@Override
	public Iterable<Edge> getEdges(Class<? extends Edge> edgeClass) throws RemoteException {
		return new EdgeIterable<Edge>(this, edgeClass);
	}

	@Override
	public Iterable<Vertex> getVertices() throws RemoteException {
		return new VertexIterable<Vertex>(this);
	}

	@Override
	public Iterable<Vertex> getVertices(VertexClass vertexclass) throws RemoteException {
		return new VertexIterable<Vertex>(this, vertexclass.getM1Class());
	}

	@Override
	public Iterable<Vertex> getVertices(Class<? extends Vertex> vertexClass) throws RemoteException {
		return new VertexIterable<Vertex>(this, vertexClass);
	}

	@Override
	public void defragment() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> JGraLabList<T> createList() throws RemoteException {
		return viewedGraph.createList();
	}

	@Override
	public <T> JGraLabList<T> createList(Collection<? extends T> collection) throws RemoteException {
		return viewedGraph.createList(collection);
	}

	@Override
	public <T> JGraLabList<T> createList(int initialCapacity) throws RemoteException {
		return viewedGraph.createList(initialCapacity);
	}

	@Override
	public <T> JGraLabSet<T> createSet() throws RemoteException {
		return viewedGraph.createSet();
	}

	@Override
	public <T> JGraLabSet<T> createSet(Collection<? extends T> collection) throws RemoteException {
		return viewedGraph.createSet(collection);
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity) throws RemoteException {
		return viewedGraph.createSet(initialCapacity);
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity, float loadFactor) throws RemoteException {
		return viewedGraph.createSet(initialCapacity, loadFactor);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap() throws RemoteException {
		return viewedGraph.createMap();
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(Map<? extends K, ? extends V> map) throws RemoteException {
		return viewedGraph.createMap(map);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity) throws RemoteException {
		return viewedGraph.createMap(initialCapacity);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity,
			float loadFactor) throws RemoteException {
		return viewedGraph.createMap(initialCapacity, loadFactor);
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io) throws RemoteException {
		return viewedGraph.createRecord(recordClass, io);
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Map<String, Object> fields) throws RemoteException {
		return viewedGraph.createRecord(recordClass, fields);
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Object... components) throws RemoteException {
		return viewedGraph.createRecord(recordClass, components);
	}

	@Override
	public void sortVertices(Comparator<Vertex> comp) throws RemoteException {
		viewedGraph.sortVertices(comp);
	}

	@Override
	public void sortEdges(Comparator<Edge> comp) throws RemoteException {
		viewedGraph.sortEdges(comp);
	}

	@Override
	public void addGraphStructureChangedListener(
			GraphStructureChangedListener newListener) throws RemoteException {
		viewedGraph.addGraphStructureChangedListener(newListener);
	}

	@Override
	public void removeGraphStructureChangedListener(
			GraphStructureChangedListener listener) throws RemoteException {
		viewedGraph.removeGraphStructureChangedListener(listener);
	}

	@Override
	public void removeAllGraphStructureChangedListeners() throws RemoteException {
		viewedGraph.removeAllGraphStructureChangedListeners();
	}

	@Override
	public int getGraphStructureChangedListenerCount() throws RemoteException {
		return viewedGraph.getGraphStructureChangedListenerCount();
	}

	@Override
	public void vertexAdded(Vertex v) throws RemoteException {
		if (containsVertex(v)) {
			setVCount(getVCount() + 1);
		}
	}

	@Override
	public void vertexDeleted(Vertex v) throws RemoteException {
		if (containsVertex(v)) {
			setVCount(getVCount() + 1);
		}
	}

	@Override
	public void edgeAdded(Edge e) throws RemoteException {
		if (containsEdge(e)) {
			setECount(getECount() + 1);
		}
	}

	@Override
	public void edgeDeleted(Edge e) throws RemoteException {
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
	public void incidenceAdded(Incidence i) throws RemoteException {
		if (containsEdge(i.getEdge())) {
			setICount(getICount() + 1);
		}
	}

	@Override
	public void incidenceDeleted(Incidence i) throws RemoteException {
		if (containsEdge(i.getEdge())) {
			setICount(getICount() + 1);
		}
	}

}
