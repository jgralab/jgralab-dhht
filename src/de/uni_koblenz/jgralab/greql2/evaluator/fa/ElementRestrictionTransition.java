package de.uni_koblenz.jgralab.greql2.evaluator.fa;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

public class ElementRestrictionTransition extends Transition {

	protected ElementRestrictionTransition(Transition t, boolean addToStates) {
		super(t, addToStates);
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
		return new ElementRestrictionTransition(this, addToStates);
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean accepts(GraphElement<?, ?, ?, ?> e, Incidence i) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GraphElement<?, ?, ?, ?> getNextElement(
			GraphElement<?, ?, ?, ?> elem, Incidence inc) {
		// TODO Auto-generated method stub
		return null;
	}

}
