package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.QuerySourceException;

/**
 * Evaluator-class for a {@link PartialSubgraphDefinition}, which defines a
 * subgraph via the selection of a specific partial graph.
 * 
 * @author jtheegarten@uni-koblenz.de 2012, Diploma Thesis
 * 
 */
public class PartialSubgraphDefinitionEvaluator extends
		SubgraphDefinitionEvaluator {

	public PartialSubgraphDefinitionEvaluator(PartialSubgraphDefinition vertex,
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
