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

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsConstrainedExpressionOf_isConstrainedExpressionOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsSubgraphDefinitionOf_isSubgraphDefinitionOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.SubgraphDefinition;
import de.uni_koblenz.jgralab.greql2.schema.SubgraphExpression;

/**
 * This is the evaluator class for {@link SubgraphExpression}-nodes.
 * 
 * @author ist@uni-koblenz.de Summer 2006, Diploma Thesis
 * @author jtheegarten@uni-koblenz.de 2012, Diploma Thesis
 * 
 */
public class SubgraphExpressionEvaluator extends
		AbstractGraphElementCollectionEvaluator {

	protected SubgraphExpression vertex;

	SubgraphDefinitionEvaluator subgraphDefinitionEval;
	
	VertexEvaluator exprEval = null;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Greql2Vertex getVertex() {
		return vertex;
	}

	@Override
	public Object evaluate() {
		// take traversal context for subgraph
		if (subgraphDefinitionEval == null) {
			IsSubgraphDefinitionOf_isSubgraphDefinitionOf_omega isSubgraphDef = vertex
					.getFirst_isSubgraphDefinitionOf_omega();
			SubgraphDefinition defVertex = (SubgraphDefinition) isSubgraphDef
					.getThat();
			subgraphDefinitionEval = (SubgraphDefinitionEvaluator) vertexEvalMarker
					.getMark(defVertex);
		}
		Graph subgraph = (Graph) subgraphDefinitionEval
				.getResult();

		// take restricted expression
		if (exprEval == null) {
			IsConstrainedExpressionOf_isConstrainedExpressionOf_omega isExprOn =  vertex
					.getFirst_isConstrainedExpressionOf_omega();
			Expression expr = (Expression) isExprOn.getThat();
			exprEval = (VertexEvaluator) vertexEvalMarker.getMark(expr);
		}

		// set traversal context
		subgraph.useAsTraversalContext();

		// evaluate restricted expression with traversal context
		result = exprEval.getResult();

		// release traversal context
		subgraph.releaseTraversalContext();
		return result;
	}

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		// return
		// greqlEvaluator.getCostModel().calculateCostsSubgraphRestrictedExpression(this,
		// graphSize);
		if (subgraphDefinitionEval == null) {
			IsSubgraphDefinitionOf_isSubgraphDefinitionOf_omega isSubgraphDef = vertex
					.getFirst_isSubgraphDefinitionOf_omega();
			SubgraphDefinition defVertex = (SubgraphDefinition) isSubgraphDef
					.getThat();
			subgraphDefinitionEval = (SubgraphDefinitionEvaluator) vertexEvalMarker
					.getMark(defVertex);
		}

		// take restricted expression
		if (exprEval == null) {
			IsConstrainedExpressionOf_isConstrainedExpressionOf_omega isExprOn =vertex
					.getFirst_isConstrainedExpressionOf_omega();
			Expression expr = (Expression) isExprOn.getThat();
			exprEval = (VertexEvaluator) vertexEvalMarker.getMark(expr);
		}
		long ownCosts = 10;
		long iteratedCosts = ownCosts * getVariableCombinations(graphSize);
		long subtree = subgraphDefinitionEval
				.getCurrentSubtreeEvaluationCosts(graphSize)
				+ exprEval.getCurrentSubtreeEvaluationCosts(graphSize)
				+ iteratedCosts;

		return new VertexCosts(ownCosts, iteratedCosts, subtree);
	}

	public SubgraphExpressionEvaluator(SubgraphExpression vertex,
			GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
	}

}
