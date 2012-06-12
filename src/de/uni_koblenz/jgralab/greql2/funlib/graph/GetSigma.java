package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetSigma extends Function {

	public GetSigma() {
		super("Returns the parent element of the given graphelement.",
				Category.GRAPH);
	}

	public GraphElement<?, ?, ?, ?> evaluate(GraphElement<?, ?, ?, ?> e) {
		return e.getSigma();
	}
}