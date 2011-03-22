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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.std.EdgeImpl;
import de.uni_koblenz.jgralab.impl.std.IncidenceImpl;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * Implementation of all methods of the interface {@link Edge} which are
 * independent of the fields of a specific EdgeImpl.
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class EdgeBaseImpl extends
		GraphElementImpl<EdgeClass, Edge, Vertex> implements Edge {

	/**
	 * Creates a new {@link Edge} instance.
	 * 
	 * @param id
	 *            int the id of the edge
	 * @param graph
	 *            {@link Graph} its corresponding graph
	 */
	protected EdgeBaseImpl(int anId, Graph graph) {
		super(graph);
		setId(anId);
	}

	@Override
	public Incidence getFirstIncidence() {
		return getFirstIncidence(getGraph().getTraversalContext());
	}

	@Override
	public Incidence getFirstIncidence(Direction direction) {
		assert isValid();
		return getFirstIncidence(getGraph().getTraversalContext(), direction);
	}

	@Override
	public Incidence getFirstIncidence(boolean thisIncidence,
			IncidenceType... incidentTypes) {
		assert isValid();
		return getFirstIncidence(getGraph().getTraversalContext(),
				thisIncidence, incidentTypes);
	}

	@Override
	public <T extends Incidence> T getFirstIncidence(Class<T> anIncidenceClass,
			Direction direction, boolean noSubclasses) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(getGraph().getTraversalContext(),
				anIncidenceClass, direction, noSubclasses);
	}

	@Override
	public Incidence getFirstIncidence(Graph traversalContext,
			Direction direction) {
		assert isValid();
		Incidence i = getFirstIncidence(traversalContext);
		while ((i != null) && direction != null && direction != Direction.BOTH
				&& i.getDirection() != direction) {
			i = i.getNextIncidenceAtEdge(traversalContext);
		}
		return i;
	}

	@Override
	public Incidence getFirstIncidence(Graph traversalContext,
			boolean thisIncidence, IncidenceType... incidentTypes) {
		assert isValid();
		Incidence i = getFirstIncidence(traversalContext);
		if (incidentTypes.length == 0) {
			return i;
		}
		while (i != null) {
			for (IncidenceType element : incidentTypes) {
				if ((thisIncidence ? i.getThisSemantics() : i
						.getThatSemantics()) == element) {
					return i;
				}
			}
			i = i.getNextIncidenceAtEdge(traversalContext);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Incidence> T getFirstIncidence(Graph traversalContext,
			Class<T> anIncidenceClass, Direction direction, boolean noSubclasses) {
		assert anIncidenceClass != null;
		assert isValid();
		Incidence currentIncidence = getFirstIncidence(traversalContext,
				direction);
		while (currentIncidence != null) {
			if (noSubclasses) {
				if (anIncidenceClass == currentIncidence.getM1Class()) {
					return (T) currentIncidence;
				}
			} else {
				if (anIncidenceClass.isInstance(currentIncidence)) {
					return (T) currentIncidence;
				}
			}
			currentIncidence = currentIncidence.getNextIncidenceAtEdge(
					traversalContext, direction);
		}
		return null;
	}

	@Override
	public Incidence getLastIncidence() {
		return getLastIncidence(getGraph().getTraversalContext());
	}

	@Override
	public Iterable<Incidence> getIncidences() {
		assert isValid();
		return new IncidenceIterableAtEdge<Incidence>(this);
	}

	@Override
	public Iterable<Incidence> getIncidences(Direction direction) {
		assert isValid();
		return new IncidenceIterableAtEdge<Incidence>(this, direction);
	}

	@Override
	public <T extends Incidence> Iterable<T> getIncidences(
			Class<T> anIncidenceClass) {
		assert isValid();
		return new IncidenceIterableAtEdge<T>(this, anIncidenceClass);
	}

	@Override
	public Iterable<Incidence> getIncidences(IncidenceClass anIncidenceClass) {
		assert isValid();
		return new IncidenceIterableAtEdge<Incidence>(this,
				anIncidenceClass.getM1Class());
	}

	@Override
	public <T extends Incidence> Iterable<T> getIncidences(
			Class<T> anIncidenceClass, Direction direction) {
		assert isValid();
		return new IncidenceIterableAtEdge<T>(this, anIncidenceClass, direction);
	}

	@Override
	public Iterable<Incidence> getIncidences(IncidenceClass anIncidenceClass,
			Direction direction) {
		assert isValid();
		return new IncidenceIterableAtEdge<Incidence>(this,
				anIncidenceClass.getM1Class(), direction);
	}

	@Override
	public Iterable<Incidence> getIncidences(Graph traversalContext) {
		assert isValid();
		return new IncidenceIterableAtEdge<Incidence>(traversalContext, this);
	}

	@Override
	public Iterable<Incidence> getIncidences(Graph traversalContext,
			Direction direction) {
		assert isValid();
		return new IncidenceIterableAtEdge<Incidence>(traversalContext, this,
				direction);
	}

	@Override
	public <T extends Incidence> Iterable<T> getIncidences(
			Graph traversalContext, Class<T> anIncidenceClass) {
		assert isValid();
		return new IncidenceIterableAtEdge<T>(traversalContext, this,
				anIncidenceClass);
	}

	@Override
	public Iterable<Incidence> getIncidences(Graph traversalContext,
			IncidenceClass anIncidenceClass) {
		assert isValid();
		return new IncidenceIterableAtEdge<Incidence>(traversalContext, this,
				anIncidenceClass.getM1Class());
	}

	@Override
	public <T extends Incidence> Iterable<T> getIncidences(
			Graph traversalContext, Class<T> anIncidenceClass,
			Direction direction) {
		assert isValid();
		return new IncidenceIterableAtEdge<T>(traversalContext, this,
				anIncidenceClass, direction);
	}

	@Override
	public Iterable<Incidence> getIncidences(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction) {
		assert isValid();
		return new IncidenceIterableAtEdge<Incidence>(traversalContext, this,
				anIncidenceClass.getM1Class(), direction);
	}

	@Override
	public Edge getNextEdge() {
		assert isValid();
		return getNextEdge(getGraph().getTraversalContext());
	}

	@Override
	public Edge getNextEdge(Class<? extends Edge> edgeClass) {
		assert edgeClass != null;
		assert isValid();
		return getNextEdge(getGraph().getTraversalContext(), edgeClass, false);
	}

	@Override
	public Edge getNextEdge(Class<? extends Edge> m1EdgeClass,
			boolean noSubclasses) {
		assert m1EdgeClass != null;
		assert isValid();
		return getNextEdge(getGraph().getTraversalContext(), m1EdgeClass,
				noSubclasses);
	}

	@Override
	public Edge getNextEdge(EdgeClass edgeClass) {
		assert edgeClass != null;
		assert isValid();
		return getNextEdge(getGraph().getTraversalContext(),
				edgeClass.getM1Class(), false);
	}

	@Override
	public Edge getNextEdge(EdgeClass edgeClass, boolean noSubclasses) {
		assert edgeClass != null;
		assert isValid();
		return getNextEdge(getGraph().getTraversalContext(),
				edgeClass.getM1Class(), noSubclasses);
	}

	@Override
	public Edge getNextEdge(Graph traversalContext,
			Class<? extends Edge> edgeClass) {
		assert edgeClass != null;
		assert isValid();
		return getNextEdge(traversalContext, edgeClass, false);
	}

	@Override
	public Edge getNextEdge(Graph traversalContext,
			Class<? extends Edge> m1EdgeClass, boolean noSubclasses) {
		assert m1EdgeClass != null;
		assert isValid();
		EdgeBaseImpl e = (EdgeBaseImpl) getNextEdge(traversalContext);
		while (e != null) {
			if (noSubclasses) {
				if (m1EdgeClass == e.getM1Class()) {
					return e;
				}
			} else {
				if (m1EdgeClass.isInstance(e)) {
					return e;
				}
			}
			e = (EdgeBaseImpl) e.getNextEdge(traversalContext);
		}
		return null;
	}

	@Override
	public Edge getNextEdge(Graph traversalContext, EdgeClass edgeClass) {
		assert edgeClass != null;
		assert isValid();
		return getNextEdge(traversalContext, edgeClass.getM1Class(), false);
	}

	@Override
	public Edge getNextEdge(Graph traversalContext, EdgeClass edgeClass,
			boolean noSubclasses) {
		assert edgeClass != null;
		assert isValid();
		return getNextEdge(traversalContext, edgeClass.getM1Class(),
				noSubclasses);
	}

	@Override
	public Edge getPreviousEdge() {
		return getPreviousEdge(getGraph().getTraversalContext());
	}

	@Override
	public Edge getPrevEdge() {
		assert isValid();
		return getPreviousEdge();
	}

	@Override
	public Iterable<Vertex> getAlphaVertices() {
		return new IncidentVertexIterable<Vertex>(this,
				Direction.VERTEX_TO_EDGE);
	}

	@Override
	public Iterable<Vertex> getAlphaVertices(VertexClass aVertexClass) {
		return new IncidentVertexIterable<Vertex>(this,
				aVertexClass.getM1Class(), Direction.VERTEX_TO_EDGE);
	}

	@Override
	public Iterable<Vertex> getAlphaVertices(
			Class<? extends Vertex> aVertexClass) {
		return new IncidentVertexIterable<Vertex>(this, aVertexClass,
				Direction.VERTEX_TO_EDGE);
	}

	@Override
	public Iterable<Vertex> getAlphaVertices(Graph traversalContext) {
		return new IncidentVertexIterable<Vertex>(traversalContext, this,
				Direction.VERTEX_TO_EDGE);
	}

	@Override
	public Iterable<Vertex> getAlphaVertices(Graph traversalContext,
			VertexClass aVertexClass) {
		return new IncidentVertexIterable<Vertex>(traversalContext, this,
				aVertexClass.getM1Class(), Direction.VERTEX_TO_EDGE);
	}

	@Override
	public Iterable<Vertex> getAlphaVertices(Graph traversalContext,
			Class<? extends Vertex> aVertexClass) {
		return new IncidentVertexIterable<Vertex>(traversalContext, this,
				aVertexClass, Direction.VERTEX_TO_EDGE);
	}

	@Override
	public Iterable<Vertex> getOmegaVertices() {
		return new IncidentVertexIterable<Vertex>(this,
				Direction.EDGE_TO_VERTEX);
	}

	@Override
	public Iterable<Vertex> getOmegaVertices(VertexClass aVertexClass) {
		return new IncidentVertexIterable<Vertex>(this,
				aVertexClass.getM1Class(), Direction.EDGE_TO_VERTEX);
	}

	@Override
	public Iterable<Vertex> getOmegaVertices(
			Class<? extends Vertex> aVertexClass) {
		return new IncidentVertexIterable<Vertex>(this, aVertexClass,
				Direction.EDGE_TO_VERTEX);
	}

	@Override
	public Iterable<Vertex> getOmegaVertices(Graph traversalContext) {
		return new IncidentVertexIterable<Vertex>(traversalContext, this,
				Direction.EDGE_TO_VERTEX);
	}

	@Override
	public Iterable<Vertex> getOmegaVertices(Graph traversalContext,
			VertexClass aVertexClass) {
		return new IncidentVertexIterable<Vertex>(traversalContext, this,
				aVertexClass.getM1Class(), Direction.EDGE_TO_VERTEX);
	}

	@Override
	public Iterable<Vertex> getOmegaVertices(Graph traversalContext,
			Class<? extends Vertex> aVertexClass) {
		return new IncidentVertexIterable<Vertex>(traversalContext, this,
				aVertexClass, Direction.EDGE_TO_VERTEX);
	}

	@Override
	public Iterable<Vertex> getIncidentVertices() {
		return new IncidentVertexIterable<Vertex>(this);
	}

	@Override
	public Iterable<Vertex> getIncidentVertices(Direction direction) {
		return new IncidentVertexIterable<Vertex>(this, direction);
	}

	@Override
	public Iterable<Vertex> getIncidentVertices(VertexClass aVertexClass) {
		return new IncidentVertexIterable<Vertex>(this,
				aVertexClass.getM1Class());
	}

	@Override
	public Iterable<Vertex> getIncidentVertices(
			Class<? extends Vertex> aVertexClass) {
		return new IncidentVertexIterable<Vertex>(this, aVertexClass);
	}

	@Override
	public Iterable<Vertex> getIncidentVertices(VertexClass aVertexClass,
			Direction direction) {
		return new IncidentVertexIterable<Vertex>(this,
				aVertexClass.getM1Class(), direction);
	}

	@Override
	public Iterable<Vertex> getIncidentVertices(
			Class<? extends Vertex> aVertexClass, Direction direction) {
		return new IncidentVertexIterable<Vertex>(this, aVertexClass, direction);
	}

	@Override
	public Iterable<Vertex> getIncidentVertices(Graph traversalContext) {
		return new IncidentVertexIterable<Vertex>(traversalContext, this);
	}

	@Override
	public Iterable<Vertex> getIncidentVertices(Graph traversalContext,
			Direction direction) {
		return new IncidentVertexIterable<Vertex>(traversalContext, this,
				direction);
	}

	@Override
	public Iterable<Vertex> getIncidentVertices(Graph traversalContext,
			VertexClass aVertexClass) {
		return new IncidentVertexIterable<Vertex>(traversalContext, this,
				aVertexClass.getM1Class());
	}

	@Override
	public Iterable<Vertex> getIncidentVertices(Graph traversalContext,
			Class<? extends Vertex> aVertexClass) {
		return new IncidentVertexIterable<Vertex>(traversalContext, this,
				aVertexClass);
	}

	@Override
	public Iterable<Vertex> getIncidentVertices(Graph traversalContext,
			VertexClass aVertexClass, Direction direction) {
		return new IncidentVertexIterable<Vertex>(traversalContext, this,
				aVertexClass.getM1Class(), direction);
	}

	@Override
	public Iterable<Vertex> getIncidentVertices(Graph traversalContext,
			Class<? extends Vertex> aVertexClass, Direction direction) {
		return new IncidentVertexIterable<Vertex>(traversalContext, this,
				aVertexClass, direction);
	}

	@Override
	public boolean isValid() {
		return graph.containsEdge(this);
	}

	@Override
	public boolean isBefore(Edge e) {
		assert e != null;
		assert getGraph() == e.getGraph();
		assert isValid() && e.isValid();
		if (this == e) {
			return false;
		}
		Edge prev = e.getPreviousEdge();
		while ((prev != null) && (prev != this)) {
			prev = e.getPreviousEdge();
		}
		return prev != null;
	}

	@Override
	public void putBefore(Edge e) {
		assert e != null;
		assert e != this;
		assert isValid();
		assert e.isValid();
		assert getGraph() == e.getGraph();
		assert isValid() && e.isValid();
		graph.putEdgeBeforeInGraph((EdgeBaseImpl) e, this);
	}

	@Override
	public boolean isAfter(Edge e) {
		assert e != null;
		assert getGraph() == e.getGraph();
		assert isValid() && e.isValid();
		if (this == e) {
			return false;
		}
		EdgeBaseImpl next = (EdgeBaseImpl) e.getNextEdge();
		while ((next != null) && (next != this)) {
			next = (EdgeBaseImpl) next.getNextEdge();
		}
		return next != null;
	}

	@Override
	public void putAfter(Edge e) {
		assert e != null;
		assert e != this;
		assert isValid();
		assert e.isValid();
		assert getGraph() == e.getGraph();
		assert isValid() && e.isValid();
		graph.putEdgeAfterInGraph((EdgeBaseImpl) e, this);
	}

	/**
	 * Puts <code>nextEdge</code> after this {@link Edge} in the sequence of all
	 * edges in the graph.
	 * 
	 * @param nextEdge
	 *            {@link Edge} which should be put after this {@link Edge}
	 */
	protected abstract void setNextEdge(Edge nextEdge);

	/**
	 * Puts <code>prevEdge</code> before this {@link Edge} in the sequence of
	 * all edges in the graph.
	 * 
	 * @param prevEdge
	 *            {@link Edge} which should be put before this {@link Edge}
	 */
	protected abstract void setPreviousEdge(Edge prevEdge);

	/**
	 * Puts <code>prevEdge</code> before this {@link Edge} in the sequence of
	 * all edges in the graph.
	 * 
	 * @param prevEdge
	 *            {@link Edge} which should be put before this {@link Edge}
	 */
	@Deprecated
	protected abstract void setPrevEdge(Edge prevEdge);

	@Override
	public int getDegree() {
		return getDegree(getGraph().getTraversalContext());
	}

	@Override
	public int getDegree(Direction direction) {
		return getDegree(getGraph().getTraversalContext(), direction);
	}

	@Override
	public int getDegree(IncidenceClass ic, boolean noSubClasses) {
		assert ic != null;
		assert isValid();
		return getDegree(getGraph().getTraversalContext(), ic, noSubClasses);
	}

	@Override
	public int getDegree(Class<? extends Incidence> ic, boolean noSubClasses) {
		assert ic != null;
		assert isValid();
		return getDegree(getGraph().getTraversalContext(), ic, noSubClasses);
	}

	@Override
	public int getDegree(IncidenceClass ic, Direction direction,
			boolean noSubClasses) {
		assert ic != null;
		assert isValid();
		return getDegree(getGraph().getTraversalContext(), ic, direction,
				noSubClasses);
	}

	@Override
	public int getDegree(Class<? extends Incidence> ic, Direction direction,
			boolean noSubClasses) {
		assert ic != null;
		assert isValid();
		return getDegree(getGraph().getTraversalContext(), ic, direction,
				noSubClasses);
	}

	@Override
	public int getDegree(Graph traversalContext) {
		int d = 0;
		Incidence i = getFirstIncidence(traversalContext);
		while (i != null) {
			d++;
			i = i.getNextIncidenceAtEdge(traversalContext);
		}
		return d;
	}

	@Override
	public int getDegree(Graph traversalContext, Direction direction) {
		if (direction == null) {
			return getDegree();
		}
		int d = 0;
		Incidence i = getFirstIncidence(traversalContext);
		while (i != null) {
			if (direction == Direction.BOTH || i.getDirection() == direction) {
				d++;
			}
			i = i.getNextIncidenceAtEdge(traversalContext);
		}
		return d;
	}

	@Override
	public int getDegree(Graph traversalContext, IncidenceClass ic,
			boolean noSubClasses) {
		assert ic != null;
		assert isValid();
		int degree = 0;
		Incidence i = getFirstIncidence(traversalContext, ic, noSubClasses);
		while (i != null) {
			++degree;
			i = i.getNextIncidenceAtEdge(traversalContext, ic, noSubClasses);
		}
		return degree;
	}

	@Override
	public int getDegree(Graph traversalContext, Class<? extends Incidence> ic,
			boolean noSubClasses) {
		assert ic != null;
		assert isValid();
		int degree = 0;
		Incidence i = getFirstIncidence(traversalContext, ic, noSubClasses);
		while (i != null) {
			++degree;
			i = i.getNextIncidenceAtEdge(traversalContext, ic, noSubClasses);
		}
		return degree;
	}

	@Override
	public int getDegree(Graph traversalContext, IncidenceClass ic,
			Direction direction, boolean noSubClasses) {
		assert ic != null;
		assert isValid();
		int degree = 0;
		Incidence i = getFirstIncidence(traversalContext, ic, direction,
				noSubClasses);
		while (i != null) {
			++degree;
			i = i.getNextIncidenceAtEdge(traversalContext, ic, direction,
					noSubClasses);
		}
		return degree;
	}

	@Override
	public int getDegree(Graph traversalContext, Class<? extends Incidence> ic,
			Direction direction, boolean noSubClasses) {
		assert ic != null;
		assert isValid();
		int degree = 0;
		Incidence i = getFirstIncidence(traversalContext, ic, direction,
				noSubClasses);
		while (i != null) {
			++degree;
			i = i.getNextIncidenceAtEdge(traversalContext, ic, direction,
					noSubClasses);
		}
		return degree;
	}

	@Override
	public String toString() {
		assert isValid();
		return "+e" + id + ": " + getType().getQualifiedName();
	}

	@Override
	public int compareTo(Edge e) {
		assert e != null;
		assert isValid();
		assert e.isValid();
		assert getGraph() == e.getGraph();

		return getId() - e.getId();
	}

	@Override
	public void delete() {
		assert isValid() : this + " is not valid!";
		graph.deleteEdge(this);
	}

	@Override
	protected void putIncidenceAfter(IncidenceImpl target, IncidenceImpl moved) {
		assert (target != null) && (moved != null);
		assert target.getGraph() == moved.getGraph();
		assert target.getGraph() == getGraph();
		assert target.getThis() == moved.getThis();
		assert target != moved;

		if ((target == moved) || (target.getNextIncidenceAtEdge() == moved)) {
			return;
		}

		// there are at least 2 incidences in the incidence list
		// such that firstIncidence != lastIncidence
		assert getFirstIncidence() != getLastIncidence();

		// remove moved incidence from lambdaSeq
		if (moved == getFirstIncidence()) {
			setFirstIncidence((IncidenceImpl) moved.getNextIncidenceAtEdge());
			if (!graph.hasSavememSupport()) {
				((IncidenceImpl) moved.getNextIncidenceAtEdge())
						.setPreviousIncidenceAtEdge(null);
			}
		} else if (moved == getLastIncidence()) {
			setLastIncidence((IncidenceImpl) moved.getPreviousIncidenceAtEdge());
			((IncidenceImpl) moved.getPreviousIncidenceAtEdge())
					.setNextIncidenceAtEdge(null);
		} else {
			((IncidenceImpl) moved.getPreviousIncidenceAtEdge())
					.setNextIncidenceAtVertex((IncidenceImpl) moved
							.getNextIncidenceAtEdge());
			if (!graph.hasSavememSupport()) {
				((IncidenceImpl) moved.getNextIncidenceAtEdge())
						.setPreviousIncidenceAtEdge((IncidenceImpl) moved
								.getPreviousIncidenceAtEdge());
			}
		}

		// insert moved incidence in lambdaSeq immediately after target
		if (target == getLastIncidence()) {
			setLastIncidence(moved);
			moved.setNextIncidenceAtEdge(null);
		} else {
			if (!graph.hasSavememSupport()) {
				((IncidenceImpl) target.getNextIncidenceAtEdge())
						.setPreviousIncidenceAtEdge(moved);
			}
			moved.setNextIncidenceAtEdge((IncidenceImpl) target
					.getNextIncidenceAtEdge());
		}
		if (!graph.hasSavememSupport()) {
			moved.setPreviousIncidenceAtEdge(target);
		}
		target.setNextIncidenceAtEdge(moved);
		incidenceListModified();
	}

	@Override
	protected void putIncidenceBefore(IncidenceImpl target, IncidenceImpl moved) {
		assert (target != null) && (moved != null);
		assert target.getGraph() == moved.getGraph();
		assert target.getGraph() == getGraph();
		assert target.getThis() == moved.getThis();
		assert target != moved;

		if ((target == moved) || (target.getPreviousIncidenceAtEdge() == moved)) {
			return;
		}

		// there are at least 2 incidences in the incidence list
		// such that firstIncidence != lastIncidence
		assert getFirstIncidence() != getLastIncidence();

		// remove moved incidence from lambdaSeq
		if (moved == getFirstIncidence()) {
			setFirstIncidence((IncidenceImpl) moved.getNextIncidenceAtEdge());
			if (!graph.hasSavememSupport()) {
				((IncidenceImpl) moved.getNextIncidenceAtEdge())
						.setPreviousIncidenceAtEdge(null);
			}
		} else if (moved == getLastIncidence()) {
			setLastIncidence((IncidenceImpl) moved.getPreviousIncidenceAtEdge());
			((IncidenceImpl) moved.getPreviousIncidenceAtEdge())
					.setNextIncidenceAtEdge(null);
		} else {
			((IncidenceImpl) moved.getPreviousIncidenceAtEdge())
					.setNextIncidenceAtEdge((IncidenceImpl) moved
							.getNextIncidenceAtEdge());
			if (!graph.hasSavememSupport()) {
				((IncidenceImpl) moved.getNextIncidenceAtEdge())
						.setPreviousIncidenceAtEdge((IncidenceImpl) moved
								.getPreviousIncidenceAtEdge());
			}
		}

		// insert moved incidence in lambdaSeq immediately before target
		if (target == getFirstIncidence()) {
			setFirstIncidence(moved);
			if (!graph.hasSavememSupport()) {
				moved.setPreviousIncidenceAtEdge(null);
			}
		} else {
			IncidenceImpl previousIncidence = (IncidenceImpl) target
					.getPreviousIncidenceAtEdge();
			previousIncidence.setNextIncidenceAtEdge(moved);
			if (!graph.hasSavememSupport()) {
				moved.setPreviousIncidenceAtEdge(previousIncidence);
			}
		}
		moved.setNextIncidenceAtEdge(target);
		if (!graph.hasSavememSupport()) {
			target.setPreviousIncidenceAtEdge(moved);
		}
		incidenceListModified();
	}

	@Override
	protected void appendIncidenceToLambdaSeq(IncidenceImpl i) {
		assert i != null;
		assert i.getEdge() != this;
		i.setIncidentEdge((EdgeImpl) this);
		i.setNextIncidenceAtEdge(null);
		if (getFirstIncidence() == null) {
			setFirstIncidence(i);
		}
		if (getLastIncidence() != null) {
			((IncidenceImpl) getLastIncidence()).setNextIncidenceAtEdge(i);
			if (!graph.hasSavememSupport()) {
				i.setPreviousIncidenceAtEdge((IncidenceImpl) getLastIncidence());
			}
		}
		setLastIncidence(i);
		incidenceListModified();
	}

	@Override
	protected void removeIncidenceFromLambdaSeq(IncidenceImpl i) {
		assert i != null;
		assert i.getEdge() == this;
		if (i == getFirstIncidence()) {
			// delete at head of incidence list
			setFirstIncidence((IncidenceImpl) i.getNextIncidenceAtEdge());
			if (getFirstIncidence() != null && !graph.hasSavememSupport()) {
				((IncidenceImpl) getFirstIncidence())
						.setPreviousIncidenceAtEdge(null);
			}
			if (i == getLastIncidence()) {
				// this incidence was the only one...
				setLastIncidence(null);
			}
		} else if (i == getLastIncidence()) {
			// delete at tail of incidence list
			setLastIncidence((IncidenceImpl) i.getPreviousIncidenceAtEdge());
			if (getLastIncidence() != null) {
				((IncidenceImpl) getLastIncidence())
						.setNextIncidenceAtEdge(null);
			}
		} else {
			// delete somewhere in the middle
			((IncidenceImpl) i.getPreviousIncidenceAtEdge())
					.setNextIncidenceAtEdge((IncidenceImpl) i
							.getNextIncidenceAtEdge());
			if (!graph.hasSavememSupport()) {
				((IncidenceImpl) i.getNextIncidenceAtEdge())
						.setPreviousIncidenceAtEdge((IncidenceImpl) i
								.getPreviousIncidenceAtEdge());
			}
		}
		// delete incidence
		i.setIncidentEdge(null);
		i.setNextIncidenceAtEdge(null);
		if (!graph.hasSavememSupport()) {
			i.setPreviousIncidenceAtEdge(null);
		}
		incidenceListModified();
	}

	@Override
	public void sortIncidences(Comparator<Incidence> comp) {
		assert isValid();

		if (getFirstIncidence() == null) {
			// no sorting required for empty incidence lists
			return;
		}
		class IncidenceList {
			IncidenceImpl first;
			IncidenceImpl last;

			public void add(IncidenceImpl i) {
				if (first == null) {
					first = i;
					assert (last == null);
					last = i;
				} else {
					if (!graph.hasSavememSupport()) {
						i.setPreviousIncidenceAtEdge(last);
					}
					last.setNextIncidenceAtEdge(i);
					last = i;
				}
				i.setNextIncidenceAtEdge(null);
			}

			public IncidenceImpl remove() {
				if (first == null) {
					throw new NoSuchElementException();
				}
				IncidenceImpl out;
				if (first == last) {
					out = first;
					first = null;
					last = null;
					return out;
				}
				out = first;
				first = (IncidenceImpl) out.getNextIncidenceAtEdge();
				if (!graph.hasSavememSupport()) {
					first.setPreviousIncidenceAtEdge(null);
				}
				return out;
			}

			public boolean isEmpty() {
				assert ((first == null) == (last == null));
				return first == null;
			}

		}

		IncidenceList a = new IncidenceList();
		IncidenceList b = new IncidenceList();
		IncidenceList out = a;

		// split
		IncidenceImpl last;
		IncidenceList l = new IncidenceList();
		l.first = (IncidenceImpl) getFirstIncidence();
		l.last = (IncidenceImpl) getLastIncidence();

		out.add(last = l.remove());
		while (!l.isEmpty()) {
			IncidenceImpl current = l.remove();
			if (comp.compare(current, last) < 0) {
				out = (out == a) ? b : a;
			}
			out.add(current);
			last = current;
		}
		if (a.isEmpty() || b.isEmpty()) {
			out = a.isEmpty() ? b : a;
			setFirstIncidence(out.first);
			setLastIncidence(out.last);
			return;
		}

		while (true) {
			if (a.isEmpty() || b.isEmpty()) {
				out = a.isEmpty() ? b : a;
				setFirstIncidence(out.first);
				setLastIncidence(out.last);
				incidenceListModified();
				return;
			}

			IncidenceList c = new IncidenceList();
			IncidenceList d = new IncidenceList();
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
				IncidenceImpl current = a.remove();
				if (comp.compare(current, last) < 0) {
					out = (out == c) ? d : c;
				}
				out.add(current);
				last = current;
			}

			// copy rest of B
			while (!b.isEmpty()) {
				IncidenceImpl current = b.remove();
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

	@Override
	public List<? extends Edge> getAdjacences(String role) {
		return getAdjacences(getGraph().getTraversalContext(),
				getIncidenceClassForRolename(role));
	}

	@Override
	public List<? extends Edge> getAdjacences(IncidenceClass ic) {
		return getAdjacences(getGraph().getTraversalContext(), ic);
	}

	@Override
	public List<? extends Edge> getAdjacences(Graph traversalContext, String role) {
		return getAdjacences(traversalContext, getIncidenceClassForRolename(role));
	}

	@Override
	public List<? extends Edge> getAdjacences(Graph traversalContext,
			IncidenceClass ic) {
		assert ic != null;
		assert isValid();
		List<Edge> adjacences = new ArrayList<Edge>();
		Class<? extends Vertex> vc = ic.getVertexClass().getM1Class();
		Direction dir = ic.getDirection();
		for (Vertex v : getIncidentVertices(traversalContext, vc, dir)) {
			for (Edge e : v.getIncidentEdges(traversalContext,
					dir == Direction.EDGE_TO_VERTEX ? Direction.VERTEX_TO_EDGE
							: Direction.EDGE_TO_VERTEX)) {
				adjacences.add(e);
			}
		}
		return adjacences;
	}

	@Override
	public Vertex addAdjacence(String incidentRole, String adjacentRole,
			Edge other) {
		return addAdjacence(getIncidenceClassForRolename(incidentRole),
				getIncidenceClassForRolename(adjacentRole), other);
	}

	@Override
	public Vertex addAdjacence(IncidenceClass incidentIc,
			IncidenceClass adjacentIc, Edge other) {
		assert incidentIc != null;
		assert adjacentIc != null;
		assert isValid();
		assert other.isValid();
		assert getGraph() == other.getGraph();

		VertexClass entry = incidentIc.getVertexClass();
		Class<? extends Vertex> vc = entry.getM1Class();

		assert adjacentIc.getVertexClass() == entry;

		Vertex v = getGraph().createVertex(vc);
		connect(incidentIc, v);
		v.connect(adjacentIc, other);

		incidenceListModified();
		((EdgeBaseImpl) other).incidenceListModified();
		graph.edgeListModified();
		return v;
	}

	@Override
	public List<Edge> removeAdjacences(String role) {
		return removeAdjacences(getIncidenceClassForRolename(role));
	}

	@Override
	public List<Edge> removeAdjacences(IncidenceClass ic) {
		// TODO (graph and incidencelists modified)
		return null;
		// assert (role != null) && (role.length() > 0);
		// assert isValid();
		//
		// DirectedM1EdgeClass entry = getEdgeForRolename(role);
		// Class<? extends Edge> ec = entry.getM1Class();
		// List<Vertex> adjacences = new ArrayList<Vertex>();
		// List<Edge> deleteList = new ArrayList<Edge>();
		// Direction dir = entry.getDirection();
		// for (Edge e : incidences(ec, dir)) {
		// deleteList.add(e);
		// adjacences.add(e.getThat());
		// }
		// for (Edge e : deleteList) {
		// e.delete();
		// }
		// return adjacences;
	}

	@Override
	public void removeAdjacence(String role, Edge other) {
		removeAdjacence(getIncidenceClassForRolename(role), other);
	}

	@Override
	public void removeAdjacence(IncidenceClass ic, Edge other) {
		// TODO (graph and incidencelists modified)
		// assert (role != null) && (role.length() > 0);
		// assert isValid();
		// assert other.isValid();
		// assert getGraph() == other.getGraph();
		//
		// DirectedM1EdgeClass entry = getEdgeForRolename(role);
		// Class<? extends Edge> ec = entry.getM1Class();
		// List<Edge> deleteList = new ArrayList<Edge>();
		// Direction dir = entry.getDirection();
		// for (Edge e : incidences(ec, dir)) {
		// if (e.getThat() == other) {
		// deleteList.add(e);
		// }
		// }
		// for (Edge e : deleteList) {
		// e.delete();
		// }
	}

	/**
	 * @see #setNextEdge(Edge)
	 */
	@Deprecated
	protected abstract void setNextEdgeInGraph(Edge nextEdge);

	/**
	 * @see #setPreviousEdge(Edge)
	 */
	@Deprecated
	protected abstract void setPrevEdgeInGraph(Edge prevEdge);

	@Override
	public Incidence connect(String rolename, Vertex elemToConnect) {
		return connect(getIncidenceClassForRolename(rolename), elemToConnect);
	}

	@Override
	public Incidence connect(IncidenceClass incidenceClass, Vertex elemToConnect) {
		return connect(incidenceClass.getM1Class(), elemToConnect);
	}

	@Override
	public <T extends Incidence> T connect(Class<T> incidenceClass,
			Vertex elemToConnect) {
		return getSchema().getGraphFactory().createIncidence(incidenceClass,
				elemToConnect, this);
	}

}
