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

package de.uni_koblenz.jgralab.schema;

import java.util.List;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Graph;

/**
 * Represents a <code>GraphClass</code> in the <code>Schema</code>, that holds
 * all <code>GraphElementClasses</code>.
 * 
 * <p>
 * <b>Note:</b> in the following, <code>graphClass</code>, and
 * <code>graphClass'</code>, will represent the states of the given
 * <code>GraphClass</code> before, respectively after, any operation.
 * </p>
 * 
 * <p>
 * <b>Note:</b> in the following it is understood that method arguments differ
 * from <code>null</code>. Therefore there will be no preconditions addressing
 * this matter.
 * </p>
 * 
 * @author ist@uni-koblenz.de
 */
public interface GraphClass extends AttributedElementClass<GraphClass, Graph> {

	public final static String DEFAULTGRAPHCLASS_NAME = "Graph";

	/**
	 * creates an edge class with the given qualified name
	 * 
	 * @param qualifiedName
	 *            the qualified name of the edge class to be created
	 * @return the created edge class
	 */
	public EdgeClass createEdgeClass(String qualifiedName);

	/**
	 * creates an binary edge class with the given qualified name
	 * 
	 * @param qualifiedName
	 *            the qualified name of the edge class to be created
	 * @return the created edge class
	 */
	public BinaryEdgeClass createBinaryEdgeClass(String qualifiedName);

	/**
	 * Creates a new IncidenceClass between vertexClass and edgeClass and adds
	 * it to those classes
	 * 
	 * @param vertexClass
	 *            the vertex class the created incidence class should be
	 *            connected to
	 * @param edgeClass
	 *            the edge class the created incidence class should be connected
	 *            to
	 * @param rolename
	 *            the name of the incidence class to be created, need to be
	 *            unique at edge and vertex class
	 * @param isAbstract
	 *            if the incidence class is an abstract one (derived in UML)
	 * @param minEdgesAtVertex
	 *            the minimal number of edges to be connected by such an
	 *            incidence to each vertex
	 * @param maxEdgesAtVertex
	 *            the maximal number of edges to be connected by such an
	 *            incidence to each vertex
	 * @param minVerticesAtEdge
	 *            the minimal number of vertices to be connected by such an
	 *            incidence to each edge
	 * @param maxVerticesAtEdge
	 *            the maximal number of vertices to be connected by such an
	 *            incidence to each edge
	 * @param dir
	 *            the direction of the incidence (from edge to vertex or vice
	 *            versa)
	 * @param kind
	 *            the kind of the incidence (aggregation, composition)
	 * @return the created IncidenceClass
	 */
	public IncidenceClass createIncidenceClass(EdgeClass edgeClass,
			VertexClass vertexClass, String rolename, boolean isAbstract,
			int minEdgesAtVertex, int maxEdgesAtVertex, int minVerticesAtEdge,
			int maxVerticesAtEdge, Direction dir, IncidenceType kind);

	/**
	 * creates a vertex class with the vertexclassname name
	 * 
	 * @param qualifiedName
	 *            the qualified name of the vertex class to be created
	 * @return the created vertex class
	 */
	public VertexClass createVertexClass(String qualifiedName);

	/**
	 * @param name
	 *            the name to search for
	 * @return the contained graph element class with the name name
	 */
	public GraphElementClass<?, ?,?,?> getGraphElementClass(String name);

	/**
	 * @return a list of all EdgeClasses this graphclass knows, including
	 *         inherited EdgeClasses
	 */
	public List<EdgeClass> getEdgeClasses();

	/**
	 * @return a list of all the edge/vertex/aggregation/composition classes of
	 *         this graph class, including inherited classes
	 */
	public List<GraphElementClass<?, ?,?,?>> getGraphElementClasses();

	/**
	 * @return a list of all the vertex classes of this graph class, including
	 *         inherited vertex classes
	 */
	public List<VertexClass> getVertexClasses();

	/**
	 * Returns the VertexClass with the given name. This GraphClass and the
	 * superclasses will be searched for a VertexClass with this name
	 * 
	 * @param name
	 *            the name of the VertexClass to search for
	 * @return the VertexClass with the given name or null, if no such
	 *         VertexClass exists
	 */
	public VertexClass getVertexClass(String name);

	/**
	 * Returns the number of VertexClasses defined in this GraphClass.
	 * 
	 * @return the number of VertexClasses defined in this GraphClass.
	 */
	public int getVertexClassCount();

	/**
	 * Returns the EdgeClass with the given name. This GraphClass and the
	 * superclasses will be searched for a EdgeClass with this name
	 * 
	 * @param name
	 *            the name of the EdgeClass to search for
	 * @return the EdgeClass with the given name or null, if no such EdgeClass
	 *         exists
	 */
	public EdgeClass getEdgeClass(String name);

	/**
	 * Returns the number of EdgeClasses (that is Edge-/Aggregation- and
	 * CompositionClasses) defined in this GraphClass.
	 * 
	 * @return the number of EdgeClasses defined in this GraphClass.
	 */
	public int getEdgeClassCount();

	/**
	 * @param aGraphElementClass
	 *            a vertex/edge/aggregation/composition class name
	 * @return true, if this graph class aggregates aGraphElementClass
	 */
	public boolean knows(GraphElementClass<?, ?, ?,?> aGraphElementClass);

	/**
	 * @param aGraphElementClass
	 *            a vertex/edge/aggregation/composition class name
	 * @return true, if this graph class aggregates aGraphElementClass
	 */
	public boolean knows(String aGraphElementClass);
}
