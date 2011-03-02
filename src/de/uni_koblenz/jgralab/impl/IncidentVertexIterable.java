package de.uni_koblenz.jgralab.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

/**
 * This class provides an {@link Iterable} for the incident {@link Vertex} at a
 * given {@link Edge}.
 * 
 * @see IncidentGraphElementIterable
 * @author ist@uni-koblenz.de
 */
public class IncidentVertexIterable<V extends Vertex> extends
		IncidentGraphElementIterable<V> {

	/**
	 * Creates an {@link Iterable} for all incident {@link Vertex} of
	 * <code>edge</code> .
	 * 
	 * @param edge
	 *            {@link Edge}
	 */
	public IncidentVertexIterable(Edge edge) {
		this(edge.getGraph().getTraversalContext(), edge, null, null);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Vertex} of
	 * <code>edge</code> with the specified <code>direction</code>.
	 * 
	 * @param edge
	 *            {@link Edge}
	 * @param direction
	 *            {@link Direction}
	 */
	public IncidentVertexIterable(Edge edge, Direction direction) {
		this(edge.getGraph().getTraversalContext(), edge, null, direction);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Vertex} of
	 * <code>edge</code> which are instances of <code>vc</code>.
	 * 
	 * @param edge
	 *            {@link Edge}
	 * @param vc
	 *            {@link Class} returned {@link Vertex} are restricted to that
	 *            class or subclasses
	 */
	public IncidentVertexIterable(Edge edge, Class<? extends Vertex> vc) {
		this(edge.getGraph().getTraversalContext(), edge, vc, null);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Vertex} of
	 * <code>edge</code> which are instances of <code>vc</code> and with the
	 * specified <code>direction</code>.
	 * 
	 * @param edge
	 *            {@link Edge}
	 * @param vc
	 *            {@link Class} returned {@link Vertex} are restricted to that
	 *            class or subclasses
	 * @param direction
	 *            {@link Direction}
	 */
	public IncidentVertexIterable(Edge edge, Class<? extends Vertex> vc,
			Direction direction) {
		assert edge != null && edge.isValid();
		iter = new IncidentVertexIterator(
				edge.getGraph().getTraversalContext(), edge, vc, direction);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Vertex} of
	 * <code>edge</code> .
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param edge
	 *            {@link Edge}
	 */
	public IncidentVertexIterable(Graph traversalContext, Edge edge) {
		this(traversalContext, edge, null, null);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Vertex} of
	 * <code>edge</code> with the specified <code>direction</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param edge
	 *            {@link Edge}
	 * @param direction
	 *            {@link Direction}
	 */
	public IncidentVertexIterable(Graph traversalContext, Edge edge,
			Direction direction) {
		this(traversalContext, edge, null, direction);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Vertex} of
	 * <code>edge</code> which are instances of <code>vc</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param edge
	 *            {@link Edge}
	 * @param vc
	 *            {@link Class} returned {@link Vertex} are restricted to that
	 *            class or subclasses
	 */
	public IncidentVertexIterable(Graph traversalContext, Edge edge,
			Class<? extends Vertex> vc) {
		this(traversalContext, edge, vc, null);
	}

	/**
	 * Creates an {@link Iterable} for all incident {@link Vertex} of
	 * <code>edge</code> which are instances of <code>vc</code> and with the
	 * specified <code>direction</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param edge
	 *            {@link Edge}
	 * @param vc
	 *            {@link Class} returned {@link Vertex} are restricted to that
	 *            class or subclasses
	 * @param direction
	 *            {@link Direction}
	 */
	public IncidentVertexIterable(Graph traversalContext, Edge edge,
			Class<? extends Vertex> vc, Direction direction) {
		assert edge != null && edge.isValid();
		iter = new IncidentVertexIterator(traversalContext, edge, vc, direction);
	}

	/**
	 * This class provides an {@link Iterator} for the incident
	 * {@link GraphElement}s at a given {@link Edge}.
	 * 
	 * @author ist@uni-koblenz.de
	 * 
	 */
	class IncidentVertexIterator extends IncidentGraphElementIterator {

		/**
		 * Creates an Iterator over the incident {@link GraphElements}s of
		 * <code>edge</code>.
		 * 
		 * @param traversalContext
		 *            {@link Graph}
		 * @param edge
		 *            {@link Edge} which {@link Incidence}s should be iterated.
		 * @param vc
		 *            {@link Class} only instances of this class are returned.
		 * @param dir
		 *            {@link Direction} of the desired {@link Incidence}s.
		 */
		public IncidentVertexIterator(Graph traversalContext, Edge edge,
				Class<? extends Vertex> vc, Direction dir) {
			super(traversalContext, edge, vc, dir);
			if (vc != null && current.getVertex().getM1Class().isInstance(vc)) {
				setCurrentToNextIncidentGraphElement();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public V next() {
			checkConcurrentModification();
			if (current == null) {
				throw new NoSuchElementException();
			}
			V result = (V) current.getVertex();
			setCurrentToNextIncidentGraphElement();
			return result;
		}

		@Override
		protected void setCurrentToNextIncidentGraphElement() {
			while (current != null
					&& !current.getVertex().getM1Class().isInstance(gc)) {
				current = current.getNextIncidenceAtEdge(traversalContext, dir);
			}
		}

	}

}
