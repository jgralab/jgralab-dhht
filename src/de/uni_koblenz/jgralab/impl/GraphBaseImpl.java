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

package de.uni_koblenz.jgralab.impl;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphStructureChangedListener;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.std.IncidenceImpl;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * Implementation of interface {@link Graph} with doubly linked lists realizing
 * eSeq, vSeq and lambdaSeq, while ensuring efficient direct access to vertices
 * and edges by id via {@link Vertex} and {@link Edge} arrays.
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class GraphBaseImpl extends GraphBaseBaseImpl {

	// ------------- GRAPH VARIABLES -------------

	/**
	 * the unique id of the graph in the schema
	 */
	private String id;

	/**
	 * The schema this graph belongs to
	 */
	private final Schema schema;

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
	 * indexed with vertex-id, holds the actual vertex-object itself
	 */
	abstract protected VertexBaseImpl[] getVertexArray();

	abstract protected void setVertexArray(VertexBaseImpl[] vertex);

	/**
	 * free index list for vertices
	 */
	protected FreeIndexList freeVertexList;

	protected FreeIndexList getFreeVertexList() {
		return freeVertexList;
	}

	/**
	 * List of vertices to be deleted by a cascading delete caused by deletion
	 * of a composition "parent".
	 */
	abstract protected List<VertexBaseImpl> getDeleteVertexList();

	abstract protected void setDeleteVertexList(
			List<VertexBaseImpl> deleteVertexList);

	// ------------- EDGE LIST VARIABLES -------------

	/**
	 * maximum number of edges
	 */
	protected int eMax;

	/**
	 * indexed with edge-id, holds the actual edge-object itself
	 */
	abstract protected EdgeBaseImpl[] getEdgeArray();

	abstract protected void setEdgeArray(EdgeBaseImpl[] edge);

	/**
	 * free index list for edges
	 */
	protected FreeIndexList freeEdgeList;

	protected FreeIndexList getFreeEdgeList() {
		return freeEdgeList;
	}

	// ------------- INCIDENCE LIST VARIABLES -------------

	private int iMax;

	/**
	 * indexed with vertex-id, holds the actual vertex-object itself
	 */
	abstract protected IncidenceBaseImpl[] getIncidenceArray();

	abstract protected void setIncidenceArray(IncidenceBaseImpl[] incidence);

	/**
	 * free index list for vertices
	 */
	protected FreeIndexList freeIncidenceList;

	protected FreeIndexList getFreeIncidenceList() {
		return freeIncidenceList;
	}

	/**
	 * Creates a graph of the given GraphClass with the given id
	 * 
	 * @param id
	 *            this Graph's id
	 * @param cls
	 *            the GraphClass of this Graph
	 */
	protected GraphBaseImpl(String id, GraphClass cls) {
		this(id, cls, 1000, 1000);
	}

	/**
	 * @param id
	 *            this Graph's id
	 * @param cls
	 *            the GraphClass of this Graph
	 * @param vMax
	 *            initial maximum number of vertices
	 * @param eMax
	 *            initial maximum number of edges
	 */
	protected GraphBaseImpl(String id, GraphClass cls, int vMax, int eMax) {
		super(id, cls);
		if (vMax < 1) {
			throw new GraphException("vMax must not be less than 1", null);
		}
		if (eMax < 1) {
			throw new GraphException("eMax must not be less than 1", null);
		}

		schema = cls.getSchema();

		expandVertexArray(vMax);
		setDeleteVertexList(new LinkedList<VertexBaseImpl>());

		expandEdgeArray(eMax);
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

		EdgeBaseImpl e = (EdgeBaseImpl) newEdge;

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

	protected void internalEdgeAdded(EdgeBaseImpl e) {
		notifyEdgeAdded(e);
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
		VertexBaseImpl v = (VertexBaseImpl) newVertex;

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

	protected void internalVertexAdded(VertexBaseImpl v) {
		notifyVertexAdded(v);
	}

	protected void internalIncidenceAdded(IncidenceBaseImpl i) {
		notifyIncidenceAdded(i);
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
		IncidenceBaseImpl i = (IncidenceBaseImpl) newIncidence;

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
	 * Appends the edge e to the global edge sequence of this graph.
	 * 
	 * @param e
	 *            an edge
	 */
	protected void appendEdgeToESeq(EdgeBaseImpl e) {
		getEdgeArray()[e.id] = e;
		setECount(getECount() + 1);
		if (getFirstEdge() == null) {
			setFirstEdge(e);
		}
		if (getLastEdge() != null) {
			((EdgeBaseImpl) getLastEdge()).setNextEdge(e);
			if (!hasSavememSupport()) {
				e.setPreviousEdge(getLastEdge());
			}
		}
		setLastEdge(e);
	}

	/**
	 * Appends the vertex v to the global vertex sequence of this graph.
	 * 
	 * @param v
	 *            a vertex
	 */
	protected void appendVertexToVSeq(VertexBaseImpl v) {
		getVertexArray()[v.id] = v;
		setVCount(getVCount() + 1);
		if (getFirstVertex() == null) {
			setFirstVertex(v);
		}
		if (getLastVertex() != null) {
			((VertexBaseImpl) getLastVertex()).setNextVertex(v);
			if (!hasSavememSupport()) {
				v.setPrevVertex(getLastVertex());
			}
		}
		setLastVertex(v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Graph#getExpandedVertexCount()
	 */
	public int getExpandedVertexCount() {
		return computeNewSize(vMax);
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
	 * @see
	 * de.uni_koblenz.jgralab.Graph#containsEdge(de.uni_koblenz.jgralab.Edge)
	 */
	@Override
	public boolean containsEdge(Edge e) {
		return (e != null) && (e.getGraph() == this)
				&& containsEdgeId(((EdgeBaseImpl) e).id);
	}

	/**
	 * Checks if the edge id eId is valid and if there is an such an edge in
	 * this graph.
	 * 
	 * @param eId
	 *            an edge id
	 * @return true if this graph contains an edge with id eId
	 */
	private final boolean containsEdgeId(int eId) {
		return (eId > 0) && (eId <= eMax) && (getEdgeArray()[eId] != null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Graph#containsVertex(de.uni_koblenz.jgralab.Vertex
	 */
	@Override
	public boolean containsVertex(Vertex v) {
		VertexBaseImpl[] vertex = getVertexArray();
		return (v != null) && (v.getGraph() == this)
				&& containsVertexId(((VertexBaseImpl) v).id)
				&& (vertex[((VertexBaseImpl) v).id] == v);
	}

	/**
	 * Checks if the vertex id evd is valid and if there is an such a vertex in
	 * this graph.
	 * 
	 * @param vId
	 *            a vertex id
	 * @return true if this graph contains a vertex with id vId
	 */
	private final boolean containsVertexId(int vId) {
		return (vId > 0) && (vId <= vMax) && (getVertexArray()[vId] != null);
	}

	/**
	 * Checks if the incidence id iId is valid and if there is an such an
	 * incidence in this graph.
	 * 
	 * @param iId
	 *            a incidence id
	 * @return true if this graph contains an incidence with id iId
	 */
	private final boolean containsIncidenceId(int iId) {
		return (iId > 0) && (iId <= vMax) && (getIncidenceArray()[iId] != null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Graph#deleteEdge(de.uni_koblenz.jgralab.Edge)
	 */
	@Override
	public void deleteEdge(Edge e) {
		assert (e != null) && e.isValid() && containsEdge(e);
		internalDeleteEdge(e);
		edgeListModified();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Graph#deleteVertex(de.uni_koblenz.jgralab.Vertex)
	 */
	@Override
	public void deleteVertex(Vertex v) {
		assert (v != null) && v.isValid() && containsVertex(v);

		getDeleteVertexList().add((VertexBaseImpl) v);
		internalDeleteVertex();
	}

	/**
	 * Callback function for triggered actions just after the edge
	 * <code>e</code> was deleted from this Graph. Override this method to
	 * implement user-defined behaviour upon deletion of edges. Note that any
	 * changes to this graph are forbidden.
	 * 
	 * Needed for transaction support.
	 * 
	 * @param e
	 *            the deleted Edge
	 * @param oldAlpha
	 *            the alpha-vertex before deletion
	 * @param oldOmega
	 *            the omega-vertex before deletion
	 */
	protected void edgeAfterDeleted(Edge e) {
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

		EdgeBaseImpl[] e = new EdgeBaseImpl[newSize + 1];
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
		VertexBaseImpl[] expandedArray = new VertexBaseImpl[newSize + 1];
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
		IncidenceBaseImpl[] expandedArray = new IncidenceBaseImpl[newSize + 1];
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

	private void setFreeIncidenceList(FreeIndexList freeIndexList) {
		this.freeIncidenceList = freeIndexList;
	}

	@Override
	public Edge getEdge(int eId) {
		assert eId != 0 : "The edge id must be != 0, given was " + eId;
		try {
			return getEdgeArray()[eId];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Graph#getId()
	 */
	@Override
	public String getId() {
		return id;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.AttributedElement#getSchema()
	 */
	@Override
	public Schema getSchema() {
		return schema;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Graph#getVertex(int)
	 */
	@Override
	public Vertex getVertex(int vId) {
		assert (vId > 0) : "The vertex id must be > 0, given was " + vId;
		try {
			return getVertexArray()[vId];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Deletes the edge from the internal structures of this graph.
	 * 
	 * @param edge
	 *            an edge
	 */
	private void internalDeleteEdge(Edge edge) {
		assert (edge != null) && edge.isValid() && containsEdge(edge);

		EdgeBaseImpl e = (EdgeBaseImpl) edge;
		internalEdgeDeleted(e);

		Incidence inc = e.getFirstIncidence();
		Set<Vertex> vertices = new HashSet<Vertex>();
		while (inc != null) {
			vertices.add(inc.getVertex());
			((VertexBaseImpl) inc.getVertex())
					.removeIncidenceFromLambdaSeq((IncidenceImpl) inc);
			inc = e.getFirstIncidence();
		}
		for (Vertex vertex : vertices) {
			((VertexBaseImpl) vertex).incidenceListModified();
		}

		removeEdgeFromESeq(e);
		edgeListModified();
		edgeAfterDeleted(e);
	}

	protected void internalEdgeDeleted(EdgeBaseImpl e) {
		assert e != null;
		notifyEdgeDeleted(e);
	}

	/**
	 * Deletes all vertices in deleteVertexList from the internal structures of
	 * this graph. Possibly, cascading deletes of child vertices occur when
	 * parent vertices of Composition classes are deleted.
	 */
	private void internalDeleteVertex() {
		boolean edgeHasBeenDeleted = false;
		while (!getDeleteVertexList().isEmpty()) {
			VertexBaseImpl v = getDeleteVertexList().remove(0);
			assert (v != null) && v.isValid() && containsVertex(v);
			internalVertexDeleted(v);
			// delete all incident edges including incidence objects
			Incidence inc = v.getFirstIncidence();

			Set<EdgeBaseImpl> edges = new HashSet<EdgeBaseImpl>();
			while (inc != null) {
				EdgeBaseImpl edge = (EdgeBaseImpl) inc.getEdge();
				boolean deleteEdge = false;
				if (edge.isBinary()) {
					BinaryEdge bedge = (BinaryEdge) edge;
					if (bedge.getAlpha() == v) {
						if (bedge.getOmegaSemantics() == IncidenceType.COMPOSITION) {
							VertexBaseImpl omega = (VertexBaseImpl) bedge
									.getOmega();
							if ((omega != v) && containsVertex(omega)
									&& !getDeleteVertexList().contains(omega)) {
								getDeleteVertexList().add(omega);
								removeEdgeFromESeq((EdgeBaseImpl) bedge);
								edgeAfterDeleted(bedge);
								deleteEdge = true;
							}
						}
					} else if (bedge.getOmega() == v) {
						if (bedge.getAlphaSemantics() == IncidenceType.COMPOSITION) {
							VertexBaseImpl alpha = (VertexBaseImpl) bedge
									.getAlpha();
							if ((alpha != v) && containsVertex(alpha)
									&& !getDeleteVertexList().contains(alpha)) {
								getDeleteVertexList().add(alpha);
								removeEdgeFromESeq((EdgeBaseImpl) bedge);
								edgeAfterDeleted(bedge);
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
			for (EdgeBaseImpl edge : edges) {
				edge.incidenceListModified();
			}
			removeVertexFromVSeq(v);
			vertexAfterDeleted(v);
		}
		vertexListModified();
		if (edgeHasBeenDeleted) {
			edgeListModified();
		}
	}

	protected void internalVertexDeleted(VertexBaseImpl v) {
		assert v != null;
		notifyVertexDeleted(v);
	}

	/**
	 * Removes the vertex v from the global vertex sequence of this graph.
	 * 
	 * @param v
	 *            a vertex
	 */
	protected void removeVertexFromVSeq(VertexBaseImpl v) {
		assert v != null;
		if (v == getFirstVertex()) {
			// delete at head of vertex list
			setFirstVertex((VertexBaseImpl) v.getNextVertex());
			if (getFirstVertex() != null) {
				if (!hasSavememSupport()) {
					((VertexBaseImpl) getFirstVertex()).setPrevVertex(null);
				}
			}
			if (v == getLastVertex()) {
				// this vertex was the only one...
				setLastVertex(null);
			}
		} else if (v == getLastVertex()) {
			// delete at tail of vertex list
			setLastVertex((VertexBaseImpl) v.getPreviousVertex());
			if (getLastVertex() != null) {
				((VertexBaseImpl) getLastVertex()).setNextVertex(null);
			}
		} else {
			// delete somewhere in the middle
			((VertexBaseImpl) v.getPreviousVertex()).setNextVertex(v
					.getNextVertex());
			if (!hasSavememSupport()) {
				((VertexBaseImpl) v.getNextVertex()).setPrevVertex(v
						.getPreviousVertex());
			}
		}
		// freeIndex(getFreeVertexList(), v.getId());
		freeVertexIndex(v.getId());
		getVertexArray()[v.getId()] = null;
		if (!hasSavememSupport()) {
			v.setPrevVertex(null);
		}
		v.setNextVertex(null);
		v.setId(0);
		setVCount(getVCount() - 1);
	}

	/**
	 * Removes the edge e from the global edge sequence of this graph.
	 * 
	 * @param e
	 *            an edge
	 */
	protected void removeEdgeFromESeq(EdgeBaseImpl e) {
		assert e != null;
		removeEdgeFromESeqWithoutDeletingIt(e);

		// freeIndex(getFreeEdgeList(), e.getId());
		freeEdgeIndex(e.getId());
		getEdgeArray()[e.getId()] = null;
		if (!hasSavememSupport()) {
			e.setPreviousEdge(null);
		}
		e.setNextEdge(null);
		e.setId(0);
		setECount(getECount() - 1);
	}

	protected void removeEdgeFromESeqWithoutDeletingIt(EdgeBaseImpl e) {
		if (e == getFirstEdge()) {
			// delete at head of edge list
			setFirstEdge((EdgeBaseImpl) e.getNextEdge());
			if (getFirstEdge() != null) {
				if (!hasSavememSupport()) {
					((EdgeBaseImpl) getFirstEdge()).setPreviousEdge(null);
				}
			}
			if (e == getLastEdge()) {
				// this edge was the only one...
				setLastEdge(null);
			}
		} else if (e == getLastEdge()) {
			// delete at tail of edge list
			setLastEdge((EdgeBaseImpl) e.getPreviousEdge());
			if (getLastEdge() != null) {
				((EdgeBaseImpl) getLastEdge()).setNextEdge(null);
			}
		} else {
			// delete somewhere in the middle
			((EdgeBaseImpl) e.getPreviousEdge()).setNextEdge(e.getNextEdge());
			if (!hasSavememSupport()) {
				((EdgeBaseImpl) e.getNextEdge()).setPreviousEdge(e
						.getPreviousEdge());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Graph#isLoading()
	 */
	@Override
	public boolean isLoading() {
		return loading;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Graph#loadingCompleted()
	 */
	@Override
	public void loadingCompleted() {
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
	protected void putEdgeAfterInGraph(EdgeBaseImpl targetEdge,
			EdgeBaseImpl movedEdge) {
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
			setFirstEdge((EdgeBaseImpl) movedEdge.getNextEdge());
			if (!hasSavememSupport()) {
				((EdgeBaseImpl) movedEdge.getNextEdge()).setPreviousEdge(null);
			}
		} else if (movedEdge == getLastEdge()) {
			setLastEdge((EdgeBaseImpl) movedEdge.getPreviousEdge());
			((EdgeBaseImpl) movedEdge.getPreviousEdge()).setNextEdge(null);
		} else {
			((EdgeBaseImpl) movedEdge.getPreviousEdge()).setNextEdge(movedEdge
					.getNextEdge());
			if (!hasSavememSupport()) {
				((EdgeBaseImpl) movedEdge.getNextEdge())
						.setPreviousEdge(movedEdge.getPreviousEdge());
			}
		}

		// insert moved edge in eSeq immediately after target
		if (targetEdge == getLastEdge()) {
			setLastEdge(movedEdge);
			movedEdge.setNextEdge(null);
		} else {
			if (!hasSavememSupport()) {
				((EdgeBaseImpl) targetEdge.getNextEdge())
						.setPreviousEdge(movedEdge);
			}
			movedEdge.setNextEdge(targetEdge.getNextEdge());
		}
		if (!hasSavememSupport()) {
			movedEdge.setPreviousEdge(targetEdge);
		}
		targetEdge.setNextEdge(movedEdge);
		edgeListModified();
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
	protected void putVertexAfter(VertexBaseImpl targetVertex,
			VertexBaseImpl movedVertex) {
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
			VertexBaseImpl newFirstVertex = (VertexBaseImpl) movedVertex
					.getNextVertex();
			setFirstVertex(newFirstVertex);
			if (!hasSavememSupport()) {
				newFirstVertex.setPrevVertex(null);
				// ((VertexImpl)
				// movedVertex.getNextVertex()).setPrevVertex(null);
			}
		} else if (movedVertex == getLastVertex()) {
			setLastVertex((VertexBaseImpl) movedVertex.getPreviousVertex());
			((VertexBaseImpl) movedVertex.getPreviousVertex())
					.setNextVertex(null);
		} else {
			((VertexBaseImpl) movedVertex.getPreviousVertex())
					.setNextVertex(movedVertex.getNextVertex());
			if (!hasSavememSupport()) {
				((VertexBaseImpl) movedVertex.getNextVertex())
						.setPrevVertex(movedVertex.getPreviousVertex());
			}
		}

		// insert moved vertex in vSeq immediately after target
		if (targetVertex == getLastVertex()) {
			setLastVertex(movedVertex);
			movedVertex.setNextVertex(null);
		} else {
			if (!hasSavememSupport()) {
				((VertexBaseImpl) targetVertex.getNextVertex())
						.setPrevVertex(movedVertex);
			}
			movedVertex.setNextVertex(targetVertex.getNextVertex());
		}
		if (!hasSavememSupport()) {
			movedVertex.setPrevVertex(targetVertex);
		}
		targetVertex.setNextVertex(movedVertex);
		vertexListModified();
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
	protected void putEdgeBeforeInGraph(EdgeBaseImpl targetEdge,
			EdgeBaseImpl movedEdge) {
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
			if (!hasSavememSupport()) {
				movedEdge.setPreviousEdge(null);
			}
		} else {
			EdgeBaseImpl previousEdge = ((EdgeBaseImpl) targetEdge
					.getPreviousEdge());
			previousEdge.setNextEdge(movedEdge);
			if (!hasSavememSupport()) {
				movedEdge.setPreviousEdge(previousEdge);
			}
		}
		movedEdge.setNextEdge(targetEdge);
		if (!hasSavememSupport()) {
			targetEdge.setPreviousEdge(movedEdge);
		}
		edgeListModified();
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
	protected void putVertexBefore(VertexBaseImpl targetVertex,
			VertexBaseImpl movedVertex) {
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
			setFirstVertex((VertexBaseImpl) movedVertex.getNextVertex());
			if (!hasSavememSupport()) {
				((VertexBaseImpl) movedVertex.getNextVertex())
						.setPrevVertex(null);
			}
		} else if (movedVertex == getLastVertex()) {
			setLastVertex((VertexBaseImpl) movedVertex.getPreviousVertex());
			((VertexBaseImpl) movedVertex.getPreviousVertex())
					.setNextVertex(null);
		} else {
			((VertexBaseImpl) movedVertex.getPreviousVertex())
					.setNextVertex(movedVertex.getNextVertex());
			if (!hasSavememSupport()) {
				((VertexBaseImpl) movedVertex.getNextVertex())
						.setPrevVertex(movedVertex.getPreviousVertex());
			}
		}

		// insert moved vertex in vSeq immediately before target
		if (targetVertex == getFirstVertex()) {
			setFirstVertex(movedVertex);
			if (!hasSavememSupport()) {
				movedVertex.setPrevVertex(null);
			}
		} else {
			VertexBaseImpl previousVertex = (VertexBaseImpl) targetVertex
					.getPreviousVertex();
			previousVertex.setNextVertex(movedVertex);
			if (!hasSavememSupport()) {
				movedVertex.setPrevVertex(previousVertex);
			}
		}
		movedVertex.setNextVertex(targetVertex);
		if (!hasSavememSupport()) {
			targetVertex.setPrevVertex(movedVertex);
		}
		vertexListModified();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Graph#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
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
	 * Callback function for triggered actions just after the vertex
	 * <code>v</code> was deleted from this Graph. Override this method to
	 * implement user-defined behaviour upon deletion of vertices. Note that any
	 * changes to this graph are forbidden.
	 * 
	 * @param v
	 *            the deleted vertex
	 */
	abstract protected void vertexAfterDeleted(Vertex v);

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
					VertexBaseImpl v = getVertexArray()[vId];
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
				VertexBaseImpl[] newVertex = new VertexBaseImpl[vMax + 1];
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
					EdgeBaseImpl e = getEdgeArray()[eId];
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
				EdgeBaseImpl[] newEdge = new EdgeBaseImpl[eMax + 1];
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
					IncidenceBaseImpl i = getIncidenceArray()[iId];
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
				IncidenceBaseImpl[] newIncidence = new IncidenceBaseImpl[iMax + 1];
				System.arraycopy(getIncidenceArray(), 0, newIncidence, 0,
						newIncidence.length);
				setIncidenceArray(newIncidence);
				System.gc();
			}
			graphModified();
			System.gc();
		}
	}

	// access to <code>FreeIndexList</code>s with these functions
	// abstract protected void freeIndex(FreeIndexList freeIndexList, int
	// index);

	/**
	 * Use to free an <code>Edge</code>-index
	 * 
	 * @param index
	 */
	abstract protected void freeEdgeIndex(int index);

	/**
	 * Use to free a <code>Vertex</code>-index.
	 * 
	 * @param index
	 */
	abstract protected void freeVertexIndex(int index);

	/**
	 * Use to allocate a <code>Vertex</code>-index.
	 * 
	 * @param currentId
	 *            needed for transaction support
	 */
	abstract protected int allocateVertexIndex(int currentId);

	/**
	 * Use to allocate a <code>Edge</code>-index.
	 * 
	 * @param currentId
	 *            needed for transaction support
	 */
	abstract protected int allocateEdgeIndex(int currentId);

	/**
	 * Use to allocate a <code>Incidence</code>-index.
	 * 
	 * @param currentId
	 *            needed for transaction support
	 */
	abstract protected int allocateIncidenceIndex(int currentId);

	/**
	 * 
	 * @param freeVertexList
	 */
	protected void setFreeVertexList(FreeIndexList freeVertexList) {
		this.freeVertexList = freeVertexList;
	}

	/**
	 * 
	 * @param freeEdgeList
	 */
	protected void setFreeEdgeList(FreeIndexList freeEdgeList) {
		this.freeEdgeList = freeEdgeList;
	}

	// handle GraphStructureChangedListener

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

	protected boolean canAddGraphElement(int graphElementId) {
		return graphElementId == 0;
	}

	@Override
	public <T extends Incidence> T connect(Class<T> cls, Vertex vertex,
			Edge edge) {
		return vertex.connect(cls, edge);
	}

	protected abstract void setICount(int count);

}
