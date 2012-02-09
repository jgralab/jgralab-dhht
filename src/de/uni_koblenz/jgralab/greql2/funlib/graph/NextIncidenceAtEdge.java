package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

public class NextIncidenceAtEdge extends Function {
	public NextIncidenceAtEdge() {
		super(
				"Returns the next incidence at the edge for a given incidence, optionally restricted by a type collection."
						, 2, 1,
				1.0, Category.GRAPH);
	}

	public Incidence evaluate(Incidence i) {
		return i.getNextIncidenceAtEdge();
	}


	public Incidence evaluate(Incidence i, TypeCollection tc) {
		for (Incidence n = i.getNextIncidenceAtEdge(); n != null; n = n.getNextIncidenceAtEdge()) {
			if (tc.acceptsType(n.getType())) {
				return n;
			}
		}
		return null;
	}



}
