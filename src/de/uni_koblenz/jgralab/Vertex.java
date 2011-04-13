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


import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * Represents a vertex, m1 classes inherit from this class.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public interface Vertex extends GraphElement<VertexClass, Vertex, Edge> {

	/**
	 * Returns the next {@link Vertex} in the sequence of all vertices in the
	 * complete {@link Graph} (vSeq). If this {@link Vertex} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @return {@link Vertex}
	 */
	public Vertex getNextVertex() throws RemoteException;

	/**
	 * Returns the previous {@link Vertex} in the sequence of all vertices in
	 * the complete {@link Graph}(vSeq). If this {@link Vertex} is the beginning
	 * of the sequence <code>null</code> is returned.
	 * 
	 * @return {@link Vertex}
	 */
	public Vertex getPreviousVertex() throws RemoteException;

	/**
	 * Returns the next {@link Vertex} in the sequence of all vertices in the
	 * complete {@link Graph} (vSeq). If this {@link Vertex} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param aVertexClass
	 *            {@link VertexClass} the next {@link Vertex} should have
	 * @return {@link Vertex}
	 */
	public Vertex getNextVertex(VertexClass aVertexClass) throws RemoteException;

	/**
	 * Returns the next {@link Vertex} in the sequence of all vertices in the
	 * complete {@link Graph} (vSeq). If this {@link Vertex} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param aM1VertexClass
	 *            {@link Class} the next {@link Vertex} should have
	 * @return {@link Vertex}
	 */
	public <T extends Vertex> T getNextVertex(Class<T> aM1VertexClass) throws RemoteException;

	/**
	 * Returns the next {@link Vertex} in the sequence of all vertices in the
	 * complete {@link Graph} (vSeq). If this {@link Vertex} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param aVertexClass
	 *            {@link VertexClass} the next {@link Vertex} should have
	 * @param noSubclasses
	 *            boolean if <code>true</code>, no verices which are an instance
	 *            of a subclass of <code>aVertexClass</code> are returned
	 * @return {@link Vertex}
	 */
	public Vertex getNextVertex(VertexClass aVertexClass, boolean noSubclasses) throws RemoteException;

	/**
	 * Returns the next {@link Vertex} in the sequence of all vertices in the
	 * complete {@link Graph} (vSeq). If this {@link Vertex} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param aM1VertexClass
	 *            {@link Class} the next {@link Vertex} should have
	 * @param noSubclasses
	 *            boolean if <code>true</code>, no verices which are an instance
	 *            of a subclass of <code>aM1VertexClass</code> are returned
	 * @return {@link Vertex}
	 */
	public <T extends Vertex> T getNextVertex(Class<T> aM1VertexClass, boolean noSubclasses) throws RemoteException;

	/**
	 * Returns the next {@link Vertex} in the sequence of all vertices in the
	 * complete {@link Graph} (vSeq). If this {@link Vertex} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Vertex}
	 */
	public Vertex getNextVertex(Graph traversalContext) throws RemoteException;

	/**
	 * Returns the previous {@link Vertex} in the sequence of all vertices in
	 * the complete {@link Graph}(vSeq). If this {@link Vertex} is the beginning
	 * of the sequence <code>null</code> is returned.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Vertex}
	 */
	public Vertex getPreviousVertex(Graph traversalContext) throws RemoteException;

	/**
	 * Returns the next {@link Vertex} in the sequence of all vertices in the
	 * complete {@link Graph} (vSeq). If this {@link Vertex} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param aVertexClass
	 *            {@link VertexClass} the next {@link Vertex} should have
	 * @return {@link Vertex}
	 */
	public Vertex getNextVertex(Graph traversalContext, VertexClass aVertexClass) throws RemoteException;

	/**
	 * Returns the next {@link Vertex} in the sequence of all vertices in the
	 * complete {@link Graph} (vSeq). If this {@link Vertex} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param aM1VertexClass
	 *            {@link Class} the next {@link Vertex} should have
	 * @return {@link Vertex}
	 */
	public <T extends Vertex> T getNextVertex(Graph traversalContext, Class<T> aM1VertexClass) throws RemoteException;

	/**
	 * Returns the next {@link Vertex} in the sequence of all vertices in the
	 * complete {@link Graph} (vSeq). If this {@link Vertex} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param aVertexClass
	 *            {@link VertexClass} the next {@link Vertex} should have
	 * @param noSubclasses
	 *            boolean if <code>true</code>, no verices which are an instance
	 *            of a subclass of <code>aVertexClass</code> are returned
	 * @return {@link Vertex}
	 */
	public Vertex getNextVertex(Graph traversalContext, VertexClass aVertexClass, boolean noSubclasses) throws RemoteException;

	/**
	 * Returns the next {@link Vertex} in the sequence of all vertices in the
	 * complete {@link Graph} (vSeq). If this {@link Vertex} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param aM1VertexClass
	 *            {@link Class} the next {@link Vertex} should have
	 * @param noSubclasses
	 *            boolean if <code>true</code>, no verices which are an instance
	 *            of a subclass of <code>aM1VertexClass</code> are returned
	 * @return {@link Vertex}
	 */
	public <T extends Vertex> T getNextVertex(Graph traversalContext,
			Class<T> aM1VertexClass, boolean noSubclasses) throws RemoteException;

	/**
	 * Returns the sequence of all incoming {@link Edge}s at this {@link Vertex}
	 * .
	 * 
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getAlphaEdges() throws RemoteException;

	/**
	 * Returns the sequence of all incoming {@link Edge}s at this {@link Vertex}
	 * which are an instance of <code>anEdgeClass</code>.
	 * 
	 * @param anEdgeClass
	 *            {@link EdgeClass}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getAlphaEdges(EdgeClass anEdgeClass) throws RemoteException;

	/**
	 * Returns the sequence of all incoming {@link Edge}s at this {@link Vertex}
	 * which are an instance of <code>anEdgeClass</code>.
	 * 
	 * @param anEdgeClass
	 *            {@link Class}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public <T extends Edge> Iterable<T> getAlphaEdges(Class<T> anEdgeClass) throws RemoteException;

	/**
	 * Returns the sequence of all incoming {@link Edge}s at this {@link Vertex}
	 * .
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getAlphaEdges(Graph traversalContext) throws RemoteException;

	/**
	 * Returns the sequence of all incoming {@link Edge}s at this {@link Vertex}
	 * which are an instance of <code>anEdgeClass</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anEdgeClass
	 *            {@link EdgeClass}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getAlphaEdges(Graph traversalContext,
			EdgeClass anEdgeClass) throws RemoteException;

	/**
	 * Returns the sequence of all incoming {@link Edge}s at this {@link Vertex}
	 * which are an instance of <code>anEdgeClass</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anEdgeClass
	 *            {@link Class}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public <T extends Edge> Iterable<T> getAlphaEdges(Graph traversalContext,
			Class<T> anEdgeClass) throws RemoteException;

	/**
	 * Returns the sequence of all outgoing {@link Edge}s at this {@link Vertex}
	 * .
	 * 
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getOmegaEdges() throws RemoteException;

	/**
	 * Returns the sequence of all outgoing {@link Edge}s at this {@link Vertex}
	 * which are an instance of <code>anEdgeClass</code>.
	 * 
	 * @param anEdgeClass
	 *            {@link EdgeClass}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getOmegaEdges(EdgeClass anEdgeClass) throws RemoteException;

	/**
	 * Returns the sequence of all outgoing {@link Edge}s at this {@link Vertex}
	 * which are an instance of <code>anEdgeClass</code>.
	 * 
	 * @param anEdgeClass
	 *            {@link Class}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public <T extends Edge> Iterable<T> getOmegaEdges(Class<T> anEdgeClass) throws RemoteException;

	/**
	 * Returns the sequence of all outgoing {@link Edge}s at this {@link Vertex}
	 * .
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getOmegaEdges(Graph traversalContext) throws RemoteException;

	/**
	 * Returns the sequence of all outgoing {@link Edge}s at this {@link Vertex}
	 * which are an instance of <code>anEdgeClass</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anEdgeClass
	 *            {@link EdgeClass}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getOmegaEdges(Graph traversalContext,
			EdgeClass anEdgeClass) throws RemoteException;

	/**
	 * Returns the sequence of all outgoing {@link Edge}s at this {@link Vertex}
	 * which are an instance of <code>anEdgeClass</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anEdgeClass
	 *            {@link Class}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public <T extends Edge> Iterable<T> getOmegaEdges(Graph traversalContext,
			Class<T> anEdgeClass) throws RemoteException;

	/**
	 * Returns a sequence of all incident {@link Edge}s.
	 * 
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getIncidentEdges() throws RemoteException;

	/**
	 * Returns a sequence of all incident {@link Edge}s which are reachable via
	 * an {@link Incidence} of direction <code>direction</code>.
	 * 
	 * @param direction
	 *            {@link Direction}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getIncidentEdges(Direction direction) throws RemoteException;

	/**
	 * Returns a sequence of all incident {@link Edge}s which are an instance of
	 * <code>anEdgeClass</code>.
	 * 
	 * @param anEdgeClass
	 *            {@link EdgeClass}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getIncidentEdges(EdgeClass anEdgeClass) throws RemoteException;

	/**
	 * Returns a sequence of all incident {@link Edge}s which are an instance of
	 * <code>anEdgeClass</code>.
	 * 
	 * @param anEdgeClass
	 *            {@link Class}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public <T extends Edge> Iterable<T> getIncidentEdges(Class<T> anEdgeClass) throws RemoteException;

	/**
	 * Returns a sequence of all incident {@link Edge}s which are an instance of
	 * <code>anEdgeClass</code> and are reachable via an {@link Incidence} of
	 * direction <code>direction</code>.
	 * 
	 * @param anEdgeClass
	 *            {@link EdgeClass}
	 * @param direction
	 *            {@link Direction}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getIncidentEdges(EdgeClass anEdgeClass,
			Direction direction) throws RemoteException;

	/**
	 * Returns a sequence of all incident {@link Edge}s which are an instance of
	 * <code>anEdgeClass</code> and are reachable via an {@link Incidence} of
	 * direction <code>direction</code>.
	 * 
	 * @param anEdgeClass
	 *            {@link Class}
	 * @param direction
	 *            {@link Direction}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public <T extends Edge> Iterable<T> getIncidentEdges(Class<T> anEdgeClass,
			Direction direction) throws RemoteException;

	/**
	 * Returns a sequence of all incident {@link Edge}s.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getIncidentEdges(Graph traversalContext) throws RemoteException;

	/**
	 * Returns a sequence of all incident {@link Edge}s which are reachable via
	 * an {@link Incidence} of direction <code>direction</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param direction
	 *            {@link Direction}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getIncidentEdges(Graph traversalContext,
			Direction direction) throws RemoteException;

	/**
	 * Returns a sequence of all incident {@link Edge}s which are an instance of
	 * <code>anEdgeClass</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anEdgeClass
	 *            {@link EdgeClass}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getIncidentEdges(Graph traversalContext,
			EdgeClass anEdgeClass) throws RemoteException;

	/**
	 * Returns a sequence of all incident {@link Edge}s which are an instance of
	 * <code>anEdgeClass</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anEdgeClass
	 *            {@link Class}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public <T extends Edge> Iterable<T> getIncidentEdges(
			Graph traversalContext, Class<T> anEdgeClass) throws RemoteException;

	/**
	 * Returns a sequence of all incident {@link Edge}s which are an instance of
	 * <code>anEdgeClass</code> and are reachable via an {@link Incidence} of
	 * direction <code>direction</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anEdgeClass
	 *            {@link EdgeClass}
	 * @param direction
	 *            {@link Direction}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getIncidentEdges(Graph traversalContext,
			EdgeClass anEdgeClass, Direction direction) throws RemoteException;

	/**
	 * Returns a sequence of all incident {@link Edge}s which are an instance of
	 * <code>anEdgeClass</code> and are reachable via an {@link Incidence} of
	 * direction <code>direction</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anEdgeClass
	 *            {@link Class}
	 * @param direction
	 *            {@link Direction}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public <T extends Edge> Iterable<T> getIncidentEdges(
			Graph traversalContext, Class<T> anEdgeClass, Direction direction) throws RemoteException;

	/**
	 * Returns a {@link List}&lt;<code>vertexType</code>&gt; over all vertices
	 * reachable from this vertex via the specified <code>pathDescription</code>
	 * .
	 * 
	 * @param pathDescription
	 *            {@link String} a GReQL path description like
	 *            <code>-->{EdgeType1}+ <>--{EdgeType2}</code>
	 * @param vertexType
	 *            {@link Class} of the vertices you can reach with that path
	 *            (acts as implicit GoalRestriction)
	 * @return {@link List} of the reachable vertices
	 */
	// public <T extends Vertex> List<T> reachableVertices(String
	// pathDescription,
	// Class<T> vertexType);

	/**
	 * Returns a {@link Set}&lt;<code>vertexType</code>&gt; over all vertices
	 * reachable from this vertex via the specified <code>pathElements</code> .
	 * 
	 * @param returnType
	 *            {@link Class} of the vertices you can reach with that path
	 *            (acts as implicit GoalRestriction)
	 * @param pathElements
	 *            {@link PathElement}...
	 * @return {@link Set} of vertices reachable by traversing the path given by
	 *         pathElements
	 */
	// public <T extends Vertex> Set<T> reachableVertices(Class<T> returnType,
	// PathElement... pathElements);

}
