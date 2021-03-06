package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Direction;


public class OutDegree extends DegreeFunction {

	public OutDegree() {
		super("Returns the out-degree of the given vertex.\n"
				+ "The scope can be limited by a path, a path system, or\n"
				+ "an type collection.", Direction.VERTEX_TO_EDGE);
	}

}
