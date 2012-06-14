package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetThis extends Function {

	public GetThis() {
		super(
				"Returns the the this-vertex of the binary edge of the given incidence.",
				Category.GRAPH);
	}

	public Vertex evaluate(Incidence i) {
		return i.getThis();
	}
}
