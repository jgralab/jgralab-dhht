package de.uni_koblenz.jgralab.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
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
		this(edge, null, null);
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
		this(edge, null, direction);
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
		this(edge, ic, null);
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
		iter = new IncidenceIteratorAtEdge(edge, ic, direction);
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
		 * @param edge
		 *            {@link Edge} which {@link Incidence}s should be iterated.
		 * @param ic
		 *            {@link Class} only instances of this class are returned.
		 * @param dir
		 *            {@link Direction} of the desired {@link Incidence}s.
		 */
		public IncidenceIteratorAtEdge(Edge edge,
				Class<? extends Incidence> ic, Direction dir) {
			super(edge, ic, dir);
		}

		@SuppressWarnings("unchecked")
		@Override
		public I next() {
			checkConcurrentModification();
			if (current == null) {
				throw new NoSuchElementException();
			}
			I result = current;
			current = (I) ((ic == null) ? current.getNextIncidenceAtEdge(dir)
					: current.getNextIncidenceAtEdge(ic, dir));
			return result;
		}

	}

}
