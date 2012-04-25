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
	public String edgeString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String prettyPrint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Transition copy(boolean addToStates) {
		return new ElementRestrictionTransition(this, addToStates, types);
	}

	@Override
	public boolean equalSymbol(Transition t) {
		// TODO Auto-generated method stub
		return false;
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

	@Override
	public GraphElement<?, ?, ?, ?> getNextElement(
			GraphElement<?, ?, ?, ?> elem, Incidence inc) {
		// TODO Auto-generated method stub
		return null;
	}

}
