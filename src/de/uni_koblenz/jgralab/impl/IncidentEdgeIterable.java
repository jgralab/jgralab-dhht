package de.uni_koblenz.jgralab.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

/**
 * This class provides an {@link Iterable} for the incident {@link Edge}s at a
 * given {@link Vertex}.
 * 
 * @see IncidentGraphElementIterable
 * @author ist@uni-koblenz.de
 */
public class IncidentEdgeIterable<E extends Edge> extends
		IncidentGraphElementIterable<E> {

	/**
	 * Creates an {@link Iterable} for all incident {@link Edge}s of
	 * <code>vertex</code>.
	 * 
	 * @param vertex
	 *            {@link Vertex}
	 */
	public IncidentEdgeIterable(Vertex vertex) {
		this(vertex.getGraph().getTraversalContext(), vertex, null, null, null);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Edge}s of
	 * <code>vertex</code> with the specified <code>direction</code>.
	 * 
	 * @param vertex
	 *            {@link Vertex}
	 * @param direction
	 *            {@link Direction}
	 */
	public IncidentEdgeIterable(Vertex vertex, Direction direction) {
		this(vertex.getGraph().getTraversalContext(), vertex, null, direction);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Edge}s of
	 * <code>vertex</code> with the specified <code>direction</code>.
	 * 
	 * @param vertex
	 *            {@link Vertex}
	 * @param direction
	 *            {@link Direction}
	 * @param ignoredIncidence
	 *            {@link Incidence} which is ignored during iteration
	 */
	public IncidentEdgeIterable(Vertex vertex, Direction direction,
			Incidence ignoredIncidence) {
		this(vertex.getGraph().getTraversalContext(), vertex, null, direction,
				ignoredIncidence);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Edge}s of
	 * <code>vertex</code> which are instances of <code>ec</code>.
	 * 
	 * @param vertex
	 *            {@link Vertex}
	 * @param ec
	 *            {@link Class} returned {@link Edge}s are restricted to that
	 *            class or subclasses
	 */
	public IncidentEdgeIterable(Vertex vertex, Class<? extends Edge> ec) {
		this(vertex.getGraph().getTraversalContext(), vertex, ec, null);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Edge}s of
	 * <code>vertex</code> which are instances of <code>ec</code> and with the
	 * specified <code>direction</code>.
	 * 
	 * @param vertex
	 *            {@link Vertex}
	 * @param ec
	 *            {@link Class} returned {@link Edge}s are restricted to that
	 *            class or subclasses
	 * @param direction
	 *            {@link Direction}
	 */
	public IncidentEdgeIterable(Vertex vertex, Class<? extends Edge> ec,
			Direction direction) {
		assert vertex != null && vertex.isValid();
		iter = new IncidentEdgeIterator(
				vertex.getGraph().getTraversalContext(), vertex, ec, direction,
				null);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Edge}s of
	 * <code>vertex</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param vertex
	 *            {@link Vertex}
	 */
	public IncidentEdgeIterable(Graph traversalContext, Vertex vertex) {
		this(traversalContext, vertex, null, null, null);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Edge}s of
	 * <code>vertex</code> with the specified <code>direction</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param vertex
	 *            {@link Vertex}
	 * @param direction
	 *            {@link Direction}
	 */
	public IncidentEdgeIterable(Graph traversalContext, Vertex vertex,
			Direction direction) {
		this(traversalContext, vertex, null, direction);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Edge}s of
	 * <code>vertex</code> with the specified <code>direction</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param vertex
	 *            {@link Vertex}
	 * @param direction
	 *            {@link Direction}
	 * @param ignoredIncidence
	 *            {@link Incidence} which is ignored during iteration
	 */
	public IncidentEdgeIterable(Graph traversalContext, Vertex vertex,
			Direction direction, Incidence ignoredIncidence) {
		this(traversalContext, vertex, null, direction, ignoredIncidence);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Edge}s of
	 * <code>vertex</code> which are instances of <code>ec</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param vertex
	 *            {@link Vertex}
	 * @param ec
	 *            {@link Class} returned {@link Edge}s are restricted to that
	 *            class or subclasses
	 */
	public IncidentEdgeIterable(Graph traversalContext, Vertex vertex,
			Class<? extends Edge> ec) {
		this(traversalContext, vertex, ec, null);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Edge}s of
	 * <code>vertex</code> which are instances of <code>ec</code> and with the
	 * specified <code>direction</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param vertex
	 *            {@link Vertex}
	 * @param ec
	 *            {@link Class} returned {@link Edge}s are restricted to that
	 *            class or subclasses
	 * @param direction
	 *            {@link Direction}
	 */
	public IncidentEdgeIterable(Graph traversalContext, Vertex vertex,
			Class<? extends Edge> ec, Direction direction) {
		assert vertex != null && vertex.isValid();
		iter = new IncidentEdgeIterator(traversalContext, vertex, ec,
				direction, null);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Edge}s of
	 * <code>vertex</code> which are instances of <code>ec</code> and with the
	 * specified <code>direction</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param vertex
	 *            {@link Vertex}
	 * @param ec
	 *            {@link Class} returned {@link Edge}s are restricted to that
	 *            class or subclasses
	 * @param direction
	 *            {@link Direction}
	 * @param ignoredIncidence
	 *            {@link Incidence} which is ignored during iteration
	 */
	public IncidentEdgeIterable(Graph traversalContext, Vertex vertex,
			Class<? extends Edge> ec, Direction direction,
			Incidence ignoredIncidence) {
		assert vertex != null && vertex.isValid();
		iter = new IncidentEdgeIterator(traversalContext, vertex, ec,
				direction, ignoredIncidence);
	}

	/**
	 * This class provides an {@link Iterator} for the incident {@link Edge}s at
	 * a given {@link Vertex}.
	 * 
	 * @author ist@uni-koblenz.de
	 * 
	 */
	class IncidentEdgeIterator extends IncidentGraphElementIterator {

		/**
		 * Creates an Iterator over the incident {@link GraphElements}s of
		 * <code>vertex</code>.
		 * 
		 * @param traversalContext
		 *            {@link Graph}
		 * @param vertex
		 *            {@link Vertex} which {@link Incidence}s should be
		 *            iterated.
		 * @param ec
		 *            {@link Class} only instances of this class are returned.
		 * @param dir
		 *            {@link Direction} of the desired {@link Incidence}s.
		 * @param ignoredIncidence
		 *            {@link Incidence} which is ignored during Iteration
		 */
		public IncidentEdgeIterator(Graph traversalContext, Vertex vertex,
				Class<? extends Edge> ec, Direction dir,
				Incidence ignoredIncidence) {
			super(traversalContext, vertex, ec, dir, ignoredIncidence);
			if (ec != null && current.getEdge().getM1Class().isInstance(ec)) {
				setCurrentToNextIncidentGraphElement();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public E next() {
			checkConcurrentModification();
			if (current == null) {
				throw new NoSuchElementException();
			}
			E result = (E) current.getEdge();
			setCurrentToNextIncidentGraphElement();
			return result;
		}

		@Override
		protected void setCurrentToNextIncidentGraphElement() {
			while (current != null
					&& !current.getEdge().getM1Class().isInstance(gc)
					&& current == ignoredIncidence) {
				current = current.getNextIncidenceAtVertex(traversalContext,
						dir);
			}
		}

	}

}
