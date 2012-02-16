
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pcollections.PVector;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VariableDeclaration;
import de.uni_koblenz.jgralab.greql2.evaluator.VariableDeclarationLayer;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsConstraintOf;
import de.uni_koblenz.jgralab.greql2.schema.IsConstraintOf_constrainedDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.IsSimpleDeclOf;
import de.uni_koblenz.jgralab.greql2.schema.IsSimpleDeclOf_parentDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.SimpleDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.Variable;

/**
 * Evaluates a Declaration vertex in the GReQL-2 Syntaxgraph
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class DeclarationEvaluator extends VertexEvaluator {

	/**
	 * This is the declaration vertex
	 */
	private Declaration vertex;
	
	private int declarationCostsFactor = 5;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Greql2Vertex getVertex() {
		return vertex;
	}

	/**
	 * @param eval
	 *            the DeclarationEvaluator this VertexEvaluator belongs to
	 * @param vertex
	 *            the vertex which gets evaluated by this VertexEvaluator
	 */
	public DeclarationEvaluator(Declaration vertex, GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
	}

	@Override
	public VariableDeclarationLayer evaluate() {
		ArrayList<VertexEvaluator> constraintList = new ArrayList<VertexEvaluator>();
		for (IsConstraintOf consInc : vertex
				.getIncidentEdgesOfType_IsConstraintOf(Direction.EDGE_TO_VERTEX)) {
			VertexEvaluator curEval = vertexEvalMarker.getMark(consInc
					.getAlpha());
			if (curEval != null) {
				constraintList.add(curEval);
			}
		}
		/* create list of VariableDeclaration objects */
		List<VariableDeclaration> varDeclList = new ArrayList<VariableDeclaration>();
		for (IsSimpleDeclOf inc : vertex
				.getIncidentEdgesOfType_IsSimpleDeclOf(Direction.EDGE_TO_VERTEX)) {
			SimpleDeclaration simpleDecl = (SimpleDeclaration) inc.getAlpha();
			SimpleDeclarationEvaluator simpleDeclEval = (SimpleDeclarationEvaluator) vertexEvalMarker
					.getMark(simpleDecl);
			@SuppressWarnings("unchecked")
			PVector<VariableDeclaration> resultCollection = (PVector<VariableDeclaration>) simpleDeclEval
					.getResult();
			for (VariableDeclaration v : resultCollection) {
				varDeclList.add(v);
			}
		}
		VariableDeclarationLayer declarationLayer = new VariableDeclarationLayer(
				vertex, varDeclList, constraintList);
		return declarationLayer;
	}

	@Override
	public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		IsSimpleDeclOf_parentDeclaration inc = vertex.getFirst_parentDeclaration();
		long simpleDeclCosts = 0;
		while (inc != null) {
			SimpleDeclaration simpleDecl = (SimpleDeclaration) inc.getThat();
			SimpleDeclarationEvaluator simpleEval = (SimpleDeclarationEvaluator) getVertexEvalMarker().getMark(simpleDecl);
			simpleDeclCosts += simpleEval
					.getCurrentSubtreeEvaluationCosts(graphSize);
			inc = inc.getNextParentDeclarationAtVertex();
		}

		IsConstraintOf_constrainedDeclaration consInc = vertex.getFirst_constrainedDeclaration();
		int constraintsCosts = 0;
		while (consInc != null) {
			VertexEvaluator constraint = getVertexEvalMarker().getMark(
					consInc.getThat());
			constraintsCosts += constraint
					.getCurrentSubtreeEvaluationCosts(graphSize);
			consInc = consInc.getNextConstrainedDeclarationAtVertex();
		}

		long iterationCosts = getDefinedVariableCombinations(graphSize)
				* declarationCostsFactor;
		long ownCosts = iterationCosts + 2;
		long iteratedCosts = ownCosts * getVariableCombinations(graphSize);
		long subtreeCosts = iteratedCosts + constraintsCosts + simpleDeclCosts;
		return new VertexCosts(ownCosts, iteratedCosts, subtreeCosts);
	}

	/**
	 * Returns the number of combinations of the variables this vertex defines
	 */
	public long getDefinedVariableCombinations(GraphSize graphSize) {
		long combinations = 1;
		Iterator<Variable> iter = getDefinedVariables().iterator();
		while (iter.hasNext()) {
			VariableEvaluator veval = (VariableEvaluator) vertexEvalMarker
					.getMark(iter.next());
			combinations *= veval.getVariableCombinations(graphSize);
		}
		return combinations;
	}

	@Override
	public long calculateEstimatedCardinality(GraphSize graphSize) {
		IsConstraintOf_constrainedDeclaration inc = vertex.getFirst_constrainedDeclaration();
		double selectivity = 1.0;
		while (inc != null) {
			VertexEvaluator constEval = getVertexEvalMarker().getMark(
					inc.getThat());
			selectivity *= constEval.getEstimatedSelectivity(graphSize);
			inc = inc.getNextConstrainedDeclarationAtVertex();
		}
		return Math.round(getDefinedVariableCombinations(graphSize)
				* selectivity);
	}

}
