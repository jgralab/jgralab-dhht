package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetNextIncidenceAtEdge extends Function {

	public GetNextIncidenceAtEdge() {
		super("Returns the next incidence of the given incidence at its edge.",
				Category.GRAPH);
	}

	public Incidence evaluate(Incidence i) {
		return i.getNextIncidenceAtEdge();
	}

}
