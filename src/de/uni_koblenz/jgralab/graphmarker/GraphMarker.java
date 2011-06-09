package de.uni_koblenz.jgralab.graphmarker;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;

public interface GraphMarker<T extends AttributedElement<?, ?>> {

	/**
	 * Checks if the given <code>graphElement</code> is marked.
	 * 
	 * @param graphElement
	 *            the graph element to check.
	 * @return true if the given <code>graphElement</code> is marked.
	 */
	public abstract boolean isMarked(T graphElement);

	/**
	 * Unmarks the given <code>graphElement</code>.
	 * 
	 * @param graphElement
	 *            the graph element to unmark.
	 * @return false if the given <code>graphElement</code> has already been
	 *         unmarked.
	 * @throws RemoteException 
	 */
	public abstract boolean removeMark(T graphElement);

	/**
	 * Returns the number of marked graph elements.
	 * 
	 * @return the number of marked graph elements.
	 */
	public abstract long size();

	/**
	 * Checks if this graph marker is empty.
	 * 
	 * @return true if this graph marker is empty.
	 */
	public abstract boolean isEmpty();

	/**
	 * Unmarks all marked graph elements.
	 */
	public abstract void clear();

	public abstract Graph getGraph();

	public abstract Iterable<T> getMarkedElements();


	public abstract void edgeDeleted(Edge e);
	

	public abstract void vertexDeleted(Vertex v);


}