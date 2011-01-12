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

package de.uni_koblenz.jgralab;

import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;

/**
 * represents a signed edge, has an orientation
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public interface Edge extends GraphElement<Edge, Vertex> {

	/**
	 * Returns the {@link EdgeClass} of which this {@link Edge} is an instance
	 * of.
	 * 
	 * @return {@link EdgeClass}
	 */
	public EdgeClass getType();

	/**
	 * @return the "this" vertex object, that is the object this directed edge
	 *         starts at
	 */
	public Vertex getThis();// old

	/**
	 * @return the "that" vertex object, that is the object this directed edge
	 *         ends at
	 */
	public Vertex getThat();// old

	/**
	 * @return the rolename of the edge at the this-vertex
	 */
	public String getThisRole();// old

	/**
	 * @return the rolename of the edge at the that-vertex
	 */
	public String getThatRole();// old

	/**
	 * Returns the next {@link Edge} in the sequence of all edges in the
	 * complete {@link Graph} (eSeq). If this {@link Edge} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @return {@link Edge}
	 */
	public Edge getNextEdge();

	/**
	 * Returns the previous {@link Edge} in the sequence of all edges in the
	 * complete {@link Graph}(eSeq). If this {@link Edge} is the beginning of
	 * the sequence <code>null</code> is returned.
	 * 
	 * @return {@link Edge}
	 */
	@Deprecated
	public Edge getPrevEdge();

	/**
	 * Returns the previous {@link Edge} in the sequence of all edges in the
	 * complete {@link Graph}(eSeq). If this {@link Edge} is the beginning of
	 * the sequence <code>null</code> is returned.
	 * 
	 * @return {@link Edge}
	 */
	public Edge getPreviousEdge();

	/**
	 * @param anEdgeClass
	 * @return next edge of anEdgeClass or its superclasses in eSeq
	 */
	public Edge getNextEdge(EdgeClass anEdgeClass);// old

	/**
	 * @param anEdgeClass
	 * @return next edge of anEdgeClass or its superclasses in eSeq
	 */
	public Edge getNextEdge(Class<? extends Edge> anEdgeClass);// old

	/**
	 * @param anEdgeClass
	 * @param noSubclasses
	 *            if true, no subclasses are returned
	 * @return next edge object of explicit anEdgeClass in eSeq
	 */
	public Edge getNextEdge(EdgeClass anEdgeClass, boolean noSubclasses);// old

	/**
	 * @param anEdgeClass
	 * @param noSubclasses
	 *            if true, no subclasses are returned
	 * @return next edge object of explicit anEdgeClass in eSeq
	 */
	public Edge getNextEdge(Class<? extends Edge> anEdgeClass,
			boolean noSubclasses);// old

	/**
	 * Returns a sequence of all start vertices.
	 * 
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getAlphaVertices();

	/**
	 * Returns a sequence of all end vertices.
	 * 
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getOmegaVertices();

	/**
	 * Returns a sequence of all incident vertices.
	 * 
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getIncidentVertices();

	/**
	 * Returns a sequence of all incident vertices which are connected via an
	 * {@link Incidence} of direction <code>dir</code>.
	 * 
	 * @param dir
	 *            {Direction}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getIncidentVertices(Direction dir);

	/**
	 * @param e
	 * @return true if this edge is somewhere before e in the lambda sequence of
	 *         the this-vertex
	 */
	public boolean isBeforeIncidence(Edge e);// old

	/**
	 * @param e
	 * @return true if this edge is somewhere after e in the lambda sequence of
	 *         the this-vertex
	 */
	public boolean isAfterIncidence(Edge e);// old

	/**
	 * @param e
	 * @return true if this edge is somewhere before e in eSeq
	 */
	public boolean isBeforeEdge(Edge e);// old

	/**
	 * puts this edge immediately before e in eSeq
	 * 
	 * @param e
	 */
	public void putBeforeEdge(Edge e);// old

	/**
	 * @param e
	 * @return true if this edge is somewhere after e in eSeq
	 */
	public boolean isAfterEdge(Edge e);// old

	/**
	 * puts this edge immediately after anEdge in eSeq
	 * 
	 * @param e
	 */
	public void putAfterEdge(Edge e);// old

	/**
	 * removes this edge from eSeq and erases its attributes @ if used on an
	 * incidence
	 */
	public void delete();// old

	/**
	 * sets the alpha vertex to v
	 * 
	 * @param v
	 *            a vertex
	 */
	void setAlpha(Vertex v);// old

	/**
	 * sets the omega vertex to v
	 * 
	 * @param v
	 *            a vertex
	 */
	void setOmega(Vertex v);// old

	/**
	 * sets the this vertex to v
	 * 
	 * @param v
	 *            a vertex
	 */
	void setThis(Vertex v);// old

	/**
	 * sets the that vertex to v
	 * 
	 * @param v
	 *            a vertex
	 */
	void setThat(Vertex v);// old

	/**
	 * puts this edge immediately before the given edge <code>e</code> in the
	 * incidence list of the <code>this-vertex</code> of this edge. This does
	 * neither affect the global edge sequence eSeq nor the alpha or omega
	 * vertices, only the order of the edges at the <code>this-vertex</code> of
	 * this edge is changed.
	 */
	public void putIncidenceBefore(Edge e);// old

	/**
	 * puts this edge after the after given edge <code>previousEdge</code> in
	 * the incidence list of the <code>this-vertex</code> of this edge. This
	 * does neither affect the global edge sequence eSeq nor the alpha or omega
	 * vertices, only the order of the edges at the <code>this-vertex</code> of
	 * this edge is changed.
	 */
	public void putIncidenceAfter(Edge e);// old

	/**
	 * returns the normal edge of this edge
	 */
	public Edge getNormalEdge();// old

	/**
	 * returns the reversed edge of this edge, e.g. for the edge -1 the reversed
	 * edge is 1, for the edge 1 the reversed edge is -1.
	 */
	public Edge getReversedEdge();// old

	/**
	 * returns true if this edge is the "normal" edge, false otherwise
	 */
	public boolean isNormal();// old

	/**
	 * The semanctics of this {@link Edge}, e.g. {@link IncidenceType#EDGE},
	 * {@link IncidenceType#AGGREGATION} or {@link IncidenceType#COMPOSITION}.
	 * 
	 * @return {@link IncidenceType}
	 */
	public IncidenceType getSemantics();

	/**
	 * The semanctics of the alpha {@link Incidence} of this {@link Edge}, e.g.
	 * {@link IncidenceType#EDGE}, {@link IncidenceType#AGGREGATION} or
	 * {@link IncidenceType#COMPOSITION}.
	 * 
	 * @return {@link IncidenceType}
	 */
	public IncidenceType getAlphaSemantics();

	/**
	 * The semanctics of the omega {@link Incidence} of this {@link Edge}, e.g.
	 * {@link IncidenceType#EDGE}, {@link IncidenceType#AGGREGATION} or
	 * {@link IncidenceType#COMPOSITION}.
	 * 
	 * @return {@link IncidenceType}
	 */
	public IncidenceType getOmegaSemantics();

}
