package de.uni_koblenz.jgralab.graphmarker;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;

/**
 * Implements a GraphMarker to mark vertices of a global DHHTGraph with objects of 
 * type <code>O</code> using LocalArrayVertexMarkers to mark elements in the several
 * partial graphs.
 * 
 * @author dbildh
 *
 * @param <O> The kind of objects that can be attached to the vertices as marks
 */
public class GlobalArrayVertexMarker<O> extends GlobalGraphMarker<Vertex> {

	public GlobalArrayVertexMarker(Graph globalGraph) {
		super(globalGraph);
	}

	protected GraphMarker<Vertex> createMarkerForPartialGraph() {
		return new LocalArrayVertexMarker<O>(graph);
	}
	
	/**
	 * Marks the given vertex <code>vertex</code> with the given object <code>value</code>
	 * @param vertex the vertex to be marked
	 * @param value the object to be used a mark
	 */
	public void mark(Vertex vertex, O value) {
		@SuppressWarnings("unchecked")
		LocalArrayVertexMarker<O> localMarker = (LocalArrayVertexMarker<O>) getOrCreateMarkerForPartialGraph(vertex);
		localMarker.mark(vertex, value);		
	}
	
	
	
	
}
