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
/**
 *
 */
package de.uni_koblenz.jgralab.greql2.funlib;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.AbstractGraphMarker;
import de.uni_koblenz.jgralab.graphmarker.GraphMarker;

/**
 * Returns a list of vertices in topological ordering.
 * 
 * <dl>
 * <dt><b>GReQL-signature</b></dt>
 * <dd><code>LIST topologicalSort()</code></dd>
 * <dd><code>LIST topologicalSort(subgraph : SubgraphTempAttribute)</code></dd>
 * <dd>&nbsp;</dd>
 * </dl>
 * <dl>
 * <dt></dt>
 * <dd>
 * <dl>
 * <dt><b>Parameters:</b></dt>
 * <dd><code>subgraph</code> - the subgraph to be sorted (optional)</dd>
 * <dt><b>Returns:</b></dt>
 * <dd>a list of vertices in topological ordering or null, if there's no
 * topological order meaning the graph has cycles</dd>
 * </dl>
 * </dd>
 * </dl>
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class TopologicalSort extends Greql2Function {

	{
		JValueType[][] x = { { JValueType.COLLECTION },
				{ JValueType.MARKER, JValueType.COLLECTION } };
		signatures = x;

		description = "Returns a list of vertices in topological ordering.\n"
				+ "The sort can be restricted to a subgraph.";

		Category[] c = { Category.GRAPH };
		categories = c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.greql2.funlib.Greql2Function#evaluate(de.uni_koblenz
	 * .jgralab.Graph, de.uni_koblenz.jgralab.BooleanGraphMarker,
	 * de.uni_koblenz.jgralab.greql2.jvalue.JValue[])
	 */
	@Override
	public JValue evaluate(Graph graph,
			AbstractGraphMarker<AttributedElement> subgraph, JValue[] arguments)
			throws EvaluateException {
		switch (checkArguments(arguments)) {
		case 0:
			break;
		case 1:
			subgraph = arguments[0].toGraphMarker();
			break;
		default:
			throw new WrongFunctionParameterException(this, arguments);
		}

		JValueList result = new JValueList();

		Queue<Vertex> queue = new ArrayDeque<Vertex>();
		GraphMarker<Integer> marker = new GraphMarker<Integer>(graph);
		int vCount = 0;
		for (Vertex v : graph.vertices()) {
			if ((subgraph == null) || subgraph.isMarked(v)) {
				int inDegree = 0;
				for (Edge inc : v.incidences(EdgeDirection.IN)) {
					if ((subgraph == null) || subgraph.isMarked(inc)) {
						inDegree++;
					}
				}
				marker.mark(v, inDegree);
				if (inDegree == 0) {
					queue.offer(v);
					result.add(new JValueImpl(v));
				}
				vCount++;
			}
		}

		while (!queue.isEmpty()) {
			Vertex v = queue.poll();
			vCount--;
			for (Edge inc : v.incidences(EdgeDirection.OUT)) {
				if ((subgraph == null) || subgraph.isMarked(inc)) {
					Vertex omega = inc.getOmega();
					assert (subgraph == null) || subgraph.isMarked(omega);
					int decVal = marker.getMark(omega) - 1;
					marker.mark(omega, decVal);
					if (decVal == 0) {
						queue.offer(omega);
						result.add(new JValueImpl(omega));
					}
				}
			}
		}

		if (vCount == 0) {
			return result;
		}

		return new JValueImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.greql2.funlib.Greql2Function#getEstimatedCardinality
	 * (int)
	 */
	@Override
	public long getEstimatedCardinality(int inElements) {
		return 1000;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.greql2.funlib.Greql2Function#getEstimatedCosts
	 * (java.util.ArrayList)
	 */
	@Override
	public long getEstimatedCosts(ArrayList<Long> inElements) {
		return 200;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.greql2.funlib.Greql2Function#getSelectivity()
	 */
	@Override
	public double getSelectivity() {
		return 1;
	}

}
