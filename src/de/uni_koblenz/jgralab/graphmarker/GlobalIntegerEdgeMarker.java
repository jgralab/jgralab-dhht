package de.uni_koblenz.jgralab.graphmarker;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;


/**
 * Implements a GraphMarker to mark edges of a global DHHTGraph with integer
 * using LocalArrayEdgeMarkers to mark elements in the several partial graphs.
 * 
 * @author dbildh
 */
public class GlobalIntegerEdgeMarker extends GlobalGraphMarker<Edge> {

	public GlobalIntegerEdgeMarker(Graph globalGraph) {
		super(globalGraph);
	}

	protected final GraphMarker<Edge> createMarkerForPartialGraph() {
		return new LocalDoubleEdgeMarker(graph);
	}
	
	/**
	 * Marks the given edge <code>edge</code> with the given object <code>value</code>
	 * @param edge the edge to be marked
	 * @param value the object to be used a mark
	 */
	public final void mark(Edge edge, int value) {
		LocalDoubleEdgeMarker localMarker = (LocalDoubleEdgeMarker) getOrCreateMarkerForPartialGraph(edge);
		localMarker.mark(edge, value);		
	}
	
	
	
	
}
