package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.QuerySourceException;
import de.uni_koblenz.jgralab.greql2.schema.KappaSubgraphDefinition;

/**
 * Evaluator-class for a {@link KappaSubgraphDefinition}, which defines a
 * subgraph via the visibility level kappa.
 * 
 * @author jtheegarten@uni-koblenz.de 2012, Diploma Thesis
 * 
 */
public class KappaSubgraphDefinitionEvaluator extends
		SubgraphDefinitionEvaluator {

	public KappaSubgraphDefinitionEvaluator(KappaSubgraphDefinition vertex,
			GreqlEvaluator eval) {
		super(vertex, eval);
	}

	@Override
	public Object evaluate() throws QuerySourceException {
		if (vertex == null) {
			return null;
		}
		int kappa = ((KappaSubgraphDefinition) vertex).get_kappa();
		result = graph.getView(kappa);
		return result;
	}

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return new VertexCosts(5, 5, 0);
	}

}
