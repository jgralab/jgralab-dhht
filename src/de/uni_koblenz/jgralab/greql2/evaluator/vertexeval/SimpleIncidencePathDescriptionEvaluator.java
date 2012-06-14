package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.exception.QuerySourceException;
import de.uni_koblenz.jgralab.greql2.schema.IncidenceRestriction;
import de.uni_koblenz.jgralab.greql2.schema.IsIncTypeIdOf;
import de.uni_koblenz.jgralab.greql2.schema.SimpleIncidencePathDescription;

/**
 * Evaluator-class for the {@link SimpleIncidencePathDescription}, which is a
 * single incidence-based path-description (+>, <+> or <+). It might be restricted
 * to a set of incidence "classes" via "{ Type1, Type2 }" after the arrow.
 * 
 * @author jtheegarten@uni-koblenz.de 2012, Diploma Thesis
 * 
 */
public class SimpleIncidencePathDescriptionEvaluator extends
		SimplePathDescriptionEvaluator {

	public SimpleIncidencePathDescriptionEvaluator(
			SimpleIncidencePathDescription vertex, GreqlEvaluator eval) {
		super(vertex, eval);
	}

	@Override
	public Object evaluate() throws QuerySourceException {

		SimpleIncidencePathDescription incVertex = (SimpleIncidencePathDescription) vertex;

		// We need to get the restrictions
		IncidenceRestriction restriction = incVertex
				.getFirst_isIncRestrOf_omega().getEdge().getAlpha();
		Set<String> roles = null;

		// And translate them to a Set of Strings
		if (restriction != null) {
			roles = new HashSet<String>();
			Iterator<IsIncTypeIdOf> incs = restriction.getIncidentEdges(
					IsIncTypeIdOf.class).iterator();
			while (incs != null && incs.hasNext()) {
				roles.add(incs.next().getAlpha().get_name());
			}
		}

		createdNFA = NFA.createSimpleIncidencePathDescriptionNFA(incVertex
				.getFirst_isIncDirOf_omega().getEdge().getAlpha().get_dir(),
				roles, vertexEvalMarker);
		return createdNFA;
	}

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return new VertexCosts(1, 1, 1);
	}

}
