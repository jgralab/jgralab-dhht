package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class ContainsElement extends Function {

	public ContainsElement() {
		super(
				"Returns true iff the first given element contains the second given element.",
				Category.GRAPH);
	}

	public boolean evaluate(GraphElement<?, ?, ?, ?> e,
			GraphElement<?, ?, ?, ?> x) {
		return e.containsElement(x);
	}
}
