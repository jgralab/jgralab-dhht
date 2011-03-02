package de.uni_koblenz.jgralab.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;

/**
 * This class provides an {@link Iterable} for the {@link Incidence}s at a given
 * {@link Edge}.
 * 
 * @author ist@uni-koblenz.de
 */
public class IncidenceIterableAtEdge<I extends Incidence> extends
		IncidenceIterable<I> {

	/**
	 * Creates an {@link Iterable} for all {@link Incidence} of {@link Edge}
	 * <code>edge</code> .
	 * 
	 * @param edge
	 *            {@link Edge}
	 */
	public IncidenceIterableAtEdge(Edge edge) {
		this(edge.getGraph().getTraversalContext(), edge, null, null);
	}

	/**
	 * Creates an {@link Iterable} for all {@link Incidence}s of {@link Edge}
	 * <code>edge</code> with the specified <code>direction</code>.
	 * 
	 * @param edge
	 *            {@link Edge}
	 * @param direction
	 *            {@link Direction}
	 */
	public IncidenceIterableAtEdge(Edge edge, Direction direction) {
		this(edge.getGraph().getTraversalContext(), edge, null, direction);
	}

	/**
	 * Creates an {@link Iterable} for all {@link Incidence}s of {@link Edge}
	 * <code>edge</code> which are instances of <code>ic</code>.
	 * 
	 * @param edge
	 *            {@link Edge}
	 * @param ic
	 *            {@link Class} returned {@link Incidence}s are restricted to
	 *            that class or subclasses
	 */
	public IncidenceIterableAtEdge(Edge edge, Class<? extends Incidence> ic) {
		this(edge.getGraph().getTraversalContext(), edge, ic, null);
	}

	/**
	 * Creates an {@link Iterable} for all {@link Incidence}s of {@link Edge}
	 * <code>edge</code> which are instances of <code>ic</code> and with the
	 * specified <code>direction</code>.
	 * 
	 * @param edge
	 *            {@link Edge}
	 * @param ic
	 *            {@link Class} returned {@link Incidence}s are restricted to
	 *            that class or subclasses
	 * @param direction
	 *            {@link Direction}
	 */
	public IncidenceIterableAtEdge(Edge edge, Class<? extends Incidence> ic,
			Direction direction) {
		assert edge != null && edge.isValid();
		iter = new IncidenceIteratorAtEdge(edge.getGraph()
				.getTraversalContext(), edge, ic, direction);
	}

	/**
	 * Creates an {@link Iterable} for all {@link Incidence} of {@link Edge}
	 * <code>edge</code> .
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param edge
	 *            {@link Edge}
	 */
	public IncidenceIterableAtEdge(Graph traversalContext, Edge edge) {
		this(traversalContext, edge, null, null);
	}

	/**
	 * Creates an {@link Iterable} for all {@link Incidence}s of {@link Edge}
	 * <code>edge</code> with the specified <code>direction</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param edge
	 *            {@link Edge}
	 * @param direction
	 *            {@link Direction}
	 */
	public IncidenceIterableAtEdge(Graph traversalContext, Edge edge,
			Direction direction) {
		this(traversalContext, edge, null, direction);
	}

	/**
	 * Creates an {@link Iterable} for all {@link Incidence}s of {@link Edge}
	 * <code>edge</code> which are instances of <code>ic</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param edge
	 *            {@link Edge}
	 * @param ic
	 *            {@link Class} returned {@link Incidence}s are restricted to
	 *            that class or subclasses
	 */
	public IncidenceIterableAtEdge(Graph traversalContext, Edge edge,
			Class<? extends Incidence> ic) {
		this(traversalContext, edge, ic, null);
	}

	/**
	 * Creates an {@link Iterable} for all {@link Incidence}s of {@link Edge}
	 * <code>edge</code> which are instances of <code>ic</code> and with the
	 * specified <code>direction</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param edge
	 *            {@link Edge}
	 * @param ic
	 *            {@link Class} returned {@link Incidence}s are restricted to
	 *            that class or subclasses
	 * @param direction
	 *            {@link Direction}
	 */
	public IncidenceIterableAtEdge(Graph traversalContext, Edge edge,
			Class<? extends Incidence> ic, Direction direction) {
		assert edge != null && edge.isValid();
		iter = new IncidenceIteratorAtEdge(traversalContext, edge, ic,
				direction);
	}

	/**
	 * This class provides an {@link Iterator} for the {@link Incidence}s at a
	 * given {@link Edge}.
	 * 
	 * @author ist@uni-koblenz.de
	 * 
	 */
	class IncidenceIteratorAtEdge extends IncidenceIterator {

		/**
		 * Creates an Iterator over the {@link Incidence}s of <code>edge</code>.
		 * 
		 * @param traversalContext
		 *            {@link Graph}
		 * @param edge
		 *            {@link Edge} which {@link Incidence}s should be iterated.
		 * @param ic
		 *            {@link Class} only instances of this class are returned.
		 * @param dir
		 *            {@link Direction} of the desired {@link Incidence}s.
		 */
		public IncidenceIteratorAtEdge(Graph traversalContext, Edge edge,
				Class<? extends Incidence> ic, Direction dir) {
			super(traversalContext, edge, ic, dir);
		}

		@SuppressWarnings("unchecked")
		@Override
		public I next() {
			checkConcurrentModification();
			if (current == null) {
				throw new NoSuchElementException();
			}
			I result = current;
			current = (I) ((ic == null) ? current.getNextIncidenceAtEdge(
					traversalContext, dir) : current.getNextIncidenceAtEdge(
					traversalContext, ic, dir));
			return result;
		}

	}

}
