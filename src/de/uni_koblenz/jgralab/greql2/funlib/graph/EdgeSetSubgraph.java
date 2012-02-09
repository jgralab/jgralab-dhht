package de.uni_koblenz.jgralab.greql2.funlib.graph;

import java.rmi.RemoteException;

import org.pcollections.PCollection;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.graphmarker.BooleanGraphMarker;
import de.uni_koblenz.jgralab.graphmarker.GlobalBooleanGraphMarker;
import de.uni_koblenz.jgralab.graphmarker.GraphMarker;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.funlib.NeedsGraphArgument;

@NeedsGraphArgument
public class EdgeSetSubgraph extends Function {

	public EdgeSetSubgraph() {
		super(
				"Returns the subgraph induced by the edge set given.",
				7, 1, 1.0, Category.GRAPH);
	}

	@SuppressWarnings("rawtypes")
	public GraphMarker evaluate(Graph graph, PCollection<Edge> edgeSet) {
		BooleanGraphMarker<GraphElement> subgraphMarker = new GlobalBooleanGraphMarker(graph);
		Edge currentEdge = graph.getFirstEdge();
		while (currentEdge != null) {
			if (edgeSet.contains(currentEdge)) {
				try {
					subgraphMarker.mark(currentEdge);
					subgraphMarker.mark(((BinaryEdge) currentEdge).getAlpha());
					subgraphMarker.mark(((BinaryEdge) currentEdge).getOmega());
				} catch (RemoteException ex) {
					throw new RuntimeException(ex);
				}

			}
			currentEdge = currentEdge.getNextEdge();
		}
		return subgraphMarker;
	}
	
}
