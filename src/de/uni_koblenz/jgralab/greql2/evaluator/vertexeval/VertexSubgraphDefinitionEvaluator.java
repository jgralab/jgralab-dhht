package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.QuerySourceException;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeExprOfSubgraphDefinition;
import de.uni_koblenz.jgralab.greql2.schema.VertexSubgraphDefinition;
import de.uni_koblenz.jgralab.impl.mem.MarkerGraphImpl;

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
	}

	@Override
	public Object evaluate() throws QuerySourceException {

		// Get the type expression and evaluate it to a list of vertex types
		IsTypeExprOfSubgraphDefinition typeExprEdge = vertex
				.getIncidentEdges(IsTypeExprOfSubgraphDefinition.class)
				.iterator().next();
		VertexEvaluator exprEval = vertexEvalMarker.getMark(typeExprEdge
				.getAlpha());

		// Get the vertices of that typ
		Iterable<Vertex> vertices = graph
				.getVertices((de.uni_koblenz.jgralab.schema.VertexClass) exprEval
						.getResult());

		MarkerGraphImpl markerGraph = new MarkerGraphImpl(graph);

		// Add all the vertices (and their incidences + that-Edges) to the
		// markerGraph
		for (Vertex vertex : vertices) {
			markerGraph.addVertexWithIncidences(vertex);
		}
		result = markerGraph;
		return result;
	}

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return new VertexCosts(5, 5, 0);
	}

}
