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
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;

public class EdgeClassImpl extends GraphElementClassImpl<EdgeClass, Edge> implements EdgeClass {

	static EdgeClass createDefaultEdgeClass(Schema schema) {
		assert schema.getDefaultGraphClass() != null : "DefaultGraphClass has not yet been created!";
		assert schema.getDefaultVertexClass() != null : "DefaultVertexClass has not yet been created!";
		assert schema.getDefaultEdgeClass() == null : "DefaultEdgeClass already created!";
		EdgeClass ec = schema.getDefaultGraphClass().createEdgeClass(DEFAULTEDGECLASS_NAME);
		//, 0,
			//	Integer.MAX_VALUE, "", AggregationKind.NONE,
				//schema.getDefaultVertexClass(), 0, Integer.MAX_VALUE, "",
				//AggregationKind.NONE);
		ec.setAbstract(true);
		return ec;
	}

	/**
	 * builds a new edge class
	 * 
	 * @param qn
	 *            the unique identifier of the edge class in the schema
	 * @param from
	 *            the vertex class from which the edge class may connect from
	 * @param fromMin
	 *            the minimum multiplicity of the 'from' vertex class,
	 *            represents the minimum allowed number of connections from the
	 *            edge class to the 'from' vertex class
	 * @param fromMax
	 *            the maximum multiplicity of the 'from' vertex class,
	 *            represents the maximum allowed number of connections from the
	 *            edge class to the 'from' vertex class
	 * @param fromRoleName
	 *            a name which identifies the 'from' side of the edge class in a
	 *            unique way
	 * @param to
	 *            the vertex class to which the edge class may connect to
	 * @param toMin
	 *            the minimum multiplicity of the 'to' vertex class, represents
	 *            the minimum allowed number of connections from the edge class
	 *            to the 'to' vertex class
	 * @param toMax
	 *            the minimum multiplicity of the 'to' vertex class, represents
	 *            the maximum allowed number of connections from the edge class
	 *            to the 'to' vertex class
	 * @param toRoleName
	 *            a name which identifies the 'to' side of the edge class in a
	 *            unique way
	 */
	protected EdgeClassImpl(String simpleName, Package pkg,
			GraphClass aGraphClass) {
		super(simpleName, pkg, aGraphClass);
		register();
	}

	@Override
	protected void register() {
		((PackageImpl) parentPackage).addEdgeClass(this);
		((GraphClassImpl) graphClass).addEdgeClass(this);
	}

	@Override
	public String getVariableName() {
		return "ec_" + getQualifiedName().replace('.', '_');
	}
	
	@Override
	public boolean isBinary() {
		return false;
	}
	

}
