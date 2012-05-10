package de.uni_koblenz.jgralab.greql2.evaluator.fa;

import java.util.List;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;

public class ElementRestrictionTransition extends Transition {

	private VertexEvaluator evaluator = null;

	protected ElementRestrictionTransition(Transition t, boolean addToStates,
			VertexEvaluator eval) {
		super(t, addToStates);
		evaluator = eval;
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
		return true;
	}

	@Override
	public boolean isEpsilon() {
		return false;
	}

	@Override
	public boolean accepts(GraphElement<?, ?, ?, ?> e, Incidence i) {
		return false;
	}

	/**
	 * Checks whether the given {@link GraphElement} (e) is among the results
	 * of the {@link VertexEvaluator} this Transition is restricted by.
	 * 
	 * @param e The GraphElement to be checked.
	 * @return
	 */
	public boolean accepts(GraphElement<?, ?, ?, ?> e) {
		boolean acceptedElement = false;

		List<GraphElement<?, ?, ?, ?>> evalResult = (List<GraphElement<?, ?, ?, ?>>) evaluator
				.getResult();
		for (GraphElement<?, ?, ?, ?> ge : evalResult) {
			if (ge.equals(e)) {
				acceptedElement = true;
				break;
			}
		}
		return acceptedElement;
	}

	@Override
	public boolean consumesIncidence() {
		return false;
	}

	/**
	 * Since this transition only checks whether the element is allowed under
	 * the restrictions of the specified path description the next element is
	 * the given element. It has just been accepted and can be used for the
	 * next step of the evaluation.
	 */
	@Override
	public GraphElement<?, ?, ?, ?> getNextElement(
			GraphElement<?, ?, ?, ?> elem, Incidence inc) {
		return elem;
	}
}
