/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */

package de.uni_koblenz.jgralab.impl.mem;

import java.lang.ref.WeakReference;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.pcollections.Empty;
import org.pcollections.PMap;
import org.pcollections.PSet;
import org.pcollections.PVector;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphStructureChangedListener;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * In-memory implementation of the complete graph containing some subordinate
 * ones Beware: Distribution is not supported by in-memory implementation
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class CompleteGraphImpl extends GraphBaseImpl {

	/**
	 * Creates a graph of the given GraphClass with the given id
	 * 
	 * @param uid
	 *            this Graph's unique id
	 * @param cls
	 *            the GraphClass of this Graph
	 */
	protected CompleteGraphImpl(String uid, GraphClass cls) {
		this(uid, cls, 1000, 1000);
	}

	/**
	 * @param partialGraphId
	 *            this Graph's id
	 * @param cls
	 *            the GraphClass of this Graph
	 * @param vMax
	 *            initial maximum number of vertices
	 * @param eMax
	 *            initial maximum number of edges
	 */
	protected CompleteGraphImpl(String uid, GraphClass cls, int vMax, int eMax) {
		super(cls);
		schema = cls.getSchema();
		graphVersion = -1;
		setGraphVersion(0);
		this.uid = uid;
		if (vMax < 1) {
			throw new GraphException("vMax must not be less than 1", null);
		}
		if (eMax < 1) {
			throw new GraphException("eMax must not be less than 1", null);
		}
		graphFactory = cls.getSchema().getGraphFactory();
		id = 1;

		expandVertexArray(vMax);
		deleteVertexList = new LinkedList<VertexImpl>();

		expandEdgeArray(eMax);
		expandIncidenceArray(vMax + eMax);
	}

	// ============================================================================
	// Methods to access schema
	// ============================================================================

	@Override
	public int compareTo(Graph a) {
		if (this == a) {
			return 0;
		}
		// every graph is smaller than the complete graph
		return -1;
	}

	/**
	 * The schema this graph belongs to
	 */
	protected final Schema schema;

	@Override
	public Schema getSchema() {
		return schema;
	}

	// ============================================================================
	// Methods to manage the current traversal context
	// ============================================================================

	/**
	 * Stores the traversal context of each {@link Thread} working on this
	 * {@link Graph}.
	 */
	private HashMap<Thread, Stack<Graph>> traversalContextMap;

	@Override
	public Graph getTraversalContext() {
		if (traversalContextMap == null) {
			return null;
		}
		Stack<Graph> stack = traversalContextMap.get(Thread.currentThread());
		if (stack == null || stack.isEmpty()) {
			return this;
		} else {
			return stack.peek();
		}
	}

	@Override
	public void useAsTraversalContext() {
		setTraversalContext(this);
	}

	@Override
	public void releaseTraversalContext() {
		if (traversalContextMap == null) {
			return;
		}
		Stack<Graph> stack = this.traversalContextMap.get(Thread
				.currentThread());
		if (stack != null) {
			stack.pop();
			if (stack.isEmpty()) {
				traversalContextMap.remove(Thread.currentThread());
				if (traversalContextMap.isEmpty()) {
					traversalContextMap = null;
				}
			}
		}
	}

	public void setTraversalContext(Graph traversalContext) {
		if (this.traversalContextMap == null) {
			this.traversalContextMap = new HashMap<Thread, Stack<Graph>>();
		}
		Stack<Graph> stack = this.traversalContextMap.get(Thread
				.currentThread());
		if (stack == null) {
			stack = new Stack<Graph>();
			this.traversalContextMap.put(Thread.currentThread(), stack);
		}
		stack.add(traversalContext);
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
	public CompleteGraphImpl getCompleteGraph() {
		return this;
	}

	@Override
	public Graph getLocalPartialGraph() {
		return this;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public AttributedElement getParentGraphOrElement() {
		return null;
	}

	@Override
	public Graph getParentGraph() {
		return null;
	}

	@Override
	public boolean isPartOfGraph(Graph other) {
		return false;
	}

	@Override
	public Graph getView(int kappa) {
		return graphFactory.createViewGraph_InMemoryStorage(this, kappa);
	}

	@Override
	public Graph getViewedGraph() {
		return this;
	}

	@Override
	public Graph createPartialGraphInGraph(String hostname) {
		throw new RuntimeException(
				"Creation of partial graphs is not supported in memory-only implementation");
	}

	public Graph getPartialGraphId(int id) {
		if (id == 1) {
			return this;
		}
		throw new RuntimeException(
				"Partial graphs are not supported in memory-only implementation");
	}

	// ============================================================================
	// Methods to access ids
	// ============================================================================

	/**
	 * the unique id of the graph in the schema
	 */
	private final String uid;

	@Override
	public String getUniqueGraphId() {
		return uid;
	}

	// Inherited from GraphBaseImpl
	// public long getGlobalId()

	// Inherited from GraphBaseImpl
	// public int getLocalId()

	@Override
	public int getPartialGraphId() {
		// Access to GraphDatabase is correct here,
		// since the method only returns the partial
		// graph id of the complete graph
		return id;// GraphDatabaseBaseImpl.getPartialGraphId(id);
	}

	// Inherited from GraphBaseImpl
	// public boolean isLocalElementId(long id)

	// ============================================================================
	// Methods to access vertices and edges of the graph including pufAfter and
	// putBefore operations
	// ============================================================================

	/**
	 * free index list for vertices
	 */
	private FreeIndexList freeVertexList;

	/**
	 * array of vertices
	 */
	private VertexImpl[] vertexArray;

	/**
	 * free index list for edges
	 */
	protected FreeIndexList freeEdgeList;

	/**
	 * array of incidences
	 */
	private EdgeImpl[] edgeArray;

	/**
	 * free index list for vertices
	 */
	protected FreeIndexList freeIncidenceList;

	/**
	 * array of incidences
	 */
	private IncidenceImpl[] incidenceArray;

	// Inherited from GraphBaseImpl
	// public <T extends Vertex> T createVertex(Class<T> cls);

	// Inherited from GraphBaseImpl
	// public <T extends Edge> T createEdge(Class<T> cls);

	// Inherited from GraphBaseImpl
	// public <T extends BinaryEdge> T createEdge(Class<T> cls, Vertex alpha,
	// Vertex omega);

	// Inherited from GraphBaseImpl
	// public <T extends Incidence> T connect(Class<T> cls, Vertex vertex, Edge
	// edge);

	// Inherited from GraphBaseImpl
	// public boolean containsVertex(Vertex v);

	@Override
	public boolean containsVertexLocally(Vertex v) {
		return (v != null) && (v.getGraph() == this)
				&& (vertexArray[((VertexImpl) v).id] == v);
	}

	// Inherited from GraphBaseImpl
	// public boolean containsEdge(Edge e);

	@Override
	public boolean containsEdgeLocally(Edge e) {
		return (e != null) && (e.getGraph() == this)
				&& (edgeArray[((EdgeImpl) e).id] == e);
	}

	// Inherited from GraphBaseImpl
	// public boolean containsElement(@SuppressWarnings("rawtypes") GraphElement
	// elem);

	/**
	 * Checks if the vertex id vId is valid and if there is an such a vertex
	 * locally in this graph.
	 * 
	 * @param vId
	 *            a vertex id
	 * @return true if this graph contains a vertex with id vId
	 */
	private final boolean containsVertexId(int vId) {
		return (vId > 0) && (vId <= vMax) && (vertexArray[vId] != null);
	}

	/**
	 * Checks if the edge id eId is valid and if there is an such an edge
	 * locally in this graph.
	 * 
	 * @param eId
	 *            an edge id
	 * @return true if this graph contains an edge with id eId
	 */
	private final boolean containsEdgeId(int eId) {
		return (eId > 0) && (eId <= eMax) && (edgeArray[eId] != null);
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
		return (iId > 0) && (iId <= vMax) && (incidenceArray[iId] != null);
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

		int vId = (int) v.getGlobalId();
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
	 * Appends the vertex v to the global vertex sequence of this graph.
	 * 
	 * @param v
	 *            a vertex
	 */
	protected void appendVertexToVSeq(VertexImpl v) {
		vertexArray[v.id] = v;
		vCount++;
		if (getFirstVertex() == null) {
			setFirstVertex(v);
		}
		if (getLastVertex() != null) {
			((VertexImpl) getLastVertex()).setNextVertex(v);
			v.setPreviousVertex(getLastVertex());
		}
		setLastVertex(v);
	}

	/**
	 * List of vertices to be deleted by a cascading delete caused by deletion
	 * of a composition "parent".
	 */
	private final List<VertexImpl> deleteVertexList;

	@Override
	public void deleteVertex(Vertex v) {
		assert (v != null) && v.isValid() && containsVertex(v);
		deleteVertexList.add((VertexImpl) v);
		internalDeleteVertex();
	}

	/**
	 * Deletes all vertices in deleteVertexList from the internal structures of
	 * this graph. Possibly, cascading deletes of child vertices occur when
	 * parent vertices of Composition classes are deleted.
	 */
	private void internalDeleteVertex() {
		boolean edgeHasBeenDeleted = false;
		while (!deleteVertexList.isEmpty()) {
			VertexImpl v = deleteVertexList.remove(0);
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
									&& !deleteVertexList.contains(omega)) {
								deleteVertexList.add(omega);
							}
						}
					} else if (bedge.getOmega() == v) {
						if (bedge.getAlphaSemantics() == IncidenceType.COMPOSITION) {
							VertexImpl alpha = (VertexImpl) bedge.getAlpha();
							if ((alpha != v) && containsVertex(alpha)
									&& !deleteVertexList.contains(alpha)) {
								deleteVertexList.add(alpha);
							}
						}
					}
					internalDeleteEdge(bedge);
					deleteEdge = true;
				}
				edgeHasBeenDeleted |= deleteEdge;
				if (!deleteEdge) {
					edges.add(edge);
					edge.removeIncidenceFromLambdaSeq((IncidenceImpl) inc);
					v.removeIncidenceFromLambdaSeq((IncidenceImpl) inc);
				}
				inc = v.getFirstIncidence();
			}
			for (EdgeImpl edge : edges) {
				edge.incidenceListModified();
			}
			removeVertexFromVSeq(v);
		}
		vertexListModified();
		if (edgeHasBeenDeleted) {
			edgeListModified();
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
		freeVertexIndex((int) v.getGlobalId());
		vertexArray[(int) v.getGlobalId()] = null;
		v.setPreviousVertex(null);
		v.setNextVertex(null);
		v.setId(0);
		vCount--;
	}

	/**
	 * Modifies vSeq such that the movedVertex is immediately before the
	 * targetVertex.
	 * 
	 * @param targetVertex
	 *            a vertex
	 * @param movedVertex
	 *            the vertex to be moved
	 */
	protected void putVertexBefore(VertexImpl targetVertex,
			VertexImpl movedVertex) {
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
	 * Modifies vSeq such that the movedVertex is immediately after the
	 * targetVertex.
	 * 
	 * @param targetVertex
	 *            a vertex
	 * @param movedVertex
	 *            the vertex to be moved
	 */
	protected void putVertexAfter(VertexImpl targetVertex,
			VertexImpl movedVertex) {
		assert (targetVertex != null) && targetVertex.isValid()
				&& containsVertex(targetVertex);
		assert (movedVertex != null) && movedVertex.isValid()
				&& containsVertex(movedVertex);
		assert targetVertex != movedVertex;

		Vertex nextVertex = targetVertex.getNextVertex();
		if ((targetVertex == movedVertex) || (nextVertex == movedVertex)) {
			return;
		}

		assert getFirstVertex() != getLastVertex();

		// remove moved vertex from vSeq
		if (movedVertex == getFirstVertex()) {
			VertexImpl newFirstVertex = (VertexImpl) movedVertex
					.getNextVertex();
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

	// Edges
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
	 * Adds an edge to this graph. If the edges id is 0, a valid id is set,
	 * otherwise the edges current id is used if possible. Should only be used
	 * by m1-Graphs derived from Graph. To create a new Edge as user, use the
	 * appropriate methods from the derived Graphs like
	 * <code>createStreet(...)</code>
	 * 
	 * @param newEdge
	 *            Edge to add
	 * @throws RemoteException
	 * @throws GraphException
	 *             an edge with same id already exists in graph, id of edge
	 *             greater than possible count of edges in graph
	 */
	protected void addEdge(Edge newEdge) {
		assert newEdge != null;
		assert (newEdge.getSchema() == schema) : "The schemas of newEdge and this graph don't match!";
		assert (newEdge.getGraph() == this) : "The graph of  newEdge and this graph don't match!";

		EdgeImpl e = (EdgeImpl) newEdge;

		int eId = (int) e.getGlobalId();
		if (isLoading()) {
			if (eId > 0) {
				// the given edge already has an id, try to use it
				if (containsEdgeId(eId)) {
					throw new GraphException("edge with id " + e.getGlobalId()
							+ " already exists");
				}
				if (eId > eMax) {
					throw new GraphException("edge id " + e.getGlobalId()
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

	/**
	 * Appends the edge e to the global edge sequence of this graph.
	 * 
	 * @param e
	 *            an edge
	 */
	protected void appendEdgeToESeq(EdgeImpl e) {
		edgeArray[e.id] = e;
		eCount++;
		if (getFirstEdge() == null) {
			setFirstEdge(e);
		}
		if (getLastEdge() != null) {
			((EdgeImpl) getLastEdge()).setNextEdge(e);
			e.setPreviousEdge(getLastEdge());
		}
		setLastEdge(e);
	}

	@Override
	public void deleteEdge(Edge e) {
		assert (e != null) && e.isValid() && containsEdge(e);
		internalDeleteEdge(e);
		edgeListModified();
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
			e.removeIncidenceFromLambdaSeq((IncidenceImpl) inc);
			inc = e.getFirstIncidence();
		}
		for (Vertex vertex : vertices) {
			((VertexImpl) vertex).incidenceListModified();
		}

		removeEdgeFromESeq(e);
		edgeListModified();
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
		freeEdgeIndex((int) e.getGlobalId());
		edgeArray[(int) e.getGlobalId()] = null;
		e.setPreviousEdge(null);
		e.setNextEdge(null);
		e.setId(0);
		eCount--;
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
	 * Modifies eSeq such that the movedEdge is immediately after the
	 * targetEdge.
	 * 
	 * @param targetEdge
	 *            an edge
	 * @param movedEdge
	 *            the edge to be moved
	 */
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
	 * targetEdge.
	 * 
	 * @param targetEdge
	 *            an edge
	 * @param movedEdge
	 *            the edge to be moved
	 */
	protected void putEdgeBeforeInGraph(EdgeImpl targetEdge, EdgeImpl movedEdge) {
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

		removeEdgeFromESeqWithoutDeletingIt(movedEdge);

		// insert moved edge in eSeq immediately before target
		if (targetEdge == getFirstEdge()) {
			setFirstEdge(movedEdge);
			movedEdge.setPreviousEdge(null);
		} else {
			EdgeImpl previousEdge = ((EdgeImpl) targetEdge.getPreviousEdge());
			previousEdge.setNextEdge(movedEdge);
			movedEdge.setPreviousEdge(previousEdge);
		}
		movedEdge.setNextEdge(targetEdge);
		targetEdge.setPreviousEdge(movedEdge);
		edgeListModified();
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

	protected void addIncidence(Incidence newIncidence) {
		IncidenceImpl i = (IncidenceImpl) newIncidence;

		int iId = (int) i.getGlobalId();
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

	// All methods to access first and last vertex and edge, respectively, are
	// inherited from GraphBaseImpl
	// @Override
	// public Vertex getFirstVertex()

	@Override
	public Vertex getVertex(long vId) {
		assert (vId > 0) : "The vertex id must be > 0, given was " + vId;
		try {
			return vertexArray[(int) vId];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	@Override
	public Edge getEdge(long eId) {
		assert eId != 0 : "The edge id must be != 0, given was " + eId;
		try {
			return edgeArray[(int) eId];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * maximum number of vertices
	 */
	protected int vMax;

	@Override
	public long getMaxVCount() {
		return vMax;
	}

	/**
	 * maximum number of edges
	 */
	protected int eMax;

	@Override
	public long getMaxECount() {
		return eMax;
	}

	/**
	 * maximal number of incidences
	 */
	protected int iMax;

	@Override
	public long getMaxICount() {
		return iMax;
	}

	/**
	 * current number of vertices
	 */
	private int vCount;

	@Override
	public long getVCount() {
		return vCount;
	}

	@Override
	protected void setVCount(int count) {
		vCount = count;
	}

	/**
	 * current number of edges
	 */
	private int eCount;

	@Override
	public long getECount() {
		return eCount;
	}

	@Override
	protected void setECount(int count) {
		eCount = count;
	}

	/**
	 * current number of incidences
	 */
	protected int iCount;

	@Override
	public long getICount() {
		return iCount;
	}

	@Override
	protected void setICount(int count) {
		iCount = count;
	}

	// Methods getEdges and getVertices are inherited from GraphBaseImpl
	// public Iterable<Vertex> getVertices();

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
				while (freeVertexList.isFragmented()) {
					while ((vId >= 1) && (vertexArray[vId] == null)) {
						--vId;
					}
					assert vId >= 1;
					VertexImpl v = vertexArray[vId];
					vertexArray[vId] = null;
					freeVertexList.freeIndex(vId);
					int newId = allocateVertexIndex(vId);
					assert newId < vId;
					v.setId(newId);
					vertexArray[newId] = v;
					--vId;
				}
			}
			int newVMax = (int) (getVCount() == 0 ? 1 : getVCount());
			if (newVMax != vMax) {
				vMax = newVMax;
				VertexImpl[] newVertex = new VertexImpl[vMax + 1];
				System.arraycopy(vertexArray, 0, newVertex, 0, newVertex.length);
				vertexArray = newVertex;
			}
			graphModified();
			System.gc();
		}
		// defragment edge array
		if (getECount() < eMax) {
			if (getECount() > 0) {
				int eId = eMax;
				while (freeEdgeList.isFragmented()) {
					while ((eId >= 1) && (edgeArray[eId] == null)) {
						--eId;
					}
					assert eId >= 1;
					EdgeImpl e = edgeArray[eId];
					edgeArray[eId] = null;
					freeEdgeList.freeIndex(eId);
					int newId = allocateEdgeIndex(eId);
					assert newId < eId;
					e.setId(newId);
					edgeArray[newId] = e;
					--eId;
				}
			}
			int newEMax = (int) (getECount() == 0 ? 1 : getECount());
			if (newEMax != eMax) {
				eMax = newEMax;
				EdgeImpl[] newEdge = new EdgeImpl[eMax + 1];
				System.arraycopy(edgeArray, 0, newEdge, 0, newEdge.length);
				edgeArray = newEdge;
				System.gc();
			}
			graphModified();
			System.gc();
		}

		if (getICount() < iMax) {
			if (getICount() > 0) {
				int iId = iMax;
				while (freeEdgeList.isFragmented()) {
					while ((iId >= 1) && (incidenceArray[iId] == null)) {
						--iId;
					}
					assert iId >= 1;
					IncidenceImpl i = incidenceArray[iId];
					incidenceArray[iId] = null;
					getFreeIncidenceList().freeIndex(iId);
					int newId = allocateIncidenceIndex(iId);
					assert newId < iId;
					i.setId(newId);
					incidenceArray[newId] = i;
					// getRevEdge()[newId] = r;
					--iId;
				}
			}
			int newIMax = (int) (getICount() == 0 ? 1 : getICount());
			if (newIMax != iMax) {
				iMax = newIMax;
				IncidenceImpl[] newIncidence = new IncidenceImpl[iMax + 1];
				System.arraycopy(incidenceArray, 0, newIncidence, 0,
						newIncidence.length);
				incidenceArray = newIncidence;
				System.gc();
			}
			graphModified();
			System.gc();
		}
	}

	// ============================================================================
	// Methods to handle graph listeners are inherited from GraphBaseImpl
	// ============================================================================

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

	// ============================================================================
	// Methods to access graph state and version (loading etc.)
	// ============================================================================

	/**
	 * Indicates if this graph is currently loading.
	 */
	private boolean loading;

	@Override
	public boolean isLoading() {
		return loading;
	}

	/**
	 * Sets the loading flag.
	 * 
	 * @param isLoading
	 */
	public void setLoading(boolean isLoading) {
		loading = isLoading;
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
	 * Changes this graph's version. graphModified() is called whenever the
	 * graph is changed, all changes like adding, creating and reordering of
	 * edges and vertices or changes of attributes of the graph, an edge or a
	 * vertex are treated as a change.
	 */
	@Override
	public void graphModified() {
		graphVersion++;
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
	 * Holds the version of the vertex sequence. For every modification (e.g.
	 * adding/deleting a vertex or changing the vertex sequence) this version
	 * number is increased by 1. It is set to 0 when the graph is loaded.
	 */
	protected long vertexListVersion;

	@Override
	protected void vertexListModified() {
		vertexListVersion++;
	}

	@Override
	public long getVertexListVersion() {
		return vertexListVersion;
	}

	protected void setVertexListVersion(long vertexListVersion) {
		this.vertexListVersion = vertexListVersion;
	}

	/**
	 * Holds the version of the edge sequence. For every modification (e.g.
	 * adding/deleting an edge or changing the edge sequence) this version
	 * number is increased by 1. It is set to 0 when the graph is loaded.
	 */
	protected long edgeListVersion;

	/**
	 * Changes the graph structure version, should be called whenever the
	 * structure of the graph is changed, for instance by creation and deletion
	 * or reordering of vertices and edges
	 */
	@Override
	protected void edgeListModified() {
		edgeListVersion++;
		graphModified();
	}

	@Override
	public long getEdgeListVersion() {
		return edgeListVersion;
	}

	protected void setEdgeListVersion(long edgeListVersion) {
		this.edgeListVersion = edgeListVersion;
	}

	/**
	 * The GraphFactory that was used to create this graph. This factory will be
	 * used to create vertices and edges in this graph.
	 */
	protected GraphFactory graphFactory;

	@Override
	public GraphFactory getGraphFactory() {
		return graphFactory;
	}

	protected boolean canAddGraphElement(int graphElementId) {
		return graphElementId == 0;
	}

	public int getExpandedVertexCount() {
		return computeNewSize(vMax);
	}

	public int getExpandedEdgeCount() {
		return computeNewSize(eMax);
	}

	protected int getExpandedIncidenceCount() {
		return computeNewSize(iMax);
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
	protected int computeNewSize(int n) {
		return n >= 1048576 ? n + 131072 : n >= 262144 ? n + 262144 : n + n;
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

	protected FreeIndexList getFreeIncidenceList() {
		return freeIncidenceList;
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
		if (edgeArray != null) {
			System.arraycopy(edgeArray, 0, e, 0, edgeArray.length);
		}
		edgeArray = e;

		if (freeEdgeList == null) {
			freeEdgeList = new FreeIndexList(newSize);
		} else {
			freeEdgeList.expandBy(newSize - eMax);
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
		if (incidenceArray != null) {
			System.arraycopy(incidenceArray, 0, expandedArray, 0,
					incidenceArray.length);
		}
		if (getFreeIncidenceList() == null) {
			freeIncidenceList = new FreeIndexList(newSize);
		} else {
			getFreeIncidenceList().expandBy(newSize - vMax);
		}
		incidenceArray = expandedArray;
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
		if (vertexArray != null) {
			System.arraycopy(vertexArray, 0, expandedArray, 0,
					vertexArray.length);
		}
		if (freeVertexList == null) {
			freeVertexList = new FreeIndexList(newSize);
		} else {
			freeVertexList.expandBy(newSize - vMax);
		}
		vertexArray = expandedArray;
		vMax = newSize;
		notifyMaxVertexCountIncreased(newSize);
	}

	// ============================================================================
	// Methods to create complex values such as lists and maps
	// ============================================================================

	@Override
	public <T> PVector<T> createList() {
		return Empty.vector();
	}

	@Override
	public <K, V> PMap<K, V> createMap() {
		return Empty.map();
	}


	
	
	

	@Override
	public <T> PSet<T> createSet() {
		return Empty.set();
	}


}
