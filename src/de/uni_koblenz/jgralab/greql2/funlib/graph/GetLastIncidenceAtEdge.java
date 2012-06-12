package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetLastIncidenceAtEdge extends Function {

	public GetLastIncidenceAtEdge() {
		super("Returns the last incidence at the given edge.", Category.GRAPH);
	}

	public Incidence evaluate(Edge e) {
		return e.getLastIncidence();
	}
}
