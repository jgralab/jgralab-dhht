package de.uni_koblenz.jgralab.impl;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;


public class AdjacentEdgesIterable<T extends Edge> extends AdjacentElementsIterable<Edge, T> {

	public AdjacentEdgesIterable(Edge elem) throws RemoteException {
		super(elem);
	}

	public AdjacentEdgesIterable(Edge elem, Class<? extends T> vertexClass) throws RemoteException {
		super(elem, vertexClass);
	}
	
	public AdjacentEdgesIterable(Edge elem, Direction dir) throws RemoteException {
		super(elem, dir);
	}
	
	public AdjacentEdgesIterable(Edge elem, Class<? extends T> ec, Direction dir) throws RemoteException {
		super(elem, ec, dir);
	}
	

}
