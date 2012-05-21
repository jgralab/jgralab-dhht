package de.uni_koblenz.jgralab.greql2.evaluator.fa;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;

public abstract class RestrictionTransition extends Transition {

	protected VertexEvaluator predicateEval = null;

	public RestrictionTransition(Transition t, boolean addToStates) {
		super(t, addToStates);
	}

	public RestrictionTransition(State start, State end,
			VertexEvaluator predicateEval) {
		super(start, end);
		this.predicateEval = predicateEval;
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

	@Override
	public boolean accepts(GraphElement<?, ?, ?, ?> e, Incidence i) {
		return accepts(e);
	}

	protected abstract boolean accepts(GraphElement<?, ?, ?, ?> e);

	@Deprecated
	public Vertex getNextVertex(Vertex v, Edge e) {
		return null;
	}

	@Deprecated
	public boolean consumesEdge() {
		return false;
	}

	@Override
	public boolean isEpsilon() {
		return false;
	}

	protected boolean acceptsPredicate() {
		if (predicateEval == null) {
			return true;
		}
		Boolean result = (Boolean) predicateEval.evaluate();
		return result.booleanValue();
	}
}