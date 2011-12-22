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

import java.util.Set;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;

public interface IncidenceClass extends TypedElementClass<IncidenceClass, Incidence > {

	public static final String DEFAULTEDGECLASS_NAME = "Incidence";


	/**
	 * @return the upper multiplicity, i.e. the maximal number of edges
	 *         connected to the vertex
	 */
	public int getMaxEdgesAtVertex();

	/**
	 * @return the upper multiplicity, i.e. the maximal number of vertices
	 *         connected to the edge
	 */
	public int getMaxVerticesAtEdge();

	/**
	 * @return the lower multiplicity, i.e. the minimal number of edges
	 *         connected to the vertex at the opposite end
	 */
	public int getMinEdgesAtVertex();

	/**
	 * @return the lower multiplicity, i.e. the minimal number of edges
	 *         connected to the vertex at the opposite end
	 */
	public int getMinVerticesAtEdge();

	/**
	 * @return the direction of this incidenceclass - either Vertex (from edge
	 *         to vertex) or edge (from vertex to edge)
	 */
	public Direction getDirection();

	/**
	 * @return the name of this incidence class, i.e. the rolename of the edge
	 *         end
	 */
	public String getRolename();

	
	/**
	 * @return the type of this IncidenceClass, EDGE for a normal edge end,
	 *         AGGREGATION for an aggregation end and COMPOSITION for a
	 *         composition end
	 */
	public IncidenceType getIncidenceType();

	/**
	 * sets the type of this IncidenceClass, EDGE for a normal edge end,
	 * AGGREGATION for an aggregation end and COMPOSITION for a composition end
	 */
	public void setIncidenceType(IncidenceType kind);

	/**
	 * @return a set of IncidenceClasses which are hidden by this at the edge
	 */
	public Set<IncidenceClass> getHiddenEndsAtEdge();

	/**
	 * @return a set of IncidenceClasses which are hidden by this at the vertex
	 */
	public Set<IncidenceClass> getHiddenEndsAtVertex();

	/**
	 * @return the VertexClass this IncidenceClass is connected to
	 */
	public VertexClass getVertexClass();

	/**
	 * @return the EdgeClass this IncidenceClass is connected to
	 */
	public EdgeClass getEdgeClass();



	/**
	 * 
	 * @param graphElementClass
	 * 
	 * @return the other GraphElementClass connected to this incidenceClass,
	 *         e.g. if graphElementClass is the EdgeClass connected, the method
	 *         returns the VertexClass and vice versa
	 */
	public <OwnTypeClass extends GraphElementClass<?,?,?,?>, OwnType extends GraphElement<?,?,?,?>, DualTypeClass extends GraphElementClass<?,?,?,?>, DualType extends GraphElement<?,?,?,?>> GraphElementClass<? extends DualTypeClass, ? extends DualType, ? extends OwnTypeClass, ? extends OwnType> getOtherGraphElementClass(
			GraphElementClass<? extends OwnTypeClass, ? extends OwnType, ? extends DualTypeClass, ? extends DualType> graphElementClass);
	
	public GraphElementClass<?,?,?,?> getConnectedGraphElementClassOfOwnType(GraphElementClass<?,?,?,?> graphElementClass);
	
	public GraphElementClass<?,?,?,?> getConnectedGraphElementClassOfDualType(GraphElementClass<?,?,?,?> graphElementClass);



	/**
	 * @return the set of all role names valid for this IncidenceClass
	 */
	public Set<String> getAllRoles();

}
