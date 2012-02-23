package de.uni_koblenz.jgralab.greql2.funlib.graph;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.graphmarker.BooleanGraphMarker;
import de.uni_koblenz.jgralab.graphmarker.GlobalBooleanGraphMarker;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.funlib.NeedsGraphArgument;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

@NeedsGraphArgument
public class EdgeTypeSubgraph extends Function {

	public EdgeTypeSubgraph() {
		super(
				"Returns the subgraph induced by the edge type given.",
				7, 1, 1.0, Category.GRAPH);
	}

	@SuppressWarnings("rawtypes")
	public BooleanGraphMarker evaluate(Graph graph, TypeCollection typeCollection) {
		BooleanGraphMarker<AttributedElement> subgraphMarker = new GlobalBooleanGraphMarker(graph);
		Edge currentEdge = graph.getFirstEdge();
		while (currentEdge != null) {
			if (typeCollection.acceptsType(currentEdge
					.getType())) {
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
