package de.uni_koblenz.jgralab.impl;

import java.util.Iterator;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

/**
 * This class provides an {@link Iterable} for the incident {@link Edge}s at a
 * given {@link Vertex}.
 * 
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
		this(vertex, null, null);
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
		this(vertex, null, direction);
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
		this(vertex, ec, null);
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
		iter = new IncidentEdgeIterator(vertex, ec, direction);
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
		 * @param vertex
		 *            {@link Vertex} which {@link Incidence}s should be
		 *            iterated.
		 * @param ec
		 *            {@link Class} only instances of this class are returned.
		 * @param dir
		 *            {@link Direction} of the desired {@link Incidence}s.
		 */
		public IncidentEdgeIterator(Vertex vertex, Class<? extends Edge> ec,
				Direction dir) {
			super(vertex, ec, dir);
			if (ec != null && current.getEdge().getM1Class().isInstance(ec)) {
				setCurrentToNextIncidentGraphElement();
			}
		}

		@Override
		protected void setCurrentToNextIncidentGraphElement() {
			while (current != null
					&& !current.getEdge().getM1Class().isInstance(gc)) {
				current = current.getNextIncidenceAtVertex(dir);
			}
		}

	}

}
