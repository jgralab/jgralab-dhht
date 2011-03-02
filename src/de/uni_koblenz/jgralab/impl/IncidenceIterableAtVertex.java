package de.uni_koblenz.jgralab.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

/**
 * This class provides an {@link Iterable} for the {@link Incidence}s at a given
 * {@link Vertex}.
 * 
 * @author ist@uni-koblenz.de
 */
public class IncidenceIterableAtVertex<I extends Incidence> extends
		IncidenceIterable<I> {

	/**
	 * Creates an {@link Iterable} for all {@link Incidence} of {@link Vertex}
	 * <code>vertex</code> .
	 * 
	 * @param vertex
	 *            {@link Vertex}
	 */
	public IncidenceIterableAtVertex(Vertex vertex) {
		this(vertex.getGraph().getTraversalContext(), vertex, null, null);
	}

	/**
	 * Creates an {@link Iterable} for all {@link Incidence}s of {@link Vertex}
	 * <code>vertex</code> with the specified <code>direction</code>.
	 * 
	 * @param vertex
	 *            {@link Vertex}
	 * @param direction
	 *            {@link Direction}
	 */
	public IncidenceIterableAtVertex(Vertex vertex, Direction direction) {
		this(vertex.getGraph().getTraversalContext(), vertex, null, direction);
	}

	/**
	 * Creates an {@link Iterable} for all {@link Incidence}s of {@link Vertex}
	 * <code>vertex</code> which are instances of <code>ic</code>.
	 * 
	 * @param vertex
	 *            {@link Vertex}
	 * @param ic
	 *            {@link Class} returned {@link Incidence}s are restricted to
	 *            that class or subclasses
	 */
	public IncidenceIterableAtVertex(Vertex vertex,
			Class<? extends Incidence> ic) {
		this(vertex.getGraph().getTraversalContext(), vertex, ic, null);
	}

	/**
	 * Creates an {@link Iterable} for all {@link Incidence}s of {@link Vertex}
	 * <code>vertex</code> which are instances of <code>ic</code> and with the
	 * specified <code>direction</code>.
	 * 
	 * @param vertex
	 *            {@link Vertex}
	 * @param ic
	 *            {@link Class} returned {@link Incidence}s are restricted to
	 *            that class or subclasses
	 * @param direction
	 *            {@link Direction}
	 */
	public IncidenceIterableAtVertex(Vertex vertex,
			Class<? extends Incidence> ic, Direction direction) {
		assert vertex != null && vertex.isValid();
		iter = new IncidenceIteratorAtVertex(vertex.getGraph()
				.getTraversalContext(), vertex, ic, direction);
	}

	/**
	 * Creates an {@link Iterable} for all {@link Incidence} of {@link Vertex}
	 * <code>vertex</code> .
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param vertex
	 *            {@link Vertex}
	 */
	public IncidenceIterableAtVertex(Graph traversalContext, Vertex vertex) {
		this(traversalContext, vertex, null, null);
	}

	/**
	 * Creates an {@link Iterable} for all {@link Incidence}s of {@link Vertex}
	 * <code>vertex</code> with the specified <code>direction</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param vertex
	 *            {@link Vertex}
	 * @param direction
	 *            {@link Direction}
	 */
	public IncidenceIterableAtVertex(Graph traversalContext, Vertex vertex,
			Direction direction) {
		this(traversalContext, vertex, null, direction);
	}

	/**
	 * Creates an {@link Iterable} for all {@link Incidence}s of {@link Vertex}
	 * <code>vertex</code> which are instances of <code>ic</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param vertex
	 *            {@link Vertex}
	 * @param ic
	 *            {@link Class} returned {@link Incidence}s are restricted to
	 *            that class or subclasses
	 */
	public IncidenceIterableAtVertex(Graph traversalContext, Vertex vertex,
			Class<? extends Incidence> ic) {
		this(traversalContext, vertex, ic, null);
	}

	/**
	 * Creates an {@link Iterable} for all {@link Incidence}s of {@link Vertex}
	 * <code>vertex</code> which are instances of <code>ic</code> and with the
	 * specified <code>direction</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param vertex
	 *            {@link Vertex}
	 * @param ic
	 *            {@link Class} returned {@link Incidence}s are restricted to
	 *            that class or subclasses
	 * @param direction
	 *            {@link Direction}
	 */
	public IncidenceIterableAtVertex(Graph traversalContext, Vertex vertex,
			Class<? extends Incidence> ic, Direction direction) {
		assert vertex != null && vertex.isValid();
		iter = new IncidenceIteratorAtVertex(traversalContext, vertex, ic,
				direction);
	}

	/**
	 * This class provides an {@link Iterator} for the {@link Incidence}s at a
	 * given {@link Vertex}.
	 * 
	 * @author ist@uni-koblenz.de
	 * 
	 */
	class IncidenceIteratorAtVertex extends IncidenceIterator {

		/**
		 * Creates an Iterator over the {@link Incidence}s of
		 * <code>vertex</code>.
		 * 
		 * @param traversalContext
		 *            {@link Graph}
		 * @param vertex
		 *            {@link Vertex} which {@link Incidence}s should be
		 *            iterated.
		 * @param ic
		 *            {@link Class} only instances of this class are returned.
		 * @param dir
		 *            {@link Direction} of the desired {@link Incidence}s.
		 */
		public IncidenceIteratorAtVertex(Graph traversalContext, Vertex vertex,
				Class<? extends Incidence> ic, Direction dir) {
			super(traversalContext, vertex, ic, dir);
		}

		@SuppressWarnings("unchecked")
		@Override
		public I next() {
			checkConcurrentModification();
			if (current == null) {
				throw new NoSuchElementException();
			}
			I result = current;
			current = (I) ((ic == null) ? current.getNextIncidenceAtVertex(
					traversalContext, dir) : current.getNextIncidenceAtVertex(
					traversalContext, ic, dir));
			return result;
		}

	}

}
