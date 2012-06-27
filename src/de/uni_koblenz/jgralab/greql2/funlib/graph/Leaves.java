package de.uni_koblenz.jgralab.greql2.funlib.graph;

import org.pcollections.PSet;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.types.PathSystem;
import de.uni_koblenz.jgralab.greql2.types.Slice;

public class Leaves extends Function {

	public Leaves() {
		super(
				"Returns the set of leaf elements in the given path system or slice.",
				Category.GRAPH);
	}

	public PSet<GraphElement<?, ?, ?, ?>> evaluate(PathSystem p) {
		return p.getLeaves();
	}

	public PSet<GraphElement<?, ?, ?, ?>> evaluate(Slice s) {
		return s.getLeaves();
	}
}
