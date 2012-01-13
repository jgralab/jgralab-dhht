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

package de.uni_koblenz.jgralab.schema.impl;

import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

public abstract class GraphElementClassImpl
	<OwnTypeClass extends GraphElementClass<OwnTypeClass, OwnType, DualTypeClass, DualType>, 
	OwnType extends GraphElement<OwnTypeClass,OwnType,DualTypeClass,DualType>,
	DualTypeClass extends GraphElementClass<DualTypeClass, DualType, OwnTypeClass, OwnType>,
	DualType extends GraphElement<DualTypeClass, DualType, OwnTypeClass, OwnType>>
	
	extends AttributedElementClassImpl<OwnTypeClass, OwnType>
	implements GraphElementClass<OwnTypeClass, OwnType, DualTypeClass, DualType> {

	
	
	
	protected GraphClass graphClass;

//	protected Set<GraphElementClass<?, ?,?,?>> allowedSigmaClasses;
	
	protected int minKappa = 0;
	
	protected int maxKappa = Integer.MAX_VALUE;
	
	private Set<IncidenceClass> incidenceClasses = new HashSet<IncidenceClass>();
	
	/**
	 * delegates its constructor to the generalized class
	 * 
	 * @param qn
	 *            the unique identifier of the element in the schema
	 */
	protected GraphElementClassImpl(String simpleName, Package pkg,
			GraphClass graphClass) {
		super(simpleName, pkg, graphClass.getSchema());
		this.graphClass = graphClass;
	//	allowedSigmaClasses = new HashSet<GraphElementClass<?, ?,?,?>>();
	}
	
	@Override
	public void addAllowedSigmaClass(GraphElementClass<?,?,?,?> gec) {
	//	allowedSigmaClasses.add(gec);
	}
	
	@Override
	public Set<GraphElementClass<?,?,?,?>> getAllowedSigmaClasses() {
		return null; // allowedSigmaClasses;
	}

	@Override
	public GraphClass getGraphClass() {
		return graphClass;
	}
	

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder(this.getClass()
				.getSimpleName()
				+ " '" + getQualifiedName() + "'");
		if (isAbstract()) {
			output.append(" (abstract)");
		}
		output.append(": \n");

		output.append("subClasses of '" + getQualifiedName() + "': ");

		for (OwnTypeClass aec : getAllSubClasses()) {
			output.append("'" + aec.getQualifiedName() + "' ");
		}
		output.append("\nsuperClasses of '" + getQualifiedName() + "': ");
		for (OwnTypeClass aec : getAllSuperClasses()) {
			output.append("'" + aec.getQualifiedName() + "' ");
		}
		output.append("\ndirectSuperClasses of '" + getQualifiedName() + "': ");
		for (OwnTypeClass aec : getDirectSuperClasses()) {
			output.append("'" + aec.getQualifiedName() + "' ");
		}

		output.append(attributesToString());
		output.append("\n");
		return output.toString();
	}


	public void addIncidenceClass(IncidenceClass incClass) {
		if ((incClass.getRolename() != null) && (incClass.getRolename() != "") && hasIncidenceClass(incClass.getRolename()))
			throwSchemaExceptionRolenameUsedTwice(incClass);
		incidenceClasses.add(incClass);
	}
	
	private void throwSchemaExceptionRolenameUsedTwice(IncidenceClass incidence) {
		throw new SchemaException("The rolename "
				+ incidence.getRolename()
				+ " is used twice at class " + getQualifiedName());
	}
	
	
	@Override
	protected void  checkSpecialization(OwnTypeClass superclass) {
		super.checkSpecialization(superclass);
		checkDuplicateRolenames(superclass);
	}
	
	
	private void checkDuplicateRolenames(OwnTypeClass other) {
		if ((other == null) || (other.equals(""))) {
			return;
		}
		checkDuplicatedRolenamesAgainstAllIncidenceClasses(other.getAllIncidenceClasses());
	}

	private void checkDuplicatedRolenamesAgainstAllIncidenceClasses(Set<IncidenceClass> incidences) {
		for (IncidenceClass incidence : incidences) {
			if (incidence.getRolename()!=null && !incidence.getRolename().equals("") && hasIncidenceClass(incidence.getRolename())) {
				throw new SchemaException("Rolename " + incidence.getRolename() + " duplicate at class " + this.getQualifiedName());
			}
		}
	}


	
	@Override
	public Set<IncidenceClass> getIncidenceClasses() {
		return incidenceClasses;
	}
	
	@Override
	public Set<IncidenceClass> getAllIncidenceClasses() {
		Set<IncidenceClass> incClasses = new HashSet<IncidenceClass>();
		incClasses.addAll(incidenceClasses);
		for (OwnTypeClass superclass : getAllSuperClasses()) {
			incClasses.addAll(superclass.getIncidenceClasses());
		}
		return incClasses;
	}

	

	protected Set<IncidenceClass> getOwnAdjacentIncidenceClasses() {
		Set<IncidenceClass> adjacentIncidenceClasses = new HashSet<IncidenceClass>();
		for (IncidenceClass ic : incidenceClasses) {
			GraphElementClass<?,?,?,?> ogc = ic.getOtherGraphElementClass(this);
			for (IncidenceClass ic2 : ogc.getIncidenceClasses()) {
				if (ic != ic2) {
					adjacentIncidenceClasses.add(ic2);
				}
			}
		}
		return adjacentIncidenceClasses;
	}
	
	protected Set<IncidenceClass> getAllAdjacentIncidenceClasses() {
		Set<IncidenceClass> adjacentIncidenceClasses = new HashSet<IncidenceClass>();
		for (IncidenceClass ic : getAllIncidenceClasses()) {
			GraphElementClass<?,?,?,?> ogc = ic.getOtherGraphElementClass(this);
			for (IncidenceClass ic2 : ogc.getAllIncidenceClasses()) {
				if ((ic != ic2) && (!ic.isSuperClassOf(ic2)) && (!ic2.isSuperClassOf(ic))) { 
					adjacentIncidenceClasses.add(ic2);
				}
			}
		}
		return adjacentIncidenceClasses;
	}
	
	
	
	
	@Override
	public boolean hasAdjacentIncidenceClass(String rolename) {
		// TODO Auto-generated method stub
		return false;
	}
	
	

	@Override
	public boolean hasIncidenceClass(String rolename) {
		for (IncidenceClass incClass : getAllIncidenceClasses()) {
			if (incClass.getRolename().equals(rolename))
				return true;
		}
		return false;
	}

	
	public void setAllowedKappaRange(int min, int max) {
		minKappa = min;
		maxKappa = max;
	}
	
	public int getAllowedMinKappa() {
		return minKappa;
	}
	
	public int getAllowedMaxKappa() {
		return maxKappa;
	}
	
}
