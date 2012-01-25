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

package de.uni_koblenz.jgralab.greql2.funlib;

import java.util.ArrayList;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.graphmarker.AbstractGraphMarker;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;

/**
 * Returns a set of all types known by the schema of the current graph. The list
 * is sortet in topological order. First come the vertex classes, then the edge
 * classes.
 * 
 * <dl>
 * <dt><b>GReQL-signature</b></dt>
 * <dd><code>LIST&lt;ATTRELEMCLASS&gt; types()</code></dd>
 * <dd>&nbsp;</dd>
 * </dl>
 * <dl>
 * <dt></dt>
 * <dd>
 * <dl>
 * <dt><b>Parameters:</b></dt>
 * <dt><b>Returns:</b></dt>
 * <dd>a list of all types known by the schema of the current graph</dd>
 * </dl>
 * </dd>
 * </dl>
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class Types extends Greql2Function {

	{
		JValueType[][] x = { { JValueType.COLLECTION },
				{ JValueType.COLLECTION, JValueType.COLLECTION },
				{ JValueType.PATH, JValueType.COLLECTION },
				{ JValueType.PATHSYSTEM, JValueType.COLLECTION } };
		signatures = x;

		description = "Returns a set of all types known by the schema of the current graph.\n"
				+ "The list is sorted in topological order (first superclasses, then\n"
				+ "subclasses). First comes the vertex classes and then the edge classes.\n"
				+ "If a collection of graph elements, a path or a path system is given,\n"
				+ "the types of the contained graph elements are returned.";

		Category[] c = { Category.SCHEMA_ACCESS };
		categories = c;
	}

	@Override
	public JValue evaluate(Graph graph,
			AbstractGraphMarker<AttributedElement> subgraph, JValue[] arguments)
			throws EvaluateException {
		switch (checkArguments(arguments)) {
		case 0:
			JValueList typeList = new JValueList();
			for (AttributedElementClass c : graph.getSchema()
					.getVertexClassesInTopologicalOrder()) {
				typeList.add(new JValueImpl(c));
			}
			for (AttributedElementClass c : graph.getSchema()
					.getEdgeClassesInTopologicalOrder()) {
				typeList.add(new JValueImpl(c));
			}
			return typeList;
		case 1:
			JValueSet resultSet = new JValueSet();
			for (JValue v : arguments[0].toCollection()) {
				if (!v.isAttributedElement()) {
					throw new EvaluateException(
							"Cannot calculate the typeSet for a collection that"
									+ " doesn't contain attributed elements.");
				}
				resultSet.add(new JValueImpl(v.toAttributedElement()
						.getMetaClass(), v.toAttributedElement()));
			}
			return resultSet;
		case 2:
			return arguments[0].toPath().types();
		case 3:
			return arguments[0].toPathSystem().types();
		default:
			throw new WrongFunctionParameterException(this, arguments);
		}
	}

	@Override
	public long getEstimatedCosts(ArrayList<Long> inElements) {
		return 50;
	}

	@Override
	public double getSelectivity() {
		return 1;
	}

	@Override
	public long getEstimatedCardinality(int inElements) {
		return 1;
	}
}
