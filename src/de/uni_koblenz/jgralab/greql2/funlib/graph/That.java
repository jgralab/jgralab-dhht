package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class That extends Function {
	public That() {
		super("Returns the far vertex of an oriented edge.", 1, 1, 1.0,
				Category.GRAPH);
	}

	public Vertex evaluate(Incidence i) {
		return i.getThat();
	}
}