package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.QuerySourceException;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IncidenceRestriction;
import de.uni_koblenz.jgralab.greql2.schema.IsIncTypeIdOf_isIncTypeIdOf_omega;

public class IncidenceRestrictionEvaluator extends VertexEvaluator {

	private IncidenceRestriction vertex;

	public IncidenceRestrictionEvaluator(IncidenceRestriction vertex,
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
		Set<String> roles = new HashSet<String>();
		IsIncTypeIdOf_isIncTypeIdOf_omega typeIdOf = vertex
				.getFirst_isIncTypeIdOf_omega();
		while (typeIdOf != null) {
			roles.add(typeIdOf.getEdge().getFirst_incTypeId().getVertex()
					.get_name());
			typeIdOf = typeIdOf.getNextIsIncTypeIdOf_omegaAtVertex();
		}
		result = roles;
		return result;
	}

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return new VertexCosts(1, 1, 1);
	}

}
