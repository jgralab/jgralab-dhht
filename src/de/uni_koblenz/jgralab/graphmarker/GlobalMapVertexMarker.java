package de.uni_koblenz.jgralab.graphmarker;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;


/**
 * Implements a GraphMarker to mark vertices of a global DHHTGraph with objects of 
 * type <code>O</code> using LocalMapVertexMarkers to mark elements in the several
 * partial graphs.
 * 
 * @author dbildh
 *
 * @param <O> The kind of objects that can be attached to the vertices as marks
 */
public class GlobalMapVertexMarker<O> extends GlobalGraphMarker<Vertex> {

	public GlobalMapVertexMarker(Graph globalGraph) {
		super(globalGraph);
	}

	protected final GraphMarker<Vertex> createMarkerForPartialGraph() {
		return new LocalMapVertexMarker<O>(graph);
	}
	
	/**
	 * Marks the given vertex <code>vertex</code> with the given object <code>value</code>
	 * @param vertex the vertex to be marked
	 * @param value the object to be used a mark
	 */
	public final void mark(Vertex vertex, O value) {
		@SuppressWarnings("unchecked")
		LocalMapVertexMarker<O> localMarker = (LocalMapVertexMarker<O>) getOrCreateMarkerForPartialGraph(vertex);
		localMarker.mark(vertex, value);		
	}
	
	
	
	
}
