package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetAlpha extends Function {

	public GetAlpha() {
		super("Returns the start-vertex of the given binary edge",
				Category.GRAPH);
	}

	public Vertex evaluate(BinaryEdge e) {
		return e.getAlpha();
	}
}
