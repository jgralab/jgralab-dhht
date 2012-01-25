package de.uni_koblenz.jgralab.graphmarker;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;


/**
 * Implements a GraphMarker to mark vertices of a global DHHTGraph with integer
 * using LocalArrayVertexMarkers to mark elements in the several partial graphs.
 * 
 * @author dbildh
 */
public class GlobalIntegerVertexMarker extends GlobalGraphMarker<Vertex> {

	public GlobalIntegerVertexMarker(Graph globalGraph) {
		super(globalGraph);
	}

	protected final GraphMarker<Vertex> createMarkerForPartialGraph() {
		return new LocalDoubleVertexMarker(graph);
	}
	
	/**
	 * Marks the given vertex <code>vertex</code> with the given object <code>value</code>
	 * @param vertex the vertex to be marked
	 * @param value the object to be used a mark
	 */
	public final void mark(Vertex vertex, int value) {
		LocalDoubleVertexMarker localMarker = (LocalDoubleVertexMarker) getOrCreateMarkerForPartialGraph(vertex);
		localMarker.mark(vertex, value);		
	}
	
	
	
	
}
