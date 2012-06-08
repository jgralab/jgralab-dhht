package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetIncidenceVertex extends Function {

	public GetIncidenceVertex() {
		super("Returns the vertex of the given incidence.", Category.GRAPH);
	}

	public Vertex evaluate(Incidence i) {
		return i.getVertex();
	}
}
