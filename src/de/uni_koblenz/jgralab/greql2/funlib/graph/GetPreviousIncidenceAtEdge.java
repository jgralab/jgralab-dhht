package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetPreviousIncidenceAtEdge extends Function {

	public GetPreviousIncidenceAtEdge() {
		super(
				"Returns the previous incidence of the given incidence at its edge.",
				Category.GRAPH);
	}

	public Incidence evaluate(Incidence i) {
		return i.getPreviousIncidenceAtEdge();
	}

}
