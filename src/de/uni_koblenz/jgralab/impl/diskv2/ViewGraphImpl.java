package de.uni_koblenz.jgralab.impl.diskv2;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.pcollections.PMap;
import org.pcollections.PSet;
import org.pcollections.PVector;

import de.uni_koblenz.jgralab.AttributedElement;
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
import de.uni_koblenz.jgralab.NoSuchAttributeException;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.EdgeIterable;
import de.uni_koblenz.jgralab.impl.RemoteGraphDatabaseAccess;
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

	private long eCount;
	private long iCount;
	private long vCount;

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
		eCount = viewedGraph.getECount();
		iCount = viewedGraph.getICount();
		vCount = viewedGraph.getVCount();
		viewedGraph.addGraphStructureChangedListener(this);
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
	
	// ============================================================================
	// Methods to access schema
	// ============================================================================

	
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
	
	
	// ============================================================================
	// Methods to manage the current traversal context 
	// ============================================================================
	
	@Override
	public Graph getTraversalContext() {
		return ((GraphDatabaseBaseImpl)getGraphDatabase()).getTraversalContext();
	}

	@Override
	public void useAsTraversalContext() {
		 ((GraphDatabaseBaseImpl)getGraphDatabase()).setTraversalContext(this);
	}

	@Override
	public void releaseTraversalContext() {
		 ((GraphDatabaseBaseImpl)getGraphDatabase()).releaseTraversalContext();
	}
	
	
	// ============================================================================
	// Methods to access hierarchy and distribution
	//
	// - General methods
	// - Nesting hierarchy
	// - Visibility layering
	// - Distribution
	// - Graph IDs
	// ============================================================================
	
	
	@Override
	public Graph getCompleteGraph() {
		return viewedGraph.getCompleteGraph();
	}
	
	@Override
	public Graph getGraph() {
		return getCompleteGraph();
	}
	
	@Override
	public Graph getLocalPartialGraph() {
		return viewedGraph.getLocalPartialGraph();
	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public AttributedElement getParentGraphOrElement() {
		AttributedElement a = getViewedGraph().getParentGraphOrElement();
		if (a instanceof Graph) {
			Graph g = (Graph) a;
			return g.getView(lowestVisibleKappaLevel);
		}
		return a;
	}


	@Override
	public Graph getParentGraph() {
		return getViewedGraph().getParentGraph().getView(lowestVisibleKappaLevel);
	}


	@Override
	public boolean isPartOfGraph(Graph other) {
		return viewedGraph.isPartOfGraph(other);
	}


	/**
	 * Returns a new view on this view, implemented and not delegated to avoid
	 * multi-delegation and unnecessary overhead
	 */
	@Override
	public Graph getView(int level) {
		if (level < lowestVisibleKappaLevel) {
			level = lowestVisibleKappaLevel;
		}
		return ((GraphBaseImpl) viewedGraph).getGraphFactory().createViewGraph_InMemoryStorage(
				this, level);
	}
	
	
	@Override
	public Graph getViewedGraph() {
		return viewedGraph;
	}
	
	
	@Override
	public Graph createPartialGraphInGraph(String hostname) {
		return viewedGraph.createPartialGraphInGraph(hostname);
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
	public void savePartialGraphs(GraphIO graphIO) {
		throw new UnsupportedOperationException();
	}
	
	
	// ============================================================================
	// Methods to access ids
	// ============================================================================

	
	
	
	@Override
	public String getUniqueGraphId() {
		return viewedGraph.getUniqueGraphId();
	}

	
	@Override
	public long getGlobalId() {
		return viewedGraph.getGlobalId();
	}

	
	@Override
	public int getLocalId() {
		return viewedGraph.getLocalId();
	}
	
	
	@Override
	public int getPartialGraphId() {
		return viewedGraph.getPartialGraphId();
	}

	
	@Override
	public boolean isLocalElementId(long id) {
		return viewedGraph.isLocalElementId(id);
	}
	

	// ============================================================================
	// Methods to access vertices and edges of the graph
	// ============================================================================


	@Override
	public <T extends Vertex> T createVertex(Class<T> cls) {
		return viewedGraph.createVertex(cls);
	}

	
	@Override
	public <T extends Edge> T createEdge(Class<T> cls) {
		return viewedGraph.createEdge(cls);
	}


	@Override
	public <T extends BinaryEdge> T createEdge(Class<T> cls, Vertex alpha, Vertex omega) {
		return viewedGraph.createEdge(cls, alpha, omega);
	}
	

	@Override
	public <T extends Incidence> T connect(Class<T> cls, Vertex vertex,	Edge edge) {
		return viewedGraph.connect(cls, vertex, edge);
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
	public boolean containsElement(@SuppressWarnings("rawtypes") GraphElement elem) {
		if (elem.getKappa() > this.lowestVisibleKappaLevel) {
			return viewedGraph.containsElement(elem);
		} else {
			return false;
		}
	}


	@Override
	public void deleteVertex(Vertex v) {
		if (containsVertex(v)) {
			viewedGraph.deleteVertex(v);
		} else {
			throw new GraphException(
					"The view with lowest visible kappa value "
							+ lowestVisibleKappaLevel
							+ " does not contain vertex " + v.getGlobalId() + ".");
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
							+ " does not contain edge " + e.getGlobalId() + ".");
		}
	}

	
	
	@Override
	public Vertex getFirstVertex() {
		Vertex v = viewedGraph.getFirstVertex();
		return (v == null || containsVertex(v)) ? v : v.getNextVertex(this);
	}

	@Override
	public Vertex getLastVertex() {
		Vertex v = viewedGraph.getLastVertex();
		return (v == null || containsVertex(v)) ? v : v.getPreviousVertex(this);
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
	public Vertex getFirstVertex(Class<? extends Vertex> vertexClass, boolean noSubclasses) {
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
	public Edge getFirstEdge(EdgeClass edgeClass, boolean noSubclasses) {
		return getFirstEdge(edgeClass.getM1Class(), noSubclasses);
	}

	@Override
	public Edge getFirstEdge(Class<? extends Edge> edgeClass) {
		return getFirstEdge(edgeClass, false);
	}

	@Override
	public Edge getFirstEdge(Class<? extends Edge> edgeClass, boolean noSubclasses) {
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
	public long getMaxVCount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getMaxECount() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public long getMaxICount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getVCount() {
		return vCount;
	}
	
//	protected void setVCount(int vCount) {
//		this.vCount = vCount;
//	}

	@Override
	public long getECount() {
		return eCount;
	}
	
//	protected void setECount(int eCount) {
//		this.eCount = eCount;
//	}

	
	@Override
	public long getICount() {
		return iCount;
	}

	
//	protected void setICount(int iCount) {
//		this.iCount = iCount;
//	}


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
	public void sortVertices(Comparator<Vertex> comp) {
		viewedGraph.sortVertices(comp);
	}

	@Override
	public void sortEdges(Comparator<Edge> comp) {
		viewedGraph.sortEdges(comp);
	}
	

	@Override
	public void defragment() {
		throw new UnsupportedOperationException();
	}

	
	// ============================================================================
	// Methods to handle graph listeners
	// ============================================================================
	
	
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
	
	
	
	// ============================================================================
	// Methods to access graph state and version (loading etc.)
	// ============================================================================
	
	
	@Override
	public boolean isLoading() {
		return viewedGraph.isLoading();
	}
	
	
//	@Override
//	public void setLoading(boolean b) {
//		viewedGraph.setLoading(b);
//	}
	
	
	@Override
	public boolean isGraphModified(long previousVersion) {
		return viewedGraph.isGraphModified(previousVersion);
	}

	
	@Override
	public long getGraphVersion() {
		return viewedGraph.getGraphVersion();
	}
	

	@Override
	public boolean isVertexListModified(long previousVersion) {
		return viewedGraph.isVertexListModified(previousVersion);
	}

	
	@Override
	public long getVertexListVersion() {
		return viewedGraph.getVertexListVersion();
	}

	
	@Override
	public boolean isEdgeListModified(long edgeListVersion) {
		return viewedGraph.isEdgeListModified(edgeListVersion);
	}

	
	@Override
	public long getEdgeListVersion() {
		return viewedGraph.getEdgeListVersion();
	}
	
	
	@Override
	public GraphFactory getGraphFactory() {
		return viewedGraph.getGraphFactory();
	}

	@Override
	public RemoteGraphDatabaseAccess getGraphDatabase() {
		return viewedGraph.getGraphDatabase();
	}

	
	// ============================================================================
	// Methods to create complex values such as lists and maps
	// ============================================================================


	@Override
	public <T> PVector<T> createList() {
		return viewedGraph.createList();
	}

	@Override
	public <T> PSet<T> createSet() {
		return viewedGraph.createSet();
	}
	@Override
	public <K, V> PMap<K, V> createMap() {
		return viewedGraph.createMap();
	}

	
	
	// ============================================================================
	// Methods of SubordinateGraph as GraphListener on the complete graph
	// ============================================================================
	

	@Override
	public void vertexAdded(Vertex v) {
		if (containsVertex(v)) {
			vCount++;
		}
	}

	@Override
	public void vertexDeleted(Vertex v) {
		if (containsVertex(v)) {
			vCount--;
		}
	}

	@Override
	public void edgeAdded(Edge e) {
		if (containsEdge(e)) {
			eCount++;
		}
	}

	@Override
	public void edgeDeleted(Edge e) {
		if (containsEdge(e)) {
			eCount--;
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
			iCount++;
		}
	}

	@Override
	public void incidenceDeleted(Incidence i) {
		if (containsEdge(i.getEdge())) {
			iCount--;
		}
	}

	
	
	
	// ============================================================================
	// Methods which are created by the code generator for the complete graph
	// ============================================================================

	
	
	
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
	public Record createRecord(Class<? extends Record> cls,
			Map<String, Object> values) {
		try {
			Constructor<?> constr = cls.getConstructor(Map.class);
			return (Record) constr.newInstance(values);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GraphException(e);
		}
	}

	
}
