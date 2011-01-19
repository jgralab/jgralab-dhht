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

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.M1ClassManager;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.exception.InheritanceException;
import de.uni_koblenz.jgralab.schema.exception.M1ClassAccessException;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

public class IncidenceClassImpl implements IncidenceClass {

	public IncidenceClassImpl(EdgeClass edgeClass, VertexClass vertexClass,
			String rolename, boolean isAbstract, int minEdgesAtVertex,
			int maxEdgesAtVertex, int minVerticesAtEdge, int maxVerticesAtEdge,
			Direction direction, IncidenceType incidenceType) {
		super();
		this.incidenceType = incidenceType;
		this.isAbstract = isAbstract;
		this.direction = direction;
		this.edgeClass = edgeClass;
		this.maxEdgesAtVertex = maxEdgesAtVertex;
		this.minEdgesAtVertex = minEdgesAtVertex;
		this.maxVerticesAtEdge = maxVerticesAtEdge;
		this.minVerticesAtEdge = minVerticesAtEdge;
		this.rolename = rolename;
		if (rolename == null) {
			rolename = "";
		}
		this.vertexClass = vertexClass;
	}

	private final Direction direction;

	private IncidenceType incidenceType;

	private boolean isAbstract = false;

	private final EdgeClass edgeClass;

	private final VertexClass vertexClass;

	private final int maxEdgesAtVertex;

	private final int minEdgesAtVertex;

	private final int maxVerticesAtEdge;

	private final int minVerticesAtEdge;

	@Override
	public GraphElementClass<?, ?> getOtherGraphElementClass(
			GraphElementClass<?, ?> connectedGc) {
		if (connectedGc == edgeClass) {
			return vertexClass;
		} else {
			return edgeClass;
		}
	}

	private final String rolename;

	private final Set<IncidenceClass> hiddenEndsAtEdge = new HashSet<IncidenceClass>();

	private final Set<IncidenceClass> hiddenEndsAtVertex = new HashSet<IncidenceClass>();

	/**
	 * The class object representing the generated interface for this
	 * AttributedElementClass
	 */
	private Class<? extends Incidence> m1Class;

	/**
	 * The class object representing the implementation class for this
	 * AttributedElementClass. This may be either the generated class or a
	 * subclass of this
	 */
	private Class<? extends Incidence> m1ImplementationClass;

	/**
	 * the immediate sub classes of this class
	 */
	protected HashSet<IncidenceClass> directSubClasses = new HashSet<IncidenceClass>();

	/**
	 * the immediate super classes of this class
	 */
	protected HashSet<IncidenceClass> directSuperClasses = new HashSet<IncidenceClass>();

	@Override
	public IncidenceType getIncidenceType() {
		return incidenceType;
	}

	@Override
	public void setIncidenceType(IncidenceType type) {
		incidenceType = type;
	}

	@Override
	public Direction getDirection() {
		return direction;
	}

	@Override
	public EdgeClass getEdgeClass() {
		return edgeClass;
	}

	@Override
	public int getMaxEdgesAtVertex() {
		return maxEdgesAtVertex;
	}

	@Override
	public int getMinEdgesAtVertex() {
		return minEdgesAtVertex;
	}

	@Override
	public int getMaxVerticesAtEdge() {
		return maxVerticesAtEdge;
	}

	@Override
	public int getMinVerticesAtEdge() {
		return minVerticesAtEdge;
	}

	/**
	 * adds a superClass to this class
	 * 
	 * @param superClass
	 *            the class to add as superclass
	 */
	@Override
	public void addSuperClass(IncidenceClass superClass) {
		checkIncidenceClassSpecialization(this, superClass);
		if ((superClass == this) || (superClass == null)) {
			return;
		}
		directSuperClasses.remove(getSchema().getDefaultIncidenceClass(
				direction));

		if (superClass.isSubClassOf(this)) {
			for (IncidenceClass incidentClass : superClass.getAllSuperClasses()) {
				System.out.println(incidentClass.getRolename());
			}
			System.out.println();
			throw new InheritanceException(
					"Cycle in class hierarchie for classes: " + getRolename()
							+ " and " + superClass.getRolename());
		}
		directSuperClasses.add(superClass);
		((IncidenceClassImpl) superClass).directSubClasses.add(this);
	}

