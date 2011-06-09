package de.uni_koblenz.jgralab.graphmarker;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;


/**
 * Implements a GraphMarker to mark edges of a global DHHTGraph with objects of 
 * type <code>O</code> using LocalArrayEdgeMarkers to mark elements in the several
 * partial graphs.
 * 
 * @author dbildh
 *
 * @param <O> The kind of objects that can be attached to the edges as marks
 */
public class GlobalDoubleEdgeMarker extends GlobalGraphMarker<Edge> {

	public GlobalDoubleEdgeMarker(Graph globalGraph) {
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
	public final void mark(Edge edge, double value) {
		LocalDoubleEdgeMarker localMarker = (LocalDoubleEdgeMarker) getOrCreateMarkerForPartialGraph(edge);
		localMarker.mark(edge, value);		
	}
	
	
	
	
}
