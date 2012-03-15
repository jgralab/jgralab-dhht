package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import java.util.Iterator;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.QuerySourceException;
import de.uni_koblenz.jgralab.greql2.schema.IsExprOfNestedSubgraphDefinition;
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

	private VertexEvaluator startEval = null;

	public NestedSubgraphDefinitionEvaluator(NestedSubgraphDefinition vertex,
			GreqlEvaluator eval) {
		super(vertex, eval);
	}

	@Override
	public Object evaluate() throws QuerySourceException {
		Graph result = null;
		if (vertex != null) {

			Iterator<IsExprOfNestedSubgraphDefinition> exprEdges = vertex
					.getAlphaEdges(IsExprOfNestedSubgraphDefinition.class)
					.iterator();
			if (exprEdges.hasNext()) {
				startEval = vertexEvalMarker.getMark(exprEdges.next()
						.getAlpha());
				GraphElement<?, ?, ?, ?> element = (GraphElement<?, ?, ?, ?>) startEval
						.getResult();
				result = element.getSubordinateGraph();
			}
		}
		return result;
	}

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return new VertexCosts(5, 5, 0);
	}

}
