package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import java.util.Iterator;

import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.QuerySourceException;
import de.uni_koblenz.jgralab.greql2.schema.IsIdOfPartialGraphDefinition;
import de.uni_koblenz.jgralab.greql2.schema.PartialSubgraphDefinition;

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
	}

	@Override
	public Object evaluate() throws QuerySourceException {
		if (vertex != null) {
			Iterator<IsIdOfPartialGraphDefinition> partialIds = vertex
					.getAlphaEdges(IsIdOfPartialGraphDefinition.class)
					.iterator();
			if (partialIds.hasNext()) {
				result = graph.getPartialGraph(partialIds.next().getAlpha()
						.get_intValue());
			}
		}
		return result;
	}

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return new VertexCosts(5, 5, 0);
	}

}
