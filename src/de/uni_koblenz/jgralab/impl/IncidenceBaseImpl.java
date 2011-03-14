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
public abstract class IncidenceBaseImpl implements Incidence {

	/**
	 * Creates a new instance of IncidenceImpl and appends it to the lambda
	 * sequences of <code>v</code> and <code>e</code>.
	 * 
	 * @param v
	 *            {@link Vertex}
	 * @param e
	 *            {@link Edge}
	 */
	protected IncidenceBaseImpl(Vertex v, Edge e) {
		super();
	}

	@Override
	public Incidence getNextIncidenceAtEdge() {
		return getNextIncidenceAtEdge(getGraph().getTraversalContext());
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Direction direction) {
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				direction);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(boolean thisIncidence,
			IncidenceType... incidenceTypes) {
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				thisIncidence, incidenceTypes);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass) {
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
			Class<? extends Incidence> anIncidenceClass, Direction direction) {
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
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses) {
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

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Direction direction) {
		Incidence i = getNextIncidenceAtEdge();
		while ((i != null) && direction != null && direction != Direction.BOTH
				&& i.getDirection() != direction) {
			i = i.getNextIncidenceAtEdge(traversalContext);
		}
		return i;
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			boolean thisIncidence, IncidenceType... incidenceTypes) {
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
			IncidenceClass anIncidenceClass, Direction direction) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass,
				direction, false);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, Direction direction) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass,
				direction, false);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			IncidenceClass anIncidenceClass, boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass, null,
				noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses) {
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
		return getNextIncidenceAtEdge(getGraph().getTraversalContext());
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Direction direction) {
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				direction);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(boolean thisIncidence,
			IncidenceType... incidenceTypes) {
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				thisIncidence, incidenceTypes);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass) {
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
			Class<? extends Incidence> anIncidenceClass, Direction direction) {
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
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses) {
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
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Direction direction) {
		Incidence i = getNextIncidenceAtVertex(traversalContext);
		while ((i != null) && direction != null && direction != Direction.BOTH
				&& i.getDirection() != direction) {
			i = i.getNextIncidenceAtVertex(traversalContext);
		}
		return i;
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			boolean thisIncidence, IncidenceType... incidenceTypes) {
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
			IncidenceClass anIncidenceClass, Direction direction) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				direction, false);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, Direction direction) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				direction, false);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			IncidenceClass anIncidenceClass, boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				null, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses) {
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

}
