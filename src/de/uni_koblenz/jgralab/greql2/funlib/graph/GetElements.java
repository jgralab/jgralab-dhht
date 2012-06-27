package de.uni_koblenz.jgralab.greql2.funlib.graph;

import org.pcollections.PSet;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.types.PathSystem;
import de.uni_koblenz.jgralab.greql2.types.Slice;

public class GetElements extends Function {

	public GetElements() {
		super("Returns the elements of a given PathSystem or Slice.",
				Category.GRAPH, Category.PATHS_AND_PATHSYSTEMS_AND_SLICES);
	}

	public PSet<GraphElement<?, ?, ?, ?>> evaluate(PathSystem p) {
		return p.getElements();
	}

	public PSet<GraphElement<?, ?, ?, ?>> evaluate(Slice s) {
		return s.getElements();
	}
}
