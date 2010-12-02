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

/**
 * Represents the incidences between the edges and vertices in a graph.
 * 
 * @author ist@uni-koblenz.de
 */
public interface Incidence {

	/**
	 * Returns the role of this {@link Incidence}.
	 * 
	 * @return {@link IncidenceClass}
	 */
	public IncidenceClass getRole();

	/**
	 * Returns the direction of this {@link Incidence}.
	 * 
	 * @return {@link Direction}
	 */
	public Direction getDirection();

	/**
	 * Returns the graph containing this {@link GraphElement}.
	 * 
	 * @return {@link Graph} containing this {@link GraphElement}
	 */
	public Graph getGraph();

	/**
	 * Returns the {@link Edge} to which this {@link Incidence} is connected.
	 * 
	 * @return {@link Edge}
	 */
	public Edge getEdge();

	/**
	 * Returns the {@link Vertex} to which this {@link Incidence} is connected.
	 * 
	 * @return {@link Vertex}
	 */
	public Vertex getVertex();

	/**
	 * Returns the next {@link Incidence} in the sequence of all
	 * {@link Incidence}s connected to {@link Incidence#getEdge()}. If the last
	 * {@link Incidence} in the sequence is reached, <code>null</code> is
	 * returned.
	 * 
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge();

	/**
	 * Returns the next {@link Incidence} in the sequence of all
	 * {@link Incidence}s connected to {@link Incidence#getVertex()}. If the
	 * last {@link Incidence} in the sequence is reached, <code>null</code> is
	 * returned.
	 * 
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex();

	/**
	 * Returns the previous {@link Incidence} in the sequence of all
	 * {@link Incidence}s connected to {@link Incidence#getEdge()}. If the first
	 * {@link Incidence} in the sequence is reached, <code>null</code> is
	 * returned.
	 * 
	 * @return {@link Incidence}
	 */
	public Incidence getPreviousIncidenceAtEdge();

	/**
	 * Returns the previous {@link Incidence} in the sequence of all
	 * {@link Incidence}s connected to {@link Incidence#getVertex()}. If the
	 * first {@link Incidence} in the sequence is reached, <code>null</code> is
	 * returned.
	 * 
	 * @return {@link Incidence}
	 */
	public Incidence getPreviousIncidenceAtVertex();

	/**
	 * Returns the sequence of all {@link Edge}s which are connected to
	 * {@link Incidence#getVertex()} via an {@link Incidence} of the same
	 * direction as this {@link Incidence} has.
	 * 
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getTheseEdges();

	/**
	 * Returns the sequence of all {@link Edge}s which are connected to
	 * {@link Incidence#getVertex()} via an {@link Incidence} of the reverted
	 * direction as this {@link Incidence} has.
	 * 
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getThoseEdges();

	/**
	 * Returns {@link Incidence#getVertex()} of a binary {@link Edge}. TODO
	 * Verhalten bei nicht binären Kanten.
	 * 
	 * @return {@link Vertex}
	 */
	public Vertex getThis();

	/**
	 * Returns a sequence of all vertices, which are connected to
	 * {@link Incidence#getEdge()} via an {@link Incidence} of the same
	 * direction as this one.
	 * 
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getTheseVertices();

	/**
	 * Returns the {@link Vertex} which is at the other end of a binary
	 * {@link Edge}. TODO Verhalten bei nicht binären Kanten.
	 * 
	 * @return {@link Vertex}
	 */
	public Vertex getThat();

	/**
	 * Returns a sequence of all vertices, which are connected to
	 * {@link Incidence#getEdge()} via an {@link Incidence} of the reverted
	 * direction as this one.
	 * 
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getThoseVertices();

}
