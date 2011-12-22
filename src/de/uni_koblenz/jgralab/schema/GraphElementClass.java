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

import de.uni_koblenz.jgralab.GraphElement;

/**
 * Base class for Vertex/Edge classes.
 * 
 * @param ConcreteAttributeElementClass the non-abstract subclass of this class, e.g. VertexClass or EdgeClass, used for generic implementations of methods such as addSuperclass
 * @param ConcreteInterface the non-abstract interface for the instances of this class, e.g. Vertex for a VertexClass
 * 
 * @author ist@uni-koblenz.de
 */
public interface GraphElementClass
       <OwnTypeClass extends GraphElementClass<OwnTypeClass, OwnType, DualTypeClass, DualType>, 
        OwnType extends GraphElement<OwnTypeClass,OwnType,DualTypeClass,DualType>,
        DualTypeClass extends GraphElementClass<DualTypeClass, DualType, OwnTypeClass, OwnType>,
        DualType extends GraphElement<DualTypeClass, DualType, OwnTypeClass, OwnType>>
       extends AttributedElementClass<OwnTypeClass, OwnType> {

	/**
	 * Returns the GraphClass of this AttributedElementClass.
	 * 
	 * @return the GraphClass in which this graph element class resides
	 */
	public GraphClass getGraphClass();
	
	
	/**
	 * 
	 * @return the set of incidence classes connected to this graph element class
	 */ 
	public Set<IncidenceClass> getIncidenceClasses();
	
	public void addIncidenceClass(IncidenceClass incClass);
	
	/**
	 * Checks if a incidence class with the given rolename is known
	 * to be incident to this graph element class either directly or
	 * by inheritance
	 * @return true iff this graph element class or a superclass defines a
	 *         incidence class with the name rolename 
	 */
	public boolean hasIncidenceClass(String rolename);
	
	/**
	 * Checks if a incidence class with the given rolename is known
	 * to be adjacent to this graph element class either directly or
	 * by inheritance
	 * @return true iff this graph element class or a superclass defines has 
	 *         an adjacent incidence class with the name rolename 
	 */
	public boolean hasAdjacentIncidenceClass(String rolename);

	/**
	 * 
	 * @return the set of all incidence classes connected to this 
	 *         class or a superclass
	 */
	public Set<IncidenceClass> getAllIncidenceClasses();


	public Set<GraphElementClass<?, ?, ? ,?>> getAllowedSigmaClasses();


	public void addAllowedSigmaClass(GraphElementClass<?,?,?,?> gec);
	
	public void setAllowedKappaRange(int min, int max);
	
	public int getAllowedMinKappa();
	
	public int getAllowedMaxKappa();
}
