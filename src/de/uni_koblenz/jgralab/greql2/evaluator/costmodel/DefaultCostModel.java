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

package de.uni_koblenz.jgralab.greql2.evaluator.costmodel;

import java.util.ArrayList;
import java.util.logging.Logger;

import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.AggregationPathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.BackwardElementSetEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ComprehensionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ConditionalExpressionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.DeclarationEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.EdgePathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.EdgeRestrictionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.EdgeSetExpressionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ExponentiatedPathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ForwardElementSetEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.FunctionApplicationEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.Greql2ExpressionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.IntLiteralEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.IntermediateVertexPathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.IteratedPathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ListConstructionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ListRangeConstructionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.MapComprehensionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.MapConstructionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.OptionalPathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.PathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.PathExistenceEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.QuantifiedExpressionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.RecordConstructionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.RecordElementEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.SequentialPathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.SetComprehensionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.SetConstructionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.SimpleDeclarationEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.SimpleEdgePathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.TableComprehensionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.TransposedPathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.TupleConstructionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.TypeIdEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VariableEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexSetExpressionEvaluator;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.schema.ConditionalExpression;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.schema.EdgePathDescription;
import de.uni_koblenz.jgralab.greql2.schema.EdgeRestriction;
import de.uni_koblenz.jgralab.greql2.schema.EdgeSetExpression;
import de.uni_koblenz.jgralab.greql2.schema.ExponentiatedPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.FunctionApplication;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Expression;
import de.uni_koblenz.jgralab.greql2.schema.IntermediateVertexPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.IsArgumentOf;
import de.uni_koblenz.jgralab.greql2.schema.IsBoundVarOf;
import de.uni_koblenz.jgralab.greql2.schema.IsDeclaredVarOf;
import de.uni_koblenz.jgralab.greql2.schema.IsFalseExprOf;
import de.uni_koblenz.jgralab.greql2.schema.IsKeyExprOfConstruction;
import de.uni_koblenz.jgralab.greql2.schema.IsPartOf;
import de.uni_koblenz.jgralab.greql2.schema.IsRecordElementOf;
import de.uni_koblenz.jgralab.greql2.schema.IsRecordExprOf;
import de.uni_koblenz.jgralab.greql2.schema.IsSequenceElementOf;
import de.uni_koblenz.jgralab.greql2.schema.IsSubPathOf;
import de.uni_koblenz.jgralab.greql2.schema.IsTrueExprOf;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeRestrOfExpression;
import de.uni_koblenz.jgralab.greql2.schema.IsValueExprOfConstruction;
import de.uni_koblenz.jgralab.greql2.schema.IteratedPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.ListComprehension;
import de.uni_koblenz.jgralab.greql2.schema.ListConstruction;
import de.uni_koblenz.jgralab.greql2.schema.ListRangeConstruction;
import de.uni_koblenz.jgralab.greql2.schema.MapComprehension;
import de.uni_koblenz.jgralab.greql2.schema.MapConstruction;
import de.uni_koblenz.jgralab.greql2.schema.OptionalPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;
import de.uni_koblenz.jgralab.greql2.schema.PathExistence;
import de.uni_koblenz.jgralab.greql2.schema.QuantifiedExpression;
import de.uni_koblenz.jgralab.greql2.schema.RecordConstruction;
import de.uni_koblenz.jgralab.greql2.schema.RecordElement;
import de.uni_koblenz.jgralab.greql2.schema.SequentialPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.SetComprehension;
import de.uni_koblenz.jgralab.greql2.schema.SetConstruction;
import de.uni_koblenz.jgralab.greql2.schema.SimpleDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.TableComprehension;
import de.uni_koblenz.jgralab.greql2.schema.TransposedPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.TupleConstruction;
import de.uni_koblenz.jgralab.greql2.schema.TypeId;
import de.uni_koblenz.jgralab.greql2.schema.Variable;
import de.uni_koblenz.jgralab.greql2.schema.VertexSetExpression;

