package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetLastIncidenceAtVertex extends Function {

	public GetLastIncidenceAtVertex() {
		super("Returns the last incidence at the given vertex.", Category.GRAPH);
	}

	public Incidence evaluate(Vertex v) {
		return v.getLastIncidence();
	}

}
