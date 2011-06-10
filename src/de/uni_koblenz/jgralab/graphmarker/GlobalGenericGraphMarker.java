package de.uni_koblenz.jgralab.graphmarker;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Vertex;


/**
 * Implements a GraphMarker to mark both vertices and edgesof a global DHHTGraph with 
 * objects of  type <code>O</code> using LocalGenericGraphMarkers to mark elements in 
 * the several partial graphs.
 * 
 * @author dbildh
 *
 * @param <O> The kind of objects that can be attached to the vertices as marks
 */
public class GlobalGenericGraphMarker<O> extends GlobalGraphMarker<GraphElement<?,?,?>> {

	public GlobalGenericGraphMarker(Graph globalGraph) {
		super(globalGraph);
	}

	protected final LocalGenericGraphMarker<O> createMarkerForPartialGraph() {
		return new LocalGenericGraphMarker<O>(graph);
	}
	
	/**
	 * Marks the given vertex or edge <code>element</code> with the given object <code>value</code>
	 * @param vertex the vertex to be marked
	 * @param value the object to be used a mark
	 */
	public final void mark(Vertex vertex, O value) {
		@SuppressWarnings("unchecked")
		LocalGenericGraphMarker<O> localMarker = (LocalGenericGraphMarker<O>) getOrCreateMarkerForPartialGraph(vertex);
		localMarker.mark(vertex, value);		
	}
	
	
	
	
}
