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

import java.util.HashSet;

import org.pcollections.PVector;

import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VariableDeclaration;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsDeclaredVarOf_isDeclaredVarOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeExprOf_isTypeExprOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.SimpleDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.Variable;

/**
 * Evaluates a simple declaration. Creates a VariableDeclaration-object, that
 * provides methods to iterate over all possible values.
 *
 * @author ist@uni-koblenz.de
 *
 */
public class SimpleDeclarationEvaluator extends VertexEvaluator {

	private SimpleDeclaration vertex;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Greql2Vertex getVertex() {
		return vertex;
	}

	/**
	 * @param eval
	 *            the SimpleDeclarationEvaluator this VertexEvaluator belongs to
	 * @param vertex
	 *            the vertex which gets evaluated by this VertexEvaluator
	 */
	public SimpleDeclarationEvaluator(SimpleDeclaration vertex,
			GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
	}

	/**
	 * returns a JValueList of VariableDeclaration objects
	 */
	@Override
	public PVector<VariableDeclaration> evaluate() {
		IsTypeExprOf_isTypeExprOf_omega inc = vertex
				.getFirst_isTypeExprOf_omega();
		Expression typeExpression = (Expression) inc.getThat();
		VertexEvaluator exprEval = vertexEvalMarker.getMark(typeExpression);
		PVector<VariableDeclaration> varDeclList = JGraLab.vector();
		IsDeclaredVarOf_isDeclaredVarOf_omega varInc = vertex
				.getFirst_isDeclaredVarOf_omega();
		while (varInc != null) {
			VariableDeclaration varDecl = new VariableDeclaration(
					(Variable) varInc.getThat(), exprEval, vertex,
					greqlEvaluator);
			varDeclList = varDeclList.plus(varDecl);
			varInc = varInc.getNextIsDeclaredVarOf_omegaAtVertex();
		}
		return varDeclList;
	}

	@Override
	public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		// Calculate the costs for the type definition
		VertexEvaluator typeExprEval = getVertexEvalMarker().getMark(
				vertex.getFirst_isTypeExprOf_omega().getThat());

		long typeCosts = typeExprEval
				.getCurrentSubtreeEvaluationCosts(graphSize);

		// Calculate the costs for the declared variables
		long declaredVarCosts = 0;
		IsDeclaredVarOf_isDeclaredVarOf_omega inc = vertex
				.getFirst_isDeclaredVarOf_omega();
		while (inc != null) {
			VariableEvaluator varEval = (VariableEvaluator) getVertexEvalMarker().getMark(inc.getThat());
			declaredVarCosts += varEval
					.getCurrentSubtreeEvaluationCosts(graphSize);
			inc = inc.getNextIsDeclaredVarOf_omegaAtVertex();
		}

		long ownCosts = 2;
		long iteratedCosts = ownCosts * getVariableCombinations(graphSize);
		long subtreeCosts = iteratedCosts + declaredVarCosts + typeCosts;
		return new VertexCosts(ownCosts, iteratedCosts, subtreeCosts);
	}

	@Override
	public void calculateNeededAndDefinedVariables() {
		neededVariables = new HashSet<Variable>();
		definedVariables = new HashSet<Variable>();
		IsDeclaredVarOf_isDeclaredVarOf_omega varInc = vertex
				.getFirst_isDeclaredVarOf_omega();
		while (varInc != null) {
			definedVariables.add((Variable) varInc.getThat());
			varInc = varInc.getNextIsDeclaredVarOf_omegaAtVertex();
		}
		IsTypeExprOf_isTypeExprOf_omega typeInc = vertex
				.getFirst_isTypeExprOf_omega();
		if (typeInc != null) {
			VertexEvaluator veval = vertexEvalMarker
					.getMark(typeInc.getThat());
			if (veval != null) {
				neededVariables.addAll(veval.getNeededVariables());
			}
		}
	}

	@Override
	public long calculateEstimatedCardinality(GraphSize graphSize) {
		VertexEvaluator typeExprEval = getVertexEvalMarker()
				.getMark(vertex.getFirst_isTypeExprOf_omega().getThat());
		long singleCardinality = typeExprEval
				.getEstimatedCardinality(graphSize);
		long wholeCardinality = singleCardinality
				* getDefinedVariables().size();
		return wholeCardinality;
	}

}
