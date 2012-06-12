package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetKappa extends Function {

	public GetKappa() {
		super("Returns the numeric kappa-value of the given element.",
				Category.GRAPH);
	}

	public int evaluate(GraphElement<?, ?, ?, ?> e) {
		return e.getKappa();
	}
}
