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
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.graphmarker.AbstractGraphMarker;

/**
 * Returns the first edge (of a type matching the given TypeCollection) in the
 * graph
 * 
 * <dl>
 * <dt><b>GReQL-signature</b></dt>
 * <dd><code>EDGE firstEdge()</code></dd>
 * <dd><code>EDGE firstEdge(tc: TYPECOLLECTION)</code></dd>
 * </dd>
 * <dd>&nbsp;</dd>
 * </dl>
 * <dl>
 * <dt></dt>
 * <dd>
 * <dd>the first edge (of a type matching tc) in Eseq
 * </dl>
 * </dd> </dl>
 * 
 * @see LastEdge, FirstVertex
 * @author ist@uni-koblenz.de
 * 
 */

public class FirstEdge extends Greql2Function {
	{
		JValueType[][] x = { { JValueType.EDGE },
				{ JValueType.TYPECOLLECTION, JValueType.EDGE } };
		signatures = x;

		description = "Returns the first edge (of optionally given type) in the graph.";

		Category[] c = { Category.GRAPH };
		categories = c;
	}

	@Override
	public JValue evaluate(Graph graph,
			AbstractGraphMarker<AttributedElement> subgraph, JValue[] arguments)
			throws EvaluateException {
		switch (checkArguments(arguments)) {
		case 0:
			return new JValueImpl(graph.getFirstEdge());
		case 1:
			Edge current = graph.getFirstEdge();
			JValueTypeCollection tc = arguments[2].toJValueTypeCollection();
			while (current != null) {
				if (tc.acceptsType(current.getMetaClass())) {
					return new JValueImpl(current);
				}
				current = current.getNextEdge();
			}
			return new JValueImpl((Edge) null);
		default:
			throw new WrongFunctionParameterException(this, arguments);
		}
	}

	@Override
	public long getEstimatedCosts(ArrayList<Long> inElements) {
		return 1000;
	}

	@Override
	public double getSelectivity() {
		return 0.2;
	}

	@Override
	public long getEstimatedCardinality(int inElements) {
		return 100;
	}

}
