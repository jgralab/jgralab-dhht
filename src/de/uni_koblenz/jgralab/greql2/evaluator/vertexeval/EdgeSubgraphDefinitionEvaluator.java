package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.QuerySourceException;
import de.uni_koblenz.jgralab.greql2.schema.EdgeSubgraphDefinition;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeExprOfSubgraphDefinition;
import de.uni_koblenz.jgralab.schema.EdgeClass;

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
		IsTypeExprOfSubgraphDefinition typeExprEdge = vertex
				.getIncidentEdges(IsTypeExprOfSubgraphDefinition.class)
				.iterator().next();
		VertexEvaluator exprEval = vertexEvalMarker.getMark(typeExprEdge
				.getAlpha());
		Iterable<Edge> edges = graph.getEdges((EdgeClass) exprEval.getResult());
		Graph markerGraph = graph.getGraphFactory().createGraph_InMemoryStorage(graph.getClass(), graph.getUniqueGraphId() + " edgeSubgraph");
		for (Edge edge : edges) {
			markerGraph.
		}
		// Create a Graph from the edges
		return null;
	}

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return new VertexCosts(5, 5, 0);
	}

}
