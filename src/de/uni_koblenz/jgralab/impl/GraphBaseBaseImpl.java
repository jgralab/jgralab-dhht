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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.GraphStructureChangedListener;
import de.uni_koblenz.jgralab.GraphStructureChangedListenerWithAutoRemove;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * Implementation of interface {@link Graph} with doubly linked lists realizing
 * eSeq, vSeq and lambdaSeq, while ensuring efficient direct access to vertices
 * and edges by id via {@link Vertex} and {@link Edge} arrays.
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class GraphBaseBaseImpl implements Graph {

	// ------------- GRAPH VARIABLES -------------

	/**
	 * TODO (Daniel B.): Implement incidence ids analogical to Vertex and Edge
	 * ids
	 */

	/**
	 * The GraphFactory that was used to create this graph. This factory will be
	 * used to create vertices and edges in this graph.
	 */
	protected GraphFactory graphFactory;

	/**
	 * Holds the version of the graph, for every modification (e.g. adding a
	 * vertex or edge or changing the vertex or edge sequence or changing of an
	 * attribute value), this version number is increased by 1, It is saved in
	 * the tg-file.
	 */
	private long graphVersion;

	// ------------- VERTEX LIST VARIABLES -------------

	/**
	 * number of vertices in the graph
	 */
	abstract protected void setVCount(int count);

	/**
	 * holds the id of the first vertex in Vseq
	 */
	abstract protected void setFirstVertex(VertexBaseImpl firstVertex);

	/**
	 * holds the id of the last vertex in Vseq
	 */
	abstract protected void setLastVertex(VertexBaseImpl lastVertex);

	/**
	 * Sets version of VSeq if it is different than previous version.
	 * 
	 * @param vertexListVersion
	 *            Version of VSeq.
	 */
	abstract protected void setVertexListVersion(long vertexListVersion);

	// ------------- EDGE LIST VARIABLES -------------

	/**
	 * number of edges in the graph
	 */
	abstract protected void setECount(int count);

	/**
	 * holds the id of the first edge in Eseq
	 */
	abstract protected void setFirstEdge(EdgeBaseImpl firstEdge);

	/**
	 * holds the id of the last edge in Eseq
	 */
	abstract protected void setLastEdge(EdgeBaseImpl lastEdge);

	/**
	 * Sets version of ESeq.
	 * 
	 * @param edgeListVersion
	 *            Version to set.
	 */
	abstract protected void setEdgeListVersion(long edgeListVersion);

	@Override
	public GraphElement<?, ?, ?> getContainingElement() {
		return null;
	}

	@Override
	public void initializeAttributesWithDefaultValues() {
		for (Attribute attr : getType().getAttributeList()) {
			try {
				if ((attr.getDefaultValueAsString() != null)
						&& !attr.getDefaultValueAsString().isEmpty()) {
					internalSetDefaultValue(attr);
				}
			} catch (GraphIOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param attr
	 * @throws GraphIOException
	 */
	protected void internalSetDefaultValue(Attribute attr)
			throws GraphIOException {
		attr.setDefaultValue(this);
	}

	/**
	 * Creates a graph of the given GraphClass with the given id
	 * 
	 * @param id
	 *            this Graph's id
	 * @param cls
	 *            the GraphClass of this Graph
	 */
	protected GraphBaseBaseImpl(String id, GraphClass cls) {

		graphFactory = cls.getSchema().getGraphFactory();
		// needed for initialization of graphVersion with transactions
		graphVersion = -1;
		setGraphVersion(0);

		setFirstVertex(null);
		setLastVertex(null);
		setVCount(0);

		setFirstEdge(null);
		setLastEdge(null);
		setECount(0);
	}

	@Override
	public void setTraversalContext(Graph traversalContext) {
		getCompleteGraph().setTraversalContext(traversalContext);
	}

	@Override
	public Graph getTraversalContext() {
		return getCompleteGraph().getTraversalContext();
	}

	@Override
	public void useAsTraversalContext() {
		getCompleteGraph().useAsTraversalContext();
	}

	@Override
	public void releaseTraversalContext() {
		getCompleteGraph().releaseTraversalContext();
	}

	protected void moveToSubordinateGraph(GraphElement<?, ?, ?> parent,
			GraphElement<?, ?, ?> child) {
		try {
			parent.addSubordinateElement((Vertex) child);
		} catch (ClassCastException e) {
			parent.addSubordinateElement((Edge) child);
		}
	}

	@Override
	public int compareTo(Graph a) {
		// TODO check for subordinateGraph
		if (a instanceof Graph) {
			Graph g = a;
			return hashCode() - g.hashCode();
		}
		return -1;
	}

	/**
	 * Creates an edge of the given class and adds this edge to the graph.
	 * <code>cls</code> has to be the "Impl" class.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends BinaryEdge> T createEdge(Class<T> cls, Vertex alpha,
			Vertex omega) {
		try {
			T edge = (T) internalCreateEdge(cls);
			IncidenceClass fromClass = null;
			IncidenceClass toClass = null;
			EdgeClass metaClass = edge.getType();
			assert (metaClass.isBinary());
			Set<IncidenceClass> incClasses = metaClass.getAllIncidenceClasses();
			for (IncidenceClass ic : incClasses) {
				if ((!ic.isAbstract())
						&& (ic.getDirection() == Direction.VERTEX_TO_EDGE)) {
					fromClass = ic;
				}
				if ((!ic.isAbstract())
						&& (ic.getDirection() == Direction.EDGE_TO_VERTEX)) {
					toClass = ic;
				}
			}
			connect(fromClass.getM1Class(), alpha, edge);
			connect(toClass.getM1Class(), omega, edge);
			return edge;
		} catch (Exception exception) {
			if (exception instanceof GraphException) {
				throw (GraphException) exception;
			} else {
				throw new GraphException("Error creating edge of class "
						+ cls.getName(), exception);
			}
		}
	}

	/**
	 * Creates an edge of the given class and adds this edge to the graph.
	 * <code>cls</code> has to be the "Impl" class.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Edge> T createEdge(Class<T> cls) {
		try {
			return (T) internalCreateEdge(cls);
		} catch (Exception exception) {
			if (exception instanceof GraphException) {
				throw (GraphException) exception;
			} else {
				throw new GraphException("Error creating edge of class "
						+ cls.getName(), exception);
			}
		}
	}

	protected Edge internalCreateEdge(Class<? extends Edge> cls) {
		return graphFactory.createEdge(cls, 0, this);
	}

	/**
	 * Creates a vertex of the given class and adds this edge to the graph.
	 * <code>cls</code> has to be the "Impl" class.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Vertex> T createVertex(Class<T> cls) {
		try {
			return (T) internalCreateVertex(cls);
		} catch (Exception ex) {
			if (ex instanceof GraphException) {
				throw (GraphException) ex;
			}
			throw new GraphException("Error creating vertex of class "
					+ cls.getName(), ex);
		}
	}

	protected Vertex internalCreateVertex(Class<? extends Vertex> cls) {
		return graphFactory.createVertex(cls, 0, this);
	}

	/**
	 * Changes the graph structure version, should be called whenever the
	 * structure of the graph is changed, for instance by creation and deletion
	 * or reordering of vertices and edges
	 */
	protected void edgeListModified() {
		setEdgeListVersion(getEdgeListVersion() + 1);
		setGraphVersion(getGraphVersion() + 1);
	}

	@Override
	abstract public int getECount();

	@Override
	abstract public long getEdgeListVersion();

	@Override
	abstract public Edge getFirstEdge();

	@Override
	abstract public Edge getLastEdge();

	@Override
	public Edge getFirstEdge(Class<? extends Edge> edgeClass) {
		assert edgeClass != null;
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
			currentEdge = currentEdge.getNextEdge();
		}
		return null;
	}

	@Override
	public Edge getFirstEdge(EdgeClass edgeClass) {
		assert edgeClass != null;
		return getFirstEdge(edgeClass.getM1Class(), false);
	}

	@Override
	public Edge getFirstEdge(EdgeClass edgeClass, boolean noSubclasses) {
		assert edgeClass != null;
		return getFirstEdge(edgeClass.getM1Class(), noSubclasses);
	}

	@Override
	abstract public Vertex getFirstVertex();

	@Override
	abstract public Vertex getLastVertex();

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
		return firstVertex.getNextVertex(vertexClass, noSubclasses);
	}

	@Override
	public Vertex getFirstVertex(VertexClass vertexClass) {
		assert vertexClass != null;
		return getFirstVertex(vertexClass, false);
	}

	@Override
	public Vertex getFirstVertex(VertexClass vertexClass, boolean noSubclasses) {
		assert vertexClass != null;
		return getFirstVertex(vertexClass.getM1Class(), noSubclasses);
	}

	@Override
	public GraphClass getGraphClass() {
		return getType();
	}

	@Override
	public long getGraphVersion() {
		return graphVersion;
	}

	@Override
	abstract public int getVCount();

	@Override
	abstract public long getVertexListVersion();

	/**
	 * Changes this graph's version. graphModified() is called whenever the
	 * graph is changed, all changes like adding, creating and reordering of
	 * edges and vertices or changes of attributes of the graph, an edge or a
	 * vertex are treated as a change.
	 */
	public void graphModified() {
		setGraphVersion(getGraphVersion() + 1);
	}

	@Override
	public boolean isEdgeListModified(long edgeListVersion) {
		return getEdgeListVersion() != edgeListVersion;
	}

	@Override
	public boolean isGraphModified(long previousVersion) {
		return getGraphVersion() != previousVersion;
	}

	@Override
	public boolean isVertexListModified(long previousVersion) {
		return getVertexListVersion() != previousVersion;
	}

	/**
	 * Sets the version counter of this graph. Should only be called immediately
	 * after loading.
	 * 
	 * @param graphVersion
	 *            new version value
	 */
	public void setGraphVersion(long graphVersion) {
		this.graphVersion = graphVersion;
	}

	/**
	 * Changes the vertex sequence version of this graph. Should be called
	 * whenever the vertex list of this graph is changed, for instance by
	 * creation and deletion or reordering of vertices.
	 */
	protected void vertexListModified() {
		setVertexListVersion(getVertexListVersion() + 1);
		setGraphVersion(getGraphVersion() + 1);
	}

	@Override
	public Iterable<Vertex> getVertices() {
		return new VertexIterable<Vertex>(this);
	}

	@Override
	public Iterable<Vertex> getVertices(Class<? extends Vertex> vertexClass) {
		return new VertexIterable<Vertex>(this, vertexClass);
	}

	@Override
	public Iterable<Vertex> getVertices(VertexClass vertexClass) {
		return new VertexIterable<Vertex>(this, vertexClass.getM1Class());
	}

	@Override
	public Iterable<Edge> getEdges() {
		return new EdgeIterable<Edge>(this);
	}

	@Override
	public Iterable<Edge> getEdges(Class<? extends Edge> edgeClass) {
		return new EdgeIterable<Edge>(this, edgeClass);
	}

	@Override
	public Iterable<Edge> getEdges(EdgeClass edgeClass) {
		return new EdgeIterable<Edge>(this, edgeClass.getM1Class());
	}

	// sort vertices
	@Override
	public void sortVertices(Comparator<Vertex> comp) {

		if (getFirstVertex() == null) {
			// no sorting required for empty vertex lists
			return;
		}
		class VertexList {
			VertexBaseImpl first;
			VertexBaseImpl last;

			public void add(VertexBaseImpl v) {
				if (first == null) {
					first = v;
					assert (last == null);
					last = v;
				} else {
					if (!hasSavememSupport()) {
						v.setPrevVertex(last);
					}
					last.setNextVertex(v);
					last = v;
				}
				v.setNextVertex(null);
			}

			public VertexBaseImpl remove() {
				if (first == null) {
					throw new NoSuchElementException();
				}
				VertexBaseImpl out;
				if (first == last) {
					out = first;
					first = null;
					last = null;
					return out;
				}
				out = first;
				first = (VertexBaseImpl) out.getNextVertex();
				if (!hasSavememSupport()) {
					first.setPrevVertex(null);
				}
				return out;
			}

			public boolean isEmpty() {
				assert ((first == null) == (last == null));
				return first == null;
			}

		}

		VertexList a = new VertexList();
		VertexList b = new VertexList();
		VertexList out = a;

		// split
		VertexBaseImpl last;
		VertexList l = new VertexList();
		l.first = (VertexBaseImpl) getFirstVertex();
		l.last = (VertexBaseImpl) getLastVertex();

		out.add(last = l.remove());
		while (!l.isEmpty()) {
			VertexBaseImpl current = l.remove();
			if (comp.compare(current, last) < 0) {
				out = (out == a) ? b : a;
			}
			out.add(current);
			last = current;
		}
		if (a.isEmpty() || b.isEmpty()) {
			out = a.isEmpty() ? b : a;
			setFirstVertex(out.first);
			setLastVertex(out.last);
			return;
		}

		while (true) {
			if (a.isEmpty() || b.isEmpty()) {
				out = a.isEmpty() ? b : a;
				setFirstVertex(out.first);
				setLastVertex(out.last);
				edgeListModified();
				return;
			}

			VertexList c = new VertexList();
			VertexList d = new VertexList();
			out = c;

			last = null;
			while (!a.isEmpty() && !b.isEmpty()) {
				int compareAToLast = last != null ? comp.compare(a.first, last)
						: 0;
				int compareBToLast = last != null ? comp.compare(b.first, last)
						: 0;

				if ((compareAToLast >= 0) && (compareBToLast >= 0)) {
					if (comp.compare(a.first, b.first) <= 0) {
						out.add(last = a.remove());
					} else {
						out.add(last = b.remove());
					}
				} else if ((compareAToLast < 0) && (compareBToLast < 0)) {
					out = (out == c) ? d : c;
					last = null;
				} else if ((compareAToLast < 0) && (compareBToLast >= 0)) {
					out.add(last = b.remove());
				} else {
					out.add(last = a.remove());
				}
			}

			// copy rest of A
			while (!a.isEmpty()) {
				VertexBaseImpl current = a.remove();
				if (comp.compare(current, last) < 0) {
					out = (out == c) ? d : c;
				}
				out.add(current);
				last = current;
			}

			// copy rest of B
			while (!b.isEmpty()) {
				VertexBaseImpl current = b.remove();
				if (comp.compare(current, last) < 0) {
					out = (out == c) ? d : c;
				}
				out.add(current);
				last = current;
			}

			a = c;
			b = d;
		}

	}

	// sort edges
	@Override
	public void sortEdges(Comparator<Edge> comp) {

		if (getFirstEdge() == null) {
			// no sorting required for empty edge lists
			return;
		}
		class EdgeList {
			EdgeBaseImpl first;
			EdgeBaseImpl last;

			public void add(EdgeBaseImpl e) {
				if (first == null) {
					first = e;
					assert (last == null);
					last = e;
				} else {
					if (!hasSavememSupport()) {
						e.setPreviousEdge(last);
					}
					last.setNextEdge(e);
					last = e;
				}
				e.setNextEdge(null);
			}

			public EdgeBaseImpl remove() {
				if (first == null) {
					throw new NoSuchElementException();
				}
				EdgeBaseImpl out;
				if (first == last) {
					out = first;
					first = null;
					last = null;
					return out;
				}
				out = first;
				first = (EdgeBaseImpl) out.getNextEdge();
				if (!hasSavememSupport()) {
					first.setPreviousEdge(null);
				}
				return out;
			}

			public boolean isEmpty() {
				assert ((first == null) == (last == null));
				return first == null;
			}

		}

		EdgeList a = new EdgeList();
		EdgeList b = new EdgeList();
		EdgeList out = a;

		// split
		EdgeBaseImpl last;
		EdgeList l = new EdgeList();
		l.first = (EdgeBaseImpl) getFirstEdge();
		l.last = (EdgeBaseImpl) getLastEdge();

		out.add(last = l.remove());
		while (!l.isEmpty()) {
			EdgeBaseImpl current = l.remove();
			if (comp.compare(current, last) < 0) {
				out = (out == a) ? b : a;
			}
			out.add(current);
			last = current;
		}
		if (a.isEmpty() || b.isEmpty()) {
			out = a.isEmpty() ? b : a;
			setFirstEdge(out.first);
			setLastEdge(out.last);
			return;
		}

		while (true) {
			if (a.isEmpty() || b.isEmpty()) {
				out = a.isEmpty() ? b : a;
				setFirstEdge(out.first);
				setLastEdge(out.last);
				edgeListModified();
				return;
			}

			EdgeList c = new EdgeList();
			EdgeList d = new EdgeList();
			out = c;

			last = null;
			while (!a.isEmpty() && !b.isEmpty()) {
				int compareAToLast = last != null ? comp.compare(a.first, last)
						: 0;
				int compareBToLast = last != null ? comp.compare(b.first, last)
						: 0;

				if ((compareAToLast >= 0) && (compareBToLast >= 0)) {
					if (comp.compare(a.first, b.first) <= 0) {
						out.add(last = a.remove());
					} else {
						out.add(last = b.remove());
					}
				} else if ((compareAToLast < 0) && (compareBToLast < 0)) {
					out = (out == c) ? d : c;
					last = null;
				} else if ((compareAToLast < 0) && (compareBToLast >= 0)) {
					out.add(last = b.remove());
				} else {
					out.add(last = a.remove());
				}
			}

			// copy rest of A
			while (!a.isEmpty()) {
				EdgeBaseImpl current = a.remove();
				if (comp.compare(current, last) < 0) {
					out = (out == c) ? d : c;
				}
				out.add(current);
				last = current;
			}

			// copy rest of B
			while (!b.isEmpty()) {
				EdgeBaseImpl current = b.remove();
				if (comp.compare(current, last) < 0) {
					out = (out == c) ? d : c;
				}
				out.add(current);
				last = current;
			}

			a = c;
			b = d;
		}

	}

	// handle GraphStructureChangedListener

	/**
	 * A list of all registered <code>GraphStructureChangedListener</code> as
	 * <i>WeakReference</i>s.
	 */
	protected List<WeakReference<GraphStructureChangedListener>> graphStructureChangedListenersWithAutoRemoval;
	protected List<GraphStructureChangedListener> graphStructureChangedListeners;
	{
		graphStructureChangedListenersWithAutoRemoval = null;
		graphStructureChangedListeners = new ArrayList<GraphStructureChangedListener>();
	}

	private void lazyCreateGraphStructureChangedListenersWithAutoRemoval() {
		if (graphStructureChangedListenersWithAutoRemoval == null) {
			graphStructureChangedListenersWithAutoRemoval = new LinkedList<WeakReference<GraphStructureChangedListener>>();
		}
	}

	@Override
	public void addGraphStructureChangedListener(
			GraphStructureChangedListener newListener) {
		assert newListener != null;
		if (newListener instanceof GraphStructureChangedListenerWithAutoRemove) {
			lazyCreateGraphStructureChangedListenersWithAutoRemoval();
			graphStructureChangedListenersWithAutoRemoval
					.add(new WeakReference<GraphStructureChangedListener>(
							newListener));
		} else {
			graphStructureChangedListeners.add(newListener);
		}
	}

	@Override
	public void removeGraphStructureChangedListener(
			GraphStructureChangedListener listener) {
		assert listener != null;
		if (listener instanceof GraphStructureChangedListenerWithAutoRemove) {
			Iterator<WeakReference<GraphStructureChangedListener>> iterator = getListenerListIteratorForAutoRemove();
			while (iterator.hasNext()) {
				GraphStructureChangedListener currentListener = iterator.next()
						.get();
				if ((currentListener == null) || (currentListener == listener)) {
					iterator.remove();
				}
			}
		} else {
			Iterator<GraphStructureChangedListener> iterator = getListenerListIterator();
			while (iterator.hasNext()) {
				GraphStructureChangedListener currentListener = iterator.next();
				if (currentListener == listener) {
					iterator.remove();
				}
			}
		}
	}

	protected void setAutoListenerListToNullIfEmpty() {
		if (graphStructureChangedListenersWithAutoRemoval.isEmpty()) {
			graphStructureChangedListenersWithAutoRemoval = null;
		}
	}

	@Override
	public void removeAllGraphStructureChangedListeners() {
		graphStructureChangedListenersWithAutoRemoval = null;
		graphStructureChangedListeners.clear();
	}

	@Override
	public int getGraphStructureChangedListenerCount() {
		return graphStructureChangedListenersWithAutoRemoval == null ? graphStructureChangedListeners
				.size() : graphStructureChangedListenersWithAutoRemoval.size()
				+ graphStructureChangedListeners.size();
	}

	protected Iterator<WeakReference<GraphStructureChangedListener>> getListenerListIteratorForAutoRemove() {
		return graphStructureChangedListenersWithAutoRemoval.iterator();
	}

	private Iterator<GraphStructureChangedListener> getListenerListIterator() {
		return graphStructureChangedListeners.iterator();
	}

	/**
	 * Notifies all registered <code>GraphStructureChangedListener</code> that
	 * the given vertex <code>v</code> is about to be deleted. All invalid
	 * <code>WeakReference</code>s are deleted automatically from the internal
	 * listener list.
	 * 
	 * @param v
	 *            the vertex that is about to be deleted.
	 */
	protected void notifyVertexDeleted(Vertex v) {
		assert (v != null) && v.isValid() && containsVertex(v);
		if (graphStructureChangedListenersWithAutoRemoval != null) {
			Iterator<WeakReference<GraphStructureChangedListener>> iterator = getListenerListIteratorForAutoRemove();
			while (iterator.hasNext()) {
				GraphStructureChangedListener currentListener = iterator.next()
						.get();
				if (currentListener == null) {
					iterator.remove();
				} else {
					currentListener.vertexDeleted(v);
				}
			}
			setAutoListenerListToNullIfEmpty();
		}
		int n = graphStructureChangedListeners.size();
		for (int i = 0; i < n; i++) {
			graphStructureChangedListeners.get(i).vertexDeleted(v);
		}
	}

	/**
	 * Notifies all registered <code>GraphStructureChangedListener</code> that
	 * the given vertex <code>v</code> has been created. All invalid
	 * <code>WeakReference</code>s are deleted automatically from the internal
	 * listener list.
	 * 
	 * @param v
	 *            the vertex that has been created.
	 */
	protected void notifyVertexAdded(Vertex v) {
		assert (v != null) && v.isValid() && containsVertex(v);
		if (graphStructureChangedListenersWithAutoRemoval != null) {
			Iterator<WeakReference<GraphStructureChangedListener>> iterator = getListenerListIteratorForAutoRemove();
			while (iterator.hasNext()) {
				GraphStructureChangedListener currentListener = iterator.next()
						.get();
				if (currentListener == null) {
					iterator.remove();
				} else {
					currentListener.vertexAdded(v);
				}
			}
			setAutoListenerListToNullIfEmpty();
		}
		int n = graphStructureChangedListeners.size();
		for (int i = 0; i < n; i++) {
			graphStructureChangedListeners.get(i).vertexAdded(v);
		}
	}

	/**
	 * Notifies all registered <code>GraphStructureChangedListener</code> that
	 * the given incidence <code>i</code> has been created. All invalid
	 * <code>WeakReference</code>s are deleted automatically from the internal
	 * listener list.
	 * 
	 * @param i
	 *            the incidence that has been created.
	 */
	protected void notifyIncidenceAdded(Incidence i) {
		if (graphStructureChangedListenersWithAutoRemoval != null) {
			Iterator<WeakReference<GraphStructureChangedListener>> iterator = getListenerListIteratorForAutoRemove();
			while (iterator.hasNext()) {
				GraphStructureChangedListener currentListener = iterator.next()
						.get();
				if (currentListener == null) {
					iterator.remove();
				} else {
					currentListener.incidenceAdded(i);
				}
			}
			setAutoListenerListToNullIfEmpty();
		}
		int n = graphStructureChangedListeners.size();
		for (int j = 0; j < n; j++) {
			graphStructureChangedListeners.get(j).incidenceAdded(i);
		}
	}

	/**
	 * Notifies all registered <code>GraphStructureChangedListener</code> that
	 * the given edge <code>e</code> is about to be deleted. All invalid
	 * <code>WeakReference</code>s are deleted automatically from the internal
	 * listener list.
	 * 
	 * @param e
	 *            the edge that is about to be deleted.
	 */
	protected void notifyEdgeDeleted(Edge e) {
		assert (e != null) && e.isValid() && containsEdge(e);
		if (graphStructureChangedListenersWithAutoRemoval != null) {
			Iterator<WeakReference<GraphStructureChangedListener>> iterator = getListenerListIteratorForAutoRemove();
			while (iterator.hasNext()) {
				GraphStructureChangedListener currentListener = iterator.next()
						.get();
				if (currentListener == null) {
					iterator.remove();
				} else {
					currentListener.edgeDeleted(e);
				}
			}
			setAutoListenerListToNullIfEmpty();
		}
		int n = graphStructureChangedListeners.size();
		for (int i = 0; i < n; i++) {
			graphStructureChangedListeners.get(i).edgeDeleted(e);
		}
	}

	/**
	 * Notifies all registered <code>GraphStructureChangedListener</code> that
	 * the given edge <code>e</code> has been created. All invalid
	 * <code>WeakReference</code>s are deleted automatically from the internal
	 * listener list.
	 * 
	 * @param e
	 *            the edge that has been created.
	 */
	protected void notifyEdgeAdded(Edge e) {
		assert (e != null) && e.isValid() && containsEdge(e);
		if (graphStructureChangedListenersWithAutoRemoval != null) {
			Iterator<WeakReference<GraphStructureChangedListener>> iterator = getListenerListIteratorForAutoRemove();
			while (iterator.hasNext()) {
				GraphStructureChangedListener currentListener = iterator.next()
						.get();
				if (currentListener == null) {
					iterator.remove();
				} else {
					currentListener.edgeAdded(e);
				}
			}
			setAutoListenerListToNullIfEmpty();
		}
		int n = graphStructureChangedListeners.size();
		for (int i = 0; i < n; i++) {
			graphStructureChangedListeners.get(i).edgeAdded(e);
		}
	}

	@Override
	public <T extends Incidence> T connect(Class<T> cls, Vertex vertex,
			Edge edge) {
		return vertex.connect(cls, edge);
	}

}
