package de.uni_koblenz.jgralab.greql2.funlib.graph;

import java.rmi.RemoteException;

import org.pcollections.PCollection;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.BooleanGraphMarker;
import de.uni_koblenz.jgralab.graphmarker.GlobalBooleanGraphMarker;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.funlib.NeedsGraphArgument;

@NeedsGraphArgument
public class VertexSetSubgraph extends Function {

	public VertexSetSubgraph() {
		super(
				"Returns the subgraph induced by the vertex type given.",
				7, 1, 1.0, Category.GRAPH);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BooleanGraphMarker evaluate(Graph graph, PCollection<Vertex> vertexSet) {
		BooleanGraphMarker subgraphMarker = new GlobalBooleanGraphMarker(graph);
		try {
		for (Vertex currentVertex : vertexSet) {
			subgraphMarker.mark(currentVertex);
		}
		// add all edges
		for (Vertex currentVertex : vertexSet) {
			Incidence currentIncidence = currentVertex.getFirstIncidence(Direction.VERTEX_TO_EDGE);
			while (currentIncidence != null) {
				if (currentIncidence.getEdge().isBinary()) {
					if (subgraphMarker.isMarked(currentIncidence.getThat())) {
						subgraphMarker.mark(currentIncidence.getEdge());
					}
				}
				currentIncidence = currentIncidence.getNextIncidenceAtVertex(Direction.VERTEX_TO_EDGE);
			}
		}
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
		return subgraphMarker;
	}
	
}
