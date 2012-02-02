package de.uni_koblenz.jgralab.graphmarker;

import de.uni_koblenz.jgralab.AttributedElement;

@SuppressWarnings("rawtypes")
public interface ObjectGraphMarker<T extends AttributedElement, O extends Object> extends GraphMarker<T> {

	/**
	 * Retrieves the mark of the element <code>elem</code>
	 * @param elem
	 * @return
	 */
	public O getMark(T elem);
	
	
	/**
	 * Marks the elem <code>elem</code> with the mark <code>mark</code>
	 * @param elem
	 * @param mark
	 * @return the old mark of the graph element <code>elem</codeY>
	 */
	public O mark(T elem, O mark);
}
