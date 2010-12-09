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

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.std.IncidenceImpl;
import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class EdgeBaseImpl extends GraphElementImpl implements Edge {

	/**
	 * @param anId
	 * @param graph
	 */
	protected EdgeBaseImpl(int anId, Graph graph) {
		super(graph);
		setId(anId);
	}

	protected abstract void setIncidentVertex(VertexBaseImpl v);

	protected abstract VertexBaseImpl getIncidentVertex();

	protected abstract void setNextIncidenceInternal(IncidenceImpl nextIncidence);

	protected abstract IncidenceImpl getNextIncidenceInternal();

	protected abstract void setPrevIncidenceInternal(IncidenceImpl prevIncidence);

	protected abstract IncidenceImpl getPrevIncidenceInternal();

	@Override
	public Incidence getFirstIncidence(Direction direction) {
		assert isValid();
		Incidence i = getFirstIncidence();
		switch (direction) {
		case EDGE_TO_VERTEX:
			while ((i != null) && i.getDirection() != Direction.EDGE_TO_VERTEX) {
				i = i.getNextIncidenceAtEdge();
			}
			return i;
		case VERTEX_TO_EDGE:
			while ((i != null) && i.getDirection() != Direction.VERTEX_TO_EDGE) {
				i = i.getNextIncidenceAtEdge();
			}
			return i;
		default:
			throw new RuntimeException("FIXME!");
		}
	}

	@Override
	public Incidence getFirstIncidence(boolean thisIncidence,
			IncidenceType... incidentTypes) {
		assert isValid();
		Incidence i = getFirstIncidence();
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
			i = i.getNextIncidenceAtEdge();
		}
		return null;
	}

	@Override
	public Incidence getFirstIncidence(
			Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		assert isValid();
		Incidence currentIncidence = getFirstIncidence(direction);
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
			currentIncidence = currentIncidence.getNextIncidenceAtEdge(/*
																		 * TODO
																		 * direction
																		 */);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#getNextEdge()
	 */
	@Override
	public Edge getNextIncidence() {
		assert isValid();
		return getNextIncidenceInternal();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#getPrevEdge()
	 */
	@Override
	public Edge getPrevIncidence() {
		assert isValid();
		return getPrevIncidenceInternal();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Edge#getNextEdge(de.uni_koblenz.jgralab.EdgeDirection
	 * )
	 */
	@Override
	public Edge getNextIncidence(EdgeDirection orientation) {
		assert isValid();
		IncidenceImpl i = getNextIncidenceInternal();
		switch (orientation) {
		case IN:
			while ((i != null) && i.isNormal()) {
				i = i.getNextIncidenceInternal();
			}
			return i;
		case OUT:
			while ((i != null) && !i.isNormal()) {
				i = i.getNextIncidenceInternal();
			}
			return i;
		case INOUT:
			return i;
		default:
			throw new RuntimeException("FIXME!");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#getNextEdge(boolean,
	 * de.uni_koblenz.jgralab.schema.AggregationKind[])
	 */
	@Override
	public Edge getNextIncidence(boolean thisIncidence,
			AggregationKind... kinds) {
		assert isValid();
		IncidenceImpl i = getNextIncidenceInternal();
		if (kinds.length == 0) {
			return i;
		}
		while (i != null) {
			for (AggregationKind element : kinds) {
				if ((thisIncidence ? i.getThisSemantics() : i
						.getThatSemantics()) == element) {
					return i;
				}
			}
			i = i.getNextIncidenceInternal();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#getNextEdgeOfClass(java.lang.Class)
	 */
	@Override
	public Edge getNextIncidence(Class<? extends Edge> anEdgeClass) {
		assert anEdgeClass != null;
		assert isValid();
		return getNextIncidence(anEdgeClass, EdgeDirection.INOUT, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#getNextEdgeOfClass(java.lang.Class,
	 * de.uni_koblenz.jgralab.EdgeDirection)
	 */
	@Override
	public Edge getNextIncidence(Class<? extends Edge> anEdgeClass,
			EdgeDirection orientation) {
		assert anEdgeClass != null;
		assert isValid();
		return getNextIncidence(anEdgeClass, orientation, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#getNextEdgeOfClass(java.lang.Class,
	 * de.uni_koblenz.jgralab.EdgeDirection, boolean)
	 */
	@Override
	public Edge getNextIncidence(Class<? extends Edge> anEdgeClass,
			EdgeDirection orientation, boolean noSubclasses) {
		assert anEdgeClass != null;
		assert isValid();
		Edge currentEdge = getNextIncidence(orientation);
		while (currentEdge != null) {
			if (noSubclasses) {
				if (anEdgeClass == currentEdge.getM1Class()) {
					return currentEdge;
				}
			} else {
				if (anEdgeClass.isInstance(currentEdge)) {
					return currentEdge;
				}
			}
			currentEdge = currentEdge.getNextIncidence(orientation);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Edge#getNextEdgeOfClass(de.uni_koblenz.jgralab
	 * .schema.EdgeClass)
	 */
	@Override
	public Edge getNextIncidence(EdgeClass anEdgeClass) {
		assert anEdgeClass != null;
		assert isValid();
		return getNextIncidence(anEdgeClass.getM1Class(), EdgeDirection.INOUT,
				false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Edge#getNextEdgeOfClass(de.uni_koblenz.jgralab
	 * .schema.EdgeClass, boolean)
	 */
	@Override
	public Edge getNextIncidence(EdgeClass anEdgeClass, boolean noSubclasses) {
		assert anEdgeClass != null;
		assert isValid();
		return getNextIncidence(anEdgeClass.getM1Class(), EdgeDirection.INOUT,
				noSubclasses);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Edge#getNextEdgeOfClass(de.uni_koblenz.jgralab
	 * .schema.EdgeClass, de.uni_koblenz.jgralab.EdgeDirection)
	 */
	@Override
	public Edge getNextIncidence(EdgeClass anEdgeClass,
			EdgeDirection orientation) {
		assert anEdgeClass != null;
		assert isValid();
		return getNextIncidence(anEdgeClass.getM1Class(), orientation, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Edge#getNextEdgeOfClass(de.uni_koblenz.jgralab
	 * .schema.EdgeClass, de.uni_koblenz.jgralab.EdgeDirection, boolean)
	 */
	@Override
	public Edge getNextIncidence(EdgeClass anEdgeClass,
			EdgeDirection orientation, boolean noSubclasses) {
		assert anEdgeClass != null;
		assert isValid();
		return getNextIncidence(anEdgeClass.getM1Class(), orientation,
				noSubclasses);
	}

	@Override
	public Edge getNextIncidence(Class<? extends Edge> anEdgeClass,
			boolean noSubclasses) {
		return getNextIncidence(anEdgeClass, EdgeDirection.INOUT, noSubclasses);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#isBefore(de.uni_koblenz.jgralab.Edge)
	 */
	@Override
	public boolean isBeforeIncidence(Edge e) {
		assert e != null;
		assert isValid();
		assert e.isValid();
		assert getGraph() == e.getGraph();
		assert getThis() == e.getThis();

		if (e == this) {
			return false;
		}
		IncidenceImpl i = getNextIncidenceInternal();
		while ((i != null) && (i != e)) {
			i = i.getNextIncidenceInternal();
		}
		return i != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#isAfter(de.uni_koblenz.jgralab.Edge)
	 */
	@Override
	public boolean isAfterIncidence(Edge e) {
		assert e != null;
		assert isValid();
		assert e.isValid();
		assert getGraph() == e.getGraph();
		assert getThis() == e.getThis();

		if (e == this) {
			return false;
		}
		IncidenceImpl i = getPrevIncidenceInternal();
		while ((i != null) && (i != e)) {
			i = i.getPrevIncidenceInternal();
		}
		return i != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Edge#putEdgeBefore(de.uni_koblenz.jgralab.Edge)
	 */
	@Override
	public void putIncidenceBefore(Edge e) {
		assert e != null;
		assert isValid();
		assert e.isValid();
		assert getGraph() == e.getGraph();
		assert getThis() == e.getThis();
		VertexBaseImpl v = (VertexBaseImpl) getThis();
		assert v.isValid();
		assert e != this;

		if (this != e) {
			v.putIncidenceBefore((IncidenceImpl) e, this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Edge#putEdgeAfter(de.uni_koblenz.jgralab.Edge)
	 */
	@Override
	public void putIncidenceAfter(Edge e) {
		assert e != null;
		assert isValid();
		assert e.isValid();
		assert getGraph() == e.getGraph();
		assert getThis() == e.getThis() : "this-vertices don't match: "
				+ getThis() + " != " + e.getThis();
		VertexBaseImpl v = (VertexBaseImpl) getThis();
		assert v.isValid();
		assert e != this;

		if (this != e) {
			v.putIncidenceAfter((IncidenceImpl) e, this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AttributedElement a) {
		assert a != null;
		assert a instanceof Edge;
		Edge e = (Edge) a;
		assert isValid();
		assert e.isValid();
		assert getGraph() == e.getGraph();

		if (e == this.getReversedEdge()) {
			return -1;
		} else {
			return Math.abs(getId()) - Math.abs(e.getId());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#delete()
	 */
	@Override
	public void delete() {
		assert isValid();
		graph.deleteEdge(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#getAlpha()
	 */
	@Override
	public Vertex getAlpha() {
		assert isValid();
		return getIncidentVertex();
	}

	@Override
	public abstract Edge getNextEdge();

	/**
	 * @param nextEdge
	 */
	abstract protected void setNextEdgeInGraph(Edge nextEdge);

	/**
	 * @param prevEdge
	 */
	abstract protected void setPrevEdgeInGraph(Edge prevEdge);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Edge#getNextEdgeOfClassInGraph(java.lang.Class)
	 */
	@Override
	public Edge getNextEdge(Class<? extends Edge> anEdgeClass) {
		assert anEdgeClass != null;
		assert isValid();
		return getNextEdge(anEdgeClass, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Edge#getNextEdgeOfClassInGraph(de.uni_koblenz.
	 * jgralab.schema.EdgeClass)
	 */
	@Override
	public Edge getNextEdge(EdgeClass anEdgeClass) {
		assert anEdgeClass != null;
		assert isValid();
		return getNextEdge(anEdgeClass.getM1Class(), false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Edge#getNextEdgeOfClassInGraph(de.uni_koblenz.
	 * jgralab.schema.EdgeClass, boolean)
	 */
	@Override
	public Edge getNextEdge(EdgeClass anEdgeClass, boolean noSubclasses) {
		assert anEdgeClass != null;
		assert isValid();
		return getNextEdge(anEdgeClass.getM1Class(), noSubclasses);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Edge#getNextEdgeOfClassInGraph(java.lang.Class,
	 * boolean)
	 */
	@Override
	public Edge getNextEdge(Class<? extends Edge> anEdgeClass,
			boolean noSubclasses) {
		assert anEdgeClass != null;
		assert isValid();
		Edge currentEdge = getNextEdge();
		while (currentEdge != null) {
			if (noSubclasses) {
				if (anEdgeClass == currentEdge.getM1Class()) {
					return currentEdge;
				}
			} else if (anEdgeClass.isInstance(currentEdge)) {
				return currentEdge;
			}
			currentEdge = currentEdge.getNextEdge();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#getNormalEdge()
	 */
	@Override
	public Edge getNormalEdge() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#getOmega()
	 */
	@Override
	public Vertex getOmega() {
		assert isValid();
		return reversedEdge.getIncidentVertex();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#getReversedEdge()
	 */
	@Override
	public Edge getReversedEdge() {
		return reversedEdge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#getThat()
	 */
	@Override
	public Vertex getThat() {
		assert isValid();
		return getOmega();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#getThatRole()
	 */
	@Override
	public String getThatRole() {
		assert isValid();
		return ((EdgeClass) this.getAttributedElementClass()).getTo()
				.getRolename();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#getThis()
	 */
	@Override
	public Vertex getThis() {
		assert isValid();
		return getAlpha();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#getThisRole()
	 */
	@Override
	public String getThisRole() {
		assert isValid();
		return ((EdgeClass) this.getAttributedElementClass()).getFrom()
				.getRolename();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Edge#isAfterInGraph(de.uni_koblenz.jgralab.Edge)
	 */
	@Override
	public boolean isAfterEdge(Edge e) {
		assert e != null;
		assert isValid();
		assert e.isValid();
		assert getGraph() == e.getGraph();
		e = e.getNormalEdge();
		if (e == this) {
			return false;
		}
		Edge p = getPrevEdge();
		while ((p != null) && (p != e)) {
			p = p.getPrevEdge();
		}
		return p != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Edge#isBeforeInGraph(de.uni_koblenz.jgralab.Edge)
	 */
	@Override
	public boolean isBeforeEdge(Edge e) {
		assert e != null;
		assert isValid();
		assert e.isValid();
		assert getGraph() == e.getGraph();

		e = e.getNormalEdge();
		if (e == this) {
			return false;
		}
		Edge n = getNextEdge();
		while ((n != null) && (n != e)) {
			n = n.getNextEdge();
		}
		return n != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#isNormal()
	 */
	@Override
	public boolean isNormal() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Edge#putAfterInGraph(de.uni_koblenz.jgralab.Edge)
	 */
	@Override
	public void putAfterEdge(Edge e) {
		assert e != null;
		assert isValid();
		assert e.isValid();
		assert getGraph() == e.getGraph();
		assert e != this;
		assert e != reversedEdge;

		graph.putEdgeAfterInGraph((EdgeBaseImpl) e.getNormalEdge(), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.Edge#putBeforeInGraph(de.uni_koblenz.jgralab.Edge)
	 */
	@Override
	public void putBeforeEdge(Edge e) {
		assert e != null;
		assert isValid();
		assert e.isValid();
		assert getGraph() == e.getGraph();
		assert e != this;
		assert e != reversedEdge;

		graph.putEdgeBeforeInGraph((EdgeBaseImpl) e.getNormalEdge(), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#setAlpha(de.uni_koblenz.jgralab.Vertex)
	 */
	@Override
	public void setAlpha(Vertex alpha) {
		assert isValid();
		assert alpha != null;
		assert alpha.isValid();
		assert getGraph() == alpha.getGraph();

		VertexBaseImpl oldAlpha = getIncidentVertex();
		if (alpha == oldAlpha) {
			return; // nothing to change
		}

		if (!alpha.isValidAlpha(this)) {
			throw new GraphException("Edges of class "
					+ getAttributedElementClass().getUniqueName()
					+ " may not start at vertices of class "
					+ alpha.getAttributedElementClass().getUniqueName());
		}

		oldAlpha.removeIncidenceFromLambdaSeq(this);
		oldAlpha.incidenceListModified();

		VertexBaseImpl newAlpha = (VertexBaseImpl) alpha;
		newAlpha.appendIncidenceToLambdaSeq(this);
		newAlpha.incidenceListModified();
		setIncidentVertex(newAlpha);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#setOmega(de.uni_koblenz.jgralab.Vertex)
	 */
	@Override
	public void setOmega(Vertex omega) {
		assert isValid();
		assert omega != null;
		assert omega.isValid();
		assert getGraph() == omega.getGraph();

		VertexBaseImpl oldOmgea = reversedEdge.getIncidentVertex();
		if (omega == oldOmgea) {
			return; // nothing to change
		}

		if (!omega.isValidOmega(this)) {
			throw new GraphException("Edges of class "
					+ getAttributedElementClass().getUniqueName()
					+ " may not end at at vertices of class "
					+ omega.getAttributedElementClass().getUniqueName());
		}

		oldOmgea.removeIncidenceFromLambdaSeq(reversedEdge);
		oldOmgea.incidenceListModified();

		VertexBaseImpl newOmega = (VertexBaseImpl) omega;
		newOmega.appendIncidenceToLambdaSeq(reversedEdge);
		newOmega.incidenceListModified();
		reversedEdge.setIncidentVertex(newOmega); // TODO Check if this is
		// really needed as
		// appenIncidenceToLambdaSeq
		// called it before.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#setThat(de.uni_koblenz.jgralab.Vertex)
	 */
	@Override
	public void setThat(Vertex v) {
		assert isValid();
		assert v != null;
		assert v.isValid();
		assert getGraph() == v.getGraph();

		setOmega(v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#setThis(de.uni_koblenz.jgralab.Vertex)
	 */
	@Override
	public void setThis(Vertex v) {
		assert isValid();
		assert v != null;
		assert v.isValid();
		assert getGraph() == v.getGraph();

		setAlpha(v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		assert isValid();
		return "+e" + id + ": "
				+ getAttributedElementClass().getQualifiedName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.Edge#isValid()
	 */
	@Override
	public boolean isValid() {
		return graph.containsEdge(this);
	}

	@Override
	public AggregationKind getThisSemantics() {
		assert isValid();
		return getAlphaSemantics();
	}

	@Override
	public AggregationKind getThatSemantics() {
		assert isValid();
		return getOmegaSemantics();
	}

	/**
	 * Creates the reversed edge for this edge. Should be implemented by the
	 * generated edge classes.
	 */
	abstract protected ReversedEdgeBaseImpl createReversedEdge();
}
