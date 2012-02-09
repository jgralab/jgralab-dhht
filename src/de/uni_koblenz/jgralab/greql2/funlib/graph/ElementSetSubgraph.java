package de.uni_koblenz.jgralab.greql2.funlib.graph;


import java.rmi.RemoteException;

import org.pcollections.PCollection;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.BooleanGraphMarker;
import de.uni_koblenz.jgralab.graphmarker.GlobalBooleanGraphMarker;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.funlib.NeedsGraphArgument;

@NeedsGraphArgument
public class ElementSetSubgraph extends Function {

	public ElementSetSubgraph() {
		super(
				"Returns the subgraph induced by the vertex type given.",
				7, 1, 1.0, Category.GRAPH);
	}

	@SuppressWarnings("rawtypes")
	public BooleanGraphMarker evaluate(Graph graph, PCollection<Vertex> vertexSet, PCollection<Edge> edgeSet) {
		try {
		BooleanGraphMarker<GraphElement> subgraphMarker = new GlobalBooleanGraphMarker(graph);
		for (Vertex currentVertex : vertexSet) {
			subgraphMarker.mark(currentVertex);
		}
		// add all edges
		for (Edge currentEdge : edgeSet) {
			if ((subgraphMarker.isMarked(((BinaryEdge) currentEdge).getAlpha()) && (subgraphMarker.isMarked(((BinaryEdge) currentEdge).getOmega())))) {
				subgraphMarker.mark(currentEdge);
			}
		}
		
		return subgraphMarker;
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
	}
	
}
