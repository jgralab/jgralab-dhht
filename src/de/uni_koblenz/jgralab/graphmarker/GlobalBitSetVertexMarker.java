package de.uni_koblenz.jgralab.graphmarker;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;


/**
 * Implements a GraphMarker to mark vertices of a global DHHTGraph with objects of 
 * type <code>O</code> using LocalArrayVertexMarkers to mark elements in the several
 * partial graphs.
 * 
 * @author dbildh
 */
public class GlobalBitSetVertexMarker<O> extends GlobalGraphMarker<Vertex> {

	public GlobalBitSetVertexMarker(Graph globalGraph) {
		super(globalGraph);
	}

	protected final GraphMarker<Vertex> createMarkerForPartialGraph() {
		return new LocalBitSetVertexMarker(graph);
	}
	
	/**
	 * Marks the given vertex <code>vertex</code> 
	 * @param vertex the vertex to be marked
	 */
	public final void mark(Vertex edge) {
		LocalBitSetVertexMarker localMarker = (LocalBitSetVertexMarker) getOrCreateMarkerForPartialGraph(edge);
		localMarker.mark(edge);		
	}
		
	
}
