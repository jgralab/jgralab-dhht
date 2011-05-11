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

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * Implementation of all methods of the interface {@link Incidence} which are
 * independent of the fields of a specific IncidenceImpl.
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class IncidenceImpl implements Incidence {

	/**
	 * Creates a new instance of IncidenceImpl and appends it to the lambda
	 * sequences of <code>v</code> and <code>e</code>.
	 * 
	 * @param id
	 *            the id of this incidence
	 * 
	 * @param v
	 *            {@link Vertex}
	 * @param e
	 *            {@link Edge}
	 */
	protected IncidenceImpl(int id, VertexImpl v, EdgeImpl e, Direction dir)
			 {
		this.id = id;
		setIncidentEdge(e);
		setIncidentVertex(v);
		this.direction = dir;

		// add this incidence to the sequence of incidences of v
		if (v.getFirstIncidence() == null) {
			// v has no incidences
			v.setFirstIncidence(this);
			v.setLastIncidence(this);
		} else {
			((IncidenceImpl) v.getLastIncidence())
					.setNextIncidenceAtVertex(this);
			setPreviousIncidenceAtVertex((IncidenceImpl) v.getLastIncidence());
			v.setLastIncidence(this);
		}

		v.incidenceListModified();

		// add this incidence to the sequence of incidences of e
		if (e.getFirstIncidence() == null) {
			// v has no incidences
			e.setFirstIncidence(this);
			e.setLastIncidence(this);
		} else {
			((IncidenceImpl) e.getLastIncidence()).setNextIncidenceAtEdge(this);
			setPreviousIncidenceAtEdge((IncidenceImpl) e.getLastIncidence());
			e.setLastIncidence(this);
		}

		e.incidenceListModified();
	}

	/**
	 * The incident {@link VertexImpl}.
	 */
	private VertexImpl incidentVertex;

	/**
	 * The next {@link Incidence} in the lambda-sequence of
	 * {@link IncidenceImpl#incidentVertex}.
	 */
	IncidenceImpl nextIncidenceAtVertex;

	/**
	 * The previous {@link Incidence} in the lambda-sequence of
	 * {@link IncidenceImpl#incidentVertex}.
	 */
	private IncidenceImpl previousIncidenceAtVertex;

	/**
	 * The incident {@link EdgeImpl}.
	 */
	private EdgeImpl incidentEdge;

	/**
	 * The next {@link Incidence} in the lambda-sequence of
	 * {@link IncidenceImpl#incidentEdge}.
	 */
	IncidenceImpl nextIncidenceAtEdge;

	/**
	 * The previous {@link Incidence} in the lambda-sequence of
	 * {@link IncidenceImpl#incidentEdge}.
	 */
	private IncidenceImpl previousIncidenceAtEdge;

	/**
	 * The direction of this {@link Incidence}.
	 */
	private Direction direction;

	public void setIncidentVertex(VertexImpl incidentVertex) {
		this.incidentVertex = incidentVertex;
	}

	public void setNextIncidenceAtVertex(IncidenceImpl nextIncidenceAtVertex) {
		this.nextIncidenceAtVertex = nextIncidenceAtVertex;
	}

	public void setPreviousIncidenceAtVertex(
			IncidenceImpl previousIncidenceAtVertex) {
		this.previousIncidenceAtVertex = previousIncidenceAtVertex;
	}

	public void setIncidentEdge(EdgeImpl edgeImpl) {
		this.incidentEdge = edgeImpl;
	}

	public void setNextIncidenceAtEdge(IncidenceImpl nextIncidenceAtEdge) {
		this.nextIncidenceAtEdge = nextIncidenceAtEdge;
	}

	public void setPreviousIncidenceAtEdge(IncidenceImpl previousIncidenceAtEdge) {
		this.previousIncidenceAtEdge = previousIncidenceAtEdge;
	}

	protected void setDirection(Direction direction) {
		this.direction = direction;
	}

	@Override
	public Direction getDirection() {
		return direction;
	}

	@Override
	public Graph getGraph() {
		// an Incidence belongs to the same partial graph as the incident edge
		return incidentEdge.getGraph();
	}

	@Override
	public Edge getEdge() {
		return incidentEdge;
	}

	@Override
	public Vertex getVertex() {
		return incidentVertex;
	}

	@Override
	public final Incidence getNextIncidenceAtEdge(Graph traversalContext)
			 {
		Incidence currentIncidence = nextIncidenceAtEdge;
		while ((traversalContext != null)
				&& (currentIncidence != null)
				&& (!traversalContext.containsVertex(currentIncidence
						.getVertex()))) {
			currentIncidence = ((IncidenceImpl) currentIncidence).nextIncidenceAtEdge;
		}
		return currentIncidence;
	}

	@Override
	public final Incidence getNextIncidenceAtVertex(Graph traversalContext)
			 {
		Incidence currentIncidence = nextIncidenceAtVertex;
		while ((traversalContext != null) && (currentIncidence != null)
				&& (!traversalContext.containsEdge(currentIncidence.getEdge()))) {
			currentIncidence = ((IncidenceImpl) currentIncidence).nextIncidenceAtVertex;
		}
		return currentIncidence;
	}

	@Override
	public Incidence getPreviousIncidenceAtEdge(Graph traversalContext)
			 {
		Incidence currentIncidence = previousIncidenceAtEdge;
		while ((traversalContext != null)
				&& (currentIncidence != null)
				&& (!traversalContext.containsVertex(currentIncidence
						.getVertex()))) {
			currentIncidence = ((IncidenceImpl) currentIncidence).previousIncidenceAtEdge;
		}
		return currentIncidence;
	}

	@Override
	public Incidence getPreviousIncidenceAtVertex(Graph traversalContext)
			 {
		Incidence currentIncidence = previousIncidenceAtVertex;
		while ((traversalContext != null) && (currentIncidence != null)
				&& (!traversalContext.containsEdge(currentIncidence.getEdge()))) {
			currentIncidence = ((IncidenceImpl) currentIncidence).previousIncidenceAtVertex;
		}
		return currentIncidence;
	}

	@Override
	public Iterable<Edge> getTheseEdges(Graph traversalContext)
			 {
		assert getGraph().getTraversalContext().getContainingElement()
				.containsElement(incidentVertex);
		return incidentVertex.getIncidentEdges(traversalContext, direction);
	}

	@Override
	public Iterable<Edge> getThoseEdges(Graph traversalContext)
			 {
		assert getGraph().getTraversalContext().getContainingElement()
				.containsElement(incidentVertex);
		return incidentVertex
				.getIncidentEdges(
						traversalContext,
						direction == Direction.EDGE_TO_VERTEX ? Direction.VERTEX_TO_EDGE
								: Direction.EDGE_TO_VERTEX);
	}

	@Override
	public Vertex getThis(Graph traversalContext) {
		if (!incidentEdge.isBinary()) {
			throw new UnsupportedOperationException(
					"This method is only supported by binary Edges.");
		} else if (getGraph().getTraversalContext().getContainingElement()
				.containsElement(incidentVertex)) {
			return incidentVertex;
		} else {
			return null;
		}
	}

	@Override
	public Iterable<Vertex> getTheseVertices(Graph traversalContext)
			 {
		assert getGraph().getTraversalContext().getContainingElement()
				.containsElement(incidentEdge);
		return incidentEdge.getIncidentVertices(traversalContext, direction);
	}

	@Override
	public Vertex getThat(Graph traversalContext) {
		if (!incidentEdge.isBinary()) {
			throw new UnsupportedOperationException(
					"This method is only supported by binary Edges.");
		}
		Vertex vertex = (getDirection() == Direction.EDGE_TO_VERTEX) ? ((BinaryEdge) incidentEdge)
				.getOmega() : ((BinaryEdge) incidentEdge).getAlpha();
		if (getGraph().getTraversalContext().getContainingElement()
				.containsElement(vertex)) {
			return vertex;
		} else {
			return null;
		}
	}

	@Override
	public Iterable<Vertex> getThoseVertices(Graph traversalContext)
			 {
		assert getGraph().getTraversalContext().getContainingElement()
				.containsElement(incidentEdge);
		return incidentEdge
				.getIncidentVertices(
						traversalContext,
						direction == Direction.EDGE_TO_VERTEX ? Direction.VERTEX_TO_EDGE
								: Direction.EDGE_TO_VERTEX);
	}

	@Override
	public void putBeforeAtVertex(Incidence i) {
		assert i != null;
		assert i != this;
		assert getGraph() == i.getGraph();

		Incidence prevIncidence = i.getPreviousIncidenceAtVertex();
		if ((i == this) || (prevIncidence == this)) {
			return;
		}

		assert i.getVertex().getFirstIncidence() != i.getVertex()
				.getLastIncidence();

		// remove this incidence from the sequence of incidences at the vertex
		if (this == getVertex().getFirstIncidence()) {
			((VertexImpl) getVertex())
					.setFirstIncidence((IncidenceImpl) getNextIncidenceAtVertex());
			((IncidenceImpl) getNextIncidenceAtVertex())
					.setPreviousIncidenceAtVertex(null);
		} else if (this == getVertex().getLastIncidence()) {
			((VertexImpl) getVertex())
					.setLastIncidence((IncidenceImpl) getPreviousIncidenceAtVertex());
			((IncidenceImpl) getPreviousIncidenceAtVertex())
					.setNextIncidenceAtVertex(null);
		} else {
			((IncidenceImpl) getPreviousIncidenceAtVertex())
					.setNextIncidenceAtVertex((IncidenceImpl) getNextIncidenceAtVertex());
			((IncidenceImpl) getNextIncidenceAtVertex())
					.setPreviousIncidenceAtVertex((IncidenceImpl) getPreviousIncidenceAtVertex());
		}

		// insert moved incidence in the sequence of incidences at the vertex
		// immediately before i
		if (i == getVertex().getFirstIncidence()) {
			((VertexImpl) getVertex()).setFirstIncidence(this);
			setPreviousIncidenceAtVertex(null);
		} else {
			IncidenceImpl previousIncidence = (IncidenceImpl) i
					.getPreviousIncidenceAtVertex();
			previousIncidence.setNextIncidenceAtVertex(this);
			setPreviousIncidenceAtVertex(previousIncidence);
		}
		setNextIncidenceAtVertex((IncidenceImpl) i);
		((IncidenceImpl) i).setPreviousIncidenceAtVertex(this);

		((VertexImpl) getVertex()).incidenceListModified();
	}

	@Override
	public void putAfterAtVertex(Incidence i) {
		assert i != null;
		assert i != this;
		assert getGraph() == i.getGraph();

		Incidence nextIncidence = i.getNextIncidenceAtVertex();
		if ((i == this) || (nextIncidence == this)) {
			return;
		}

		assert i.getVertex().getLastIncidence() != i.getVertex()
				.getFirstIncidence();

		// remove this incidence from the sequence of incidences at the vertex
		if (this == getVertex().getFirstIncidence()) {
			((VertexImpl) getVertex())
					.setFirstIncidence((IncidenceImpl) getNextIncidenceAtVertex());
			((IncidenceImpl) getNextIncidenceAtVertex())
					.setPreviousIncidenceAtVertex(null);
		} else if (this == getVertex().getLastIncidence()) {
			((VertexImpl) getVertex())
					.setLastIncidence((IncidenceImpl) getPreviousIncidenceAtVertex());
			((IncidenceImpl) getPreviousIncidenceAtVertex())
					.setNextIncidenceAtVertex(null);
		} else {
			((IncidenceImpl) getPreviousIncidenceAtVertex())
					.setNextIncidenceAtVertex((IncidenceImpl) getNextIncidenceAtVertex());
			((IncidenceImpl) getNextIncidenceAtVertex())
					.setPreviousIncidenceAtVertex((IncidenceImpl) getPreviousIncidenceAtVertex());
		}

		// insert moved incidence in the sequence of incidences at the vertex
		// immediately after i
		if (i == getVertex().getLastIncidence()) {
			((VertexImpl) getVertex()).setLastIncidence(this);
			setNextIncidenceAtVertex(null);
		} else {
			IncidenceImpl nxtIncidence = (IncidenceImpl) i
					.getNextIncidenceAtVertex();
			setNextIncidenceAtVertex(nxtIncidence);
			nxtIncidence.setPreviousIncidenceAtVertex(this);
		}
		((IncidenceImpl) i).setNextIncidenceAtVertex(this);
		setPreviousIncidenceAtVertex((IncidenceImpl) i);

		((VertexImpl) getVertex()).incidenceListModified();
	}

	@Override
	public void putBeforeAtEdge(Incidence i) {
		assert i != null;
		assert i != this;
		assert getGraph() == i.getGraph();

		Incidence prevIncidence = i.getPreviousIncidenceAtEdge();
		if ((i == this) || (prevIncidence == this)) {
			return;
		}

		assert i.getEdge().getFirstIncidence() != i.getEdge()
				.getLastIncidence();

		// remove this incidence from the sequence of incidences at the vertex
		if (this == getEdge().getFirstIncidence()) {
			((EdgeImpl) getEdge())
					.setFirstIncidence((IncidenceImpl) getNextIncidenceAtEdge());
			((IncidenceImpl) getNextIncidenceAtEdge())
					.setPreviousIncidenceAtEdge(null);
		} else if (this == getEdge().getLastIncidence()) {
			((EdgeImpl) getEdge())
					.setLastIncidence((IncidenceImpl) getPreviousIncidenceAtEdge());
			((IncidenceImpl) getPreviousIncidenceAtEdge())
					.setNextIncidenceAtEdge(null);
		} else {
			((IncidenceImpl) getPreviousIncidenceAtEdge())
					.setNextIncidenceAtEdge((IncidenceImpl) getNextIncidenceAtEdge());
			((IncidenceImpl) getNextIncidenceAtEdge())
					.setPreviousIncidenceAtEdge((IncidenceImpl) getPreviousIncidenceAtEdge());
		}

		// insert moved incidence in the sequence of incidences at the vertex
		// immediately before i
		if (i == getEdge().getFirstIncidence()) {
			((EdgeImpl) getEdge()).setFirstIncidence(this);
			setPreviousIncidenceAtEdge(null);
		} else {
			IncidenceImpl previousIncidence = (IncidenceImpl) i
					.getPreviousIncidenceAtEdge();
			previousIncidence.setNextIncidenceAtEdge(this);
			setPreviousIncidenceAtEdge(previousIncidence);
		}
		setNextIncidenceAtEdge((IncidenceImpl) i);
		((IncidenceImpl) i).setPreviousIncidenceAtEdge(this);
		((EdgeImpl) getEdge()).incidenceListModified();
	}

	@Override
	public void putAfterAtEdge(Incidence i) {
		assert i != null;
		assert i != this;
		assert getGraph() == i.getGraph();

		Incidence nextIncidence = i.getNextIncidenceAtEdge();
		if ((i == this) || (nextIncidence == this)) {
			return;
		}

		assert i.getEdge().getLastIncidence() != i.getEdge()
				.getFirstIncidence();

		// remove this incidence from the sequence of incidences at the vertex
		if (this == getEdge().getFirstIncidence()) {
			((EdgeImpl) getEdge())
					.setFirstIncidence((IncidenceImpl) getNextIncidenceAtEdge());
			((IncidenceImpl) getNextIncidenceAtEdge())
					.setPreviousIncidenceAtEdge(null);
		} else if (this == getEdge().getLastIncidence()) {
			((EdgeImpl) getEdge())
					.setLastIncidence((IncidenceImpl) getPreviousIncidenceAtEdge());
			((IncidenceImpl) getPreviousIncidenceAtEdge())
					.setNextIncidenceAtEdge(null);
		} else {
			((IncidenceImpl) getPreviousIncidenceAtEdge())
					.setNextIncidenceAtEdge((IncidenceImpl) getNextIncidenceAtEdge());
			((IncidenceImpl) getNextIncidenceAtEdge())
					.setPreviousIncidenceAtEdge((IncidenceImpl) getPreviousIncidenceAtEdge());
		}

		// insert moved incidence in the sequence of incidences at the vertex
		// immediately after i
		if (i == getEdge().getLastIncidence()) {
			((EdgeImpl) getEdge()).setLastIncidence(this);
			setNextIncidenceAtEdge(null);
		} else {
			IncidenceImpl nxtIncidence = (IncidenceImpl) i
					.getNextIncidenceAtEdge();
			setNextIncidenceAtEdge(nxtIncidence);
			nxtIncidence.setPreviousIncidenceAtEdge(this);
		}
		((IncidenceImpl) i).setNextIncidenceAtEdge(this);
		setPreviousIncidenceAtEdge((IncidenceImpl) i);

		((EdgeImpl) getEdge()).incidenceListModified();
	}

	protected int id;

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int compareTo(Incidence i) {
			assert getGraph() == i.getGraph();
			return getId() - i.getId();
	}

	@Override
	public Incidence getNextIncidenceAtEdge() {
		if (getGraph().getTraversalContext() == null) {
			return nextIncidenceAtEdge;
		} else {
			return getNextIncidenceAtEdge(getGraph().getTraversalContext());
		}
	}

	@Override
	public final Incidence getNextIncidenceAtEdge(Direction direction)
			 {
		if (getGraph().getTraversalContext() == null) {
			Incidence i = nextIncidenceAtEdge;
			if ((direction != null) && (direction != Direction.BOTH)) {
				while ((i != null) && (direction != i.getDirection())) {
					i = ((IncidenceImpl) i).nextIncidenceAtEdge;
				}
			}
			return i;
		} else {
			return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
					direction);
		}
	}

	@Override
	public Incidence getNextIncidenceAtEdge(boolean thisIncidence,
			IncidenceType... incidenceTypes) {
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				thisIncidence, incidenceTypes);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass)
			 {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass,
			Direction direction) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass, direction);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass, Direction direction)
			 {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass, direction);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses)
			 {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass,
			Direction direction, boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass, direction, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass, direction, noSubclasses);
	}

	// @Override
	// public Incidence getNextIncidenceAtEdge(Graph traversalContext, Direction
	// direction) {
	// Incidence i = getNextIncidenceAtEdge(traversalContext);
	// while ((i != null) && (direction != null) && (direction !=
	// Direction.BOTH) && (direction != i.getDirection()))
	// i = i.getNextIncidenceAtEdge(traversalContext);
	// return i;
	// }

	@Override
	public final Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Direction direction) {
		Incidence i = nextIncidenceAtEdge;
		if (traversalContext == null) {
			while (((i != null) && (direction != null)
					&& (direction != Direction.BOTH) && (direction != i
					.getDirection()))) {
				i = ((IncidenceImpl) i).nextIncidenceAtEdge;
			}
		} else {
			if ((direction != null) && (direction != Direction.BOTH)) {
				while ((i != null)
						&& ((!traversalContext.containsVertex(i.getVertex())) || (direction != i
								.getDirection()))) {
					i = ((IncidenceImpl) i).nextIncidenceAtEdge;
				}
			} else {
				while ((i != null)
						&& (!traversalContext.containsVertex(i.getVertex()))) {
					i = ((IncidenceImpl) i).nextIncidenceAtEdge;
				}
			}

		}
		return i;
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			boolean thisIncidence, IncidenceType... incidenceTypes)
			 {
		Incidence i = getNextIncidenceAtEdge(traversalContext);
		if (incidenceTypes.length == 0) {
			return i;
		}
		while (i != null) {
			for (IncidenceType element : incidenceTypes) {
				if ((thisIncidence ? i.getThisSemantics() : i
						.getThatSemantics()) == element) {
					return i;
				}
			}
			i = i.getNextIncidenceAtEdge(traversalContext);
		}
		return null;
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			IncidenceClass anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass, null,
				false);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass, null,
				false);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction)
			 {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass,
				direction, false);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, Direction direction)
			 {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass,
				direction, false);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			IncidenceClass anIncidenceClass, boolean noSubclasses)
			 {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass, null,
				noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses)
			 {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass, null,
				noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext,
				anIncidenceClass.getM1Class(), direction, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		Incidence currentIncidence = getNextIncidenceAtEdge(traversalContext,
				direction);
		while (currentIncidence != null) {
			if (noSubclasses) {
				if (anIncidenceClass == currentIncidence.getM1Class()) {
					return currentIncidence;
				}
			} else {
				if (anIncidenceClass.isInstance(currentIncidence)) {
					return currentIncidence;
				}
			}
			currentIncidence = currentIncidence.getNextIncidenceAtEdge(
					traversalContext, direction);
		}
		return null;
	}

	@Override
	public Incidence getPreviousIncidenceAtEdge() {
		return getPreviousIncidenceAtEdge(getGraph().getTraversalContext());
	}

	@Override
	public Incidence getNextIncidenceAtVertex() {
		return getNextIncidenceAtVertex(getGraph().getTraversalContext());
	}

	@Override
	public final Incidence getNextIncidenceAtVertex(Direction direction)
			 {
		if (getGraph().getTraversalContext() == null) {
			Incidence i = nextIncidenceAtVertex;
			if ((direction != null) && (direction != Direction.BOTH)) {
				while ((i != null) && (direction != i.getDirection())) {
					i = ((IncidenceImpl) i).nextIncidenceAtVertex;
				}
			}
			return i;
		} else {
			return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
					direction);
		}
	}

	@Override
	public Incidence getNextIncidenceAtVertex(boolean thisIncidence,
			IncidenceType... incidenceTypes) {
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				thisIncidence, incidenceTypes);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass)
			 {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass,
			Direction direction) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass, direction);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass, Direction direction)
			 {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass, direction);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses)
			 {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass,
			Direction direction, boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass, direction, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass, direction, noSubclasses);
	}

	@Override
	public final Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Direction direction) {
		Incidence i = nextIncidenceAtVertex;
		if (traversalContext == null) {
			while (((i != null) && (direction != null)
					&& (direction != Direction.BOTH) && (direction != i
					.getDirection()))) {
				i = ((IncidenceImpl) i).nextIncidenceAtVertex;
			}
		} else {
			if ((direction != null) && (direction != Direction.BOTH)) {
				while ((i != null)
						&& ((!traversalContext.containsEdge(i.getEdge())) || (direction != i
								.getDirection()))) {
					i = ((IncidenceImpl) i).nextIncidenceAtVertex;
				}
			} else {
				while ((i != null)
						&& (!traversalContext.containsEdge(i.getEdge()))) {
					i = ((IncidenceImpl) i).nextIncidenceAtVertex;
				}
			}

		}
		return i;
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			boolean thisIncidence, IncidenceType... incidenceTypes)
			 {
		Incidence i = getNextIncidenceAtVertex(traversalContext);
		if (incidenceTypes.length == 0) {
			return i;
		}
		while (i != null) {
			for (IncidenceType element : incidenceTypes) {
				if ((thisIncidence ? i.getThisSemantics() : i
						.getThatSemantics()) == element) {
					return i;
				}
			}
			i = i.getNextIncidenceAtVertex(traversalContext);
		}
		return null;
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			IncidenceClass anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				null, false);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				null, false);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction)
			 {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				direction, false);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, Direction direction)
			 {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				direction, false);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			IncidenceClass anIncidenceClass, boolean noSubclasses)
			 {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				null, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses)
			 {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				null, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext,
				anIncidenceClass.getM1Class(), direction, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		Incidence currentIncidence = getNextIncidenceAtVertex(traversalContext,
				direction);
		while (currentIncidence != null) {
			if (noSubclasses) {
				if (anIncidenceClass == currentIncidence.getM1Class()) {
					return currentIncidence;
				}
			} else {
				if (anIncidenceClass.isInstance(currentIncidence)) {
					return currentIncidence;
				}
			}
			currentIncidence = currentIncidence.getNextIncidenceAtVertex(
					traversalContext, direction);
		}
		return null;
	}

	@Override
	public Incidence getPreviousIncidenceAtVertex() {
		return getPreviousIncidenceAtVertex(getGraph().getTraversalContext());
	}

	@Override
	public Iterable<Edge> getThoseEdges() {
		return getThoseEdges(getGraph().getTraversalContext());
	}

	@Override
	public Iterable<Edge> getTheseEdges() {
		return getTheseEdges(getGraph().getTraversalContext());
	}

	@Override
	public Vertex getThis() {
		return getThis(getGraph().getTraversalContext());
	}

	@Override
	public Iterable<Vertex> getTheseVertices() {
		return getTheseVertices(getGraph().getTraversalContext());
	}

	@Override
	public Vertex getThat() {
		return getThat(getGraph().getTraversalContext());
	}

	@Override
	public Iterable<Vertex> getThoseVertices() {
		return getThoseVertices(getGraph().getTraversalContext());
	}

	@Override
	public boolean isBeforeAtVertex(Incidence i) {
		assert i != null;
		assert getGraph() == i.getGraph();
		if (this == i) {
			return false;
		}
		Incidence prev = i.getPreviousIncidenceAtVertex();
		while ((prev != null) && (prev != this)) {
			prev = i.getPreviousIncidenceAtVertex();
		}
		return prev != null;
	}

	@Override
	public boolean isAfterAtVertex(Incidence i) {
		assert i != null;
		assert getGraph() == i.getGraph();
		if (this == i) {
			return false;
		}
		Incidence next = i.getNextIncidenceAtVertex();
		while ((next != null) && (next != this)) {
			next = i.getNextIncidenceAtVertex();
		}
		return next != null;
	}

	@Override
	public boolean isBeforeAtEdge(Incidence i) {
		assert i != null;
		assert getGraph() == i.getGraph();
		if (this == i) {
			return false;
		}
		Incidence prev = i.getPreviousIncidenceAtEdge();
		while ((prev != null) && (prev != this)) {
			prev = i.getPreviousIncidenceAtEdge();
		}
		return prev != null;
	}

	@Override
	public boolean isAfterAtEdge(Incidence i) {
		assert i != null;
		assert getGraph() == i.getGraph();
		if (this == i) {
			return false;
		}
		Incidence next = i.getNextIncidenceAtEdge();
		while ((next != null) && (next != this)) {
			next = i.getNextIncidenceAtEdge();
		}
		return next != null;
	}

	@Override
	public GraphClass getGraphClass() {
		return getVertex().getGraphClass();
	}

	@Override
	public Schema getSchema() {
		return getVertex().getSchema();
	}

	@Override
	public IncidenceType getThisSemantics() {
		if (getEdge().isBinary()) {
			return this.getType().getIncidenceType();
		} else {
			throw new UnsupportedOperationException(
					"getThisSemantics() may be called only for incidences of binary edges");
		}
	}

	@Override
	public IncidenceType getThatSemantics() {
		if (getEdge().isBinary()) {
			return getThatIncidence().getType().getIncidenceType();
		} else {
			throw new UnsupportedOperationException(
					"getThatSemantics() may be called only for incidences of binary edges");
		}
	}

	public Incidence getThatIncidence() {
		if (getEdge().isBinary()) {
			return getEdge().getFirstIncidence() == this ? getEdge()
					.getFirstIncidence() : getEdge().getLastIncidence();
		} else {
			throw new UnsupportedOperationException(
					"getThatIncidence() may be called only for incidences of binary edges");
		}
	}

	public void setId(int iId) {
		this.id = iId;
	}

	@Override
	public Graph getLocalGraph() {
		return getEdge().getLocalGraph();
	}

}
