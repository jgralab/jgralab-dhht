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

import org.pcollections.PCollection;

import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.schema.IsPartOf;
import de.uni_koblenz.jgralab.greql2.schema.IsPartOf_isPartOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.TupleConstruction;
import de.uni_koblenz.jgralab.greql2.types.Tuple;

/**
 * Evaluates a TupleConstruction vertex in the GReQL 2 syntaxgraph
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class TupleConstructionEvaluator extends ValueConstructionEvaluator {

	public TupleConstructionEvaluator(TupleConstruction vertex,
			GreqlEvaluator eval) {
		super(vertex, eval);
	}

	@Override
	public PCollection<Object> evaluate() {
		return createValue(Tuple.empty());
	}

	@Override
	public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		TupleConstruction tupCons = (TupleConstruction) getVertex();
		IsPartOf_isPartOf_omega inc = tupCons.getFirst_isPartOf_omega();
		long parts = 0;
		long partCosts = 0;
		while (inc != null) {
			VertexEvaluator veval = getVertexEvalMarker().getMark(
					inc.getThat());
			partCosts += veval.getCurrentSubtreeEvaluationCosts(graphSize);
			parts++;
			inc = inc.getNextIsPartOf_omegaAtVertex();
		}

		long ownCosts = (parts * 1) + 2;
		long iteratedCosts = ownCosts * getVariableCombinations(graphSize);
		long subtreeCosts = iteratedCosts + partCosts;
		return new VertexCosts(ownCosts, iteratedCosts, subtreeCosts);
	}

	@Override
	public long calculateEstimatedCardinality(GraphSize graphSize) {
		TupleConstruction tupleCons = (TupleConstruction) getVertex();
		IsPartOf_isPartOf_omega inc = tupleCons.getFirst_isPartOf_omega();
		long parts = 0;
		while (inc != null) {
			parts++;
			inc = inc.getNextIsPartOf_omegaAtVertex();
		}
		return parts;
	}

}
