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
import de.uni_koblenz.jgralab.schema.exception.M1ClassAccessException;

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
	 * Defines if this IncidenceClass is abstract. Abstract IncidenceClass can´t
	 * have instances.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>incidenceClass.setAbstract(value);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>incidenceClass'</code> is abstract and no
	 * new instances can be created
	 * </p>
	 * 
	 * @param isAbstract
	 *            the new value defining the state of this IncidenceClass
	 */
	public void setAbstract(boolean isAbstract);

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
	 * Returns the M1-class of this {@link Incidence}.
	 * 
	 * @return {@link Class}
	 */
	public Class<? extends Incidence> getM1Class();

	/**
	 * Checks if the current element is a direct or indirect subclass of another
	 * incidence class.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isSubClass = attrElement.isSubClassOf(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isSubClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> incidence class is a
	 * direct or inherited superclass of this incidence</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>incClass</code> and the given <code>other</code> incidence
	 * class are the same</li>
	 * <li>the <code>other</code> incidence class is not a direct or inherited
	 * superclass of <code>incClass</code></li>
	 * <li>the <code>other</code> incidence class has no relation with
	 * <code>incClass</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param anIncidenceClass
	 *            the possible superclass of this incidence class
	 * @return <code>true</code> if <code>anIncidenceClass</code> is a direct or
	 *         indirect subclass of this incidence class, otherwise
	 *         <code>false</code>
	 */

	/**
	 * 
	 * @param graphElementClass
	 * 
	 * @return the other GraphElementClass connected to this incidenceClass,
	 *         e.g. if graphElementClass is the EdgeClass connected, the method
	 *         returns the VertexClass and vice versa
	 */
	public GraphElementClass<?, ?> getOtherGraphElementClass(
			GraphElementClass<?, ?> graphElementClass);
	
	public GraphElementClass<?, ?> getConnectedGraphElementClassOfOwnType(GraphElementClass<?, ?> graphElementClass);
	
	public GraphElementClass<?, ?> getConnectedGraphElementClassOfDualType(GraphElementClass<?, ?> graphElementClass);

	public boolean isSubClassOf(IncidenceClass anIncidenceClass);

	/**
	 * Lists all direct and indirect superclasses of this IncidenceClass.
	 * 
	 * <p>
	 * <b>Note:</b> Each instance of a subclass of
	 * <code>IncidenceClassClass</code> has a dedicated default superclass at
	 * the top of its inheritance hierarchy. Please consult the specifications
	 * of the used subclass for details.
	 * </p>
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>superClasses = incidenceClass.getAllSuperClasses();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>superClasses != null </code></li>
	 * <li><code>superClasses.size() >= 0</code></li>
	 * <li><code>superClasses</code> holds all of <code>incidenceClass´s</code>
	 * direct and indirect superclasses (including the default superclass)</li>
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all direct and indirect superclasses of this
	 *         IncidenceClass
	 */
	public Set<IncidenceClass> getAllSuperClasses();

	/**
	 * Returns all direct and indirect subclasses of this IncidenceClass.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>subClasses = incidenceClass.getAllSubClasses();</code>
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
	 * <li><code>subClasses</code> holds all of <code>incidenceClass´s</code>
	 * direct and indirect subclasses</li>
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all direct and indirect subclasses of this
	 *         IncidenceClass
	 */
	public Set<IncidenceClass> getAllSubClasses();

	/**
	 * Returns the name of the {@link Class} of <code>ic</code>.
	 * 
	 * @param ic
	 *            {@link IncidenceClass}
	 * @return {@link String}
	 */
	public String getIncidenceClassName(IncidenceClass ic);

	/**
	 * Returns the M1 implementation class for this IncidenceClass.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>m1ImplClass = incidenceClass.getM1Class();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> not yet defined
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> not yet defined
	 * </p>
	 * 
	 * @return the M1 implementation class for this IncidenceClass
	 * 
	 * @throws M1ClassAccessException
	 *             if:
	 *             <ul>
	 *             <li>this IncidenceClass is abstract</li>
	 *             <li>there are reflection exceptions</li>
	 *             </ul>
	 */
	public Class<? extends Incidence> getM1ImplementationClass();

	/**
	 * Checks if the current element is a direct subclass of another
	 * IncidenceClass.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isDirectSubClass = incidenceClass.isDirectSubClassOf(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isDirectSubClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> IncidenceClass is a
	 * direct superclass of this element</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>incidenceClass</code> and the given <code>other</code>
	 * IncidenceClass are the same</li>
	 * <li>the <code>other</code> IncidenceClass is not a direct superclass of
	 * <code>incidenceClass</code></li>
	 * <li>the <code>other</code> IncidenceClass has no relation with
	 * <code>incidenceClass</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param anIncidenceClass
	 *            the possible superclass of this IncidenceClass
	 * @return <code>true</code> if <code>anIncidenceClass</code> is a direct
	 *         subclass of this IncidenceClass, otherwise <code>false</code>
	 */
	public boolean isDirectSubClassOf(IncidenceClass anIncidenceClass);

	/**
	 * Checks if the current IncidenceClass is a direct superclass of another
	 * IncidenceClass.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isDirectSuperClass = incidenceClass.isDirectSuperClassOf(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isDirectSuperClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> IncidenceClass is a
	 * direct subclass of this IncidenceClass</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>incidenceClass</code> and the given <code>other</code>
	 * IncidenceClass are the same</li>
	 * <li>the <code>other</code> IncidenceClass is not a direct subclass of
	 * <code>incidenceClass</code></li>
	 * <li>the <code>other</code> IncidenceClass has no relation with
	 * <code>incidenceClass</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param anIncidenceClass
	 *            the possible subclass of this IncidenceClass
	 * @return <code>true</code> if <code>anIncidenceClass</code> is a direct
	 *         subclass of this IncidenceClass, otherwise <code>false</code>
	 */
	public boolean isDirectSuperClassOf(IncidenceClass anIncidenceClass);

	/**
	 * Checks if the current element is a direct or inherited superclass of
	 * another IncidenceClass.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isSuperClass = incidenceClass.isSuperClass(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isSuperClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> incidenceClass is a
	 * direct or inherited subclass of this element</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>incidenceClass</code> and the given <code>other</code>
	 * IncidenceClass are the same</li>
	 * <li>the <code>other</code> IncidenceClass is not a direct or indirect
	 * subclass of <code>incidenceClass</code></li>
	 * <li>the <code>other</code> IncidenceClass has no relation with
	 * <code>incidenceClass</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param anIncidenceClass
	 *            the possible subclass of this IncidenceClass
	 * @return <code>true</code> if <code>anIncidenceClass</code> is a direct or
	 *         indirect subclass of this IncidenceClass, otherwise
	 *         <code>false</code>
	 */
	public boolean isSuperClassOf(IncidenceClass anIncidenceClass);

	/**
	 * Tests if the current element equals another aIncidenceClass or is another
	 * IncidenceClass´ direct or indirect superclass.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isSuperClassOrEquals = incidenceClass.isSuperClassOfOrEquals(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isSuperClassOrEquals</code> is:
	 * <ul>
	 * <li><code>true</code> if one of the following occurs:
	 * <ul>
	 * <li>the <code>other</code> IncidenceClass is a direct or indirect
	 * subclass of this IncidenceClass</li>
	 * <li><code>incidenceClass == other</code></li>
	 * </ul>
	 * </li>
	 * <li><code>false</code> if the <code>other</code> IncidenceClass has no
	 * relation with <code>incidenceClass</code> (not the same, not a direct or
	 * indirect subclass)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param anIncidenceClass
	 *            the possible subclass of this IncidenceClass
	 * @return <code>true</code> if <code>anIncidenceClass</code> is a direct or
	 *         indirect subclass of this IncidenceClass or <code>this</code>
	 *         IncidenceClass itself, otherwise <code>false</code>
	 */
	public boolean isSuperClassOfOrEquals(IncidenceClass anIncidenceClass);

	/**
	 * @return {@link Schema} of {@link #getEdgeClass()}
	 */
	public Schema getSchema();

	/**
	 * @return the set of all role names valid for this IncidenceClass
	 */
	public Set<String> getAllRoles();

	/** 
	 * @return the QualifiedName of this IncidenceClass, this is a combination of 
	 *         the EdgeClassName and the rolename, since the rolename as such is 
	 *         not necessarily unique
	 */
	public String getQualifiedName();
}
