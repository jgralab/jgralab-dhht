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

import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IntermediateVertexPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.IsSubPathOf_isSubPathOf_omega;

/**
 * Evaluates an IntermediateVertexPathDescription.
 *
 * @author ist@uni-koblenz.de Summer 2006, Diploma Thesis
 *
 */
public class IntermediateVertexPathDescriptionEvaluator extends
		PathDescriptionEvaluator {

	/**
	 * The IntermediateVertexPathDescription-Vertex this evaluator evaluates
	 */
	private IntermediateVertexPathDescription vertex;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Greql2Vertex getVertex() {
		return vertex;
	}

	/**
	 * Creates a new IntermediateVertexPathDescriptionEvaluator for the given
	 * vertex
	 *
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
	public IntermediateVertexPathDescriptionEvaluator(
			IntermediateVertexPathDescription vertex, GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
	}

	@Override
	public NFA evaluate() {
		IsSubPathOf_isSubPathOf_omega inc = vertex.getFirst_isSubPathOf_omega();
		PathDescriptionEvaluator firstEval = (PathDescriptionEvaluator) vertexEvalMarker
				.getMark(inc.getThat());
		NFA firstNFA = firstEval.getNFA();
		inc = inc.getNextIsSubPathOf_omegaAtVertex();
		PathDescriptionEvaluator secondEval = (PathDescriptionEvaluator) vertexEvalMarker
				.getMark(inc.getThat());
		NFA secondNFA = secondEval.getNFA();
		VertexEvaluator vertexEval = vertexEvalMarker.getMark(vertex
				.getFirst_isIntermediateVertexOf_omega()
				.getThat());
		return NFA.createIntermediateVertexPathDescriptionNFA(firstNFA,
				vertexEval, secondNFA);
	}

	@Override
	public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		IsSubPathOf_isSubPathOf_omega inc = vertex.getFirst_isSubPathOf_omega();
		PathDescriptionEvaluator firstPathEval = (PathDescriptionEvaluator) 
				getVertexEvalMarker().getMark(inc.getThat());
		inc = inc.getNextIsSubPathOf_omegaAtVertex();
		PathDescriptionEvaluator secondPathEval = (PathDescriptionEvaluator) 
				getVertexEvalMarker().getMark(inc.getThat());
		long firstCosts = firstPathEval
				.getCurrentSubtreeEvaluationCosts(graphSize);
		long secondCosts = secondPathEval
				.getCurrentSubtreeEvaluationCosts(graphSize);
		VertexEvaluator vertexEval = getVertexEvalMarker().getMark(
				vertex.getFirst_isIntermediateVertexOf_omega().getThat());
		long intermVertexCosts = vertexEval
				.getCurrentSubtreeEvaluationCosts(graphSize);
		long ownCosts = 10;
		long iteratedCosts = 10;
		long subtreeCosts = iteratedCosts + intermVertexCosts + firstCosts
				+ secondCosts;
		return new VertexCosts(ownCosts, iteratedCosts, subtreeCosts);
	}

}
