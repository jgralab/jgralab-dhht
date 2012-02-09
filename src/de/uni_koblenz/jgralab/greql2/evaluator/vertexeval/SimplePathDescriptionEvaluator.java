package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.schema.SimplePathDescription;

/**
 * Superclass of the Edge- and Incidence-specific path description evaluator
 * classes.
 * 
 * @author jtheegarten@uni-koblenz.de 2012, Diploma Thesis
 * 
 */
public abstract class SimplePathDescriptionEvaluator extends
		PrimaryPathDescriptionEvaluator {

	public SimplePathDescriptionEvaluator(SimplePathDescription vertex,
			GreqlEvaluator eval) {
		super(vertex, eval);
	}
}