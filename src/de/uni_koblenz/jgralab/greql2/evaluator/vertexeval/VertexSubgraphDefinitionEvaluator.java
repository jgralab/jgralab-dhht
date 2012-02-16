package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.QuerySourceException;
import de.uni_koblenz.jgralab.greql2.schema.VertexSubgraphDefinition;

/**
 * Evaluator-class for a {@link VertexSubgraphDefinition}, which defines a
 * subgraph via a VertexType.
 * 
 * @author jtheegarten@uni-koblenz.de 2012, Diploma Thesis
 * 
 */

public class VertexSubgraphDefinitionEvaluator extends
		SubgraphDefinitionEvaluator {

	public VertexSubgraphDefinitionEvaluator(VertexSubgraphDefinition vertex,
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
