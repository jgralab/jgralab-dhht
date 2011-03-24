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
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * represents a signed edge, has an orientation
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public interface Edge extends GraphElement<EdgeClass, Edge, Vertex> {

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
	public Edge getPreviousEdge();

	/**
	 * Returns the next {@link Edge} in the sequence of all edges in the
	 * complete {@link Graph} (eSeq). If this {@link Edge} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param anEdgeClass
	 *            {@link EdgeClass} the next {@link Edge} should have
	 * @return {@link Edge}
	 */
	public Edge getNextEdge(EdgeClass anEdgeClass);

	/**
	 * Returns the next {@link Edge} in the sequence of all edges in the
	 * complete {@link Graph} (eSeq). If this {@link Edge} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param aM1EdgeClass
	 *            {@link Class} the next {@link Edge} should have
	 * @return {@link Edge}
	 */
	public Edge getNextEdge(Class<? extends Edge> aM1EdgeClass);

	/**
	 * Returns the next {@link Edge} in the sequence of all edges in the
	 * complete {@link Graph} (eSeq). If this {@link Edge} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param anEdgeClass
	 *            {@link EdgeClass} the next {@link Edge} should have
	 * @param noSubclasses
	 *            boolean if <code>true</code>, no edges which are an instance
	 *            of a subclass of <code>anEdgeClass</code> are returned
	 * @return {@link Edge}
	 */
	public Edge getNextEdge(EdgeClass anEdgeClass, boolean noSubclasses);

	/**
	 * Returns the next {@link Edge} in the sequence of all edges in the
	 * complete {@link Graph} (eSeq). If this {@link Edge} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param aM1EdgeClass
	 *            {@link Class} the next {@link Edge} should have
	 * @param noSubclasses
	 *            boolean if <code>true</code>, no edges which are an instance
	 *            of a subclass of <code>aM1EdgeClass</code> are returned
	 * @return {@link Edge}
	 */
	public Edge getNextEdge(Class<? extends Edge> aM1EdgeClass,
			boolean noSubclasses);

	/**
	 * Returns the next {@link Edge} in the sequence of all edges in the
	 * complete {@link Graph} (eSeq). If this {@link Edge} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Edge}
	 */
	public Edge getNextEdge(Graph traversalContext);

	/**
	 * Returns the previous {@link Edge} in the sequence of all edges in the
	 * complete {@link Graph}(eSeq). If this {@link Edge} is the beginning of
	 * the sequence <code>null</code> is returned.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Edge}
	 */
	public Edge getPreviousEdge(Graph traversalContext);

	/**
	 * Returns the next {@link Edge} in the sequence of all edges in the
	 * complete {@link Graph} (eSeq). If this {@link Edge} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anEdgeClass
	 *            {@link EdgeClass} the next {@link Edge} should have
	 * @return {@link Edge}
	 */
	public Edge getNextEdge(Graph traversalContext, EdgeClass anEdgeClass);

	/**
	 * Returns the next {@link Edge} in the sequence of all edges in the
	 * complete {@link Graph} (eSeq). If this {@link Edge} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param aM1EdgeClass
	 *            {@link Class} the next {@link Edge} should have
	 * @return {@link Edge}
	 */
	public Edge getNextEdge(Graph traversalContext,
			Class<? extends Edge> aM1EdgeClass);

	/**
	 * Returns the next {@link Edge} in the sequence of all edges in the
	 * complete {@link Graph} (eSeq). If this {@link Edge} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anEdgeClass
	 *            {@link EdgeClass} the next {@link Edge} should have
	 * @param noSubclasses
	 *            boolean if <code>true</code>, no edges which are an instance
	 *            of a subclass of <code>anEdgeClass</code> are returned
	 * @return {@link Edge}
	 */
	public Edge getNextEdge(Graph traversalContext, EdgeClass anEdgeClass,
			boolean noSubclasses);

	/**
	 * Returns the next {@link Edge} in the sequence of all edges in the
	 * complete {@link Graph} (eSeq). If this {@link Edge} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param aM1EdgeClass
	 *            {@link Class} the next {@link Edge} should have
	 * @param noSubclasses
	 *            boolean if <code>true</code>, no edges which are an instance
	 *            of a subclass of <code>aM1EdgeClass</code> are returned
	 * @return {@link Edge}
	 */
	public Edge getNextEdge(Graph traversalContext,
			Class<? extends Edge> aM1EdgeClass, boolean noSubclasses);

	/**
	 * Returns a sequence of all start vertices.
	 * 
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getAlphaVertices();

	/**
	 * Returns a sequence of all start vertices which are an instance of
	 * <code>aVertexClass</code>.
	 * 
	 * @param aVertexClass
	 *            {@link VertexClass}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getAlphaVertices(VertexClass aVertexClass);

	/**
	 * Returns a sequence of all start vertices which are an instance of
	 * <code>aVertexClass</code>.
	 * 
	 * @param aVertexClass
	 *            {@link Class}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getAlphaVertices(
			Class<? extends Vertex> aVertexClass);

	/**
	 * Returns a sequence of all start vertices.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getAlphaVertices(Graph traversalContext);

	/**
	 * Returns a sequence of all start vertices which are an instance of
	 * <code>aVertexClass</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param aVertexClass
	 *            {@link VertexClass}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getAlphaVertices(Graph traversalContext,
			VertexClass aVertexClass);

	/**
	 * Returns a sequence of all start vertices which are an instance of
	 * <code>aVertexClass</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param aVertexClass
	 *            {@link Class}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getAlphaVertices(Graph traversalContext,
			Class<? extends Vertex> aVertexClass);

	/**
	 * Returns a sequence of all end vertices.
	 * 
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getOmegaVertices();

	/**
	 * Returns a sequence of all end vertices which are an instance of
	 * <code>aVertexClass</code>.
	 * 
	 * @param aVertexClass
	 *            {@link VertexClass}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getOmegaVertices(VertexClass aVertexClass);

	/**
	 * Returns a sequence of all end vertices which are an instance of
	 * <code>aVertexClass</code>.
	 * 
	 * @param aVertexClass
	 *            {@link Class}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getOmegaVertices(
			Class<? extends Vertex> aVertexClass);

	/**
	 * Returns a sequence of all end vertices.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getOmegaVertices(Graph traversalContext);

	/**
	 * Returns a sequence of all end vertices which are an instance of
	 * <code>aVertexClass</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param aVertexClass
	 *            {@link VertexClass}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getOmegaVertices(Graph traversalContext,
			VertexClass aVertexClass);

	/**
	 * Returns a sequence of all end vertices which are an instance of
	 * <code>aVertexClass</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param aVertexClass
	 *            {@link Class}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getOmegaVertices(Graph traversalContext,
			Class<? extends Vertex> aVertexClass);

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
	 * Returns a sequence of all incident vertices which are an instance of
	 * <code>aVertexClass</code>.
	 * 
	 * @param aVertexClass
	 *            {@link VertexClass}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getIncidentVertices(VertexClass aVertexClass);

	/**
	 * Returns a sequence of all incident vertices which are an instance of
	 * <code>aVertexClass</code>.
	 * 
	 * @param aVertexClass
	 *            {@link Class}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getIncidentVertices(
			Class<? extends Vertex> aVertexClass);

	/**
	 * Returns a sequence of all incident vertices which are an instance of
	 * <code>aVertexClass</code> and are reachable via an {@link Incidence} of
	 * direction <code>direction</code>.
	 * 
	 * @param aVertexClass
	 *            {@link VertexClass}
	 * @param direction
	 *            {@link Direction}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getIncidentVertices(VertexClass aVertexClass,
			Direction direction);

	/**
	 * Returns a sequence of all incident vertices which are an instance of
	 * <code>aVertexClass</code> and are reachable via an {@link Incidence} of
	 * direction <code>direction</code>.
	 * 
	 * @param aVertexClass
	 *            {@link Class}
	 * @param direction
	 *            {@link Direction}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getIncidentVertices(
			Class<? extends Vertex> aVertexClass, Direction direction);

	/**
	 * Returns a sequence of all incident vertices.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getIncidentVertices(Graph traversalContext);

	/**
	 * Returns a sequence of all incident vertices which are connected via an
	 * {@link Incidence} of direction <code>dir</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param dir
	 *            {Direction}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getIncidentVertices(Graph traversalContext,
			Direction dir);

	/**
	 * Returns a sequence of all incident vertices which are an instance of
	 * <code>aVertexClass</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param aVertexClass
	 *            {@link VertexClass}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getIncidentVertices(Graph traversalContext,
			VertexClass aVertexClass);

	/**
	 * Returns a sequence of all incident vertices which are an instance of
	 * <code>aVertexClass</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param aVertexClass
	 *            {@link Class}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getIncidentVertices(Graph traversalContext,
			Class<? extends Vertex> aVertexClass);

	/**
	 * Returns a sequence of all incident vertices which are an instance of
	 * <code>aVertexClass</code> and are reachable via an {@link Incidence} of
	 * direction <code>direction</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param aVertexClass
	 *            {@link VertexClass}
	 * @param direction
	 *            {@link Direction}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getIncidentVertices(Graph traversalContext,
			VertexClass aVertexClass, Direction direction);

	/**
	 * Returns a sequence of all incident vertices which are an instance of
	 * <code>aVertexClass</code> and are reachable via an {@link Incidence} of
	 * direction <code>direction</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param aVertexClass
	 *            {@link Class}
	 * @param direction
	 *            {@link Direction}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getIncidentVertices(Graph traversalContext,
			Class<? extends Vertex> aVertexClass, Direction direction);

	/**
	 * Tests if this edge is a binary one (i.e., its edge class has only two
	 * incidences both with multiplicity 1 for the number of vertices at the
	 * edge)
	 * 
	 * @return true for binary edges, false otherwise
	 */
	public boolean isBinary();

}
