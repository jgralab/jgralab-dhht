package de.uni_koblenz.jgralab.greql2.evaluator.fa;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;
import de.uni_koblenz.jgralab.schema.TypedElementClass;

public class ElementRestrictionTransition extends Transition {

	private TypeCollection types = null;

	protected ElementRestrictionTransition(Transition t, boolean addToStates,
			TypeCollection types) {
		super(t, addToStates);
		this.types = types;
	}

	@Override
	public String incidenceString() {
		StringBuilder desc = new StringBuilder(
				"ElementRestrictionTransition (Types: ");
		if (types != null) {
			desc.append("\n " + types.toString() + "\n ");
		}
		desc.append(")");
		return desc.toString();
	}

	@Override
	public String prettyPrint() {
		return "{" + types.toString() + "}";
	}

	@Override
	public Transition copy(boolean addToStates) {
		return new ElementRestrictionTransition(this, addToStates, types);
	}

	@Override
	public boolean equalSymbol(Transition t) {
		if (!(t instanceof ElementRestrictionTransition)) {
			return false;
		}
		if (!(((ElementRestrictionTransition) t).types.equals(this.types))) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isEpsilon() {
		return false;
	}

	@Deprecated
	public Vertex getNextVertex(Vertex v, Edge e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Deprecated
	public boolean consumesEdge() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean consumesIncidence() {
		return false;
	}

	@Override
	public boolean accepts(GraphElement<?, ?, ?, ?> e, Incidence i) {
		return accepts(e);
	}

	/**
	 * Checks whether the given {@link GraphElement} is from one of the accepted types. Accepts the transition if it is, not if it isn't.
	 * @param e The {@link GraphElement} to be "used" in this transition.
	 * @return
	 */
	public boolean accepts(GraphElement<?, ?, ?, ?> e) {
		boolean typeAccepted = false;

		for (TypedElementClass<?, ?> eClass : types.getAllowedTypes()) {
			if (((TypedElementClass<?, ?>) e.getType()) == eClass) {
				typeAccepted = true;
				break;
			}
		}
		if (!typeAccepted) {
			return false;
		}

		// TODO jtheegarten: check for concrete elements defined as the
		// restriction, but might be better as its own transition
		return true;
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
