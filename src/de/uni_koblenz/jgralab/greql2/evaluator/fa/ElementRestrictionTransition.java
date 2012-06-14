package de.uni_koblenz.jgralab.greql2.evaluator.fa;

import java.util.List;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;

/**
 * Evaluates an ElementRestriction (curly brackets with an Expression,
 * which evaluates to a list of elements).
 * @author jtheegarten@uni-koblenz.de 2012, Diploma Thesis
 *
 */
public class ElementRestrictionTransition extends RestrictionTransition {

	protected VertexEvaluator evaluator = null;

	protected ElementRestrictionTransition(Transition t, boolean addToStates,
			VertexEvaluator eval) {
		super(t, addToStates);
		evaluator = eval;
	}

	public ElementRestrictionTransition(State start, State end,
			VertexEvaluator evaluator, VertexEvaluator predicateEval) {
		super(start, end, predicateEval);
		this.evaluator = evaluator;
	}

	@Override
	public String incidenceString() {
		StringBuilder desc = new StringBuilder("ElementRestrictionTransition");
		return desc.toString();
	}

	@Override
	public String prettyPrint() {
		return "{ " + evaluator.getVertex().getType().toString() + " }";
	}

	@Override
	public Transition copy(boolean addToStates) {
		return new ElementRestrictionTransition(this, addToStates, evaluator);
	}

	@Override
	public boolean equalSymbol(Transition t) {
		if (!(t instanceof ElementRestrictionTransition)) {
			return false;
		}
		if (!(((ElementRestrictionTransition) t).evaluator == evaluator)) {
			return false;
		}
		if (predicateEval != null) {
			if (predicateEval != ((ElementRestrictionTransition) t).predicateEval) {
				return false;
			}
		} else {
			if (((ElementRestrictionTransition) t).predicateEval != null) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean accepts(GraphElement<?, ?, ?, ?> e, Incidence i) {
		return accepts(e);
	}

	/**
	 * Checks whether the given {@link GraphElement} (e) is among the results
	 * of the {@link VertexEvaluator} this Transition is restricted by.
	 * 
	 * @param e The GraphElement to be checked.
	 * @return
	 */
	@Override
	public boolean accepts(GraphElement<?, ?, ?, ?> e) {
		if (!super.acceptsPredicate()) {
			return false;
		}

		@SuppressWarnings("unchecked")
		List<GraphElement<?, ?, ?, ?>> evalResult = (List<GraphElement<?, ?, ?, ?>>) evaluator
				.getResult();

		if (evalResult == null) {
			return true;
		}

		boolean acceptedElement = false;

		for (GraphElement<?, ?, ?, ?> ge : evalResult) {
			if (ge.equals(e)) {
				acceptedElement = true;
				break;
			}
		}
		return acceptedElement;
	}

}
