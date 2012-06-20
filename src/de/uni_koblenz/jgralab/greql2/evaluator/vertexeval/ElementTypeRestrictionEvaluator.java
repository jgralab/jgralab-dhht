package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.QuerySourceException;
import de.uni_koblenz.jgralab.greql2.schema.ElementTypeRestriction;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeIdOfRestriction_isTypeIdOfRestriction_omega;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

public class ElementTypeRestrictionEvaluator extends
		ElementRestrictionEvaluator {

	private ElementTypeRestriction vertex;

	public ElementTypeRestrictionEvaluator(ElementTypeRestriction vertex,
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
		TypeCollection collection = new TypeCollection();

		IsTypeIdOfRestriction_isTypeIdOfRestriction_omega typeInc = vertex
				.getFirst_isTypeIdOfRestriction_omega();
		while (typeInc != null) {
			TypeIdEvaluator typeEval = (TypeIdEvaluator) vertexEvalMarker
					.getMark(typeInc.getThat());
			collection.addTypes((TypeCollection) typeEval.getResult());
			typeInc = typeInc.getNextIsTypeIdOfRestriction_omegaAtVertex();
		}
		result = collection;
		return result;
	}

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return new VertexCosts(1, 1, 1);
	}
}
