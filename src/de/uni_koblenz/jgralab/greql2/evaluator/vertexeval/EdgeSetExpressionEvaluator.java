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

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.EdgeSetExpression;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeRestrOfExpression_isTypeRestrOfExpression_omega;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;

/**
 * Calculates a subset of the datagraph edges
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class EdgeSetExpressionEvaluator extends ElementSetExpressionEvaluator {

	/**
	 * Creates a new ElementSetExpressionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
	public EdgeSetExpressionEvaluator(EdgeSetExpression vertex,
			GreqlEvaluator eval) {
		super(vertex, eval);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public PSet<Edge> evaluate() {
		Graph datagraph = greqlEvaluator.getDatagraph();
		// create the resulting set
		PSet<Edge> resultSet = JGraLab.set();
		Edge currentEdge = datagraph.getFirstEdge();
		TypeCollection typeCollection = getTypeCollection();
		while (currentEdge != null) {
			AttributedElementClass edgeClass = currentEdge
					.getType();
			if (typeCollection.acceptsType(edgeClass)) {
				resultSet = resultSet.plus(currentEdge);
			}
			currentEdge = currentEdge.getNextEdge();
		}
		return resultSet;
	}

	@Override
	public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		long typeRestrCosts = 0;
		IsTypeRestrOfExpression_isTypeRestrOfExpression_omega inc = vertex.getFirst_isTypeRestrOfExpression_omega();
		while (inc != null) {
			TypeIdEvaluator tideval = (TypeIdEvaluator) getVertexEvalMarker()
					.getMark(inc.getThat());
			typeRestrCosts += tideval
					.getCurrentSubtreeEvaluationCosts(graphSize);
			inc = inc.getNextIsTypeRestrOfExpression_omegaAtVertex();
		}

		long edgeSetExpressionCostsFactor = 10;
		long ownCosts = graphSize.getEdgeCount() * edgeSetExpressionCostsFactor ;
		return new VertexCosts(ownCosts, ownCosts, typeRestrCosts + ownCosts);
	}

	@Override
	public long calculateEstimatedCardinality(GraphSize graphSize) {
		Incidence inc = vertex.getFirstIncidenceToIsTypeRestrOfExpression(Direction.BOTH);
		double selectivity = 1.0;
		if (inc != null) {
			TypeIdEvaluator typeIdEval = (TypeIdEvaluator) getVertexEvalMarker().getMark(inc.getThat());
			selectivity = typeIdEval.getEstimatedSelectivity(graphSize);
		}
		return Math.round(graphSize.getEdgeCount() * selectivity);
	}

}
