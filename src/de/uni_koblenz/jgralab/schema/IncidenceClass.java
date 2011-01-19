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
import de.uni_koblenz.jgralab.Incidence;

public interface IncidenceClass {

	public static final String DEFAULTEDGECLASS_NAME = "Incidence";

	/**
	 * adds a superclass to the list of superclasses
	 * 
	 * @param superClass
	 *            the edge class to be added to the list of superclasses if an
	 *            attribute name exists in superClass and in this class
	 * 
	 */
	public void addSuperClass(IncidenceClass superClass);

	/**
	 * Lists all direct subclasses of this element.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>subClasses = incidences.getDirectSubClasses();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>subClasses != null</code></li>
	 * <li><code>subClasses.size() >= 0</code></li>
	 * <li><code>subClasses</code> holds all of <code>attrElement´s</code>
	 * direct subclasses</li>
	 * <li><code>subClasses</code> does not hold any of
	 * <code>attrElement´s</code> inherited subclasses</li>
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all direct subclasses of this element
	 */
	public Set<IncidenceClass> getDirectSubClasses();

	/**
	 * Returns all direct superclasses of this element.
	 * 
	 * <p>
	 * <b>Note:</b> Each instance of a subclass of <code>IncidenceClass</code>
	 * has one default direct superclass. Please consult the specifications of
	 * the used subclass for details.
	 * </p>
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>superClasses = incidence.getDirectSuperClasses();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>superClasses != null</code></li>
	 * <li><code>superClasses.size() >= 0</code></li>
	 * <li><code>superClasses</code> holds all of <code>incidence´s</code>
	 * direct superclasses (including the default superclass)</li>
	 * <li><code>superClasses</code> does not hold any of
	 * <code>attrElement´s</code> inherited superclasses
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all direct superclasses of this element
	 */
	public Set<IncidenceClass> getDirectSuperClasses();

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
	 * States if this IncidenceClass is abstract. Abstract elements can´t have
	 * instances.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>isAbstract = incidenceClass.isAbstract();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isAbstract</code> is:
	 * <ul>
	 * <li><code>true</code> if <code>incidenceClass</code> is abstract and
	 * therefore may not have any instances</li>
	 * <li>otherwise <code>false</code>
	 * </ul>
	 * 
	 * @return <code>true</code>, if the element is abstract , otherwise
	 *         <code>false</code>
	 */
	public boolean isAbstract();

	/**
	 * @return the type of this IncidenceClass, EDGE for a normal edge end,
	 *         AGGREGATION for an aggregation end and COMPOSITION for a
	 *         composition end
	 */
	public IncidenceType getIncidentType();

	/**
	 * sets the type of this IncidenceClass, EDGE for a normal edge end,
	 * AGGREGATION for an aggregation end and COMPOSITION for a composition end
	 */
	public void setIncidenceType(IncidenceType kind);

	/**
	 * TODO
	 * 
	 * @return the set of IncidenceClasses which are subsetted (i.e.
	 *         specialized) by this IncidenceClass
	 */
	@Deprecated
	public Set<IncidenceClass> getSubsettedIncidenceClasses();

	/**
	 * TODO
	 * 
	 * @return the set of IncidenceClasses which are directly subsetted (i.e.
	 *         specialized) by this IncidenceClass
	 */
	@Deprecated
	public Set<IncidenceClass> getOwnSubsettedIncidenceClasses();

	/**
	 * TODO
	 * 
	 * @return the set of IncidenceClasses which are redefined (i.e. specialized
	 *         and overwritten) by this IncidenceClass
	 */
	@Deprecated
	public Set<IncidenceClass> getRedefinedIncidenceClasses();

	/**
	 * TODO
	 * 
	 * @return the set of IncidenceClasses which are directly redefined (i.e.
	 *         specialized and overwritten) by this IncidenceClass
	 */
	@Deprecated
	public Set<IncidenceClass> getOwnRedefinedIncidenceClasses();

	public Set<IncidenceClass> getHidingEndsAtEdge();

	public Set<IncidenceClass> getHiddenEndsAtEdge();

	public Set<IncidenceClass> getHidingEndsAtVertex();

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
	 * @return the set of all role names valid for this IncidenceClass
	 */
	public Set<String> getAllRoles();

	/**
	 * TODO
	 * 
	 * @return the set of roles which are redefined by this IncidenceClass
	 */
	public Set<String> getRedefinedRoles();

	/**
	 * TODO Marks a role which is already subsetted by this IncidenceClass as
	 * redefined.
	 */
	public void addRedefinedRole(String rolename);

	/**
	 * TODO Marks a set of roles which are already subsetted by this
	 * IncidenceClass as redefined.
	 */
	public void addRedefinedRoles(Set<String> rolenames);

	/**
	 * @return the IncidenceClass at the other end of the EdgeClass this
	 *         IncidenceClass belongs to
	 */
	public IncidenceClass getOpposite();

	/**
	 * Returns the M1-class of this {@link Incidence}.
	 * 
	 * @return {@link Class}
	 */
	public Class<? extends Incidence> getM1Class();

}
