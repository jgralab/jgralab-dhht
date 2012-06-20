package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.QuerySourceException;
import de.uni_koblenz.jgralab.greql2.schema.ElementSetRestriction;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;

public class ElementSetRestrictionEvaluator extends ElementRestrictionEvaluator {

	private ElementSetRestriction vertex;

	public ElementSetRestrictionEvaluator(ElementSetRestriction vertex,
			GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
	}

	@Override
	public Greql2Vertex getVertex() {
		return vertex;
	}

	@Override
	public Object evaluate() throws QuerySourceException {
		ElementSetRestriction restriction = vertex;
		VertexEvaluator evaluator = vertexEvalMarker.getMark(restriction
				.getFirst_isExpressionOfRestriction_omega().getEdge()
				.getFirst_restrExpression().getVertex());
		result = evaluator.getResult();
		return result;
	}

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return new VertexCosts(1, 1, 1);
	}
}
