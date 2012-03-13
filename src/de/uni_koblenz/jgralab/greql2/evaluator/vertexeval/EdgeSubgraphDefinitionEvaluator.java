package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.QuerySourceException;
import de.uni_koblenz.jgralab.greql2.schema.EdgeSubgraphDefinition;

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
	}

	@Override
	public Object evaluate() throws QuerySourceException {
		return null;
	}

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return new VertexCosts(5, 5, 0);
	}

}
