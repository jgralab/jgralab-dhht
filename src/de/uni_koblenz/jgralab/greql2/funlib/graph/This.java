package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class This extends Function {
	public This() {
		super("Returns the near vertex of an oriented edge.", 1, 1, 1.0,
				Category.GRAPH);
	}

	public Vertex evaluate(Incidence i) {
		return i.getThis();
	}
}