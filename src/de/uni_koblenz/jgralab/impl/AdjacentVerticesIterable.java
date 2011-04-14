package de.uni_koblenz.jgralab.impl;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Vertex;

public class AdjacentVerticesIterable<T extends Vertex> extends AdjacentElementsIterable<Vertex, T> {

	public AdjacentVerticesIterable(Vertex elem) throws RemoteException {
		super(elem);
	}

	public AdjacentVerticesIterable(Vertex elem, Class<? extends T> vertexClass) throws RemoteException {
		super(elem, vertexClass);
	}
	
	public AdjacentVerticesIterable(Vertex elem, Direction dir) throws RemoteException {
		super(elem, dir);
	}
	
	public AdjacentVerticesIterable(Vertex elem, Class<? extends T> ec, Direction dir) throws RemoteException {
		super(elem, ec, dir);
	}
	

}
