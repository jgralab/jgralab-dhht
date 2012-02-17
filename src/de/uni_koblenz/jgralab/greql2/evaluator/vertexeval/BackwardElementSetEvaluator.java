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

import org.pcollections.PSet;

import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.DFA;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.funlib.graph.ReachableVertices;
import de.uni_koblenz.jgralab.greql2.schema.BackwardElementSet;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;

/**
 * evaluates a BackwardVertexSet
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class BackwardElementSetEvaluator extends PathSearchEvaluator {

	BackwardElementSet vertex;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Greql2Vertex getVertex() {
		return vertex;
	}

	public BackwardElementSetEvaluator(BackwardElementSet vertex,
			GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
	}

	private boolean initialized = false;

	private VertexEvaluator targetEval = null;

	private final void initialize() {
		PathDescription p = (PathDescription) vertex.getFirst_isPathOf_GoesTo_PathExpression().getThat();
		PathDescriptionEvaluator pathDescEval = (PathDescriptionEvaluator) vertexEvalMarker.getMark(p);

		Expression targetExpression = (Expression) vertex
				.getFirst_isTargetExprOf_omega().getThat();
		targetEval = vertexEvalMarker.getMark(targetExpression);
		NFA revertedNFA = NFA.revertNFA(pathDescEval.getNFA());
		searchAutomaton = new DFA(revertedNFA);

		initialized = true;
	}

	@Override
	public PSet<Vertex> evaluate() {
		if (!initialized) {
			initialize();
		}
		Vertex targetVertex = null;
		targetVertex = (Vertex) targetEval.getResult();

		return ReachableVertices.search(targetVertex, searchAutomaton);
	}

	@Override
	public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		Expression targetExpression = (Expression) vertex.getFirst_isTargetExprOf_omega().getThat();
		VertexEvaluator vertexEval = getVertexEvalMarker().getMark(
				targetExpression);
		long targetCosts = vertexEval
				.getCurrentSubtreeEvaluationCosts(graphSize);
		PathDescription p = (PathDescription) vertex
				.getFirst_isPathOf_GoesTo_PathExpression().getThat();
		PathDescriptionEvaluator pathDescEval = (PathDescriptionEvaluator) getVertexEvalMarker().getMark(p);
		long pathDescCosts = pathDescEval
				.getCurrentSubtreeEvaluationCosts(graphSize);
		long searchCosts = Math.round(pathDescCosts * searchFactor
				* Math.sqrt(graphSize.getEdgeCount()));
		long ownCosts = searchCosts;
		long iteratedCosts = ownCosts * getVariableCombinations(graphSize);
		long subtreeCosts = targetCosts + pathDescCosts + iteratedCosts;
		return new VertexCosts(ownCosts, iteratedCosts, subtreeCosts);
	}

	@Override
	public long calculateEstimatedCardinality(GraphSize graphSize) {
		return 5;
	}

}
