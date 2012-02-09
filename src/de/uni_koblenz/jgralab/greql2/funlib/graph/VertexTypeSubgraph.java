package de.uni_koblenz.jgralab.greql2.funlib.graph;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.BooleanGraphMarker;
import de.uni_koblenz.jgralab.graphmarker.GlobalBooleanGraphMarker;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.funlib.NeedsGraphArgument;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

@NeedsGraphArgument
public class VertexTypeSubgraph extends Function {

	public VertexTypeSubgraph() {
		super(
				"Returns the subgraph induced by the vertex type given.",
				7, 1, 1.0, Category.GRAPH);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BooleanGraphMarker evaluate(Graph graph, TypeCollection typeCollection) {
		BooleanGraphMarker subgraphMarker = new GlobalBooleanGraphMarker(graph);
		Vertex currentVertex = graph.getFirstVertex();
		try {
		while (currentVertex != null) {
			if (typeCollection.acceptsType(currentVertex
					.getType())) {
				subgraphMarker.mark(currentVertex);
			}
			currentVertex = currentVertex.getNextVertex();
		}
		// add all edges
		Edge currentEdge = graph.getFirstEdge();
		while (currentEdge != null && currentEdge.isBinary()) {
			if (subgraphMarker.isMarked(((BinaryEdge) currentEdge).getAlpha())
					&& subgraphMarker.isMarked(((BinaryEdge) currentEdge).getOmega())) {
				subgraphMarker.mark(currentEdge);
			}
			currentEdge = currentEdge.getNextEdge();
		}
		return subgraphMarker;
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
	}
	
}
