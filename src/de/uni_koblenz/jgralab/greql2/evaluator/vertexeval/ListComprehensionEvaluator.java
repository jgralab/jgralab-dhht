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
import java.util.List;

import org.pcollections.PCollection;
import org.pcollections.PVector;

import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.schema.IsTableHeaderOf;
import de.uni_koblenz.jgralab.greql2.schema.IsTableHeaderOf_isTableHeaderOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.ListComprehension;
import de.uni_koblenz.jgralab.greql2.types.Table;

/**
 * Evaluates a ListComprehensionvertex in the GReQL-2 Syntaxgraph
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ListComprehensionEvaluator extends ComprehensionEvaluator {

	/**
	 * The ListComprehension-Vertex this evaluator evaluates
	 */
	private ListComprehension vertex;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public ListComprehension getVertex() {
		return vertex;
	}

	/**
	 * Creates a new ListComprehensionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
	public ListComprehensionEvaluator(ListComprehension vertex,
			GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
	}

	private Boolean createHeader = null;

	private List<VertexEvaluator> headerEvaluators = null;

	@Override
	protected PCollection<Object> getResultDatastructure() {
		if (createHeader == null) {
			if (vertex.getFirst_isTableHeaderOf_omega() != null) {
				headerEvaluators = new ArrayList<VertexEvaluator>();
				createHeader = true;
				for (IsTableHeaderOf_isTableHeaderOf_omega tableInc : vertex
						.getIncidences(IsTableHeaderOf_isTableHeaderOf_omega.class)) {
					VertexEvaluator headerEval = vertexEvalMarker
							.getMark(tableInc.getThat());
					headerEvaluators.add(headerEval);
				}
			} else {
				createHeader = false;
			}
		}
		if (createHeader) {
			PVector<String> headerTuple = JGraLab.<String> vector();
			for (VertexEvaluator headerEvaluator : headerEvaluators) {
				headerTuple = headerTuple.plus((String) headerEvaluator
						.getResult());
			}
			Table<Object> table = Table.empty();
			return table.withTitles(headerTuple);
		}
		return JGraLab.vector();
	}

	@Override
	public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		Declaration decl = (Declaration) vertex.getFirst_isCompDeclOf_omega().getThat();
		DeclarationEvaluator declEval = (DeclarationEvaluator) 
				getVertexEvalMarker().getMark(decl);
		long declCosts = declEval.getCurrentSubtreeEvaluationCosts(graphSize);

		Vertex resultDef = vertex.getFirst_isCompDeclOf_omega().getThat();
		VertexEvaluator resultDefEval = getVertexEvalMarker().getMark(
				resultDef);
		long resultCosts = resultDefEval
				.getCurrentSubtreeEvaluationCosts(graphSize);

		long ownCosts = declEval.getEstimatedCardinality(graphSize)
				* 1;
		long iteratedCosts = ownCosts * getVariableCombinations(graphSize);
		long subtreeCosts = iteratedCosts + resultCosts + declCosts;
		return new VertexCosts(ownCosts, iteratedCosts, subtreeCosts);
	}

	@Override
	public long calculateEstimatedCardinality(GraphSize graphSize) {
		Declaration decl = (Declaration) vertex.getFirst_isCompDeclOf_omega().getThat();
		DeclarationEvaluator declEval = (DeclarationEvaluator) getVertexEvalMarker().getMark(decl);
		return declEval.getEstimatedCardinality(graphSize);
	}

}
