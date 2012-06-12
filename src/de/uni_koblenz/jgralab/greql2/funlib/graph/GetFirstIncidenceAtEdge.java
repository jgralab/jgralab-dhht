package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetFirstIncidenceAtEdge extends Function {

	public GetFirstIncidenceAtEdge() {
		super("Returns the first incidence at the given edge.", Category.GRAPH);
	}

	public Incidence evaluate(Edge e) {
		return e.getFirstIncidence();
	}
}
