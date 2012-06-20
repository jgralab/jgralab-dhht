package de.uni_koblenz.jgralab.greql2.evaluator.fa;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;
import de.uni_koblenz.jgralab.schema.TypedElementClass;

/**
 * Evaluates a TypeRestriction (curly brackets with a list
 * of types EdgeTypes or VertexTypes).
 * @author jtheegarten@uni-koblenz.de 2012, Diploma Thesis
 *
 */
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
		return "{" + types != null ? types.toString() : "" + " "
				+ predicateEval != null ? "@ " + predicateEval.toString() : ""
				+ "}";
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
		TypeRestrictionTransition trans = (TypeRestrictionTransition) t;
		if (trans.types == null) {
			if (this.types != null) {
				return false;
			}
		} else if (!trans.types.equals(this.types)) {
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
		if (types == null) {
			return true;
		}
		if (types.getAllowedTypes().size() == 0
				&& types.getForbiddenTypes().size() == 0) {
			return true;
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
