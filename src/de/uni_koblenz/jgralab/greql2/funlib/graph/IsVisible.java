package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class IsVisible extends Function {

	public IsVisible() {
		super(
				"Returns true iff the given element is visible at the given kappa-level.",
				Category.GRAPH);
	}

	public boolean evaluate(GraphElement<?, ?, ?, ?> e, int k) {
		return e.isVisible(k);
	}

}