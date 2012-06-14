package de.uni_koblenz.jgralab.greql2.evaluator.fa;

import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.schema.IncDirection;
import de.uni_koblenz.jgralab.schema.IncidenceType;

public class AggregationIncidenceTransition extends SimpleIncidenceTransition {

	public AggregationIncidenceTransition(State start, State end,
			IncDirection dir) {
		super(start, end, dir);
	}

	public AggregationIncidenceTransition(State start, State end,
			IncDirection dir, Set<String> roles) {
		super(start, end, dir, roles);
	}

	protected AggregationIncidenceTransition(AggregationIncidenceTransition t,
			boolean addToStates) {
		super(t, addToStates);
		validDirection = t.validDirection;
		roles = new HashSet<String>(roles);
	}

	@Override
	public String incidenceString() {
		String desc = "AggregationIncidenceTransition (Dir:"
				+ validDirection.toString();
		if (roles != null) {
			desc = desc + "\n " + roles.toString() + "\n ";
		}
		desc += ")";
		return desc;
	}

	@Override
	public boolean equalSymbol(Transition t) {
		if (!(t instanceof AggregationIncidenceTransition)) {
			return false;
		}
		AggregationIncidenceTransition et = (AggregationIncidenceTransition) t;
		if (!roles.equals(et.roles)) {
			return false;
		}
		if (!validDirection.equals(et.validDirection)) {
			return false;
		}

		return true;
	}

	@Override
	public Transition copy(boolean addToStates) {
		return new AggregationIncidenceTransition(this, addToStates);

	}

	@Override
	public boolean accepts(GraphElement<?, ?, ?, ?> e, Incidence i) {
		if (i.getType().getIncidenceType() == IncidenceType.EDGE) {
			return false;
		}
		return super.accepts(e, i);
	}
}
