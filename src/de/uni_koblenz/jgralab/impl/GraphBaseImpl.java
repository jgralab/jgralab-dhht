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
import de.uni_koblenz.jgralab.JGraLabServer;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * Implementation of interface {@link Graph} with doubly linked lists realizing
 * eSeq, vSeq and lambdaSeq, while ensuring efficient direct access to vertices
 * and edges by id via {@link Vertex} and {@link Edge} arrays.
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class GraphBaseImpl implements Graph {
	
	static final int PARTIAL_GRAPH_MASK = 0xff000000; 
	
	static final int LOCAL_ELEMENT_MASK = 0xffffffff ^ PARTIAL_GRAPH_MASK;
		
	public static final int getLocalId(int graphElementId) {
		return graphElementId & LOCAL_ELEMENT_MASK;
	}
	
	public static final int getPartialGraphId(int graphElementId) {
		return graphElementId & PARTIAL_GRAPH_MASK;
	}
	
	public static final int getGlobalId(int partialGraphId, int localElementId) {
		assert getPartialGraphId(localElementId) == 0;
		return partialGraphId & localElementId;
	}
	
	
	// ------------- PARTIAL GRAPH VARIABLES ------------
	
	protected List<PartialGraphImpl> partialGraphs = null;
	
	
	// ------------- VERTEX LIST VARIABLES -------------
	
	private VertexImpl firstVertex;
	private VertexImpl lastVertex;

	/**
	 * number of vertices in the graph
	 */
	abstract protected void setVCount(int count);
	
	@Override
	public Vertex getFirstVertex() {
		return firstVertex;
	}
	
	@Override
	public Vertex getLastVertex() {
		return lastVertex;
	}

	/**
	 * holds the id of the first vertex in Vseq
	 */
	protected void setFirstVertex(VertexImpl firstVertex) {
		this.firstVertex = firstVertex;
	}

	/**
	 * holds the id of the last vertex in Vseq
	 */
	protected void setLastVertex(VertexImpl lastVertex) {
		this.lastVertex = lastVertex;
	}

	/**
	 * Sets version of VSeq if it is different than previous version.
	 * 
	 * @param vertexListVersion
	 *            Version of VSeq.
	 */
	//abstract protected void setVertexListVersion(long vertexListVersion);

	// ------------- EDGE LIST VARIABLES -------------
	
	private EdgeImpl firstEdge;
	private EdgeImpl lastEdge;

	/**
	 * number of edges in the graph
	 */
	abstract protected void setECount(int count);
	
	@Override
	public Edge getFirstEdge() {
		return firstEdge;
	}

	@Override
	public Edge getLastEdge() {
		return lastEdge;
	}

	/**
	 * holds the id of the first edge in Eseq
	 */
	protected void setFirstEdge(EdgeImpl firstEdge) {
		this.firstEdge = firstEdge;
	}

	/**
	 * holds the id of the last edge in Eseq
	 */
	protected void setLastEdge(EdgeImpl lastEdge) {
		this.lastEdge = lastEdge;
	}

	/**
	 * Sets version of ESeq.
	 * 
	 * @param edgeListVersion
	 *            Version to set.
	 */
	//abstract protected void setEdgeListVersion(long edgeListVersion);

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
	protected GraphBaseImpl(GraphClass cls) {
		setFirstVertex(null);
		setLastVertex(null);
		setVCount(0);

		setFirstEdge(null);
		setLastEdge(null);
		setECount(0);
	}

	
	/*
	 * Sets <code>traversalContext</code> as the traversal context.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 */
	protected void setTraversalContext(Graph traversalContext) {
		((GraphBaseImpl)getCompleteGraph()).setTraversalContext(traversalContext);
	}

	@Override
	public Graph getTraversalContext() {
		return getCompleteGraph().getTraversalContext();
	}

	@Override
	public void useAsTraversalContext() {
		((GraphBaseImpl)getCompleteGraph()).setTraversalContext(this);
	//	getCompleteGraph().useAsTraversalContext();
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
		return getGraphFactory().createEdge(cls, 0, this);
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
		return getGraphFactory().createVertex(cls, 0, this);
	}

	
	protected abstract void edgeListModified();
	
	
	protected abstract void vertexListModified();



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
	abstract public int getVCount();

	@Override
	abstract public long getVertexListVersion();



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
	 * Changes this graph's version. graphModified() is called whenever the
	 * graph is changed, all changes like adding, creating and reordering of
	 * edges and vertices or changes of attributes of the graph, an edge or a
	 * vertex are treated as a change. 
	 */
	public abstract void graphModified();



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
			VertexImpl first;
			VertexImpl last;

			public void add(VertexImpl v) {
				if (first == null) {
					first = v;
					assert (last == null);
					last = v;
				} else {
				//	if (!hasSavememSupport()) {
						v.setPreviousVertex(last);
				//	}
					last.setNextVertex(v);
					last = v;
				}
				v.setNextVertex(null);
			}

			public VertexImpl remove() {
				if (first == null) {
					throw new NoSuchElementException();
				}
				VertexImpl out;
				if (first == last) {
					out = first;
					first = null;
					last = null;
					return out;
				}
				out = first;
				first = (VertexImpl) out.getNextVertex();
			//	if (!hasSavememSupport()) {
					first.setPreviousVertex(null);
			//	}
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
		VertexImpl last;
		VertexList l = new VertexList();
		l.first = (VertexImpl) getFirstVertex();
		l.last = (VertexImpl) getLastVertex();

		out.add(last = l.remove());
		while (!l.isEmpty()) {
			VertexImpl current = l.remove();
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
				VertexImpl current = a.remove();
				if (comp.compare(current, last) < 0) {
					out = (out == c) ? d : c;
				}
				out.add(current);
				last = current;
			}

			// copy rest of B
			while (!b.isEmpty()) {
				VertexImpl current = b.remove();
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
			EdgeImpl first;
			EdgeImpl last;

			public void add(EdgeImpl e) {
				if (first == null) {
					first = e;
					assert (last == null);
					last = e;
				} else {
			//		if (!hasSavememSupport()) {
						e.setPreviousEdge(last);
			//		}
					last.setNextEdge(e);
					last = e;
				}
				e.setNextEdge(null);
			}

			public EdgeImpl remove() {
				if (first == null) {
					throw new NoSuchElementException();
				}
				EdgeImpl out;
				if (first == last) {
					out = first;
					first = null;
					last = null;
					return out;
				}
				out = first;
				first = (EdgeImpl) out.getNextEdge();
			//	if (!hasSavememSupport()) {
					first.setPreviousEdge(null);
			//	}
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
		EdgeImpl last;
		EdgeList l = new EdgeList();
		l.first = (EdgeImpl) getFirstEdge();
		l.last = (EdgeImpl) getLastEdge();

		out.add(last = l.remove());
		while (!l.isEmpty()) {
			EdgeImpl current = l.remove();
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
				EdgeImpl current = a.remove();
				if (comp.compare(current, last) < 0) {
					out = (out == c) ? d : c;
				}
				out.add(current);
				last = current;
			}

			// copy rest of B
			while (!b.isEmpty()) {
				EdgeImpl current = b.remove();
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

	protected abstract void setICount(int count);
	
	/**
	 * Adds a partial graph on the given host to the sequence
	 * of partial graphs and returns a local proxy  
	 * @param hostname name of the host running the remote JGraLab instance
	 * @return
	 */
	protected Graph createPartialGraph(String hostname) {
		JGraLabServer remote = JGraLabServerImpl.getLocalInstance().getRemoteInstance(hostname);
		Schema s = remote.getSchema(getSchema().getQualifiedName());
		PartialGraphImpl partialGraph = s.getGraphFactory().createPartialGraph(this);
		
		if (partialGraphs == null) {
			partialGraphs = new ArrayList<PartialGraphImpl>();
			partialGraphs.add(partialGraph);
		}
		
		return partialGraph;
	}
	
	/**
	 * 
	 * @return the list of partial graphs directly and indirectly contained in this graph
	 */
	public List<PartialGraphImpl> getPartialGraphs() {
		LinkedList<PartialGraphImpl> list = new LinkedList<PartialGraphImpl>();
		for (PartialGraphImpl p : partialGraphs) {
			list.add(p);
			list.addAll(p.getPartialGraphs());
		}
		return list;
	}

	@Override
	public boolean containsEdge(Edge e) {
		if (containsEdgeLocally(e))
			return true;
		return e.getContainingGraph().isPartOfGraph(this) && e.getContainingGraph().containsEdge(e);
	}


	@Override
	public boolean containsVertex(Vertex v) {
		if (containsVertexLocally(v))
			return true;
		return v.getContainingGraph().isPartOfGraph(this)  && v.getContainingGraph().containsVertex(v);
	}
	
	/**
	 * @return the distributed graph this graph belongs to
	 */
	public abstract GraphBaseImpl getParentDistributedGraph() ;
	
	/**
	 * @return the distributed graph this graph belongs to
	 */
	public abstract GraphBaseImpl getSuperordinateGraph();
	
	/**
	 * @return the complete top-level DHHTGraph 
	 */
	public abstract GraphBaseImpl getCompleteGraph();
	
	
	/**
	 * checks if the vertex v is contained directly in this graph,
	 * ant not as a member of one of its partial graphs
	 */
	public abstract boolean containsVertexLocally(Vertex v);
	
	/**
	 * checks if the edge e is contained directly in this graph,
	 * ant not as a member of one of its partial graphs
	 */
	public abstract boolean containsEdgeLocally(Edge e);
	
}
