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

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.funlib.FunLib;
import de.uni_koblenz.jgralab.greql2.funlib.FunLib.FunctionInfo;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;
import de.uni_koblenz.jgralab.greql2.schema.PathExistence;

/**
 * Evaluates a path existence, that's the question if there is a path of a
 * specific regular form form startElement to targetElement
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class PathExistenceEvaluator extends PathSearchEvaluator {

	/**
	 * this is the PathExistence vertex in the GReQL Syntaxgraph this evaluator
	 * evaluates
	 */
	private PathExistence vertex;
	private FunctionInfo fi;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Greql2Vertex getVertex() {
		return vertex;
	}

	public PathExistenceEvaluator(PathExistence vertex, GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
	}

	@Override
	public Object evaluate() {
		PathDescription p = (PathDescription) vertex
				.getFirst_isPathOf_GoesTo_PathExpression().getThat();
		PathDescriptionEvaluator pathDescEval = (PathDescriptionEvaluator) vertexEvalMarker
				.getMark(p);
		Expression startExpression = (Expression) vertex
				.getFirst_isStartExprOf_omega().getThat();
		VertexEvaluator startEval = vertexEvalMarker.getMark(startExpression);
		Object res = startEval.getResult();
		/**
		 * check if the result is invalid, this may occur because the
		 * restrictedExpression may return a null-value
		 */
		if (res == null) {
			return null;
		}
		GraphElement<?, ?, ?, ?> startElement = (GraphElement<?, ?, ?, ?>) res;

		Expression targetExpression = (Expression) vertex
				.getFirst_isTargetExprOf_omega().getThat();
		VertexEvaluator targetEval = vertexEvalMarker.getMark(targetExpression);
		GraphElement<?, ?, ?, ?> targetElement = null;
		res = targetEval.getResult();
		if (res == null) {
			return null;
		}
		targetElement = (GraphElement<?, ?, ?, ?>) res;

		if (searchAutomaton == null) {
			searchAutomaton = pathDescEval.getNFA().getDFA();
			// searchAutomaton.printAscii();
		}
		Object[] arguments = new Object[3];
		arguments[0] = startElement;
		arguments[1] = targetElement;
		arguments[2] = searchAutomaton;
		if (fi == null) {
			fi = FunLib.getFunctionInfo("isReachable");
		}
		return FunLib.apply(fi, arguments);
	}

	@Override
	public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		Expression startExpression = (Expression) vertex
				.getFirst_isStartExprOf_omega().getThat();
		VertexEvaluator vertexEval = getVertexEvalMarker().getMark(
				startExpression);
		long startCosts = vertexEval
				.getCurrentSubtreeEvaluationCosts(graphSize);
		Expression targetExpression = (Expression) vertex
				.getFirst_isTargetExprOf_omega().getThat();
		vertexEval = getVertexEvalMarker().getMark(targetExpression);
		long targetCosts = vertexEval
				.getCurrentSubtreeEvaluationCosts(graphSize);
		PathDescription p = (PathDescription) vertex
				.getFirst_isPathOf_GoesTo_PathExpression().getThat();
		PathDescriptionEvaluator pathDescEval = (PathDescriptionEvaluator) getVertexEvalMarker()
				.getMark(p);
		long pathDescCosts = pathDescEval
				.getCurrentSubtreeEvaluationCosts(graphSize);
		long searchCosts = Math.round(((pathDescCosts * searchFactor) / 2.0)
				* Math.sqrt(graphSize.getEdgeCount()));
		long ownCosts = searchCosts;
		long iteratedCosts = ownCosts * getVariableCombinations(graphSize);
		long subtreeCosts = targetCosts + pathDescCosts + iteratedCosts
				+ startCosts;
		return new VertexCosts(ownCosts, iteratedCosts, subtreeCosts);
	}

	@Override
	public double calculateEstimatedSelectivity(GraphSize graphSize) {
		return 0.1;
	}

}
