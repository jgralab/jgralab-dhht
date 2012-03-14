/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2011 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * For bug reports, documentation and further information, visit
 * 
 *                         http://jgralab.uni-koblenz.de
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

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.ConditionalExpression;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsFalseExprOf_isFalseExprOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsTrueExprOf_isTrueExprOf_omega;

/**
 * Evaluates a ConditionalExpression vertex in the GReQL-2 Syntaxgraph
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ConditionalExpressionEvaluator extends VertexEvaluator {

	/**
	 * The ConditionalExpression-Vertex this evaluator evaluates
	 */
	private ConditionalExpression vertex;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Greql2Vertex getVertex() {
		return vertex;
	}

	/**
	 * Creates a new ConditionExpressionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
	public ConditionalExpressionEvaluator(ConditionalExpression vertex,
			GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
	}

	/**
	 * evaluates the conditional expression
	 */
	@Override
	public Object evaluate() {
		Expression condition = (Expression) vertex
				.getFirstIncidenceToIsConditionOf(Direction.EDGE_TO_VERTEX).getThat();
		VertexEvaluator conditionEvaluator = vertexEvalMarker
				.getMark(condition);
		Object conditionResult = conditionEvaluator.getResult();
		Expression expressionToEvaluate = null;

		Boolean value = (Boolean) conditionResult;
		if (value.booleanValue()) {
			expressionToEvaluate = (Expression) vertex
					.getFirstIncidenceToIsTrueExprOf(Direction.EDGE_TO_VERTEX).getThat();
		} else {
			expressionToEvaluate = (Expression) vertex
					.getFirstIncidenceToIsFalseExprOf(Direction.EDGE_TO_VERTEX)
					.getThat();
		}

		if (expressionToEvaluate != null) {
			VertexEvaluator exprEvaluator = vertexEvalMarker
					.getMark(expressionToEvaluate);
			result = exprEvaluator.getResult();
		} else {
			result = null;
		}
		return result;
	}

	@Override
	public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		Expression condition = (Expression) vertex.getFirstIncidenceToIsConditionOf(Direction.EDGE_TO_VERTEX)
				.getThat();
		VertexEvaluator conditionEvaluator = getVertexEvalMarker().getMark(
				condition);
		long conditionCosts = conditionEvaluator
				.getCurrentSubtreeEvaluationCosts(graphSize);
		Expression expressionToEvaluate;
		expressionToEvaluate = (Expression) vertex.getFirstIncidenceToIsTrueExprOf(Direction.EDGE_TO_VERTEX)
				.getThat();
		VertexEvaluator vertexEval = getVertexEvalMarker().getMark(
				expressionToEvaluate);
		long trueCosts = vertexEval.getCurrentSubtreeEvaluationCosts(graphSize);
		expressionToEvaluate = (Expression) vertex.getFirstIncidenceToIsFalseExprOf(Direction.EDGE_TO_VERTEX)
				.getThat();
		vertexEval = getVertexEvalMarker().getMark(expressionToEvaluate);
		long falseCosts = vertexEval
				.getCurrentSubtreeEvaluationCosts(graphSize);
		long maxCosts = trueCosts;
		if (falseCosts > trueCosts) {
			maxCosts = falseCosts;
		}
		long ownCosts = 4;
		long iteratedCosts = ownCosts * getVariableCombinations(graphSize);
		long subtreeCosts = iteratedCosts + maxCosts + conditionCosts;
		return new VertexCosts(ownCosts, iteratedCosts, subtreeCosts);
	}
	
	@Override
	public long calculateEstimatedCardinality(GraphSize graphSize) {
		IsTrueExprOf_isTrueExprOf_omega trueInc = vertex.getFirst_isTrueExprOf_omega();
		long trueCard = 0;
		if (trueInc != null) {
			VertexEvaluator trueEval = getVertexEvalMarker().getMark(
					trueInc.getThat());
			trueCard = trueEval.getEstimatedCardinality(graphSize);
		}
		IsFalseExprOf_isFalseExprOf_omega falseInc = vertex.getFirst_isFalseExprOf_omega();
		long falseCard = 0;
		if (falseInc != null) {
			VertexEvaluator falseEval = getVertexEvalMarker().getMark(
					falseInc.getThat());
			falseCard = falseEval.getEstimatedCardinality(graphSize);
		}
		long maxCard = trueCard;
		if (falseCard > maxCard) {
			maxCard = falseCard;
		}
		return maxCard;
	}
}
