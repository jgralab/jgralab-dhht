package de.uni_koblenz.jgralab.impl;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Vertex;

public class AdjacentVerticesIterable<T extends Vertex> extends AdjacentElementsIterable<Vertex, T> {

	public AdjacentVerticesIterable(Vertex elem)  {
		super(elem);
	}

	public AdjacentVerticesIterable(Vertex elem, Class<? extends T> vertexClass)  {
		super(elem, vertexClass);
	}
	
	public AdjacentVerticesIterable(Vertex elem, Direction dir)  {
		super(elem, dir);
	}
	
	public AdjacentVerticesIterable(Vertex elem, Class<? extends T> ec, Direction dir)  {
		super(elem, ec, dir);
	}
	

}
