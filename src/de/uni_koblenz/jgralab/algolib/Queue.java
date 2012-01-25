package de.uni_koblenz.jgralab.algolib;

import java.util.LinkedList;

public class Queue<T> extends LinkedList<T> implements Buffer<T> {


	public Queue() {
		super();
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -3407565090966892641L;

	@Override
	public T get() {
		return poll();
	}


}
