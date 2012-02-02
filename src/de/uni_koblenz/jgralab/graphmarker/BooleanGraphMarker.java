package de.uni_koblenz.jgralab.graphmarker;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.AttributedElement;

@SuppressWarnings("rawtypes")
public interface BooleanGraphMarker<T extends AttributedElement> extends GraphMarker<T> {

	/**
	 * 
	 * @param graphElement
	 * @return the former mark of the graph element
	 * @throws RemoteException 
	 */
	public boolean mark(T graphElement) throws RemoteException;
	
}
