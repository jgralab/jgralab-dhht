package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetIncidenceEdge extends Function {

	public GetIncidenceEdge() {
		super("Returns the edge of the given incidence.", Category.GRAPH);
	}

	public Edge evaluate(Incidence i) {
		return i.getEdge();
	}
}
