package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import java.util.Iterator;

import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.QuerySourceException;
import de.uni_koblenz.jgralab.greql2.schema.Identifier;
import de.uni_koblenz.jgralab.greql2.schema.IsIdOfNestedSubgraphDefinition;
import de.uni_koblenz.jgralab.greql2.schema.NestedSubgraphDefinition;

/**
 * Evaluator-class for a {@link NestedSubgraphDefinition}, which defines a
 * subgraph via the nested elements of a graph-element (Vertex or Edge).
 * 
 * @author jtheegarten@uni-koblenz.de 2012, Diploma Thesis
 * 
 */

public class NestedSubgraphDefinitionEvaluator extends
		SubgraphDefinitionEvaluator {

	public NestedSubgraphDefinitionEvaluator(NestedSubgraphDefinition vertex,
			GreqlEvaluator eval) {
		super(vertex, eval);
	}

	@Override
	public Object evaluate() throws QuerySourceException {
		if (vertex != null) {

			Iterator<IsIdOfNestedSubgraphDefinition> idEdges = vertex
					.getAlphaEdges(IsIdOfNestedSubgraphDefinition.class)
					.iterator();
			if (idEdges.hasNext()) {
				Iterator<Identifier> id = idEdges.next()
						.getAlphaVertices(Identifier.class).iterator();
				if (id.hasNext()) {
					String name = id.next().get_name();
				}
			}
		}
		return null;
	}

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		// TODO Auto-generated method stub
		return null;
	}

}
