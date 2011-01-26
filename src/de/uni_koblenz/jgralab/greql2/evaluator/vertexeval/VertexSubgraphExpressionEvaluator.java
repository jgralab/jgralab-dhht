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

package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.BooleanGraphMarker;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.EvaluateException;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueImpl;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueTypeCollection;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;

/**
 * Evaluates the given vertex subgraph expression. All Vertices and Edges that
 * belong to the generated subgraph are marked with a temporary attribut
 * <code>SubgraphTempAttribute</code>
 */
public class VertexSubgraphExpressionEvaluator extends
		SubgraphExpressionEvaluator {

	public VertexSubgraphExpressionEvaluator(VertexSubgraphExpression vertex,
			GreqlEvaluator eval) {
		super(vertex, eval);
	}

	@Override
	public JValue evaluate() throws EvaluateException {
		Graph dataGraph = greqlEvaluator.getDatagraph();
		BooleanGraphMarker subgraphAttr = new BooleanGraphMarker(dataGraph);
		Vertex currentVertex = dataGraph.getFirstVertex();
		while (currentVertex != null) {
			JValueTypeCollection typeCollection = getTypeCollection();
			if ((subgraph == null) || (subgraph.isMarked(currentVertex))) {
				AttributedElementClass vertexClass = currentVertex
						.getAttributedElementClass();
				if (typeCollection.acceptsType(vertexClass)) {
					subgraphAttr.mark(currentVertex);
				}
			}
			currentVertex = currentVertex.getNextVertex();
		}
		// add all edges
		Edge currentEdge = dataGraph.getFirstEdge();
		while (currentEdge != null) {
			if (subgraphAttr.isMarked(currentEdge.getAlpha())
					&& subgraphAttr.isMarked(currentEdge.getOmega())) {
				subgraphAttr.mark(currentEdge);
			}
			currentEdge = currentEdge.getNextEdge();
		}
		return new JValueImpl(subgraphAttr);
	}

	@Override
	public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return this.greqlEvaluator.getCostModel()
				.calculateCostsVertexSubgraphExpression(this, graphSize);
	}

	public GraphSize calculateSubgraphSize(GraphSize graphSize) {
		return this.greqlEvaluator.getCostModel().calculateVertexSubgraphSize(
				this, graphSize);
	}

}
