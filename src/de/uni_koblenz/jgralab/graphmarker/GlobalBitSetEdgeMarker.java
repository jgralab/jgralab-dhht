package de.uni_koblenz.jgralab.graphmarker;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;


/**
 * Implements a GraphMarker to mark edges of a global DHHTGraph with objects of 
 * type <code>O</code> using LocalArrayEdgeMarkers to mark elements in the several
 * partial graphs.
 * 
 * @author dbildh
 */
public class GlobalBitSetEdgeMarker<O> extends GlobalGraphMarker<Edge> {

	public GlobalBitSetEdgeMarker(Graph globalGraph) {
		super(globalGraph);
	}

	protected GraphMarker<Edge> createMarkerForPartialGraph() {
		return new LocalBitSetEdgeMarker(graph);
	}
	
	/**
	 * Marks the given edge <code>edge</code> 
	 * @param edge the edge to be marked
	 */
	public void mark(Edge edge) {
		LocalBitSetEdgeMarker localMarker = (LocalBitSetEdgeMarker) getOrCreateMarkerForPartialGraph(edge);
		localMarker.mark(edge);		
	}
		
	
}
