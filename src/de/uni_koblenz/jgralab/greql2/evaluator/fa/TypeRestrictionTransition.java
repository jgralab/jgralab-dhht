package de.uni_koblenz.jgralab.greql2.evaluator.fa;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;
import de.uni_koblenz.jgralab.schema.TypedElementClass;

public class TypeRestrictionTransition extends RestrictionTransition {

	private TypeCollection types = null;

	protected TypeRestrictionTransition(Transition t, boolean addToStates,
			TypeCollection types) {
		super(t, addToStates);
		this.types = types;
	}

	public TypeRestrictionTransition(State start, State end,
			TypeCollection types, VertexEvaluator predicateEval) {
		super(start, end, predicateEval);
		this.types = types;

	}

	@Override
	public String incidenceString() {
		StringBuilder desc = new StringBuilder(
				"TypeRestrictionTransition (Types: ");
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
		return new TypeRestrictionTransition(this, addToStates, types);
	}

	@Override
	public boolean equalSymbol(Transition t) {
		if (!(t instanceof TypeRestrictionTransition)) {
			return false;
		}
		if (!(((TypeRestrictionTransition) t).types.equals(this.types))) {
			return false;
		}
		if (predicateEval != null) {
			if (predicateEval != ((TypeRestrictionTransition) t).predicateEval) {
				return false;
			}
		} else {
			if (((TypeRestrictionTransition) t).predicateEval != null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether the given {@link GraphElement} is from one of the accepted types. Accepts the transition if it is, not if it isn't.
	 * @param e The {@link GraphElement} to be "used" in this transition.
	 * @return
	 */
	@Override
	public boolean accepts(GraphElement<?, ?, ?, ?> e) {
		if (!super.acceptsPredicate()) {
			return false;
		}
		boolean typeAccepted = false;

		for (TypedElementClass<?, ?> eClass : types.getAllowedTypes()) {
			if (((TypedElementClass<?, ?>) e.getType()) == eClass) {
				typeAccepted = true;
				break;
			}
		}
		return typeAccepted;
	}

}
