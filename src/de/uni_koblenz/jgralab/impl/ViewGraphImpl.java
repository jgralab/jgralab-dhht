package de.uni_koblenz.jgralab.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphClass getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphClass getGraphClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Schema getSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(Graph arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public GraphElement<?, ?, ?> getContainingElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph getCompleteGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph getTraversalContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void useAsTraversalContext() {
		// TODO Auto-generated method stub

	}

	@Override
	public void releaseTraversalContext() {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Vertex> T createVertex(Class<T> cls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Edge> T createEdge(Class<T> cls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Incidence> T connect(Class<T> cls, Vertex vertex,
			Edge edge) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends BinaryEdge> T createEdge(Class<T> cls, Vertex alpha,
			Vertex omega) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLoading() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void loadingCompleted() {
		// TODO Auto-generated method stub

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
	public Vertex getFirstVertex() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vertex getLastVertex() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vertex getFirstVertex(VertexClass vertexClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vertex getFirstVertex(VertexClass vertexClass, boolean noSubclasses) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vertex getFirstVertex(Class<? extends Vertex> vertexClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vertex getFirstVertex(Class<? extends Vertex> vertexClass,
			boolean noSubclasses) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Edge getFirstEdge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Edge getLastEdge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Edge getFirstEdge(EdgeClass edgeClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Edge getFirstEdge(EdgeClass edgeClass, boolean noSubclasses) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Edge getFirstEdge(Class<? extends Edge> edgeClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Edge getFirstEdge(Class<? extends Edge> edgeClass,
			boolean noSubclasses) {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public int getMaxVCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxECount() {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Edge> getEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Edge> getEdges(EdgeClass edgeClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Edge> getEdges(Class<? extends Edge> edgeClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Vertex> getVertices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Vertex> getVertices(VertexClass vertexclass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Vertex> getVertices(Class<? extends Vertex> vertexClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void defragment() {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> JGraLabList<T> createList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> JGraLabList<T> createList(Collection<? extends T> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> JGraLabList<T> createList(int initialCapacity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> JGraLabSet<T> createSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> JGraLabSet<T> createSet(Collection<? extends T> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity, float loadFactor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(Map<? extends K, ? extends V> map) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity,
			float loadFactor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Map<String, Object> fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Object... components) {
		// TODO Auto-generated method stub
		return null;
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
