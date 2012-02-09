package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Direction;

public class InDegree extends DegreeFunction {

	public InDegree() {
		super("Returns the in-degree of the given vertex.\n"
				+ "The scope can be limited by a path, a path system, or\n"
				+ "an type collection.", Direction.VERTEX_TO_EDGE);
	}

}
