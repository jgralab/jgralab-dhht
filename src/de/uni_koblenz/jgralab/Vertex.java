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

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.impl.DirectedM1EdgeClass;

/**
 * represents a vertex, m1 classes inherit from this class
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public interface Vertex extends GraphElement {

	/**
	 * Returns the {@link VertexClass} of which this {@link Vertex} is an
	 * instance of.
	 * 
	 * @return {@link VertexClass}
	 */
	public VertexClass getType();

	/**
	 * Checks if the list of incident edges has changed with respect to the
	 * given <code>incidenceListVersion</code>.
	 */
	public boolean isIncidenceListModified(long incidenceListVersion);// old

	/**
	 * @return the internal vertex structure version
	 * @see #isIncidenceListModified(long)
	 */
	public long getIncidenceListVersion();// old

	/**
	 * @return the number of connected incidences to the vertex
	 */
	public int getDegree();// old

	/**
	 * @param orientation
	 *            of connected incidences,
	 * @return number of IN or OUT incidences connected to the vertex
	 */
	public int getDegree(EdgeDirection orientation);// old

	/**
	 * @param ec
	 *            an EdgeClass
	 * @return number of IN or OUT incidences of the specified EdgeClass
	 */
	public int getDegree(EdgeClass ec);// old

	/**
	 * @param ec
	 *            an EdgeClass
	 * @return number of IN or OUT incidences of the specified EdgeClass
	 */
	public int getDegree(Class<? extends Edge> ec);// old

	/**
	 * @param ec
	 *            an EdgeClass
	 * @param noSubClasses
	 *            if set to <code>true</code>, subclasses of <code>ec</code> are
	 *            not counted
	 * @return number of IN or OUT incidences of the specified EdgeClass
	 */
	public int getDegree(EdgeClass ec, boolean noSubClasses);// old

	/**
	 * @param ec
	 *            an EdgeClass
	 * @param noSubClasses
	 *            if set to <code>true</code>, subclasses of <code>ec</code> are
	 *            not counted
	 * @return number of IN or OUT incidences of the specified EdgeClass
	 */
	public int getDegree(Class<? extends Edge> ec, boolean noSubClasses);// old

	/**
	 * @param ec
	 *            an EdgeClass
	 * @param orientation
	 *            of connected incidences,
	 * @return number of IN or OUT incidences connected to the vertex
	 */
	public int getDegree(EdgeClass ec, EdgeDirection orientation);// old

	/**
	 * @param ec
	 *            an EdgeClass
	 * @param orientation
	 *            of connected incidences,
	 * @return number of IN or OUT incidences connected to the vertex
	 */
	public int getDegree(Class<? extends Edge> ec, EdgeDirection orientation);// old

	/**
	 * @param ec
	 *            an EdgeClass
	 * @param orientation
	 *            of connected incidences,
	 * @param noSubClasses
	 *            if set to <code>true</code>, subclasses of <code>ec</code> are
	 *            not counted
	 * @return number of IN or OUT incidences connected to the vertex
	 */
	public int getDegree(EdgeClass ec, EdgeDirection orientation,
			boolean noSubClasses);// old

	/**
	 * @param ec
	 *            an EdgeClass
	 * @param orientation
	 *            of connected incidences,
	 * @param noSubClasses
	 *            if set to <code>true</code>, subclasses of <code>ec</code> are
	 *            not counted
	 * @return number of IN or OUT incidences connected to the vertex
	 */
	public int getDegree(Class<? extends Edge> ec, EdgeDirection orientation,
			boolean noSubClasses);// old

	/**
	 * Returns the next {@link Vertex} in the sequence of all vertices in the
	 * complete {@link Graph} (vSeq). If this {@link Vertex} is the end of the
	 * sequence <code>null</code> is returned.
	 * 
	 * @return {@link Vertex}
	 */
	public Vertex getNextVertex();

	/**
	 * Returns the previous {@link Vertex} in the sequence of all vertices in
	 * the complete {@link Graph}(vSeq). If this {@link Vertex} is the beginning
	 * of the sequence <code>null</code> is returned.
	 * 
	 * @return {@link Vertex}
	 */
	public Vertex getPrevVertex();

	/**
	 * @param aVertexClass
	 *            the class of the next vertex
	 * @return the next vertex in vSeq of class aVertexClass or its superclasses
	 */
	public Vertex getNextVertex(VertexClass aVertexClass);// old

	/**
	 * @param aM1VertexClass
	 *            the class of the next vertex
	 * @return the next vertex in vSeq of class aVertexClass or its superclasses
	 */
	public Vertex getNextVertex(Class<? extends Vertex> aM1VertexClass);// old

	/**
	 * @param aVertexClass
	 *            the class of the next vertex
	 * @param noSubclasses
	 *            if true, no subclasses are returned
	 * @return the next vertex in vSeq of explicit class aVertexClass
	 */
	public Vertex getNextVertex(VertexClass aVertexClass, boolean noSubclasses);// old

	/**
	 * @param aM1VertexClass
	 *            the class of the next vertex
	 * @param noSubclasses
	 *            if true, no subclasses are returned
	 * @return the next vertex in vSeq of explicit class aVertexClass
	 */
	public Vertex getNextVertex(Class<? extends Vertex> aM1VertexClass,
			boolean noSubclasses);// old

	/**
	 * Returns the sequence of all incoming {@link Edge}s at this {@link Vertex}
	 * .
	 * 
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getAlphaEdges();

	/**
	 * Returns the sequence of all outgoing {@link Edge}s at this {@link Vertex}
	 * .
	 * 
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getOmegaEdges();

	/**
	 * Returns a sequence of all incident {@link Edge}s.
	 * 
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getIncidentEdges();

	/**
	 * @param orientation
	 *            of connected incidences,
	 * @return the first incidence of vertex with direction IN or OUT
	 */
	public Edge getFirstIncidence(EdgeDirection orientation);// old

	/**
	 * Get the first incident edge which as one of the aggregation semantics
	 * given by <code>kind</code> at either this vertex (thisIncidence == true)
	 * or that vertex (thisIncidence == false). If no <code>kind</code> is
	 * given, it simply returns the first incident edge.<br/>
	 * <br/>
	 * For example, this returns the first edge to a parent vertex in the
	 * containment hierarchy.
	 * 
	 * <pre>
	 * v.getFirstIncidence(true, AggregationKind.SHARED, AggregationKind.COMPOSITE)
	 * </pre>
	 * 
	 * And this returns the first edge to a child vertex in the containment
	 * hierarchy.
	 * 
	 * <pre>
	 * v.getFirstIncidence(false, AggregationKind.SHARED, AggregationKind.COMPOSITE)
	 * </pre>
	 * 
	 * @see Edge#getNextIncidence(boolean, AggregationKind...)
	 * 
	 * @param thisIncidence
	 *            if true, <code>kinds</code> has to match the incidence at this
	 *            vertex, else it has to match the opposite incidence
	 * 
	 * @return the first incident edge where the incidence at this vertex
	 *         (thisIncidence == true) or that vertex (thisIncidence == false)
	 *         has one of the aggregation semantics given by <code>kind</code>.
	 */
	public Edge getFirstIncidence(boolean thisIncidence,
			AggregationKind... kinds);// old

	/**
	 * @param anEdgeClass
	 *            the edge class to search for
	 * @return the first incidence in iSeq where the corresponding edge is of
	 *         class anEdgeClass
	 */
	public Edge getFirstIncidence(EdgeClass anEdgeClass);// old

	/**
	 * @param anEdgeClass
	 *            the edge class to search for
	 * @return the first incidence in iSeq where the corresponding edge is of
	 *         class anEdgeClass
	 */
	public Edge getFirstIncidence(Class<? extends Edge> anEdgeClass);// old

	/**
	 * @param anEdgeClass
	 *            the edge class to search for
	 * @param orientation
	 *            of the edge
	 * @return the first incidence in iSeq where the corresponding edge is of
	 *         class anEdgeClass
	 */
	public Edge getFirstIncidence(EdgeClass anEdgeClass,
			EdgeDirection orientation);// old

	/**
	 * @param anEdgeClass
	 *            the edge class to search for
	 * @param orientation
	 *            of the edge
	 * @return the first incidence in iSeq where the corresponding edge is of
	 *         class anEdgeClass
	 */
	public Edge getFirstIncidence(Class<? extends Edge> anEdgeClass,
			EdgeDirection orientation);// old

	/**
	 * @param anEdgeClass
	 *            the edge class to search for
	 * @param noSubclasses
	 *            if true, no subclasses are returned
	 * @return the first incidence in iSeq where the corresponding edge is of
	 *         explicit class anEdgeClass
	 */
	public Edge getFirstIncidence(EdgeClass anEdgeClass, boolean noSubclasses);// old

	/**
	 * @param anEdgeClass
	 *            the edge class to search for
	 * @param noSubclasses
	 *            if true, no subclasses are returned
	 * @return the first incidence in iSeq where the corresponding edge is of
	 *         explicit class anEdgeClass
	 */
	public Edge getFirstIncidence(Class<? extends Edge> anEdgeClass,
			boolean noSubclasses);// old

	/**
	 * @param anEdgeClass
	 *            the edge class to search for
	 * @param orientation
	 *            set to TRUE, if edge has the 'in'-orientation, set to FALSE,
	 *            if edge has the 'out'-orientation
	 * @param noSubclasses
	 *            if true, no subclasses are returned
	 * @return the first incidence in iSeq where the corresponding edge is of
	 *         explicit class anEdgeClass
	 */
	public Edge getFirstIncidence(EdgeClass anEdgeClass,
			EdgeDirection orientation, boolean noSubclasses);// old

	/**
	 * @param anEdgeClass
	 *            the edge class to search for
	 * @param orientation
	 *            set to TRUE, if edge has the 'in'-orientation, set to FALSE,
	 *            if edge has the 'out'-orientation
	 * @param noSubclasses
	 *            if true, no subclasses are returned
	 * @return the first incidence in iSeq where the corresponding edge is of
	 *         explicit class anEdgeClass
	 */
	public Edge getFirstIncidence(Class<? extends Edge> anEdgeClass,
			EdgeDirection orientation, boolean noSubclasses);// old

	/**
	 * @param v
	 * @return true, if this vertex is somewhere before v in vSeq
	 */
	public boolean isBefore(Vertex v);// old

	/**
	 * puts this vertex immediately before v in vSeq
	 * 
	 * @param v
	 */
	public void putBefore(Vertex v);// old

	/**
	 * @param v
	 * @return true, if this vertex is somewhere after v in vSeq
	 */
	public boolean isAfter(Vertex v);// old

	/**
	 * puts this vertex immediately after v in vSeq
	 * 
	 * @param v
	 */
	public void putAfter(Vertex v);// old

	/**
	 * removes this vertex from vSeq and erases its attributes
	 */
	public void delete();// old

	/**
	 * Using this method, one can simply iterate over all incident edges of this
	 * vertex using the advanced for-loop
	 * 
	 * @return a iterable object which can be iterated through using the
	 *         advanced for-loop
	 */
	@Deprecated
	public Iterable<Edge> incidences();// old

	/**
	 * Return an List&lt;vertexType&gt; over all vertices reachable from this
	 * vertex via the specified <code>pathDescription</code>.
	 * 
	 * @param pathDescription
	 *            a GReQL path description like
	 *            <code>-->{EdgeType1}+ <>--{EdgeType2}</code>
	 * @param vertexType
	 *            the class of the vertices you can reach with that path (acts
	 *            as implicit GoalRestriction)
	 * @return a List of the reachable vertices
	 */
	public <T extends Vertex> List<T> reachableVertices(String pathDescription,
			Class<T> vertexType);// old

	/**
	 * @param <T>
	 * @param returnType
	 *            the class of the vertices you can reach with that path (acts
	 *            as implicit GoalRestriction)
	 * @param pathElements
	 *            an array of {@link PathElement}s
	 * @return a Set of vertices reachable by traversing the path given by
	 *         pathElements
	 */
	public <T extends Vertex> Set<T> reachableVertices(Class<T> returnType,
			PathElement... pathElements);// old

	/**
	 * Using this method, one can simply iterate over all incident edges of this
	 * vertex using the advanced for-loop
	 * 
	 * @param dir
	 *            the direction of the edges which should be iterated, either
	 *            EdgeDirection.IN or EdgeDirection.OUT
	 * @return a iterable object which can be iterated through using the
	 *         advanced for-loop
	 */
	public Iterable<Edge> incidences(EdgeDirection dir);// old

	/**
	 * Using this method, one can simply iterate over all incident edges of this
	 * vertex using the advanced for-loop
	 * 
	 * @param eclass
	 *            the EdgeClass of the edges which should be iterated
	 * @param dir
	 *            the direction of the edges which should be iterated, either
	 *            EdgeDirection.IN or EdgeDirection.OUT
	 * @return a iterable object which can be iterated through using the
	 *         advanced for-loop
	 */
	public Iterable<Edge> incidences(EdgeClass eclass, EdgeDirection dir);// old

	/**
	 * Using this method, one can simply iterate over all incident edges of this
	 * vertex using the advanced for-loop
	 * 
	 * @param eclass
	 *            the M1-Class of the edges which should be iterated
	 * @param dir
	 *            the direction of the edges which should be iterated, either
	 *            EdgeDirection.IN or EdgeDirection.OUT
	 * @return a iterable object which can be iterated through using the
	 *         advanced for-loop
	 */
	public Iterable<Edge> incidences(Class<? extends Edge> eclass,
			EdgeDirection dir);// old

	/**
	 * Using this method, one can simply iterate over all incident edges of this
	 * vertex using the advanced for-loop
	 * 
	 * @param eclass
	 *            the EdgeClass of the edges which should be iterated
	 * @return a iterable object which can be iterated through using the
	 *         advanced for-loop
	 */
	public Iterable<Edge> incidences(EdgeClass eclass);// old

	/**
	 * Using this method, one can simply iterate over all incident edges of this
	 * vertex using the advanced for-loop
	 * 
	 * @param eclass
	 *            the M1-Class of the edges which should be iterated
	 * @return a iterable object which can be iterated through using the
	 *         advanced for-loop
	 */
	public Iterable<Edge> incidences(Class<? extends Edge> eclass);// old

	/**
	 * tests if the Edge <code>edge</code> may start at this vertex
	 * 
	 * @return <code>true</code> iff <code>edge</code> may start at this vertex
	 */
	public boolean isValidAlpha(Edge edge);// old

	/**
	 * tests if the Edge <code>edge</code> may end at this vertex
	 * 
	 * @return <code>true</code> iff <code>edge</code> may end at this vertex
	 */
	public boolean isValidOmega(Edge edge);// old

	/**
	 * Sorts the incidence sequence according to the given comparator in
	 * ascending order.
	 * 
	 * @param comp
	 *            the comparator that defines the desired incidence order.
	 */
	public void sortIncidences(Comparator<Edge> comp);// old

	public DirectedM1EdgeClass getEdgeForRolename(String rolename);// old

	public List<? extends Vertex> adjacences(String role);// old

	public Edge addAdjacence(String role, Vertex other);// old

	public List<Vertex> removeAdjacences(String role);// old

	public void removeAdjacence(String role, Vertex other);// old

}
