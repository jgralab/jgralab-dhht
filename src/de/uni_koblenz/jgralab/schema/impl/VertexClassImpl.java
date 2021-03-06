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



import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

public final class VertexClassImpl extends GraphElementClassImpl<VertexClass, Vertex, EdgeClass, Edge> implements
		VertexClass {



	static VertexClass createDefaultVertexClass(Schema schema) {
		assert schema.getDefaultGraphClass() != null : "DefaultGraphClass has not yet been created!";
		assert schema.getDefaultVertexClass() == null : "DefaultVertexClass already created!";
		VertexClass vc = schema.getDefaultGraphClass().createVertexClass(
				DEFAULTVERTEXCLASS_NAME);
		vc.setAbstract(true);
		return vc;
	}

	/**
	 * builds a new vertex class object
	 * 
	 * @param qn
	 *            the unique identifier of the vertex class in the schema
	 */
	protected VertexClassImpl(String simpleName, Package pkg,
			GraphClass aGraphClass) {
		super(simpleName, pkg, aGraphClass);
		register();
	}

	@Override
	protected void register() {
		((PackageImpl) parentPackage).addVertexClass(this);
		((GraphClassImpl) graphClass).addVertexClass(this);
	}

	@Override
	public String getVariableName() {
		return "vc_" + getQualifiedName().replace('.', '_');
	}

	@Override
    public void addIncidenceClass(IncidenceClass incClass) {
		if (incClass.getVertexClass() != this) {
			throw new SchemaException(
			"IncidenceClasses may be added only to vertices they are connected to");
		}
		super.addIncidenceClass(incClass);
    }

	
	@Override
	public VertexClass getDefaultClass() {
		return graphClass.getSchema().getDefaultVertexClass();
	}

	
}
