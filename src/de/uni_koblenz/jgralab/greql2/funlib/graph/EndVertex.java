package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.types.Path;

public class EndVertex extends Function {

	public EndVertex() {
		super("Returns the end vertex of an edge or a path.", Category.GRAPH,
				Category.PATHS_AND_PATHSYSTEMS_AND_SLICES);
	}

	public Vertex evaluate(BinaryEdge e) {
		return e.getOmega();
	}

	public Vertex evaluate(Path p) {
		return p.getEndVertex();
	}
}
