package de.uni_koblenz.jgralab.graphmarker;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;


/**
 * Implements a GraphMarker to mark edges of a global DHHTGraph with integer
 * using LocalArrayEdgeMarkers to mark elements in the several partial graphs.
 * 
 * @author dbildh
 */
public class GlobalLongEdgeMarker extends GlobalGraphMarker<Edge> {

	public GlobalLongEdgeMarker(Graph globalGraph) {
		super(globalGraph);
	}

	protected final GraphMarker<Edge> createMarkerForPartialGraph() {
		return new LocalDoubleEdgeMarker(graph);
	}
	
	/**
	 * Marks the given vertex <code>vertex</code> with the given object <code>value</code>
	 * @param vertex the vertex to be marked
	 * @param value the object to be used a mark
	 */
	public final void mark(Edge vertex, long value) {
		LocalDoubleEdgeMarker localMarker = (LocalDoubleEdgeMarker) getOrCreateMarkerForPartialGraph(vertex);
		localMarker.mark(vertex, value);		
	}
	
	
	
	
}
