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

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.graphmarker.AbstractGraphMarker;

/**
 * Returns the number of outgoing edges, which are connected to the given vertex
 * and which are part of the given structure. If no structure is given, the
 * graph to which the vertex belongs to is used as structure.
 * 
 * <dl>
 * <dt><b>GReQL-signature</b></dt>
 * <dd><code>INT outDegree(v:Vertex)</code></dd>
 * <dd><code>INT outDegree(v:Vertex, tc:TYPECOLLECTION)</code></dd>
 * <dd><code>INT outDegree(v:Vertex, ps:PATH)</code></dd>
 * <dd><code>INT outDegree(v:Vertex, ps:PATHSYSTEM)</code></dd>
 * <dd>
 * <code>INT outDegree(v:Vertex, ps:PATHSYSTEM, tc:TYPECOLLECTION)</code></dd>
 * <dd>&nbsp;</dd>
 * </dl>
 * <dl>
 * <dt></dt>
 * <dd>
 * <dl>
 * <dt><b>Parameters:</b></dt>
 * <dd><code>v</code> - vertex to calculate the outdegree for</dd>
 * <dd><code>p</code> - path to limit scope to</dd>
 * <dd><code>ps</code> - pathsystem to limit scope to</dd>
 * <dd><code>tc</code> - typecollection to limit types that are taken into
 * account</dd>
 * <dt><b>Returns:</b></dt>
 * <dd>the indegree of the given vertex</dd>
 * <dd><code>Null</code> if one of the parameters is <code>Null</code></dd>
 * </dl>
 * </dd>
 * </dl>
 * 
 * @see Degree
 * @see InDegree
 * @author ist@uni-koblenz.de
 * 
 */

public class OutDegree extends DegreeFunction {
	{
		description = "Returns the out-degree of the given vertex.\n"
				+ "The scope can be limited by a path, a path system, or\n"
				+ "an type collection.";
	}

	@Override
	public JValue evaluate(Graph graph,
			AbstractGraphMarker<AttributedElement> subgraph, JValue[] arguments)
			throws EvaluateException {
		return evaluate(subgraph, arguments, EdgeDirection.OUT);
	}
}
