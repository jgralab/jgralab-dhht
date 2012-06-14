package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetOmega extends Function {

	public GetOmega() {
		super("Returns the end-vertex of a given binary edge", Category.GRAPH);
	}

	public Vertex evaluate(BinaryEdge e) {
		return e.getOmega();
	}
}
