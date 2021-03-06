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

import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsPartOf_isPartOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsRecordElementOf_isRecordElementOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.RecordConstruction;
import de.uni_koblenz.jgralab.greql2.schema.RecordElement;
import de.uni_koblenz.jgralab.impl.RecordImpl;

/**
 * Evaluates a record construction, this is for instance rec( name:"element")
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class RecordConstructionEvaluator extends VertexEvaluator {

	private RecordConstruction vertex;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Greql2Vertex getVertex() {
		return vertex;
	}

	/**
	 * Creates a new RecordConstructionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
	public RecordConstructionEvaluator(RecordConstruction vertex,
			GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
	}

	@Override
	public Record evaluate() {
		RecordImpl resultRecord = RecordImpl.empty();
		IsRecordElementOf_isRecordElementOf_omega inc = vertex
				.getFirst_isRecordElementOf_omega();
		while (inc != null) {
			RecordElement currentElement = (RecordElement) inc.getThat();
			RecordElementEvaluator vertexEval = (RecordElementEvaluator) vertexEvalMarker
					.getMark(currentElement);
			resultRecord = resultRecord.plus(vertexEval.getId(),
					vertexEval.getResult());
			inc = inc.getNextIsRecordElementOf_omegaAtVertex();
		}
		return resultRecord;
	}

	@Override
	public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		IsPartOf_isPartOf_omega inc = vertex.getFirst_isPartOf_omega();
		long recElems = 0;
		long recElemCosts = 0;
		while (inc != null) {
			RecordElement recElem = (RecordElement) inc.getThat();
			VertexEvaluator veval = getVertexEvalMarker().getMark(recElem);
			recElemCosts += veval.getCurrentSubtreeEvaluationCosts(graphSize);
			recElems++;
			inc = inc.getNextIsPartOf_omegaAtVertex();
		}

		long ownCosts = (recElems * 1) + 2;
		long iteratedCosts = ownCosts * getVariableCombinations(graphSize);
		long subtreeCosts = iteratedCosts + recElemCosts;
		return new VertexCosts(ownCosts, iteratedCosts, subtreeCosts);
	}

	@Override
	public long calculateEstimatedCardinality(GraphSize graphSize) {
		IsRecordElementOf_isRecordElementOf_omega inc = vertex.getFirst_isRecordElementOf_omega();
		long parts = 0;
		while (inc != null) {
			parts++;
			inc = inc.getNextIsRecordElementOf_omegaAtVertex();
		}
		return parts;
	}

}
