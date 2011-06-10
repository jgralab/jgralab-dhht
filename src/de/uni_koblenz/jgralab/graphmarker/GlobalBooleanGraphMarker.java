package de.uni_koblenz.jgralab.graphmarker;


import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.schema.GraphElementClass;


/**
 * Implements a GraphMarker to mark edges of a global DHHTGraph with objects of 
 * type <code>O</code> using LocalArrayEdgeMarkers to mark elements in the several
 * partial graphs.
 * 
 * @author dbildh
 *
 * @param <O> The kind of objects that can be attached to the edges as marks
 */
@SuppressWarnings("rawtypes")
public class GlobalBooleanGraphMarker extends GlobalGraphMarker<GraphElement<? extends GraphElementClass, ? extends GraphElement, ? extends GraphElement>> {

	public GlobalBooleanGraphMarker(Graph globalGraph) {
		super(globalGraph);
	}

	protected final  GraphMarker<GraphElement<? extends GraphElementClass, ? extends GraphElement, ? extends GraphElement>> createMarkerForPartialGraph() {
		return  new LocalBooleanGraphMarker(graph);
	}
	
	/**
	 * Marks the given edge <code>edge</code> with the given object <code>value</code>
	 * @param edge the edge to be marked
	 * @param value the object to be used a mark
	 */
	public final void mark(GraphElement<? extends GraphElementClass, ? extends GraphElement, ? extends GraphElement> elem) {
		LocalBooleanGraphMarker localMarker = (LocalBooleanGraphMarker) getOrCreateMarkerForPartialGraph(elem);
		localMarker.mark(elem);		
	}
	
	
	
	
}