/**
 * This is the default costmodel the evaluator uses if no other costmodel is
 * set.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class DefaultCostModel extends CostModelBase implements CostModel {



	@Override
	public long calculateCardinalityConditionalExpression(
			ConditionalExpressionEvaluator e, GraphSize graphSize) {
		ConditionalExpression condExp = (ConditionalExpression) e.getVertex();
		IsTrueExprOf trueInc = condExp.getFirstIsTrueExprOfIncidence();
		long trueCard = 0;
		if (trueInc != null) {
			VertexEvaluator trueEval = getVertexEvalMarker().getMark(
					trueInc.getThat());
			trueCard = trueEval.getEstimatedCardinality(graphSize);
		}
		IsFalseExprOf falseInc = condExp.getFirstIsFalseExprOf_omega();
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



	@Override
	public long calculateCardinalityTableComprehension(
			TableComprehensionEvaluator e, GraphSize graphSize) {
		TableComprehension tableComp = (TableComprehension) e.getVertex();
		Declaration decl = tableComp.getFirstIsCompDeclOfIncidence().getThat();
		DeclarationEvaluator declEval = (DeclarationEvaluator) e
				.getVertexEvalMarker().getMark(decl);
		return declEval.getEstimatedCardinality(graphSize);
	}

	@Override
	public long calculateCardinalityTupleConstruction(
			TupleConstructionEvaluator e, GraphSize graphSize) {
		TupleConstruction tupleCons = (TupleConstruction) e.getVertex();
		IsPartOf inc = tupleCons.getFirstIsPartOfIncidence(EdgeDirection.IN);
		long parts = 0;
		while (inc != null) {
			parts++;
			inc = inc.getNextIsPartOfIncidence(EdgeDirection.IN);
		}
		return parts;
	}

	@Override
	public long calculateCardinalityVertexSetExpression(
			VertexSetExpressionEvaluator e, GraphSize graphSize) {
		VertexSetExpression exp = (VertexSetExpression) e.getVertex();
		IsTypeRestrOfExpression inc = exp.getFirstIsTypeRestrOfExpressionIncidence();
		double selectivity = 1.0;
		if (inc != null) {
			TypeIdEvaluator typeIdEval = (TypeIdEvaluator) e
					.getVertexEvalMarker().getMark(inc.getThat());
			selectivity = typeIdEval.getEstimatedSelectivity(graphSize);
		}
		return Math.round(graphSize.getVertexCount() * selectivity);
	}






	@Override
	public VertexCosts calculateCostsForwardVertexSet(
			ForwardElementSetEvaluator e, GraphSize graphSize) {
		ForwardVertexSet bwvertex = (ForwardVertexSet) e.getVertex();
		Expression targetExpression = bwvertex.getFirstIsStartExprOfIncidence()
				.getThat();
		VertexEvaluator vertexEval = getVertexEvalMarker().getMark(
				targetExpression);
		long targetCosts = vertexEval
				.getCurrentSubtreeEvaluationCosts(graphSize);
		PathDescription p = (PathDescription) bwvertex
				.getFirstIsPathOfIncidence().getThat();
		PathDescriptionEvaluator pathDescEval = (PathDescriptionEvaluator) e
				.getVertexEvalMarker().getMark(p);
		long pathDescCosts = pathDescEval
				.getCurrentSubtreeEvaluationCosts(graphSize);
		long searchCosts = Math.round(pathDescCosts * searchFactor
				* Math.sqrt(graphSize.getEdgeCount()));
		long ownCosts = searchCosts;
		long iteratedCosts = ownCosts * e.getVariableCombinations(graphSize);
		long subtreeCosts = targetCosts + pathDescCosts + iteratedCosts;
		return new VertexCosts(ownCosts, iteratedCosts, subtreeCosts);
	}




	/*
	 * (non-Javadoc)
	 * 
	 * @seede.uni_koblenz.jgralab.greql2.evaluator.costmodel.CostModel#
	 * calculateCostsTableComprehension
	 * (de.uni_koblenz.jgralab.greql2.evaluator.vertexeval
	 * .TableComprehensionEvaluator,
	 * de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize)
	 */
	@Override
	public VertexCosts calculateCostsTableComprehension(
			TableComprehensionEvaluator e, GraphSize graphSize) {
		// TODO (heimdall): What is a TableComprehension? Syntax? Where do the
		// costs differ from a ListComprehension?
		TableComprehension tableComp = (TableComprehension) e.getVertex();

		Declaration decl = tableComp.getFirstIsCompDeclOfIncidence().getThat();
		DeclarationEvaluator declEval = (DeclarationEvaluator) e
				.getVertexEvalMarker().getMark(decl);
		long declCosts = declEval.getCurrentSubtreeEvaluationCosts(graphSize);

		Vertex resultDef = tableComp.getFirstIsCompResultDefOfIncidence()
				.getThat();
		VertexEvaluator resultDefEval = getVertexEvalMarker().getMark(
				resultDef);
		long resultCosts = resultDefEval
				.getCurrentSubtreeEvaluationCosts(graphSize);

		long ownCosts = resultDefEval.getEstimatedCardinality(graphSize)
				* addToListCosts;
		long iteratedCosts = ownCosts * e.getVariableCombinations(graphSize);
		long subtreeCosts = iteratedCosts + resultCosts + declCosts;
		return new VertexCosts(ownCosts, iteratedCosts, subtreeCosts);
	}

	@Override
	public VertexCosts calculateCostsTransposedPathDescription(
			TransposedPathDescriptionEvaluator e, GraphSize graphSize) {
		TransposedPathDescription transPath = (TransposedPathDescription) e
				.getVertex();
		PathDescriptionEvaluator pathEval = (PathDescriptionEvaluator) e
				.getVertexEvalMarker().getMark(
						transPath.getFirstIsTransposedPathOfIncidence()
								.getThat());
		long pathCosts = pathEval.getCurrentSubtreeEvaluationCosts(graphSize);
		long transpositionCosts = pathCosts / 20;
		long subtreeCosts = transpositionCosts + pathCosts;
		return new VertexCosts(transpositionCosts, transpositionCosts,
				subtreeCosts);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.uni_koblenz.jgralab.greql2.evaluator.costmodel.CostModel#
	 * calculateCostsTupleConstruction
	 * (de.uni_koblenz.jgralab.greql2.evaluator.vertexeval
	 * .TupleConstructionEvaluator,
	 * de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize)
	 */
	@Override
	public VertexCosts calculateCostsTupleConstruction(
			TupleConstructionEvaluator e, GraphSize graphSize) {
		TupleConstruction tupCons = (TupleConstruction) e.getVertex();
		IsPartOf inc = tupCons.getFirstIsPartOfIncidence(EdgeDirection.IN);
		long parts = 0;
		long partCosts = 0;
		while (inc != null) {
			VertexEvaluator veval = getVertexEvalMarker().getMark(
					inc.getThat());
			partCosts += veval.getCurrentSubtreeEvaluationCosts(graphSize);
			parts++;
			inc = inc.getNextIsPartOfIncidence(EdgeDirection.IN);
		}

		long ownCosts = (parts * addToTupleCosts) + 2;
		long iteratedCosts = ownCosts * e.getVariableCombinations(graphSize);
		long subtreeCosts = iteratedCosts + partCosts;
		return new VertexCosts(ownCosts, iteratedCosts, subtreeCosts);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.uni_koblenz.jgralab.greql2.evaluator.costmodel.CostModel#
	 * calculateCostsTypeId
	 * (de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.TypeIdEvaluator,
	 * de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize)
	 */
	@Override
	public VertexCosts calculateCostsTypeId(TypeIdEvaluator e,
			GraphSize graphSize) {
		long costs = graphSize.getKnownEdgeTypes()
				+ graphSize.getKnownVertexTypes();
		return new VertexCosts(costs, costs, costs);
	}

	@Override
	public VertexCosts calculateCostsVariable(VariableEvaluator e,
			GraphSize graphSize) {
		return new VertexCosts(1, 1, 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.uni_koblenz.jgralab.greql2.evaluator.costmodel.CostModel#
	 * calculateCostsVertexSetExpression
	 * (de.uni_koblenz.jgralab.greql2.evaluator.
	 * vertexeval.VertexSetExpressionEvaluator,
	 * de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize)
	 */
	@Override
	public VertexCosts calculateCostsVertexSetExpression(
			VertexSetExpressionEvaluator e, GraphSize graphSize) {
		VertexSetExpression vse = (VertexSetExpression) e.getVertex();

		long typeRestrCosts = 0;
		IsTypeRestrOfExpression inc = vse.getFirstIsTypeRestrOfExpressionIncidence();
		while (inc != null) {
			TypeIdEvaluator tideval = (TypeIdEvaluator) getVertexEvalMarker()
					.getMark(inc.getThat());
			typeRestrCosts += tideval
					.getCurrentSubtreeEvaluationCosts(graphSize);
			inc = inc.getNextIsTypeRestrOfExpressionIncidence();
		}

		long ownCosts = graphSize.getVertexCount()
				* vertexSetExpressionCostsFactor;
		return new VertexCosts(ownCosts, ownCosts, typeRestrCosts + ownCosts);
	}


	@Override
	public double calculateSelectivityTypeId(TypeIdEvaluator e,
			GraphSize graphSize) {
		int typesInSchema = (int) Math
				.round((graphSize.getKnownEdgeTypes() + graphSize
						.getKnownVertexTypes()) / 2.0);
		double selectivity = 1.0;
		TypeId id = (TypeId) e.getVertex();
		if (id.is_type()) {
			selectivity = 1.0 / typesInSchema;
		} else {
			double avgSubclasses = (graphSize.getAverageEdgeSubclasses() + graphSize
					.getAverageVertexSubclasses()) / 2.0;
			selectivity = avgSubclasses / typesInSchema;
		}
		if (id.is_excluded()) {
			selectivity = 1 - selectivity;
		}
		return selectivity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.uni_koblenz.jgralab.greql2.evaluator.costmodel.CostModel#
	 * calculateVariableAssignments
	 * (de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VariableEvaluator,
	 * de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize)
	 */
	@Override
	public long calculateVariableAssignments(VariableEvaluator e,
			GraphSize graphSize) {
		Variable v = (Variable) e.getVertex();
		IsDeclaredVarOf inc = v.getFirstIsDeclaredVarOfIncidence();
		if (inc != null) {
			SimpleDeclaration decl = inc.getOmega();
			VertexEvaluator typeExpEval = getVertexEvalMarker().getMark(
					decl.getFirstIsTypeExprOfIncidence().getThat());
			return typeExpEval.getEstimatedCardinality(graphSize);
		} else {
			// if there exists no "isDeclaredVarOf"-Edge, the variable is not
			// declared but defined, so there exists only 1 possible assignment
			return 1;
		}
	}




}
