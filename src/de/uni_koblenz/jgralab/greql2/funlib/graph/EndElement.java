package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.types.HyperPath;

public class EndElement extends Function {

	public EndElement() {
		super("Returns the end-element of a given HyperPath.", Category.GRAPH,
				Category.PATHS_AND_PATHSYSTEMS_AND_SLICES);
	}

	public GraphElement<?, ?, ?, ?> evaluate(HyperPath p) {
		return p.getEndElement();
	}
}
