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

import org.pcollections.PMap;
import org.pcollections.PVector;

import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.GreqlException;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsKeyExprOfConstruction_isKeyExprOfConstruction_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsValueExprOfComprehension_isValueExprOfComprehension_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsValueExprOfConstruction_isValueExprOfConstruction_omega;
import de.uni_koblenz.jgralab.greql2.schema.MapConstruction;

public class MapConstructionEvaluator extends VertexEvaluator {
	private MapConstruction mapConstruction;

	public MapConstructionEvaluator(MapConstruction vertex, GreqlEvaluator eval) {
		super(eval);
		mapConstruction = vertex;
	}

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		IsKeyExprOfConstruction_isKeyExprOfConstruction_omega keyInc = mapConstruction
				.getFirst_isKeyExprOfConstruction_omega();
		IsValueExprOfConstruction_isValueExprOfConstruction_omega valInc = mapConstruction
				.getFirst_isValueExprOfConstruction_omega();
		long parts = 0;
		long partCosts = 0;
		while (keyInc != null) {
			VertexEvaluator keyEval = getVertexEvalMarker().getMark(
					keyInc.getThat());
			partCosts += keyEval.getCurrentSubtreeEvaluationCosts(graphSize);
			VertexEvaluator valueEval = getVertexEvalMarker().getMark(
					valInc.getThat());
			partCosts += keyEval.getCurrentSubtreeEvaluationCosts(graphSize)
					+ valueEval.getCurrentSubtreeEvaluationCosts(graphSize);
			parts++;
			keyInc = keyInc.getNextIsKeyExprOfConstruction_omegaAtVertex();
			valInc = valInc.getNextIsValueExprOfConstruction_omegaAtVertex();
		}

		long ownCosts = (parts) + 2;
		long iteratedCosts = ownCosts * getVariableCombinations(graphSize);
		long subtreeCosts = iteratedCosts + partCosts;
		return new VertexCosts(ownCosts, iteratedCosts, subtreeCosts);
	}

	@Override
	public Object evaluate() {
		PMap<Object, Object> map = JGraLab.map();
		PVector<Object> keys = JGraLab.vector();
		for (IsKeyExprOfConstruction_isKeyExprOfConstruction_omega e : mapConstruction
				.getIncidences(IsKeyExprOfConstruction_isKeyExprOfConstruction_omega.class)) {
			Vertex exp = e.getThat();
			VertexEvaluator expEval = vertexEvalMarker.getMark(exp);
			keys = keys.plus(expEval.getResult());
		}

		PVector<Object> values = JGraLab.vector();
		for (IsValueExprOfComprehension_isValueExprOfComprehension_omega e : mapConstruction
				.getIncidences(IsValueExprOfComprehension_isValueExprOfComprehension_omega.class)) {
			Vertex exp = e.getThat();
			VertexEvaluator expEval = vertexEvalMarker.getMark(exp);
			values = values.plus(expEval.getResult());
		}

		if (keys.size() != values.size()) {
			throw new GreqlException("Map construction has " + keys.size()
					+ " key(s) and " + values.size() + " value(s).");
		}

		for (int i = 0; i < keys.size(); i++) {
			map = map.plus(keys.get(i), values.get(i));
		}

		return map;
	}

	@Override
	public long calculateEstimatedCardinality(GraphSize graphSize) {
		long mappings = 0;
		IsKeyExprOfConstruction_isKeyExprOfConstruction_omega inc =mapConstruction.getFirst_isKeyExprOfConstruction_omega();
		while (inc != null) {
			mappings++;
			inc = inc.getNextIsKeyExprOfConstruction_omegaAtVertex();
		}
		return mappings;
	}

	@Override
	public Greql2Vertex getVertex() {
		return mapConstruction;
	}

}
