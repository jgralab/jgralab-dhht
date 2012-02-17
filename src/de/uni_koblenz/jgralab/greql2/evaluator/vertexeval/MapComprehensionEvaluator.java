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
/**
 *
 */
package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import org.pcollections.PCollection;
import org.pcollections.PMap;

import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VariableDeclarationLayer;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.Comprehension;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.schema.MapComprehension;

/**
 * @author <ist@uni-koblenz.de>
 * 
 */
public class MapComprehensionEvaluator extends ComprehensionEvaluator {
	private MapComprehension vertex;

	public MapComprehensionEvaluator(MapComprehension vertex,
			GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
	}
	
	
	DeclarationEvaluator declarationEvaluator = null;
	
	VertexEvaluator keyEvaluator = null;
	
	VertexEvaluator valueEvaluator = null;
	
	private void init() {
		if (declarationEvaluator == null) {
			Declaration decl = (Declaration) vertex.getFirst_isCompDeclOf_omega().getThat();
			declarationEvaluator = (DeclarationEvaluator) getVertexEvalMarker().getMark(decl);
			Vertex key = vertex.getFirst_isKeyExprOfComprehension_omega().getThat();
			keyEvaluator = getVertexEvalMarker().getMark(key);
			Vertex value = vertex.getFirst_isValueExprOfComprehension_omega().getThat();
			valueEvaluator = getVertexEvalMarker().getMark(value);
		}
	}
	

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		init();
		long declCosts = declarationEvaluator.getCurrentSubtreeEvaluationCosts(graphSize);
		long resultCosts = keyEvaluator.getCurrentSubtreeEvaluationCosts(graphSize)
				+ valueEvaluator.getCurrentSubtreeEvaluationCosts(graphSize);
		long ownCosts = keyEvaluator.getEstimatedCardinality(graphSize)
				+ (valueEvaluator.getEstimatedCardinality(graphSize) * 1);
		long iteratedCosts = ownCosts * getVariableCombinations(graphSize);
		long subtreeCosts = iteratedCosts + resultCosts + declCosts;
		return new VertexCosts(ownCosts, iteratedCosts, subtreeCosts);
	}


	@Override
	public Object evaluate() {
		init();
		VariableDeclarationLayer declLayer = getVariableDeclationLayer();

		PMap<Object, Object> resultMap = JGraLab.map();
		declLayer.reset();
		while (declLayer.iterate()) {
			Object jkey = keyEvaluator.getResult();
			Object jval = valueEvaluator.getResult();
			resultMap = resultMap.plus(jkey, jval);
		}
		return resultMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator#getVertex
	 * ()
	 */
	@Override
	public Comprehension getVertex() {
		return vertex;
	}

	@Override
	public long calculateEstimatedCardinality(GraphSize graphSize) {
		return declarationEvaluator.getEstimatedCardinality(graphSize);
	}

	@Override
	protected PCollection<Object> getResultDatastructure() {
		return null;
	}

}
