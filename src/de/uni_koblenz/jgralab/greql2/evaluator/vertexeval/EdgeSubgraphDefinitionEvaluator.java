package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.QuerySourceException;
import de.uni_koblenz.jgralab.greql2.schema.EdgeSubgraphDefinition;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeExprOfSubgraphDefinition;
import de.uni_koblenz.jgralab.impl.mem.MarkerGraphImpl;
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

		// Get the type expression and evaluate it to a list of edge types
		IsTypeExprOfSubgraphDefinition typeExprEdge = vertex
				.getIncidentEdges(IsTypeExprOfSubgraphDefinition.class)
				.iterator().next();
		VertexEvaluator exprEval = vertexEvalMarker.getMark(typeExprEdge
				.getAlpha());

		// Get the edges of that type
		Iterable<Edge> edges = graph.getEdges((EdgeClass) exprEval.getResult());

		MarkerGraphImpl markerGraph = new MarkerGraphImpl(graph);

		// Add all the edges (and their incidences + that-vertex) to the
		// markerGraph
		for (Edge edge : edges) {
			markerGraph.addEdgeWithIncidences(edge);
		}
		return markerGraph;
	}

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return new VertexCosts(5, 5, 0);
	}

}
