package de.uni_koblenz.jgralab.impl;

import java.io.IOException;
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
public abstract class ViewGraphImpl implements Graph {

	/**
	 * the lowest kappa level of the elements which should be visible in this
	 * graph
	 */
	private final int lowestVisibleKappaLevel;

	/**
	 * The Graph viewed by this view
	 */
	private final Graph viewedGraph;

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
	public ViewGraphImpl(Graph viewedGraph, int lowestVisibleKappaLevel) {
		this.lowestVisibleKappaLevel = lowestVisibleKappaLevel;
		this.viewedGraph = viewedGraph;
	}

	/**
	 * Returns a new view on this view, implemented and not delegated to avoid
	 * multi-delegation and unnecessary overhead
	 */
	@Override
	public ViewGraphImpl getView(int level) {
		if (level < lowestVisibleKappaLevel) {
			level = lowestVisibleKappaLevel;
		}
		return ((GraphBaseImpl) getCompleteGraph()).getGraphFactory()
				.createViewGraph(this, level);
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
	public Class<? extends Graph> getM1Class() {
		return getCompleteGraph().getM1Class();
	}

	@Override
	public GraphClass getType() {
		return getCompleteGraph().getType();
	}

	@Override
	public GraphClass getGraphClass() {
		return getCompleteGraph().getGraphClass();
	}

	@Override
	public Schema getSchema() {
		return getCompleteGraph().getSchema();
	}

	@Override
	public int compareTo(Graph arg0) {
		// TODO Auto-generated method stub
		return 0;
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
	public Graph getTraversalContext() {
		return getCompleteGraph().getTraversalContext();
	}

	@Override
	public void useAsTraversalContext() {
		((GraphBaseImpl) getCompleteGraph()).setTraversalContext(this);
	}

	@Override
	public void releaseTraversalContext() {
		getCompleteGraph().releaseTraversalContext();
	}

	@Override
	public <T extends Vertex> T createVertex(Class<T> cls) {
		return getCompleteGraph().createVertex(cls);
	}

	@Override
	public <T extends Edge> T createEdge(Class<T> cls) {
		return getCompleteGraph().createEdge(cls);
	}

	@Override
	public <T extends Incidence> T connect(Class<T> cls, Vertex vertex,
			Edge edge) {
		return getCompleteGraph().connect(cls, vertex, edge);
	}

	@Override
	public <T extends BinaryEdge> T createEdge(Class<T> cls, Vertex alpha,
			Vertex omega) {
		return getCompleteGraph().createEdge(cls, alpha, omega);
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getGraphVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isVertexListModified(long previousVersion) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getVertexListVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEdgeListModified(long edgeListVersion) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getEdgeListVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean containsVertex(Vertex v) {
		return v.isVisible(lowestVisibleKappaLevel)
				&& getCompleteGraph().containsVertex(v);
	}

	@Override
	public boolean containsEdge(Edge e) {
		return e.isVisible(lowestVisibleKappaLevel)
				&& getCompleteGraph().containsEdge(e);
	}

	@Override
	public void deleteVertex(Vertex v) {
		if (containsVertex(v)) {
			getCompleteGraph().deleteVertex(v);
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
			getCompleteGraph().deleteEdge(e);
		} else {
			throw new GraphException(
					"The view with lowest visible kappa value "
							+ lowestVisibleKappaLevel
							+ " does not contain edge " + e.getId() + ".");
		}
	}

	@Override
	public Vertex getFirstVertex() {
		Vertex v = getCompleteGraph().getFirstVertex();
		return v == null || containsVertex(v) ? v : v.getNextVertex(this);
	}

	@Override
	public Vertex getLastVertex() {
		Vertex v = getCompleteGraph().getLastVertex();
		return v == null || containsVertex(v) ? v : v.getPreviousVertex(this);
	}

	@Override
	public Vertex getFirstVertex(VertexClass vertexClass) {
		assert vertexClass != null;
		return getFirstVertex(vertexClass.getM1Class(), false);
	}

	@Override
	public Vertex getFirstVertex(VertexClass vertexClass, boolean noSubclasses) {
		assert vertexClass != null;
		return getFirstVertex(vertexClass.getM1Class(), noSubclasses);
	}

	@Override
	public Vertex getFirstVertex(Class<? extends Vertex> vertexClass) {
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
		Edge e = getCompleteGraph().getFirstEdge();
		return e == null || containsEdge(e) ? e : e.getNextEdge(this);
	}

	@Override
	public Edge getLastEdge() {
		Edge e = getCompleteGraph().getLastEdge();
		return e == null || containsEdge(e) ? e : e.getPreviousEdge(this);
	}

	@Override
	public Edge getFirstEdge(EdgeClass edgeClass) {
		return getFirstEdge(edgeClass.getM1Class(), false);
	}

	@Override
	public Edge getFirstEdge(EdgeClass edgeClass, boolean noSubclasses) {
		return getFirstEdge(edgeClass.getM1Class(), noSubclasses);
	}

	@Override
	public Edge getFirstEdge(Class<? extends Edge> edgeClass) {
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
	public Vertex getVertex(int id) {
		Vertex v = getCompleteGraph().getVertex(id);
		if (v.isVisible(lowestVisibleKappaLevel)) {
			return v;
		} else {
			return null;
		}
	}

	@Override
	public Edge getEdge(int id) {
		Edge e = getCompleteGraph().getEdge(id);
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getECount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getICount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getUid() {
		throw new UnsupportedOperationException();
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
	public Iterable<Edge> getEdges(Class<? extends Edge> edgeClass) {
		return new EdgeIterable<Edge>(this, edgeClass);
	}

	@Override
	public Iterable<Vertex> getVertices() {
		return new VertexIterable<Vertex>(this);
	}

	@Override
	public Iterable<Vertex> getVertices(VertexClass vertexclass) {
		return new VertexIterable<Vertex>(this, vertexclass.getM1Class());
	}

	@Override
	public Iterable<Vertex> getVertices(Class<? extends Vertex> vertexClass) {
		return new VertexIterable<Vertex>(this, vertexClass);
	}

	@Override
	public void defragment() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> JGraLabList<T> createList() {
		return getCompleteGraph().createList();
	}

	@Override
	public <T> JGraLabList<T> createList(Collection<? extends T> collection) {
		return getCompleteGraph().createList(collection);
	}

	@Override
	public <T> JGraLabList<T> createList(int initialCapacity) {
		return getCompleteGraph().createList(initialCapacity);
	}

	@Override
	public <T> JGraLabSet<T> createSet() {
		return getCompleteGraph().createSet();
	}

	@Override
	public <T> JGraLabSet<T> createSet(Collection<? extends T> collection) {
		return getCompleteGraph().createSet(collection);
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity) {
		return getCompleteGraph().createSet(initialCapacity);
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity, float loadFactor) {
		return getCompleteGraph().createSet(initialCapacity, loadFactor);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap() {
		return getCompleteGraph().createMap();
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(Map<? extends K, ? extends V> map) {
		return getCompleteGraph().createMap(map);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity) {
		return getCompleteGraph().createMap(initialCapacity);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity,
			float loadFactor) {
		return getCompleteGraph().createMap(initialCapacity, loadFactor);
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io) {
		return getCompleteGraph().createRecord(recordClass, io);
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Map<String, Object> fields) {
		return getCompleteGraph().createRecord(recordClass, fields);
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Object... components) {
		return getCompleteGraph().createRecord(recordClass, components);
	}

	@Override
	public void sortVertices(Comparator<Vertex> comp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sortEdges(Comparator<Edge> comp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addGraphStructureChangedListener(
			GraphStructureChangedListener newListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeGraphStructureChangedListener(
			GraphStructureChangedListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAllGraphStructureChangedListeners() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getGraphStructureChangedListenerCount() {
		// TODO Auto-generated method stub
		return 0;
	}

}