	@Override
	public Set<IncidenceClass> getAllSubClasses() {
		Set<IncidenceClass> returnSet = new HashSet<IncidenceClass>();
		for (IncidenceClass subclass : directSubClasses) {
			returnSet.add(subclass);
			returnSet.addAll(subclass.getAllSubClasses());
		}
		return returnSet;
	}

	@Override
	public Set<IncidenceClass> getDirectSubClasses() {
		return directSubClasses;
	}

	@Override
	public Set<IncidenceClass> getDirectSuperClasses() {
		return directSuperClasses;
	}

	@Override
	public String getIncidenceClassName(IncidenceClass ic) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Incidence> getM1Class() {
		if (m1Class == null) {
			String m1ClassName = getIncidenceClassName(this);
			try {
				m1Class = (Class<? extends Incidence>) Class
						.forName(m1ClassName, true, M1ClassManager
								.instance(getSchema().getQualifiedName()));
			} catch (ClassNotFoundException e) {
				throw new M1ClassAccessException(
						"Can't load M1 class for IncidenceClass '"
								+ getIncidenceClassName(this) + "'", e);
			}
		}
		return m1Class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Incidence> getM1ImplementationClass() {
		if (isAbstract()) {
			throw new M1ClassAccessException(
					"Can't get M1 implementation class. IncidenceClass '"
							+ getIncidenceClassName(this) + "' is abstract!");
		}
		if (m1ImplementationClass == null) {
			try {
				Field f = getM1Class().getField("IMPLEMENTATION_CLASS");
				m1ImplementationClass = (Class<? extends Incidence>) f
						.get(m1Class);
			} catch (SecurityException e) {
				throw new M1ClassAccessException(e);
			} catch (NoSuchFieldException e) {
				throw new M1ClassAccessException(e);
			} catch (IllegalArgumentException e) {
				throw new M1ClassAccessException(e);
			} catch (IllegalAccessException e) {
				throw new M1ClassAccessException(e);
			}
		}
		return m1ImplementationClass;
	}

	@Override
	public Set<IncidenceClass> getAllSuperClasses() {
		HashSet<IncidenceClass> allSuperClasses = new HashSet<IncidenceClass>();
		allSuperClasses.addAll(directSuperClasses);
		for (IncidenceClass superClass : directSuperClasses) {
			allSuperClasses.addAll(superClass.getAllSuperClasses());
		}
		return allSuperClasses;
	}

	@Override
	public boolean isAbstract() {
		return isAbstract;
	}

	@Override
	public boolean isDirectSubClassOf(IncidenceClass anIncidenceClass) {
		return directSuperClasses.contains(anIncidenceClass);
	}

	@Override
	public boolean isDirectSuperClassOf(IncidenceClass anIncidenceClass) {
		return (((IncidenceClassImpl) anIncidenceClass).directSuperClasses
				.contains(this));
	}

	@Override
	public boolean isSubClassOf(IncidenceClass anIncidenceClass) {
		return getAllSuperClasses().contains(anIncidenceClass);
	}

	@Override
	public boolean isSuperClassOf(IncidenceClass anIncidenceClass) {
		return anIncidenceClass.getAllSuperClasses().contains(this);
	}

	@Override
	public boolean isSuperClassOfOrEquals(IncidenceClass anIncidenceClass) {
		return ((this == anIncidenceClass) || (isSuperClassOf(anIncidenceClass)));
	}

	@Override
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	@Override
	public String getRolename() {
		return rolename;
	}

	@Override
	public VertexClass getVertexClass() {
		return vertexClass;
	}

	@Override
	public Schema getSchema() {
		return getEdgeClass().getSchema();
	}

	@Override
	public Set<String> getAllRoles() {
		Set<String> result = new HashSet<String>();
		result.add(getRolename());
		for (IncidenceClass ic : getAllSuperClasses()) {
			result.add(ic.getRolename());
		}
		return result;
	}

	@Override
	public Set<IncidenceClass> getHiddenEndsAtEdge() {
		return hiddenEndsAtEdge;
	}

	@Override
	public Set<IncidenceClass> getHiddenEndsAtVertex() {
		return hiddenEndsAtVertex;
	}

	/**
	 * checks if the incidence classes own and inherited are compatible, i.e. if
	 * the upper multiplicity of own is lower or equal than the one of inherited
	 * and so on
	 * 
	 * @param special
	 * @param general
	 * @return true iff the IncidenceClasses are compatible
	 */
	public static void checkIncidenceClassSpecialization(
			IncidenceClass special, IncidenceClass general) {
		// Vertex same
		if ((special.getVertexClass() != general.getVertexClass())
				&& (!general.getVertexClass().isSuperClassOf(
						special.getVertexClass()))) {
			String dir = special.getDirection() == Direction.VERTEX_TO_EDGE ? "Alpha"
					: "Omega";
			throw new SchemaException(
					"An IncidenceClass may specialize only IncidenceClasses whose connected vertex class is identical or a superclass of the own one. Offending"
							+ "EdgeClasses are "
							+ special.getEdgeClass().getQualifiedName()
							+ " and "
							+ general.getEdgeClass().getQualifiedName()
							+ " at end " + dir);
		}
		// Multiplicities
		if (special.getMaxEdgesAtVertex() > general.getMaxEdgesAtVertex()) {
			String dir = special.getDirection() == Direction.VERTEX_TO_EDGE ? "Alpha"
					: "Omega";
			throw new SchemaException(
					"The multiplicity of an edge class may not be larger than the multiplicities of its superclass. Offending"
							+ "EdgeClasses are "
							+ special.getEdgeClass().getQualifiedName()
							+ " and "
							+ general.getEdgeClass().getQualifiedName()
							+ " at end " + dir);
		}
		if (special.getMaxVerticesAtEdge() > general.getMaxVerticesAtEdge()) {
			String dir = special.getDirection() == Direction.VERTEX_TO_EDGE ? "Alpha"
					: "Omega";
			throw new SchemaException(
					"The multiplicity of an vertex class may not be larger than the multiplicities of its superclass. Offending"
							+ "VertexClasses are "
							+ special.getVertexClass().getQualifiedName()
							+ " and "
							+ general.getVertexClass().getQualifiedName()
							+ " at end " + dir);
		}

		// name clashes
		if (general.getRolename().equals(special.getRolename())
				&& !general.getRolename().isEmpty()
				&& !special.getRolename().isEmpty()) {
			String dir = special.getDirection() == Direction.VERTEX_TO_EDGE ? "Alpha"
					: "Omega";
			throw new SchemaException(
					"An IncidenceClass may only redefine (or subset) an IncidenceClass with a different name. Offending"
							+ "EdgeClasses are "
							+ special.getEdgeClass().getQualifiedName()
							+ " and "
							+ general.getEdgeClass().getQualifiedName()
							+ " at end " + dir);
		}
		for (IncidenceClass ic : general.getAllSuperClasses()) {
			if (ic.getRolename().equals(special.getRolename())
					&& !general.getRolename().isEmpty()
					&& !ic.getRolename().isEmpty()) {
				String dir = ic.getDirection() == Direction.VERTEX_TO_EDGE ? "Alpha"
						: "Omega";
				throw new SchemaException(
						"An IncidenceClass may only redefine (or subset) an IncidenceClass with a different name. Offending"
								+ "EdgeClasses are "
								+ special.getEdgeClass().getQualifiedName()
								+ " and "
								+ ic.getEdgeClass().getQualifiedName()
								+ " at end " + dir);
			}
		}

	}
}
