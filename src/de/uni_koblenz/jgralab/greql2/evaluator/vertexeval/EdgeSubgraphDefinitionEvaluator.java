package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.QuerySourceException;

/**
 * Evaluator-class for an {@link EdgeSubgraphDefinition}, which defines a
 * subgraph via an EdgeType.
 * 
 * @author jtheegarten@uni-koblenz.de 2012, Diploma Thesis
 * 
 */
public class EdgeSubgraphDefinitionEvaluator extends
		SubgraphDefinitionEvaluator {

	public EdgeSubgraphDefinitionEvaluator(EdgeSubgraphDefinition vertex,
			GreqlEvaluator eval) {
		super(vertex, eval);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object evaluate() throws QuerySourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		// TODO Auto-generated method stub
		return null;
	}

}
