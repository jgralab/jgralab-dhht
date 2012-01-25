package de.uni_koblenz.jgralab.greql2.funlib.graph;

import org.pcollections.PVector;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.funlib.NeedsGraphArgument;

@NeedsGraphArgument
public class TopologicalSort extends Function {
	public TopologicalSort() {
		super(
				"Returns a list of vertices in topological order, iff the graph $g$ is acyclic."
						+ " Otherwise, the result is undefined.", 100, 1, 0.1,
				Category.GRAPH);
	}

	public PVector<? extends Vertex> evaluate(Graph g) {
		DepthFirstSearch dfs = new IterativeDepthFirstSearch(g);
		TopologicalOrderWithDFS a = new TopologicalOrderWithDFS(g, dfs);
		try {
			a.execute();
		} catch (AlgorithmTerminatedException e) {
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
		if (!a.isAcyclic()) {
			return null;
		}
		PVector<Vertex> result = JGraLab.vector();
		Permutation<Vertex> t = a.getTopologicalOrder();
		for (Vertex v : t.getRangeElements()) {
			result = result.plus(v);
		}
		return result;
	}

	@Override
	public long getEstimatedCardinality(int inElements) {
		return inElements;
	}
}
